package Dummy.Map;

import java.util.HashMap;
import java.util.Map;

public class PoliticsStats {
    public Map<Long, Integer> stats;
    public final static long Facility = 0L;
    public final static long Military = 1L;
    public final static long Economy = 2L;

    public PoliticsStats(int facility, int military, int economy) {
        stats = new HashMap<>();
        stats.put(Facility, facility);
        stats.put(Military, military);
        stats.put(Economy, economy);
    }

    public int getStats(long statType) {
        return stats.get(statType);
    }

    public void addStats(long statType, int value) {
        stats.put(statType, getStats(statType) + value);
    }
}
