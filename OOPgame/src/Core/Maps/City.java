package Core.Maps;

import java.awt.*;
import java.util.ArrayList;
import org.json.*;

/**
 * Represents a City in the game world.
 * Cities contain population, political statistics, and manage the voting logic
 * based on player contributions (cards played).
 */
public class City {
    private final String cityName;
    /** The political and economic stats of the city. */
    public PoliticsStats stats;
    /** The total number of citizens in the city. */
    public int population;
    /** List of map grids that belong to this city. */
    public ArrayList<Grid> grids = new ArrayList<Grid>();
    private Color color;

    // --- Config ค่าคงที่ต่างๆ (ปรับ Balance ที่นี่) ---
    /** Multiplier for the logarithmic scoring formula. */
    public static final double K_LOG_MULTIPLIER = 200.0;
    /** Ratio of population required to earn one council seat. */
    public static final int POP_PER_SEAT = 10000;
    /** Base score added per council seat. */
    public static final double SCORE_PER_SEAT_BASE = 50.0;

    /** Total number of players in the game. */
    /** Number of seats available in this city's council. */
    public int councilSeats;
    /** The initial base score for the city. */
    public double baseScore;
    /** Current weighted scores for each player in this city. */
    public double[] playerScores;

    private int ownerId = -1;

    /**
     * Constructs a new City with specified parameters and initializes scoring.
     */
    public City(String cityName, int facility, int environment, int economy, int population) {
        // เริ่มต้นสถานะของเมือง
        this.stats = new PoliticsStats(facility, environment, economy);
        this.population = population;
        this.cityName = cityName;

        // 1. คำนวณจำนวนที่นั่งสภา (Council Seats)
        this.councilSeats = Math.max(1, this.population / POP_PER_SEAT);

        // 2. คำนวณ Dynamic Base Score (คะแนนตั้งต้น)
        this.baseScore = this.councilSeats * SCORE_PER_SEAT_BASE;

        // 3. สร้างคะแนนดิบเริ่มต้นให้ผู้เล่นทุกคนเท่ากัน
        int safeNumPlayers = 4;
        this.playerScores = new double[safeNumPlayers];
        for (int i = 0; i < safeNumPlayers; i++) {
            this.playerScores[i] = this.baseScore;
        }
    }

    public City(String cityName, int facility, int environment, int economy, int population, int numPlayers) {
        this.stats = new PoliticsStats(facility, environment, economy);
        this.population = population;
        this.cityName = cityName;

        this.councilSeats = Math.max(1, this.population / POP_PER_SEAT);


        this.baseScore = this.councilSeats * SCORE_PER_SEAT_BASE;

        int safeNumPlayers = Math.max(1, numPlayers);
        this.playerScores = new double[safeNumPlayers];
        for (int i = 0; i < safeNumPlayers; i++) {
            this.playerScores[i] = this.baseScore;
        }
        updateOwner();
    }

    public String getCityName() {
        return this.cityName;
    }

    /**
     * Calculates a score based on logarithmic diminishing returns.
     * @param currentStatVal The current value of the city's stat.
     * @param cardVal The value being added by a card.
     * @return The calculated score increase.
     */
    public double calculateLogScore(double currentStatVal, double cardVal) {
        // ป้องกันค่าติดลบ
        double currentVal = Math.max(0, currentStatVal);
        double newVal = Math.max(0, currentStatVal + cardVal);

        // สูตร: K * [ln(new + 1) - ln(old + 1)]
        return K_LOG_MULTIPLIER * (Math.log(newVal + 1) - Math.log(currentVal + 1));
    }

    /**
     * Applies a single stat change from a player's card to the city.
     * @param playerId Index of the player playing the card.
     * @param statType The type of stat being modified.
     * @param cardVal The amount of change.
     */
    public void applyCard(int playerId, long statType, double cardVal) {
        double currentStat = stats.getStats(statType);

        // คำนวณคะแนนที่ได้รับ
        double scoreGained = calculateLogScore(currentStat, cardVal);

        // อัปเดตคะแนนผู้เล่น (Score Weight)
        playerScores[playerId] += scoreGained;

        // อัปเดต Stat เมือง
        stats.addStats(statType, (int) cardVal);

        updateOwner();

        String statName = statType == PoliticsStats.FACILITY ? "Facility" :
                statType == PoliticsStats.ENVIRONMENT ? "Environment" : "Economy";

        System.out.printf("[%s] Player %d ลงการ์ด %s (+%.1f)%n", cityName, playerId, statName, cardVal);
        System.out.printf("   -> Stat เมืองเปลี่ยนจาก %.1f เป็น %d%n", currentStat, stats.getStats(statType));
        System.out.printf("   -> ได้คะแนนดิบเพิ่ม +%.2f คะแนน%n", scoreGained);

        // TODO: Sound Effect ตัวเอง
    }
    /**
     * Applies all statistics from a card to the city for a specific player.
     * @param playerId Index of the player.
     * @param cardStats The PoliticsStats object containing card effects.
     */
    public void applyStats(int playerId, PoliticsStats cardStats) {
        if (cardStats != null && cardStats.stats != null) {
            for (java.util.Map.Entry<Long, Integer> entry : cardStats.stats.entrySet()) {
                if (entry.getValue() != 0) {
                    applyCard(playerId, entry.getKey(), entry.getValue());
                }
            }
            getVotingResults();
        }
    }

    /**
     * Convenience method to apply stats for the default player (Player 0).
     * @param cardStats The stats to apply.
     */
    public void applyStats(PoliticsStats cardStats) {
        // หากไม่ระบุ Player ให้ถือว่าเป็น Player 0
        applyStats(0, cardStats);
    }

