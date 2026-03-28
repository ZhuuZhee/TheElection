package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.UI.UINotificationToast;
import Core.ZhuzheeGame;

/**
 * A specific Policy Card that protects the Environment stat.
 * If an Action Card would decrease the Environment stat, this policy negates the penalty.
 */
public class GreenPolicy extends PolicyCard {
    public GreenPolicy(int x, int y, String imagePath) {
        super("Green Policy", x, y, imagePath, -5);
        this.description = "Skill: If you play a Development card with Environment < 0. Change Environment to 0";
    }

    @Override
    public boolean isActive() {
        if (ZhuzheeGame.POLICY_CARD_HAND != null) {
            return ZhuzheeGame.POLICY_CARD_HAND.containsCard(this);
        }
        return false;
    }

    @Override
    /**
     * Intercepts Action Card stats. If Environment is negative, it sets it to 0
     * to prevent ecological damage.
     */
    public void onActionCardPlayed(ActionCard playedCard, City city) {
        // เช็คว่าการ์ดที่เล่นเป็น DevelopCard หรือไม่
//        if (playedCard instanceof ActionCard) {
//            PoliticsStats stats = playedCard.getStats();
//            if (stats != null) {
//                int env = stats.getStats(PoliticsStats.ENVIRONMENT);
//                if (env < 0) {
//                    UINotificationToast.showNotification("🌿 [Green Policy] ป้องกันค่า Environment ติดลบให้ " + playedCard.getName());
//                    stats.setStats(PoliticsStats.ENVIRONMENT, 0);
//                }
//            }
//        }
        return;
    }
    @Override
    public PoliticsStats calculateStats(ActionCard playedCard, City city) {
        if (playedCard instanceof ActionCard) {
            PoliticsStats stats = new PoliticsStats(playedCard.getStats());
            if (stats != null) {
                int env = stats.getStats(PoliticsStats.ENVIRONMENT);
                if (env < 0) {
                    UINotificationToast.showNotification("[Green Policy] Activate! " + playedCard.getName() + "Change Environment to 0!");
                    stats.setStats(PoliticsStats.ENVIRONMENT, 0);
                }
            }
        }
        return stats;
    }
}
