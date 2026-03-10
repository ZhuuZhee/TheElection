package Dummy.Maps;

import java.util.HashMap;
import java.util.Map;

public class PoliticsStats {
    public Map<Long, Integer> stats;
    public final static long Facility = 0L;
    public final static long Environment = 1L;
    public final static long Economy = 2L;

    public PoliticsStats(int facility, int environment, int economy) {
        stats = new HashMap<>();
        stats.put(Facility, facility);
        stats.put(Environment, environment);
        stats.put(Economy, economy);
    }

    public int getStats(long statType) {
        return stats.get(statType);
    }

    public void addStats(long statType, int value) {
        stats.put(statType, getStats(statType) + value);
    }

    public void setStats(long statType, int value) { stats.put(statType, value); }
}
