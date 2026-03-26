package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.CardSlot;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;

/**
 * NaturePact: ถ้าการ์ดที่ลงมี Environment > 0 → ทำให้ทุก Stat เพิ่มขึ้น +Environment ด้วย (Synergy)
 */
public class NaturePact extends PolicyCard {
    public NaturePact(int x, int y, String imagePath) {
        super("Nature Pact", x, y, imagePath, -7);
    }

    @Override
    public boolean IsActivate() { return true; }

    @Override
    public void onActionCardPlayed(ActionCard playedCard, City city) {
        if (!IsActivate()) return;
        PoliticsStats stats = playedCard.getStats();
        if (stats == null) return;

        int env = stats.getStats(PoliticsStats.Environment);
        if (env > 0) {
            System.out.println("----------------------------------");
            System.out.println("🌿 [NATURE PACT] All stats +" + env + " synergy bonus!");
            System.out.println("----------------------------------");
            stats.addStats(PoliticsStats.Facility, env);
            stats.addStats(PoliticsStats.Economy, env);
            // Environment stays the same – it's the catalyst
        }
    }

    @Override
    protected boolean isDroppable(Object bottom) {
        return bottom instanceof CardSlot;
    }
}
