package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.UI.UINotificationToast;
import Core.ZhuzheeGame;

/**
 * EconomicBoom: เมื่อลงการ์ดใดก็ตาม ถ้า Economy บนdevelopment card > 0 → คูณ Economy x2
 */
public class EconomicBoom extends PolicyCard {
    public EconomicBoom(int x, int y, String imagePath) {
        super("Economic Boom", x, y, imagePath, -10);
        this.description = "Skill: If played card has Economy > 0. Economy x4 and Environment x2 bonus!";
    }

    @Override
    public boolean isActive() {
        if (ZhuzheeGame.POLICY_CARD_HAND != null) {
            return ZhuzheeGame.POLICY_CARD_HAND.containsCard(this);
        }
        return false;
    }
    @Override
    public PoliticsStats calculateStats(ActionCard playedCard, City city) {
        PoliticsStats stats = new PoliticsStats(playedCard.getStats());
        if (!isActive()) {
            return null;
        }
        if (stats == null) {
            return null;
        }
        int eco = stats.getStats(PoliticsStats.ECONOMY);
        int env = stats.getStats(PoliticsStats.ENVIRONMENT);

        if (eco > 0) {
            UINotificationToast.showNotification("[Economic Boom] Activate! " + playedCard.getName()  + " Economy x4 and Environment x2 bonus!");
            //Economy x4
            stats.setStats(PoliticsStats.ECONOMY, eco * 4);

            //Environment x2 (คูณจากค่าเดิมที่มีอยู่)
            stats.setStats(PoliticsStats.ENVIRONMENT, env * 2);
        }
        return stats;
    }

    @Override
    public void onActionCardPlayed(ActionCard playedCard, City city) {
//        if (!isActive()) return;
//        PoliticsStats stats = playedCard.getStats();
//        if (stats == null) return;
//
//        int eco = stats.getStats(PoliticsStats.ECONOMY);
//        int env = stats.getStats(PoliticsStats.ENVIRONMENT);
//
//        if (eco > 0) {
//            UINotificationToast.showNotification("💰 [ECONOMIC BOOM] x4 Economy & x2 Environment!");
//            //Economy x4
//            stats.setStats(PoliticsStats.ECONOMY, eco * 4);
//
//            //Environment x2 (คูณจากค่าเดิมที่มีอยู่)
//            stats.setStats(PoliticsStats.ENVIRONMENT, env * 2);
//        }
        return;
    }

}
