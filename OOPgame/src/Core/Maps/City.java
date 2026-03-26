package Core.Maps;

import java.awt.*;
import java.util.ArrayList;

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
    public static final double K_LOG_MULTIPLIER = 100.0;
    /** Ratio of population required to earn one council seat. */
    public static final int POP_PER_SEAT = 10000;
    /** Base score added per council seat. */
    public static final double SCORE_PER_SEAT_BASE = 50.0;

    /** Total number of players in the game. */
    public int numPlayers = 4;
    /** Number of seats available in this city's council. */
    public int councilSeats;
    /** The initial base score for the city. */
    public double baseScore;
    /** Current weighted scores for each player in this city. */
    public double[] playerScores;

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
        this.playerScores = new double[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            this.playerScores[i] = this.baseScore;
        }
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
        stats.addStats(statType, (int)cardVal);

        String statName = statType == PoliticsStats.FACILITY ? "Facility" :
                statType == PoliticsStats.ENVIRONMENT ? "Environment" : "Economy";

        System.out.printf("[%s] Player %d ลงการ์ด %s (+%.1f)%n", cityName, playerId, statName, cardVal);
        System.out.printf("   -> Stat เมืองเปลี่ยนจาก %.1f เป็น %d%n", currentStat, stats.getStats(statType));
        System.out.printf("   -> ได้คะแนนดิบเพิ่ม +%.2f คะแนน%n", scoreGained);
    }

    /** 
     * Applies all statistics from a card to the city for a specific player.
     * @param playerId Index of the player.
     * @param cardStats The PoliticsStats object containing card effects.
     */
    public void applyStats(int playerId, PoliticsStats cardStats) {
        if (cardStats != null) {
            int newEconOffset = cardStats.getStats(PoliticsStats.ECONOMY);
            if (newEconOffset != 0) applyCard(playerId, PoliticsStats.ECONOMY, newEconOffset);

            int newFacOffset = cardStats.getStats(PoliticsStats.FACILITY);
            if (newFacOffset != 0) applyCard(playerId, PoliticsStats.FACILITY, newFacOffset);

            int newEnvOffset = cardStats.getStats(PoliticsStats.ENVIRONMENT);
            if (newEnvOffset != 0) applyCard(playerId, PoliticsStats.ENVIRONMENT, newEnvOffset);

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
