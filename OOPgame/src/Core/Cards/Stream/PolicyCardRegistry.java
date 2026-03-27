package Core.Cards.Stream;

import Core.Cards.PolicyCard;
import Core.Cards.PolicyCardDox.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PolicyCardRegistry: อ่านการ์ด Policy จาก policy_cards.json
 * และ instantiate เฉพาะการ์ดที่สุ่มได้เท่านั้น (lazy factory)
 *
 * เพิ่มการ์ดใหม่: สร้าง class ใน PolicyCardDox แล้วเพิ่ม entry ใน policy_cards.json
 */
public class PolicyCardRegistry {

    private static final String JSON_PATH = "OOPgame/Assets/policy_cards.json";

    /**
     * สุ่มการ์ดออกมา maxCount ใบจาก JSON pool
     * จะ instantiate เฉพาะใบที่สุ่มได้เท่านั้น
     */
    public static List<PolicyCard> rollCards(int maxCount) {
        List<PolicyCard> result = new ArrayList<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(JSON_PATH)));
            JSONArray jsonArray = new JSONArray(content);

            // สร้าง list ของ index แล้ว shuffle
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) indices.add(i);
            Collections.shuffle(indices);

            // instantiate เฉพาะ maxCount ใบแรกที่ถูกสุ่ม
            int count = Math.min(maxCount, indices.size());
            for (int i = 0; i < count; i++) {
                JSONObject obj = jsonArray.getJSONObject(indices.get(i));
                String className = obj.getString("class");
                String img = obj.optString("img", "OOPgame/Assets/ImageForCards/gay.png");

                PolicyCard card = createCard(className, img);
                if (card != null) result.add(card);
            }
        } catch (IOException e) {
            System.err.println("[PolicyCardRegistry] ไม่พบไฟล์: " + JSON_PATH);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Factory: map className → class จริงใน PolicyCardDox
     * เพิ่ม class ใหม่ที่นี่เมื่อมีการ์ดใหม่
     */
    private static PolicyCard createCard(String className, String img) {
        return switch (className) {
            case "GreenPolicy"   -> new GreenPolicy(0, 0, img);
            case "CoinBoom"      -> new CoinBoom(0,0, img);
            case "LocalCampaign" -> new LocalCampaign(0, 0, img);
            case "Public_PrivatePartnership" -> new Public_PrivatePartnership(0, 0, img);
            case "Recount"       -> new Recount(0, 0, img);
            case "EconomicBoom"  -> new EconomicBoom(0, 0, img);
            case "IronWill"      -> new IronWill(0, 0, img);
            case "NaturePact"    -> new NaturePact(0, 0, img);
            case "InfrastructureBudget"   -> new InfrastructureBudget(0,0,img);
            case "DamageControl"   -> new DamageControl(0,0,img);
            case "PublicBriefing"   -> new PublicBriefing(0,0,img);
            case "Fundraiser"   -> new Fundraiser(0,0,img);
            default -> {
                System.err.println("[PolicyCardRegistry] ไม่รู้จัก class: " + className);
                yield null;
            }
        };
    }
}
