package io.github.sircesarium.beaconcore.core.util;

public final class TextUtil {
    public static String findClosestMatch(String target, Iterable<String> candidates) {
        String closest = null;
        int minDistance = Integer.MAX_VALUE;

        for (String candidate : candidates) {
            int distance = calculateLevenshteinDistance(target.toLowerCase(), candidate.toLowerCase());
            int threshold = Math.max(1, candidate.length() / 3);

            if (distance < minDistance && distance <= threshold) {
                minDistance = distance;
                closest = candidate;
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
