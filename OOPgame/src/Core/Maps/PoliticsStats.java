package Core.Maps;

import java.util.HashMap;
import java.util.Map;

public class PoliticsStats {
    public Map<Long, Integer> stats;
    public final static long FACILITY = 0L;
    public final static long ENVIRONMENT = 1L;
    public final static long ECONOMY = 2L;

    public PoliticsStats(int facility, int environment, int economy) {
        stats = new HashMap<>();
        stats.put(FACILITY, facility);
        stats.put(ENVIRONMENT, environment);
        stats.put(ECONOMY, economy);
    }

    public int getStats(long statType) {
        return stats.getOrDefault(statType, 0);
    }

    public void addStats(long statType, int value) {
        stats.put(statType, getStats(statType) + value);
    }

    public void setStats(long statType, int value) { stats.put(statType, value); }
}
