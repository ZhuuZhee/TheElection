package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.CardSlot;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.ZhuzheeGame;

/**
 * EconomicBoom: เมื่อลงการ์ดใดก็ตาม ถ้า Economy บนdevelopment card > 0 → คูณ Economy x2
 */
public class EconomicBoom extends PolicyCard {
    public EconomicBoom(int x, int y, String imagePath) {
        super("Economic Boom", x, y, imagePath, -6);
        this.description = "Skill: If you have Development Card with Economy > 0. Gain x2 Economy.";
    }

    @Override
    public boolean isActive() {
        if (ZhuzheeGame.POLICY_CARD_HAND != null) {
            return ZhuzheeGame.POLICY_CARD_HAND.containsCard(this);
        }
        return false;
    }

    @Override
    public void onActionCardPlayed(ActionCard playedCard, City city) {
        if (!isActive()) return;
        PoliticsStats stats = playedCard.getStats();
        if (stats == null) return;

        int eco = stats.getStats(PoliticsStats.ECONOMY);
        if (eco > 0) {
            System.out.println("----------------------------------");
            System.out.println("💰 [ECONOMIC BOOM] Economy x2!");
            System.out.println("----------------------------------");
            stats.setStats(PoliticsStats.ECONOMY, eco * 2);
        }
    }

}
