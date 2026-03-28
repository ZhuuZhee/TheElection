package Core.Maps;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import Core.Player.Player;
import Core.ZhuzheeGame;
import org.json.*;

/**
 * Represents a City in the game world.
 * Cities contain population, political statistics, and manage the voting logic
 * based on player contributions (cards played).
 */
public class City {
    private final String cityName;
    /**
     * The political and economic stats of the city.
     */
    public PoliticsStats stats;
    /**
     * The total number of citizens in the city.
     */
    public int population;
    /**
     * List of map grids that belong to this city.
     */
    public ArrayList<Grid> grids = new ArrayList<Grid>();
    private Color color;

    // --- Config ค่าคงที่ต่างๆ (ปรับ Balance ที่นี่) ---
    /**
     * Multiplier for the logarithmic scoring formula.
     */
    public static final float K_LOG_MULTIPLIER = 200.0f;
    /**
     * Ratio of population required to earn one council seat.
     */
    public static final int POP_PER_SEAT = 10000;
    /**
     * Base score added per council seat.
     */
    public static final float SCORE_PER_SEAT_BASE = 50.0f;

    /** Total number of players in the game. */
    /**
     * Number of seats available in this city's council.
     */
    public int councilSeats;
    /**
     * The initial base score for the city.
     */
    public float baseScore;
    /**
     * Current weighted scores for each player in this city.
     */
    public HashMap<String, Float> playerScores = new HashMap<>();
    public ArrayList<String> playerIds = new ArrayList<>();

    private String ownerId = "";

    /**
     * Constructs a new City with specified parameters and initializes scoring.
     */
    public City(String cityName, int facility, int environment, int economy, int population, ArrayList<Player> playerList) {
        // เริ่มต้นสถานะของเมือง
        this.stats = new PoliticsStats(facility, environment, economy);
        this.population = population;
        this.cityName = cityName;

        // 1. คำนวณจำนวนที่นั่งสภา (Council Seats)
        this.councilSeats = Math.max(1, this.population / POP_PER_SEAT);
        this.baseScore = this.councilSeats * SCORE_PER_SEAT_BASE;

        // 3. สร้างคะแนนดิบเริ่มต้นให้ผู้เล่นทุกคนเท่ากัน
        for (Player p : playerList) {
            playerIds.add(p.getPlayerId());
            playerScores.put(p.getPlayerId(), baseScore);
        }
    }

    public String getCityName() {
        return this.cityName;
    }

    /**
     * Calculates a score based on logarithmic diminishing returns.
     *
     * @param currentStatVal The current value of the city's stat.
     * @param cardVal        The value being added by a card.
     * @return The calculated score increase.
     */
    public float calculateLogScore(double currentStatVal, double cardVal) {
        // ป้องกันค่าติดลบ
        float currentVal = (float) Math.max(0, currentStatVal);
        float newVal = (float) Math.max(0, currentStatVal + cardVal);

        // สูตร: K * [ln(new + 1) - ln(old + 1)]
        return (float) (K_LOG_MULTIPLIER * (Math.log(newVal + 1) - Math.log(currentVal + 1)));
    }

    /**
     * Applies a single stat change from a player's card to the city.
     *
     * @param playerId Index of the player playing the card.
     * @param statType The type of stat being modified.
     * @param cardVal  The amount of change.
     */
    public void aplyStat(String playerId, long statType, float cardVal) {
        float currentStat = stats.getStats(statType);

        // คำนวณคะแนนที่ได้รับ
        float scoreGained = calculateLogScore(currentStat, cardVal);

        // อัปเดตคะแนนผู้เล่น (Score Weight) - แก้ไขให้เป็นการบวกเพิ่ม
        float currentScore = playerScores.getOrDefault(playerId, 0f);
        playerScores.put(playerId, currentScore + scoreGained);

        // อัปเดต Stat เมือง
        stats.addStats(statType, (int) cardVal);

        updateOwner();

        String statName = statType == PoliticsStats.FACILITY ? "Facility" :
                statType == PoliticsStats.ENVIRONMENT ? "Environment" : "Economy";
        Player localPlayer = ZhuzheeGame.CLIENT.getLocalPlayer();
        String p = playerId.equals(localPlayer.getPlayerId())? localPlayer.getPlayerName() : playerId;
        System.out.printf("[%s] Player %s ลงการ์ด %s (+%.1f)%n", cityName, p, statName, cardVal);
        System.out.printf("   -> Stat เมืองเปลี่ยนจาก %.1f เป็น %d%n", currentStat, stats.getStats(statType));
        System.out.printf("   -> ได้คะแนนดิบเพิ่ม +%.2f คะแนน%n", scoreGained);
    }

