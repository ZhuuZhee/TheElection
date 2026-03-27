package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.ZhuzheeGame;

public class Public_PrivatePartnership extends PolicyCard {
    public Public_PrivatePartnership(int x, int y, String imagePath) {
        super("Public-Private Partnership", x, y, imagePath, -4);
        this.description = "Skill: If you play a Development card with +Economic and +Facility. Gain 10 coins.";
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
        if (playedCard == null || ZhuzheeGame.CLIENT == null || ZhuzheeGame.CLIENT.getLocalPlayer() == null) return;

        PoliticsStats stats = playedCard.getStats();
        if (stats == null) return;

        int eco = stats.getStats(PoliticsStats.ECONOMY);
        int fac = stats.getStats(PoliticsStats.FACILITY);

        if (eco > 0 && fac > 0) {
            int currentCoin = ZhuzheeGame.CLIENT.getLocalPlayer().getCoin();
            ZhuzheeGame.CLIENT.getLocalPlayer().setCoin(currentCoin + 10);
            System.out.println("Public-Private Partnership activated: +10 coin");
        }
    }
}
