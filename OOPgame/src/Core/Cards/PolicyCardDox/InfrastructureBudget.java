package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;

public class InfrastructureBudget extends PolicyCard {

    public InfrastructureBudget(int x, int y, String imagePath) {
        super("Infrastructure Budget", x, y, imagePath, -5);
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void onActionCardPlayed(ActionCard playedCard, City city) {
        // Guard Clause: ถ้าการ์ดไม่ได้เปิดใช้งานอยู่ ให้ข้ามไปเลย
        if (!isActive()) return;
        PoliticsStats stats = playedCard.getStats();
        if (stats == null) return;

        int currentFacility = stats.getStats(PoliticsStats.FACILITY);
        if (currentFacility >= 3) {
            stats.setStats(PoliticsStats.FACILITY, currentFacility + 5);
            System.out.println("----------------------------------");
            System.out.println("🏗️ [INFRASTRUCTURE BUDGET] ทำงาน!");
            System.out.println("เนื่องจากคุณวางการ์ดที่มี Facility 3 หรือมากกว่า");
            System.out.println(">>> การ์ด " + playedCard.getName() + " ได้รับ +5 Facility ทันที! <<<");
            System.out.println("----------------------------------");
        }
    }
}