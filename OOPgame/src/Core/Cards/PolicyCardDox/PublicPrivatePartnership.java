package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.UI.UINotificationToast;
import Core.ZhuzheeGame;

public class PublicPrivatePartnership extends PolicyCard {
    public PublicPrivatePartnership(int x, int y, String imagePath) {
        super("Public-Private Partnership", x, y, imagePath, -4);
        this.description = "Skill: If you play a Development card with +Economic and +Facility. Gain that 2 stats +10.";
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
        if (playedCard == null || ZhuzheeGame.CLIENT == null || ZhuzheeGame.CLIENT.getLocalPlayer() == null) return null;

        PoliticsStats stats = new PoliticsStats(playedCard.getStats());
        int eco = stats.getStats(PoliticsStats.ECONOMY);
        int fac = stats.getStats(PoliticsStats.FACILITY);
        if (eco > 0 && fac > 0) {
            stats.setStats(PoliticsStats.ECONOMY, eco + 10);
            stats.setStats(PoliticsStats.ECONOMY, fac + 10);
            UINotificationToast.showNotification("[Public-Private Partnership] Activate! " + playedCard.getName() + " Gain that 2 stats +10!");
        }
        return stats;
    }
}
