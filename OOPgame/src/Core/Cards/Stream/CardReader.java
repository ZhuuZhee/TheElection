package Core.Cards.Stream;

import Core.Cards.ActionCard;
import Core.Cards.Card;
import Dummy.Maps.PoliticsStats;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CardReader {
    public static List<CardBufferObject> readActionCards(String filePath) {
        List<CardBufferObject> cards = new ArrayList<>();
        try {
            // Read file content
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONArray jsonArray = new JSONArray(content);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                String name = obj.getString("name");
                int coin = obj.optInt("coin");

                JSONObject statsObj = obj.getJSONObject("stats");
                int economy = statsObj.optInt("economy", 0);
                int facility = statsObj.optInt("facility", 0);
                int environment = statsObj.optInt("environment", 0);

                // Construct PoliticsStats (Order based on Citybanna usage: Facility, Environment, Economy)
                PoliticsStats stats = new PoliticsStats(facility, environment, economy);

                String imagePath = obj.optString("img", "");

                cards.add(new CardBufferObject(name, coin,stats,imagePath));
            }
        } catch (IOException e) {
            System.err.println("Error reading cards: " + e.getMessage());
        }
        return cards;
    }
}
