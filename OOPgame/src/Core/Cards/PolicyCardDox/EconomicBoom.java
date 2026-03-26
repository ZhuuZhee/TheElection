package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.CardSlot;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;

/**
 * EconomicBoom: เมื่อลงการ์ดใดก็ตาม ถ้า Economy บนการ์ดนั้น > 0 → คูณ Economy x2
 */
public class EconomicBoom extends PolicyCard {
    public EconomicBoom(int x, int y, String imagePath) {
        super("Economic Boom", x, y, imagePath, -8);
    }

    @Override
    public boolean IsActivate() {
        return true;
    }

    @Override
    public void onActionCardPlayed(ActionCard playedCard, City city) {
        if (!IsActivate()) return;
        PoliticsStats stats = playedCard.getStats();
        if (stats == null) return;

        int eco = stats.getStats(PoliticsStats.Economy);
        if (eco > 0) {
            System.out.println("----------------------------------");
            System.out.println("💰 [ECONOMIC BOOM] Economy x2!");
            System.out.println("----------------------------------");
            stats.setStats(PoliticsStats.Economy, eco * 2);
        }
    }

    @Override
    protected boolean isDroppable(Object bottom) {
        return bottom instanceof CardSlot;
    }
}
