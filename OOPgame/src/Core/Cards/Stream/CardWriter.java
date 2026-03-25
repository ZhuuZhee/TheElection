package Core.Cards.Stream;

import Core.Cards.ActionCard;
import Core.Maps.PoliticsStats;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CardWriter {

    /**
     * write card as json object in this format
     * {
     * "stats": {
     * "environment": 20,
     * "economy": 30,
     * "facility": 10
     * },
     * "name": "Test Card A",
     * "type": "ActionCard"
     * }
     *
     * @param cards
     * @param filePath
     */
    public static void writeActionCards(List<ActionCard> cards, String filePath) {
        JSONArray jsonArray = new JSONArray();

        for (ActionCard card : cards) {
            JSONObject obj = new JSONObject();

            PoliticsStats stats = card.getStats();
            JSONObject statsObj = new JSONObject();
            if (stats != null) {
                statsObj.put("economy", stats.getStats(PoliticsStats.Economy));
                statsObj.put("facility", stats.getStats(PoliticsStats.Facility));
                statsObj.put("environment", stats.getStats(PoliticsStats.Environment));
            }
            obj.put("stats", statsObj);
            obj.put("img", card.getImagePath());
            obj.put("type", "ActionCard");
            obj.put("name", card.getName());
            jsonArray.put(obj);
        }

        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonArray.toString(4)); // Indent with 4 spaces for readability
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
