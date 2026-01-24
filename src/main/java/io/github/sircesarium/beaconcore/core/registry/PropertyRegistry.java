package io.github.sircesarium.beaconcore.core.registry;

import io.github.sircesarium.beaconcore.core.error.BeaconRegistryException;
import io.github.sircesarium.beaconcore.core.util.BeaconErrorFormatter;
import io.github.sircesarium.beaconcore.core.util.TextUtil;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class PropertyRegistry {
    private static final Map<String, Supplier<Item.Properties>> ITEM_PROPS = new HashMap<>();

    public static void saveItemProps(String name, Supplier<Item.Properties> propsSupplier) {
        if (ITEM_PROPS.containsKey(name)) {
            throw new BeaconRegistryException(BeaconErrorFormatter.formatDuplicateProperty(name));
        }
        ITEM_PROPS.put(name, propsSupplier);
    }

    public static Item.Properties getItemProps(String id) {
        Supplier<Item.Properties> supplier = ITEM_PROPS.get(id);

        if (supplier == null) {
            String suggestion = TextUtil.findClosestMatch(id, ITEM_PROPS.keySet());
            throw new BeaconRegistryException(BeaconErrorFormatter.formatMissingProperty(id, suggestion));
        }

        Item.Properties props = supplier.get();

        if (props == null) {
            throw new BeaconRegistryException("The property supplier for '" + id + "' returned null.");
        }

        return props;
    }

    public static boolean existsInItemProps(String id) {
        return ITEM_PROPS.containsKey(id);
    }

    public static Set<String> getAllRegisteredItemIds() {
        return ITEM_PROPS.keySet();
    }
}
