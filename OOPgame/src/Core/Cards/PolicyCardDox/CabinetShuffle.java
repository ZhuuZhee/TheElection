package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.UI.UINotificationToast;
import Core.ZhuzheeGame;

public class CabinetShuffle extends PolicyCard {
    public CabinetShuffle(int x, int y, String imagePath) {
        super("Cabinet Shuffle", x, y, imagePath, -5);
        this.description = "Skill: If you play a Development card, positive stats become negative and negative stats become positive.";
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
        if (!isActive()) {
            return null;
        }
        PoliticsStats originalStats = playedCard.getStats();
        if (originalStats == null) {
            return null;
        }
        PoliticsStats statsDelta = new PoliticsStats(0, 0, 0);
        int facValue = originalStats.getStats(PoliticsStats.FACILITY);
        int envValue = originalStats.getStats(PoliticsStats.ENVIRONMENT);
        int ecoValue = originalStats.getStats(PoliticsStats.ECONOMY);

        statsDelta.setStats(PoliticsStats.FACILITY, -facValue);
        statsDelta.setStats(PoliticsStats.ENVIRONMENT, -envValue);
        statsDelta.setStats(PoliticsStats.ECONOMY, -ecoValue);

        UINotificationToast.showNotification("[Cabinet Shuffle] Activate! " + playedCard.getName() + " positive stats become negative and negative stats become positive!");
        return statsDelta;
    }

    @Override
    public void onActionCardPlayed(ActionCard playedCard, City city) {}
}
