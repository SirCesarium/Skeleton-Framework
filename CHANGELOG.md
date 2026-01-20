Update `@SkeletonItem` annotation to accept custom `Item` classes.

Usage:

Define a custom class extending `Item`

```java
public class CustomItem extends Item {/* ... */}
```

```java
@SkeletonItem(value = "my_item", type = CustomItem.class)
public static Item MY_ITEM;
```
