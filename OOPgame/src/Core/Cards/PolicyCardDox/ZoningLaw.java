package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.ZhuzheeGame;

public class ZoningLaw extends PolicyCard {

    public ZoningLaw(int x, int y, String imagePath) {
        super("Zoning Law", x, y, imagePath, -5);
        this.description = "Skill: If Development cards in hand <= 2, all stats of played card gain +2.";
    }

    //เงื่อนไข: เช็คว่าการ์ดอยู่ใน Slot และในมือมี Development Card น้อยกว่า 2 ใบ
    @Override
    public boolean isActive() {
        boolean inSlot = ZhuzheeGame.POLICY_CARD_HAND != null && ZhuzheeGame.POLICY_CARD_HAND.containsCard(this);

        //เช็คจำนวนการ์ดในมือ
        boolean lowHandCount = ZhuzheeGame.DEVLOPMENT_CARD_HAND != null
                && ZhuzheeGame.DEVLOPMENT_CARD_HAND.getCards().size() < 2;

        return inSlot && lowHandCount;
}
    @Override
    public void update() {
        super.update();
    }

    //ถ้าเงื่อนไขครบ ค่า Stats ทุกอย่างจะเพิ่ม +2 เมื่อเล่นการ์ด
    @Override
    public void onActionCardPlayed(ActionCard playedCard, City city) {
        //ถ้าเงื่อนไข isActive ไม่เป็นจริง (การ์ดไม่อยู่ใน slot หรือการ์ดในมือ > 2) ให้หยุดทำงาน
        if (!isActive()) return;

        PoliticsStats stats = playedCard.getStats();
        if (stats == null) return;

        //เพิ่มค่า Stats ทุกอย่าง +2
        stats.setStats(PoliticsStats.FACILITY, stats.getStats(PoliticsStats.FACILITY) + 2);
        stats.setStats(PoliticsStats.ENVIRONMENT, stats.getStats(PoliticsStats.ENVIRONMENT) + 2);
        stats.setStats(PoliticsStats.ECONOMY, stats.getStats(PoliticsStats.ECONOMY) + 2);
        System.out.println("ZoningLaw +2 all stats");
    }
}