package Core.Maps;

import java.util.HashMap;
import java.util.Map;

import Core.UI.PolicyCardHolderUI;
import org.json.JSONObject;

/**
 * Represents a collection of political and economic statistics.
 * This class is used to track values for Facilities, Environment, and Economy
 * for both Cities and Cards.
 */
public class PoliticsStats {
    /**
     * Internal map storing stat types (IDs) and their integer values.
     */
    public Map<Long, Integer> stats;

    /**
     * Constant ID for the Facility statistic.
     */
    public final static long FACILITY = 0L;
    /**
     * Constant ID for the Environment statistic.
     */
    public final static long ENVIRONMENT = 1L;
    /**
     * Constant ID for the Economy statistic.
     */
    public final static long ECONOMY = 2L;

    public PoliticsStats(PoliticsStats stats) {
        this(stats.getStats(FACILITY), stats.getStats(ENVIRONMENT), stats.getStats(ECONOMY));
    }

    /**
     * Constructs a new PoliticsStats object with initial values.
     *
     * @param facility    Initial value for facilities.
     * @param environment Initial value for environment.
     * @param economy     Initial value for economy.
     */
    public PoliticsStats(int facility, int environment, int economy) {
        stats = new HashMap<>();
        stats.put(FACILITY, facility);
        stats.put(ENVIRONMENT, environment);
        stats.put(ECONOMY, economy);
    }

    /**
     * Retrieves the value of a specific statistic.
     *
     * @param statType The ID of the statistic (FACILITY, ENVIRONMENT, or ECONOMY).
     * @return The current value of the statistic, or 0 if not found.
     */
    public int getStats(long statType) {
        return stats.getOrDefault(statType, 0);
    }

    /**
     * Adds a value to an existing statistic.
     *
     * @param statType The ID of the statistic to modify.
     * @param value    The amount to add (can be negative).
     */
    public void addStats(long statType, int value) {
        stats.put(statType, getStats(statType) + value);
    }

    public void addStats(PoliticsStats stats) {
        if (stats != null) {
            this.stats.put(FACILITY, stats.getStats(FACILITY));
            this.stats.put(ECONOMY, stats.getStats(ECONOMY));
            this.stats.put(ENVIRONMENT, stats.getStats(ENVIRONMENT));
        }
    }

    /**
     * Directly sets the value of a specific statistic.
     *
     * @param statType The ID of the statistic to set.
     * @param value    The new value.
     */
    public void setStats(long statType, int value) {
        stats.put(statType, value);
    }

    public static String getStatName(long id) {
        if (id == FACILITY) return "Facility";
        if (id == ENVIRONMENT) return "Environment";
        if (id == ECONOMY) return "Economics";
        return "Unknown " + id;
    }

    public static long getStatId(String name) {
        if (name.equals("Facility")) return FACILITY;
        if (name.equals("Environment")) return ENVIRONMENT;
        if (name.equals("Economics")) return ECONOMY;
        return -1L;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        for (Map.Entry<Long, Integer> entry : stats.entrySet()) {
            json.put(getStatName(entry.getKey()), entry.getValue());
        }
        return json;
    }

    public void updateFromJson(JSONObject json) {
        if (json == null) return;
        for (String key : json.keySet()) {
            long id = getStatId(key);
            if (id != -1L) {
                stats.put(id, json.getInt(key));
            }
        }
    }
}
