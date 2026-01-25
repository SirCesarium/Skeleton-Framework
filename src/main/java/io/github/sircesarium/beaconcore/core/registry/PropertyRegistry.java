package io.github.sircesarium.beaconcore.core.registry;

import io.github.sircesarium.beaconcore.core.error.BeaconRegistryException;
import io.github.sircesarium.beaconcore.core.util.BeaconErrorFormatter;
import io.github.sircesarium.beaconcore.core.util.TextUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class PropertyRegistry {
    private static final Map<String, Supplier<Item.Properties>> ITEM_PROPS = new HashMap<>();
    private static final Map<String, Supplier<BlockBehaviour.Properties>> BLOCK_PROPS = new HashMap<>();

    // --- GENERIC ---
    private static <T> void save(String id, Supplier<T> supplier, Map<String, Supplier<T>> targetMap, String logType) {
        if (targetMap.containsKey(id)) {
            throw new BeaconRegistryException(BeaconErrorFormatter.formatDuplicateProperty(id, logType));
        }
        targetMap.put(id, supplier);
    }

    private static <T> T get(String id, Map<String, Supplier<T>> targetMap) {
        Supplier<T> supplier = targetMap.get(id);
        if (supplier == null) {
            String suggestion = TextUtil.findClosestMatch(id, targetMap.keySet());
            throw new BeaconRegistryException(BeaconErrorFormatter.formatMissingProperty(id, suggestion));
        }
        T props = supplier.get();
        if (props == null) {
            throw new BeaconRegistryException("The property supplier for '" + id + "' returned null.");
        }
        return props;
    }

    // --- ITEM PUBLIC API ---
    public static void saveItemProps(String name, Supplier<Item.Properties> propsSupplier) {
        save(name, propsSupplier, ITEM_PROPS, "Item");
    }

    public static Item.Properties getItemProps(String id) {
        return get(id, ITEM_PROPS);
    }

    public static boolean existsInItemProps(String id) {
        return ITEM_PROPS.containsKey(id);
    }

    public static Set<String> getAllRegisteredItemIds() {
        return ITEM_PROPS.keySet();
    }

    // --- BLOCK PUBLIC API ---
    public static void saveBlockProps(String name, Supplier<BlockBehaviour.Properties> propsSupplier) {
        save(name, propsSupplier, BLOCK_PROPS, "Block");
    }

    public static BlockBehaviour.Properties getBlockProps(String id) {
        return get(id, BLOCK_PROPS);
    }

    public static boolean existsInBlockProps(String id) {
        return BLOCK_PROPS.containsKey(id);
    }

    public static Set<String> getAllRegisteredBlockIds() {
        return BLOCK_PROPS.keySet();
    }

}
