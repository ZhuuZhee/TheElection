package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.UI.UINotificationToast;
import Core.ZhuzheeGame;

/**
 * NaturePact: ถ้าการ์ดที่ลงมี Environment > 0 → ทำให้ทุก Stat เพิ่มขึ้น +Environment ด้วย (Synergy)
 */
public class NaturePact extends PolicyCard {
    public NaturePact(int x, int y, String imagePath) {
        super("Nature Pact", x, y, imagePath, -7);
        this.description = "Skill: If you play a Development card with Environment > 0. Gain +Environment in FACILITY and ECONOMY";
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
//        if (!isActive()) return;
//        PoliticsStats stats = playedCard.getStats();
//        if (stats == null) return;
//
//        int env = stats.getStats(PoliticsStats.ENVIRONMENT);
//        if (env > 0) {
//            UINotificationToast.showNotification("🌿 [NATURE PACT] ทุกสแตทเพิ่มขึ้น +" + env + " จากโบนัส Synergy!");
//            stats.addStats(PoliticsStats.FACILITY, env);
//            stats.addStats(PoliticsStats.ECONOMY, env);
//            // Environment stays the same – it's the catalyst
//        }
        return;
    }
    @Override
    public PoliticsStats calculateStats(ActionCard playedCard, City city) {
        if (!isActive()) return null;
        PoliticsStats stats = new PoliticsStats(playedCard.getStats());
        if (stats == null) return null;

        int env = stats.getStats(PoliticsStats.ENVIRONMENT);
        if (env > 0) {
            UINotificationToast.showNotification("[Nature Pact] Activate! " + playedCard.getName() + " Gain +Environment in FACILITY and ECONOMY!");
            stats.addStats(PoliticsStats.FACILITY, env);
            stats.addStats(PoliticsStats.ECONOMY, env);
            // Environment stays the same – it's the catalyst
        }
        return stats;
    }

}
