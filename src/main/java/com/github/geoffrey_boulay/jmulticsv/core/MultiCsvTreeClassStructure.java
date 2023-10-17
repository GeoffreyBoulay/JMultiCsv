package com.github.geoffrey_boulay.jmulticsv.core;

import com.github.geoffrey_boulay.jmulticsv.exception.CsvDataStructureException;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Stack;

@Slf4j
class MultiCsvTreeClassStructure {

    private final StatelessMultiCsvTreeClassStructure baseStructure;

    private final Stack<StructureStackElement> structureStack;

    private int lineIndex;

    MultiCsvTreeClassStructure(final Class<?> dtoClass) {
        baseStructure = new StatelessMultiCsvTreeClassStructure(dtoClass);
        structureStack = new Stack<>();
        lineIndex = 0;
    }


    public final Object accept(String[] values) {
        try {
            lineIndex++;
            return doAccept(values);
        } catch (RuntimeException e) {
            log.error("At line {} => {}", lineIndex, values, e);
            throw e;
        }
    }

    protected Object doAccept(String[] values) {
        Object toReturn = null;
        String headerColumn = values[0];

        boolean found = false;
        while (structureStack.size() > 0 && !found) {
            StructureStackElement lastStructure = structureStack.pop();
            StatelessMultiCsvTreeClassStructure subStructure = lastStructure.getStructure().getSubStructures().get(headerColumn);
            if (subStructure != null) {
                Object subObject = subStructure.initObject(values);
                lastStructure.linkObjects(headerColumn, subObject);
                structureStack.push(lastStructure);
                structureStack.push(new StructureStackElement(subStructure, subObject));
                found = true;
            } else {
                if (structureStack.isEmpty()) {
                    toReturn = lastStructure.getBuildingObject();
                }
            }
        }

        if (structureStack.isEmpty()) {
            if (headerColumn.equals(baseStructure.getHeader())) {
                structureStack.add(new StructureStackElement(baseStructure, baseStructure.initObject(values)));
            } else {
                invalidLine(values);
            }

        }

        return toReturn;
    }

    protected void invalidLine(String[] values) {
        structureStack.clear();
        throw new CsvDataStructureException("Invalid line value" + Arrays.asList(values));
    }

    public Object flush() {
        Object toReturn = structureStack.isEmpty() ? null : structureStack.get(0).getBuildingObject();
        structureStack.clear();
        return toReturn;
    }

    @Value
    private static class StructureStackElement {

        private final StatelessMultiCsvTreeClassStructure structure;

        private final Object buildingObject;

        public void linkObjects(String headerColumn, Object subObject) {
            structure.linkObjects(headerColumn, buildingObject, subObject);
        }
    }

}
