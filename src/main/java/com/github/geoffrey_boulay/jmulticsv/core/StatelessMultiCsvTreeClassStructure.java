package com.github.geoffrey_boulay.jmulticsv.core;

import com.github.geoffrey_boulay.jmulticsv.annotation.Column;
import com.github.geoffrey_boulay.jmulticsv.util.PredicateUtils;
import com.github.geoffrey_boulay.jmulticsv.annotation.HeaderColumn;
import com.github.geoffrey_boulay.jmulticsv.exception.CsvBadClassStructureException;
import com.github.geoffrey_boulay.jmulticsv.exception.CsvDataStructureException;
import com.github.geoffrey_boulay.jmulticsv.exception.CsvReflectionException;
import com.github.geoffrey_boulay.jmulticsv.function.TriConsumer;
import lombok.AccessLevel;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@Getter
class StatelessMultiCsvTreeClassStructure {

    private String header;

    private Map<String, StatelessMultiCsvTreeClassStructure> subStructures;

    @Getter(AccessLevel.PRIVATE)
    private Map<String, TriConsumer<Object, String, Object>> linkMethod;

    @Getter(AccessLevel.PRIVATE)
    private ClassicCsvStructure<?> classicCsvStructure;

    @Getter(AccessLevel.PRIVATE)
    private List<Consumer<Object>> doOnInitObject;

    StatelessMultiCsvTreeClassStructure(Class<?> dtoClass) {
        this(dtoClass, dtoClass.getAnnotation(HeaderColumn.class).value(), new ArrayList<>());
    }