    /**
     * Applies all statistics from a card to the city for a specific player.
     *
     * @param playerId  Index of the player.
     * @param cardStats The PoliticsStats object containing card effects.
     */
    public void applyCard(String playerId, PoliticsStats cardStats) {
        if (cardStats != null && cardStats.stats != null) {
            //add stats to cities
            for (java.util.Map.Entry<Long, Integer> entry : cardStats.stats.entrySet()) {
                if (entry.getValue() != 0) {
                    aplyStat(playerId, entry.getKey(), entry.getValue());
                }
            }
        }
        getVotingResults();
    }

    /**
     * Checks if the specified player has the highest score in the city.
     * ตรวจสอบว่าผู้เล่นคนนี้มีคะแนนสูงสุดในเมืองหรือไม่ (รวมถึงกรณีที่คะแนนสูงสุดเท่ากับผู้อื่น)
     *
     * @param playerId ดัชนีของผู้เล่นที่ต้องการตรวจสอบ
     * @return true หากผู้เล่นมีคะแนนมากที่สุดหรือเท่ากับคะแนนสูงสุดในขณะนั้น
     */
    public boolean isPlayerDominateCity(String playerId) {
        double targetScore = playerScores.get(playerId);
        for (double score : playerScores.values()) {
            if (score > targetScore) return false;
        }
        return true;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void updateOwner() {
        // 1. นำข้อมูลจาก HashMap (ID ผู้เล่น และ คะแนน) มาใส่ใน ArrayList เพื่อจัดลำดับ
        ArrayList<java.util.Map.Entry<String, Float>> sortedScores = new ArrayList<>(playerScores.entrySet());

        // 2. จัดลำดับจาก "มากไปน้อย" (Descending Order) ตามค่าคะแนน (Value)
        sortedScores.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        String bestPlayerId = "";
        if (!sortedScores.isEmpty()) {
            float maxScore = sortedScores.get(0).getValue();
            // 3. ตรวจสอบว่าคะแนนสูงสุดมีการ "เสมอ" กันหรือไม่
            boolean tie = sortedScores.size() > 1 && sortedScores.get(1).getValue() == maxScore;

            // ถ้าไม่เสมอและคะแนนมากกว่า 0 ให้หา Index ของผู้เล่นจากรายชื่อ playerIds
            if (!tie && maxScore > 0) {
                bestPlayerId = sortedScores.getFirst().getKey();
            }
        }

        // อัปเดต ID เจ้าของเมือง (ถ้าเสมอจะเป็น -1)
        ownerId = bestPlayerId;

        if (Core.ZhuzheeGame.PLAYER_LIST_UI != null) {
            Core.ZhuzheeGame.PLAYER_LIST_UI.updatePlayerList();
        }
    }

    /**
     * Calculates the current percentage of influence a player has in the city.
     *
     * @param playerId Index of the player.
     * @return Percentage (0.0 to 100.0).
     */
    public float getPlayerPercentage(String playerId) {
        if (playerId.isEmpty() || playerScores.isEmpty()) return 0;
        double totalScore = 0;
        for (double score : playerScores.values()) {
            totalScore += score;
        }
        if (totalScore == 0) return 0;
        return (float) (playerScores.get(playerId) / totalScore) * 100f;
    }

    /**
     * Prints the current voting simulation results to the console.
     */
    public void getVotingResults() {
        double totalScore = 0;
        for (float score : playerScores.values()) {
            totalScore += score;
        }

        System.out.printf("%n--- ผลการเลือกตั้งเมือง: %s (ประชากร: %,d, ที่นั่ง: %d) ---%n", cityName, population, councilSeats);
        Player localPlayer = ZhuzheeGame.CLIENT.getLocalPlayer();

        if (totalScore == 0) return;
        for (String playerId : playerScores.keySet()) {

            String p = playerId.equals(localPlayer.getPlayerId())? localPlayer.getPlayerName() : playerId;

            double percent = (playerScores.get(playerId) / totalScore) * 100;
            int votes = (int) ((playerScores.get(playerId) / totalScore) * this.population);
            System.out.printf("Player %s: %.2f%% (%,d เสียง)%n", p, percent, votes);
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

        if (playerScores != null) {
            JSONArray playerScoresArr = new JSONArray();
            for (String id : playerIds) {
                JSONObject pScore = new JSONObject();
                pScore.put("score",playerScores.get(id));
                pScore.put("id",id);
                playerScoresArr.put(pScore);
            }
            cityJson.put("players score", playerScoresArr);
        }
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
            for (int i = 0; i < scores.length(); i++) {
                JSONObject pScoreData = scores.getJSONObject(i);
                String pId = pScoreData.getString("id");
                float pScore = pScoreData.getFloat("score");
                playerScores.put(pId,pScore);
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
