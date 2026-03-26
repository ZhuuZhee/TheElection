package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.CardSlot;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;

/**
 * IronWill:  ถ้า Facility บนการ์ดเป็นลบ → ป้องกันเปลี่ยนให้เป็น 0
 * ถ้า Facility เป็นบวกอยู่แล้ว → บวกเพิ่มอีก +5
 */
public class IronWill extends PolicyCard {
    public IronWill(int x, int y, String imagePath) {
        super("Iron Will", x, y, imagePath, -6);
    }

    @Override
    public boolean isActive() { return true; }

    @Override
    public void onActionCardPlayed(ActionCard playedCard, City city) {
        if (!isActive()) return;
        PoliticsStats stats = playedCard.getStats();
        if (stats == null) return;

        int fac = stats.getStats(PoliticsStats.FACILITY);
        if (fac < 0) {
            System.out.println("----------------------------------");
            System.out.println("🛡️ [IRON WILL] Facility damage blocked!");
            System.out.println("----------------------------------");
            stats.setStats(PoliticsStats.FACILITY, 0);
        } else if (fac > 0) {
            System.out.println("----------------------------------");
            System.out.println("🛡️ [IRON WILL] Facility +5 bonus!");
            System.out.println("----------------------------------");
            stats.addStats(PoliticsStats.FACILITY, 5);
        }
    }

}
