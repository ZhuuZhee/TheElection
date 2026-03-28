package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.UI.UINotificationToast;
import Core.ZhuzheeGame;

public class LocalCampaign extends PolicyCard {
    public LocalCampaign(int x, int y, String imagePath) {
        super("Local Campaign", x, y, imagePath, -3);
        this.description = "Skill: If you play a Development card with Economy > 0. Gain 6 coins and +20 Economy.";
    }
    @Override
    public boolean isActive() {
        if (ZhuzheeGame.POLICY_CARD_HAND != null) {
            return ZhuzheeGame.POLICY_CARD_HAND.containsCard(this);
        }
        return false;
    }

    @Override
    public void onActionCardPlayed(ActionCard playedCard, City city) {}

    @Override
    public PoliticsStats calculateStats(ActionCard playedCard, City city) {
        if (!isActive()) {
            return null;
        }
        PoliticsStats stats = new PoliticsStats(playedCard.getStats());
        int ecoValue = stats.getStats(PoliticsStats.ECONOMY);
        if (ecoValue > 0) {
            UINotificationToast.showNotification("[Local Campaign] Activate! " + playedCard.getName() + " Gain 6 coins and +20 Economy!");
            if (ZhuzheeGame.CLIENT != null && ZhuzheeGame.CLIENT.getLocalPlayer() != null) {
                int currentCoin = ZhuzheeGame.CLIENT.getLocalPlayer().getCoin();
                ZhuzheeGame.CLIENT.getLocalPlayer().setCoin(currentCoin + 6);
            }
            stats.addStats(PoliticsStats.ECONOMY, ecoValue + 20);
        }
        return stats;
    }
}
