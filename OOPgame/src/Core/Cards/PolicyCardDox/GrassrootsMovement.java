package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.UI.UINotificationToast;
import Core.ZhuzheeGame;

public class GrassrootsMovement extends PolicyCard {
    public GrassrootsMovement(int x, int y, String imagePath) {
        super("Grassroots Movement", x, y, imagePath, -3);
        this.description = "Skill: Play Dev Card with Environment <= 0 change Environment to +20.";
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
        if (!isActive()) return null;
        PoliticsStats stats = new PoliticsStats(playedCard.getStats());
        int currentEnv = stats.getStats(PoliticsStats.ENVIRONMENT);
        if (currentEnv <= 0) {
            stats.setStats(PoliticsStats.ENVIRONMENT, 20);
            UINotificationToast.showNotification("[Grassroots Movement] Activate! " + playedCard.getName() + " Gain Environment to +20");
        }
        return stats;
    }
}