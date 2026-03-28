package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.UI.UINotificationToast;
import Core.ZhuzheeGame;

public class InfrastructureBudget extends PolicyCard {

    public InfrastructureBudget(int x, int y, String imagePath) {
        super("Infrastructure Budget", x, y, imagePath, -5);
        this.description = "Skill: While this card is active. Every time you play a Development card, gain +10 Facility.";
    }

    @Override
    public boolean isActive() { // อยู่บนมือทำงานตลอด
        if (ZhuzheeGame.POLICY_CARD_HAND != null) {
            return ZhuzheeGame.POLICY_CARD_HAND.containsCard(this);
        }
        return false;
    }

    @Override
    public void onActionCardPlayed(ActionCard playedCard, City city) {
        // Guard Clause: ถ้าการ์ดไม่ได้เปิดใช้งานอยู่ ให้ข้ามไปเลย
        if (!isActive()) return;

        PoliticsStats stats = playedCard.getStats();
        if (stats == null) return;

        // ดึงค่า Facility ปัจจุบันของการ์ดที่เล่น
        int currentFacility = stats.getStats(PoliticsStats.FACILITY);

        // แก้ไขความสามารถ: บวก 10 Facility ให้กับการ์ดทุกใบที่วาง (ตามเงื่อนไขใหม่)
        stats.setStats(PoliticsStats.FACILITY, currentFacility + 10);

        UINotificationToast.showNotification("🏗️ [INFRASTRUCTURE BUDGET] " + playedCard.getName() + " (+10 Facility)!");
    }
}