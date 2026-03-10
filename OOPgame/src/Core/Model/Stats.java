package Core.Model;

import java.util.HashMap;
import java.util.Map;

public class Stats {
    private final Map<Byte, Integer> stats = new HashMap<>();

    public static final byte FACILITY = 0;
    public static final byte ECONOMY = 1;
    public static final byte ENVIRONMENT = 2;
    public static final byte Population = 3;

    /**
     * Sets a specific stat. In Java, .put() handles both
     * adding a new key and updating an existing one.
     */
    public void setStat(byte statType, int val) {
        stats.put(statType, val);
    }

    /**
     * Retrieves a stat.
     * .getOrDefault ensures you don't get a NullPointerException if the stat isn't set yet.
     */
    public int getStat(byte statType) {
        return stats.getOrDefault(statType, 0);
    }

    /**
     * Helper to increment a stat (common for game/app stats)
     */
    public void incrementStat(byte statType, int amount) {
        stats.put(statType, getStat(statType) + amount);
    }
}