    private StatelessMultiCsvTreeClassStructure(Class<?> dtoClass, String header, List<String> notAllowHeader) {
        checkNoConflicts(header, notAllowHeader);
        classicCsvStructure = new ClassicCsvStructure<>(dtoClass);
        subStructures = new HashMap<>();
        linkMethod = new HashMap<>();
        doOnInitObject = new ArrayList<>();
        this.header = header;
        List<String> subHeaderColumns = subHeaderColumns(dtoClass);
        for (Field field : dtoClass.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                initFieldIfIsHeaderColumn(field, notAllowHeader, subHeaderColumns);
            }
        }
    }

    private void initFieldIfIsHeaderColumn(Field field, List<String> notAllowHeader, List<String> subHeaderColumns) {
        final Class<?> fieldClass = field.getType();
        Class<?> subClass = null;
        final TriConsumer<Object, String, Object> linkMethod;
        Optional<Consumer<Object>> initMethod = Optional.empty();
        if (List.class.isAssignableFrom(fieldClass)) {
            subClass = getGenericClass(field);
            initMethod = Optional.of(initWithNewInstance(field, List.class.equals(fieldClass) ? ArrayList.class : fieldClass));
            linkMethod = addInCollection(field);
        } else if (Set.class.isAssignableFrom(fieldClass)) {
            subClass = getGenericClass(field);
            initMethod = Optional.of(initWithNewInstance(field, Set.class.equals(fieldClass) ? HashSet.class : fieldClass));
            linkMethod = addInCollection(field);
        } else if (Optional.class.equals(fieldClass)) {
            subClass = getGenericClass(field);
            initMethod = Optional.of(initWithValue(field, Optional.empty()));
            linkMethod = checkEmptyAndSetPresent(field);
        } else {
            linkMethod = checkNullAndSet(field);
        }

        final Class<?> threeClass = subClass == null ? fieldClass : subClass;


        HeaderColumn headerColumn = field.getAnnotation(HeaderColumn.class);
        if (headerColumn == null) {
            headerColumn = threeClass.getAnnotation(HeaderColumn.class);
        }

        if (headerColumn != null) {
            field.setAccessible(true);
            String subHeader = headerColumn.value();
            getLinkMethod().put(subHeader, linkMethod);
            initMethod.ifPresent(doOnInitObject::add);


            notAllowHeader.addAll(subHeaderColumns);
            notAllowHeader.remove(subHeader);
            subStructures.put(subHeader, new StatelessMultiCsvTreeClassStructure(threeClass, subHeader, notAllowHeader));
            notAllowHeader.removeAll(subHeaderColumns);
        }


    }

    public Object initObject(String[] values) {
        Object result = classicCsvStructure.convert(values);
        for (Consumer<Object> consumer : doOnInitObject) {
            consumer.accept(result);
        }
        return result;
    }

    public void linkObjects(String headerColumn, Object ownerObject, Object subObject) {
        getLinkMethod().get(headerColumn).accept(ownerObject, headerColumn, subObject);
    }

    private List<String> subHeaderColumns(Class<?> dtoClass) {
        List<String> result = new ArrayList<>();
        for (Field field : dtoClass.getDeclaredFields()) {
            if (field.getAnnotation(Column.class) == null && !Modifier.isStatic(field.getModifiers())) {
                Class<?> fieldClass = field.getType();
                boolean isGeneric = List.class.isAssignableFrom(fieldClass)
                        || Set.class.isAssignableFrom(fieldClass)
                        || Optional.class.equals(fieldClass);

                final Class<?> threeClass = isGeneric ? getGenericClass(field) : fieldClass;

                HeaderColumn headerColumn = field.getAnnotation(HeaderColumn.class);
                if (headerColumn == null) {
                    headerColumn = threeClass.getAnnotation(HeaderColumn.class);
                }

                if (headerColumn != null) {
                    result.add(headerColumn.value());
                }
            }
        }
        return result;
    }

    private void checkNoConflicts(String header, List<String> notAllowHeader) {
        boolean alreadyInStack = notAllowHeader.contains(header);
        notAllowHeader.add(header);
        if (alreadyInStack) {
            throw new CsvBadClassStructureException("Cyclic definition of header " + notAllowHeader);
        }
    }

    private TriConsumer<Object, String, Object> addInCollection(Field field) {
        return (ownerObject, subObjectHeader, subObject) -> {
            try {
                Collection<Object> collection = (Collection<Object>) field.get(ownerObject);
                collection.add(subObject);
            } catch (IllegalAccessException e) {
                throw new CsvReflectionException(e);
            }
        };
    }

    private TriConsumer<Object, String, Object> checkEmptyAndSetPresent(Field field) {
        return checkCurrentValueAndSet(field, PredicateUtils.not(Optional::isPresent), Optional::of);
    }

    private TriConsumer<Object, String, Object> checkNullAndSet(Field field) {
        return checkCurrentValueAndSet(field, Objects::isNull, Function.identity());
    }

    private <X> TriConsumer<Object, String, Object> checkCurrentValueAndSet(Field field, Predicate<X> emptyPredicate, Function<Object, X> converter) {
        return (ownerObject, subObjectHeader, subObject) -> {
            try {
                X currentValue = (X) field.get(ownerObject);
                if (!emptyPredicate.test(currentValue)) {
                    throw new CsvDataStructureException("Multiple line with header " + subObjectHeader + " under line with header " + header);
                }
                field.set(ownerObject, converter.apply(subObject));
            } catch (IllegalAccessException e) {
                throw new CsvReflectionException(e);
            }
        };
    }


    private Consumer<Object> initWithNewInstance(Field field, Class<?> classType) {
        return (x) -> {
            try {
                field.set(x, classType.getConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new CsvReflectionException(e);
            }
        };
    }

    private Consumer<Object> initWithValue(Field field, Object value) {
        return (x) -> {
            try {
                field.set(x, value);
            } catch (IllegalAccessException e) {
                throw new CsvReflectionException(e);
            }
        };
    }

    private Class getGenericClass(Field field) {
        Type type = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        return toClass(type);
    }

    private Class<?> toClass(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        }
        try {
            Class<?> clazz = getClass().getClassLoader().loadClass(type.getTypeName());
            return clazz;
        } catch (ClassNotFoundException e) {
            throw new CsvReflectionException(e);
        }
    }

}
