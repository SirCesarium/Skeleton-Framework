package io.github.sircesarium.beaconcore.core.util;

public final class BeaconErrorFormatter {
    public static String formatMissingProperty(String id, String suggestion) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n§c§l[Beacon Core Error]§r\n");
        sb.append("§fProperty with ID '§e").append(id).append("§f' was not found!\n");

        if (suggestion != null) {
            sb.append("\n💡 §6§nDid you mean §l").append(suggestion).append("§6§n?§r\n");
            sb.append("\n§7Try annotating a field with:\n");
            sb.append("§e@WithItemProps(\"").append(suggestion).append("\")§r\n");
        }

        return sb.toString();
    }

    public static String formatMissingFallback(String fallbackId, String targetField, String suggestion) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n§c§l[Beacon Core Error]§r\n");
        sb.append("§fThe fallback '§e").append(fallbackId).append("§f' defined in '§b")
                .append(targetField).append("§f' was not found!\n");

        if (suggestion != null) {
            sb.append("\n💡 §6§nDid you mean §l").append(suggestion).append("§6§n?§r\n");
        }

        sb.append("\n§7A fallback must be a previously registered ID via @RegisterItemProps.\n");
        sb.append("§7Ensure the fallback is loaded before this field.§r\n");

        return sb.toString();
    }

    public static String formatDuplicateProperty(String id, String propName) {
        return "\n\n§c§l[Beacon Core Error]§r\n" +
                "§fDuplicate " + propName + " Property ID detected: '§e" + id + "§f'\n" +
                "§7Properties cannot be overwritten once registered. Please use a unique ID.§r\n";
    }
}
