package io.github.sircesarium.beaconcore.core.registry;

import io.github.sircesarium.beaconcore.core.error.BeaconRegistryException;
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
            String suggestion = findClosestMatch(id);

            StringBuilder sb = new StringBuilder();
            sb.append("\n\n§c§l[Beacon Core Error]§r\n");
            sb.append("§fProperty with ID '§e").append(id).append("§f' was not found!\n");

            if (suggestion != null) {
                sb.append("\n💡 §6§nDid you mean §l").append(suggestion).append("§6§n?§r\n");
            }

            sb.append("\n§7Try annotating a field with:\n");
            sb.append("§e@WithItemProps(\"").append(suggestion).append("\")§r\n");

            throw new BeaconRegistryException(sb.toString());
        }
        return ITEM_PROPS.get(id);
    }

    private static String findClosestMatch(String target) {
        String closest = null;
        int minDistance = Integer.MAX_VALUE;

        for (String existingId : ITEM_PROPS.keySet()) {
            int distance = calculateLevenshteinDistance(target.toLowerCase(), existingId.toLowerCase());

            int threshold = Math.max(1, existingId.length() / 3);

            if (distance < minDistance && distance <= threshold) {
                minDistance = distance;
                closest = existingId;
            }
        }
        return closest;
    }

    private static int calculateLevenshteinDistance(String s1, String s2) {
        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) costs[j] = j;
                else if (j > 0) {
                    int newValue = costs[j - 1];
                    if (s1.charAt(i - 1) != s2.charAt(j - 1))
                        newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                    costs[j - 1] = lastValue;
                    lastValue = newValue;
                }
            }
            if (i > 0) costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }
}
