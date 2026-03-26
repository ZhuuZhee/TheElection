package Core.Maps;

import java.awt.*;
import java.util.ArrayList;

public class City {
    private final String cityName;
    public PoliticsStats stats;
    public int population;
    public ArrayList<Grid> grids = new ArrayList<Grid>();
    private Color color;

    // --- Config ค่าคงที่ต่างๆ (ปรับ Balance ที่นี่) ---
    public static final double K_LOG_MULTIPLIER = 100.0;
    public static final int POP_PER_SEAT = 10000;
    public static final double SCORE_PER_SEAT_BASE = 50.0;

    public int numPlayers = 4;
    public int councilSeats;
    public double baseScore;
    public double[] playerScores;

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

    public double calculateLogScore(double currentStatVal, double cardVal) {
        // ป้องกันค่าติดลบ
        double currentVal = Math.max(0, currentStatVal);
        double newVal = Math.max(0, currentStatVal + cardVal);

        // สูตร: K * [ln(new + 1) - ln(old + 1)]
        return K_LOG_MULTIPLIER * (Math.log(newVal + 1) - Math.log(currentVal + 1));
    }

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

    /** Xynezter 11/3/2026 17:42 : fix method stat**/
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

    public void applyStats(PoliticsStats cardStats) {
        // หากไม่ระบุ Player ให้ถือว่าเป็น Player 0
        applyStats(0, cardStats);
    }

    public double getPlayerPercentage(int playerId) {
        if (playerId < 0 || playerId >= playerScores.length) return 0;
        double totalScore = 0;
        for (double score : playerScores) {
            totalScore += score;
        }
        if (totalScore == 0) return 0;
        return (playerScores[playerId] / totalScore) * 100;
    }

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
