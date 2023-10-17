# JMultiCsv

A Java framework to manage multi schema CSV with java bean

## Example

For a CSV

```csv
BKT;1548454
ITEM;145;2
ITEM;147;4
BKT;9458785
ITEM;175;2
ITEM;177;4
```

```java
@Data
public class BasketItem {

    @Column(position = 1)
    private String sku;

    @Column(position = 2)
    private int quantity;
}
```


```java
@HeaderColumn("BKT")
@Data
public class Basket {

    @Column(position = 1)
    private String id;

    @HeaderColumn("ITEM")
    private List<BasketItem> items;
}
```

```java
public class Main {
    public static void main(String[] args) {
        try (CsvReader<Basket> reader = CsvReaderFactory.instanceOf("/path/to/file/xxxx.csv", Basket.class)) {
            do {
                Basket basket = reader.read();
                if (basket != null) {
                    // DO something with basket
                }
            } while (basket != null);
        }
    }
}
```



