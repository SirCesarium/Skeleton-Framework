package io.github.sircesarium.skeletonframework.core.registry;

import io.github.sircesarium.skeletonframework.core.error.SkeletonRegistryException;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

public class PropertyRegistry {
    private static final Map<String, Item.Properties> ITEM_PROPS = new HashMap<>();

    public static void saveItemProps(String name, Item.Properties props) {
        ITEM_PROPS.put(name, props);
    }

    public static Item.Properties getItemProps(String id) {
        if (!ITEM_PROPS.containsKey(id)) {
            throw new SkeletonRegistryException(
                    "Property ID '" + id + "' was not found! " +
                            "Make sure you have a field annotated with @SkeletonItemProps(\"" + id + "\")."
            );
        }
        return ITEM_PROPS.get(id);
    }
}
