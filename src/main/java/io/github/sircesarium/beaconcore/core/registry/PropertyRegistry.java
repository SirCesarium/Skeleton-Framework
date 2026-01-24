package io.github.sircesarium.beaconcore.core.registry;

import io.github.sircesarium.beaconcore.core.error.BeaconRegistryException;
import io.github.sircesarium.beaconcore.core.util.BeaconErrorFormatter;
import io.github.sircesarium.beaconcore.core.util.TextUtil;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PropertyRegistry {
    private static final Map<String, Item.Properties> ITEM_PROPS = new HashMap<>();

    public static void saveItemProps(String name, Item.Properties props) {
        if (ITEM_PROPS.containsKey(name)) {
            throw new BeaconRegistryException(BeaconErrorFormatter.formatDuplicateProperty(name));
        }
        ITEM_PROPS.put(name, props);
    }

    public static Item.Properties getItemProps(String id) {
        Item.Properties props = ITEM_PROPS.get(id);

        if (props == null) {
            String suggestion = TextUtil.findClosestMatch(id, ITEM_PROPS.keySet());
            String errorMessage = BeaconErrorFormatter.formatMissingProperty(id, suggestion);
            throw new BeaconRegistryException(errorMessage);
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