    /**
     * Checks if the specified player has the highest score in the city.
     * ตรวจสอบว่าผู้เล่นคนนี้มีคะแนนสูงสุดในเมืองหรือไม่ (รวมถึงกรณีที่คะแนนสูงสุดเท่ากับผู้อื่น)
     *
     * @param playerId ดัชนีของผู้เล่นที่ต้องการตรวจสอบ
     * @return true หากผู้เล่นมีคะแนนมากที่สุดหรือเท่ากับคะแนนสูงสุดในขณะนั้น
     */
    public boolean isPlayerDominateCity(int playerId){
        if (playerId < 0 || playerId >= playerScores.length) return false;

        double targetScore = playerScores[playerId];
        for (double score : playerScores) {
            if (score > targetScore) return false;
        }
        return true;
    }

    public double[] getPlayerScores() {
        return playerScores;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void updateOwner() {
        if (playerScores == null || playerScores.length == 0) {
            ownerId = -1;
            return;
        }
        int bestPlayer = -1;
        double maxScore = -1;
        boolean tie = false;
        for (int i = 0; i < playerScores.length; i++) {
            if (playerScores[i] > maxScore) {
                maxScore = playerScores[i];
                bestPlayer = i;
                tie = false;
            } else if (playerScores[i] == maxScore) {
                tie = true;
            }
        }
        ownerId = tie ? -1 : bestPlayer;

        if (Core.ZhuzheeGame.PLAYER_LIST_UI != null) {
            Core.ZhuzheeGame.PLAYER_LIST_UI.updatePlayerList();
        }
    }

    /**
     * Calculates the current percentage of influence a player has in the city.
     * @param playerId Index of the player.
     * @return Percentage (0.0 to 100.0).
     */
    public double getPlayerPercentage(int playerId) {
        if (playerId < 0 || playerId >= playerScores.length) return 0;
        double totalScore = 0;
        for (double score : playerScores) {
            totalScore += score;
        }
        if (totalScore == 0) return 0;
        return (playerScores[playerId] / totalScore) * 100;
    }

    /**
     * Prints the current voting simulation results to the console.
     */
    public void getVotingResults() {
        double totalScore = 0;
        for (double score : playerScores) totalScore += score;

        System.out.printf("%n--- ผลการเลือกตั้งเมือง: %s (ประชากร: %,d, ที่นั่ง: %d) ---%n", cityName, population, councilSeats);

        for (int i = 0; i < playerScores.length; i++) {
            double percent = (playerScores[i] / totalScore) * 100;
            int votes = (int)((playerScores[i] / totalScore) * this.population);
            System.out.printf("Player %d: %.2f%% (%,d เสียง)%n", i, percent, votes);
        }
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public ArrayList<Grid> getGrids() {
        return grids;
    }

    public JSONObject toJson() {
        JSONObject cityJson = new JSONObject();
        cityJson.put("name", getCityName());
        cityJson.put("population", population);

        if (playerScores != null) cityJson.put("players score", new JSONArray(playerScores));
        if (stats != null) cityJson.put("stats", stats.toJson());

        cityJson.put("color", color != null
            ? new JSONArray(new int[]{color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()})
            : new JSONArray(new int[]{255, 255, 255, 255}));

        if (grids != null) {
            JSONArray gridsArray = new JSONArray();
            for (Grid g : grids) gridsArray.put(g.toJsonPosition());
            cityJson.put("grids", gridsArray);
        }
        return cityJson;
    }


    public void updateFromJson(JSONObject cityData) {
        if (cityData == null) return;

        if (cityData.has("players score")) {
            JSONArray scores = cityData.getJSONArray("players score");
            if (this.playerScores == null || this.playerScores.length != scores.length()) {
                this.playerScores = new double[scores.length()];
            }
            for (int i = 0; i < scores.length(); i++) {
                this.playerScores[i] = scores.getDouble(i);
            }
            updateOwner();
        }

        if (cityData.has("stats")) {
            int oldFac = this.stats.getStats(PoliticsStats.FACILITY);
            int oldEnv = this.stats.getStats(PoliticsStats.ENVIRONMENT);
            int oldEcon = this.stats.getStats(PoliticsStats.ECONOMY);

            this.stats.updateFromJson(cityData.getJSONObject("stats"));

            int newFac = this.stats.getStats(PoliticsStats.FACILITY);
            int newEnv = this.stats.getStats(PoliticsStats.ENVIRONMENT);
            int newEcon = this.stats.getStats(PoliticsStats.ECONOMY);

            int dFac = newFac - oldFac;
            int dEnv = newEnv - oldEnv;
            int dEcon = newEcon - oldEcon;

            // TODO: แสดงผล Effect ตอนsyncข้อมูล เช่น poptext เห็นคะแนนเพื่มที่แผนที่
        }

        if (cityData.has("color")) {
            JSONArray c = cityData.getJSONArray("color");
            if (c.length() == 4) this.color = new Color(c.getInt(0), c.getInt(1), c.getInt(2), c.getInt(3));
        }
    }

    /**
     * Prints city details and stats to the console for debugging.
     */
    public void printStats() {
        System.out.println("----------------------------------");
        System.out.println("City: " + this.getCityName());
        System.out.println("Economic: " + stats.getStats(PoliticsStats.ECONOMY));
        System.out.println("Facility: " + stats.getStats(PoliticsStats.FACILITY));
        System.out.println("Environment: " + stats.getStats(PoliticsStats.ENVIRONMENT));
        System.out.println("Population: " + population);
        System.out.println("----------------------------------");
    }
}
