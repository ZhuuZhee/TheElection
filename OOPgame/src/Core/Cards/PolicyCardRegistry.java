package Core.Cards;

import Core.Cards.PolicyCardDox.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PolicyCardRegistry: ลงทะเบียนการ์ด Policy ทั้งหมดที่มีในเกม
 * เพิ่มการ์ดใหม่ได้ที่เมธอด getAllCards() เลยครับ
 */
public class PolicyCardRegistry {

    private static final String IMG = "OOPgame/Assets/ImageForCards/gay.png"; // placeholder image

    /**
     * คืนค่า list ของการ์ด Policy ทั้งหมดที่มีในเกม (clone ใหม่ทุกครั้ง)
     */
    public static List<PolicyCard> getAllCards() {
        List<PolicyCard> pool = new ArrayList<>();

        // -------- การ์ดเดิม --------
        pool.add(new GreenPolicy(0, 0, IMG));
        pool.add(new LocalCampaign(0, 0, IMG));
        pool.add(new Recount(0, 0, IMG));

        // -------- การ์ดใหม่ --------
        pool.add(new EconomicBoom(0, 0, IMG));   // Economy x2 เมื่อ Economy > 0
        pool.add(new IronWill(0, 0, IMG));       // Facility shield / +5
        pool.add(new NaturePact(0, 0, IMG));     // Env synergy → All +Env

        return pool;
    }

    /**
     * สุ่มการ์ดออกมา maxCount ใบจาก Pool ทั้งหมด
     */
    public static List<PolicyCard> rollCards(int maxCount) {
        List<PolicyCard> pool = getAllCards();
        Collections.shuffle(pool);
        return new ArrayList<>(pool.subList(0, Math.min(maxCount, pool.size())));
    }
}
