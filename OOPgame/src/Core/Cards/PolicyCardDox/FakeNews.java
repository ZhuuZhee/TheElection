package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.Player.Player;
import Core.UI.UINotificationToast;
import Core.ZhuzheeGame;

public class FakeNews extends PolicyCard {
    public FakeNews(int x, int y, String imagePath) {
        super("Fake News", x, y, imagePath, -5);
        this.description = "Skill: Play a card with Facility = 0. Gain +1 coins, Economy x3, and Environment -2.";
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
        if (!isActive()) return null;
        if (ZhuzheeGame.CLIENT == null) return null;
        Player localPlayer = ZhuzheeGame.CLIENT.getLocalPlayer();
        if (localPlayer == null) return null;

        PoliticsStats stats = new PoliticsStats(playedCard.getStats());
        if (stats == null) return null;

        int fac = stats.getStats(PoliticsStats.FACILITY);
        if (fac == 0) {
            int currentCoin = localPlayer.getCoin();
            localPlayer.setCoin(currentCoin + 1);

            int currentEco = stats.getStats(PoliticsStats.ECONOMY);
            stats.setStats(PoliticsStats.ECONOMY, currentEco * 3);

            int currentEnv = stats.getStats(PoliticsStats.ENVIRONMENT);
            stats.setStats(PoliticsStats.ENVIRONMENT, currentEnv - 2);

            UINotificationToast.showNotification("🗣️ [FAKE NEWS] Smear Campaign Active! (+1 Coin, x3 Economy)");
        }
        return stats;
    }

    @Override
    public void onActionCardPlayed(ActionCard playedCard, City city) {
//        if (!isActive()) return;
//        if (ZhuzheeGame.CLIENT == null) return;
//        Player localPlayer = ZhuzheeGame.CLIENT.getLocalPlayer();
//        if (localPlayer == null) return;
//
//        PoliticsStats stats = playedCard.getStats();
//        if (stats == null) return;
//
//        int fac = stats.getStats(PoliticsStats.FACILITY);
//        if (fac == 0) {
//            int currentCoin = localPlayer.getCoin();
//            localPlayer.setCoin(currentCoin + 1);
//
//            int currentEco = stats.getStats(PoliticsStats.ECONOMY);
//            stats.setStats(PoliticsStats.ECONOMY, currentEco * 3);
//
//            int currentEnv = stats.getStats(PoliticsStats.ENVIRONMENT);
//            stats.setStats(PoliticsStats.ENVIRONMENT, currentEnv - 2);
//
//            UINotificationToast.showNotification("🗣️ [FAKE NEWS] Smear Campaign Active! (+1 Coin, x3 Economy)");
//        }
        return;
    }
}