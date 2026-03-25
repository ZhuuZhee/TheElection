package Dummy.Maps;

import java.awt.*;
import java.util.ArrayList;

public class City {
    private final String cityName;
    public PoliticsStats stats;
    public int population;
    public ArrayList<Grid> Grids = new ArrayList<Grid>();
    private Color color;

    // --- Config ค่าคงที่ต่างๆ (ปรับ Balance ที่นี่) ---
    public final double K_LOG_MULTIPLIER = 100.0;
    public final int POP_PER_SEAT = 10000;
    public final double SCORE_PER_SEAT_BASE = 50.0;
    
    public int num_players = 4;
    public int council_seats;
    public double base_score;
    public double[] player_scores;

    public City(String cityName, int facility, int environment, int economy, int population) {
        // เริ่มต้นสถานะของเมือง
        this.stats = new PoliticsStats(facility, environment, economy);
        this.population = population;
        this.cityName = cityName;

        // 1. คำนวณจำนวนที่นั่งสภา (Council Seats)
        this.council_seats = Math.max(1, this.population / this.POP_PER_SEAT);

        // 2. คำนวณ Dynamic Base Score (คะแนนตั้งต้น)
        this.base_score = this.council_seats * this.SCORE_PER_SEAT_BASE;
        
        // 3. สร้างคะแนนดิบเริ่มต้นให้ผู้เล่นทุกคนเท่ากัน
        this.player_scores = new double[num_players];
        for (int i = 0; i < num_players; i++) {
            this.player_scores[i] = this.base_score;
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
        player_scores[playerId] += scoreGained;
        
        // อัปเดต Stat เมือง
        stats.addStats(statType, (int)cardVal);
        
        String statName = statType == PoliticsStats.Facility ? "Facility" : 
                          statType == PoliticsStats.Environment ? "Environment" : "Economy";

        System.out.printf("[%s] Player %d ลงการ์ด %s (+%.1f)%n", cityName, playerId, statName, cardVal);
        System.out.printf("   -> Stat เมืองเปลี่ยนจาก %.1f เป็น %d%n", currentStat, stats.getStats(statType));
        System.out.printf("   -> ได้คะแนนดิบเพิ่ม +%.2f คะแนน%n", scoreGained);
    }

    /** Xynezter 11/3/2026 17:42 : fix method stat**/
    public void applyStats(int playerId, PoliticsStats cardStats) {
        if (cardStats != null) {
            int newEconOffset = cardStats.getStats(PoliticsStats.Economy);
            if (newEconOffset != 0) applyCard(playerId, PoliticsStats.Economy, newEconOffset);

            int newFacOffset = cardStats.getStats(PoliticsStats.Facility);
            if (newFacOffset != 0) applyCard(playerId, PoliticsStats.Facility, newFacOffset);

            int newEnvOffset = cardStats.getStats(PoliticsStats.Environment);
            if (newEnvOffset != 0) applyCard(playerId, PoliticsStats.Environment, newEnvOffset);
            
            getVotingResults();
        }
    }

    public void applyStats(PoliticsStats cardStats) {
        // หากไม่ระบุ Player ให้ถือว่าเป็น Player 0
        applyStats(0, cardStats);
    }

    public void getVotingResults() {
        double totalScore = 0;
        for (double score : player_scores) totalScore += score;
        
        System.out.printf("%n--- ผลการเลือกตั้งเมือง: %s (ประชากร: %,d, ที่นั่ง: %d) ---%n", cityName, population, council_seats);
        
        for (int i = 0; i < player_scores.length; i++) {
            double percent = (player_scores[i] / totalScore) * 100;
            int votes = (int)((player_scores[i] / totalScore) * this.population);
            System.out.printf("Player %d: %.2f%% (%,d เสียง)%n", i, percent, votes);
        }
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    // wait for business logic
    public void updatePopulation(PoliticsStats cardStats) {
        return;
    }

    public void printStats() {
        System.out.println("----------------------------------");
        System.out.println("City: " + this.getCityName());
        System.out.println("Economic: " + stats.getStats(PoliticsStats.Economy));
        System.out.println("Facility: " + stats.getStats(PoliticsStats.Facility));
        System.out.println("Environment: " + stats.getStats(PoliticsStats.Environment));
        System.out.println("Population: " + population);
        System.out.println("----------------------------------");
    }
}
