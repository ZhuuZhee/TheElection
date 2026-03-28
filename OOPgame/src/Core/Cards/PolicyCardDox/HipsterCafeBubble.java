package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.Player.Player;
import Core.UI.UINotificationToast;
import Core.ZhuzheeGame;

public class HipsterCafeBubble extends PolicyCard {
    public HipsterCafeBubble(int x, int y, String imagePath) {
        super("Hipster Cafe Bubble", x, y, imagePath, -5);
        this.description = "Skill: Play Dev Card with Economy > 0. Gain x2 Economy and -3 Coins.";
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
//        if (ZhuzheeGame.CLIENT == null) return;
//        Player localPlayer = ZhuzheeGame.CLIENT.getLocalPlayer();
//        if (localPlayer == null) return;
//
//        PoliticsStats stats = playedCard.getStats();
//        if (stats == null) return;
//        int currentEco = stats.getStats(PoliticsStats.ECONOMY);
//        if (currentEco > 0) {
//            stats.setStats(PoliticsStats.ECONOMY, currentEco * 2);
//            int currentCoin = localPlayer.getCoin();
//            localPlayer.setCoin(Math.max(0, currentCoin - 3));
//            UINotificationToast.showNotification("☕ [HIPSTER CAFE] ปั่นกระแสคาเฟ่ฮิปสเตอร์ให้ " + playedCard.getName() + "! (x2 Economy, -3 Coin)");
//        }
        return;
    }

    @Override
    public PoliticsStats calculateStats(ActionCard playedCard, City city) {
        if (!isActive()) return null;
        if (ZhuzheeGame.CLIENT == null) return null;
        Player localPlayer = ZhuzheeGame.CLIENT.getLocalPlayer();
        if (localPlayer == null) return null;

        PoliticsStats stats = new PoliticsStats(playedCard.getStats());
        if (stats == null) return null;
        int currentEco = stats.getStats(PoliticsStats.ECONOMY);
        if (currentEco > 0) {
            stats.setStats(PoliticsStats.ECONOMY, currentEco * 2);
            int currentCoin = localPlayer.getCoin();
            localPlayer.setCoin(Math.max(0, currentCoin - 3));
            UINotificationToast.showNotification("[Hipster Cafe Bubble] Activate! " + playedCard.getName() + " Gain x2 Economy and -3 Coins!");
        }
        return stats;
    }
}