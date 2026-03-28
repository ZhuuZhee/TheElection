package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.ZhuzheeGame;

public class CoinBoom extends PolicyCard {
    private boolean rewardGrantedAtCurrentThreshold = false;

    public CoinBoom(int x, int y, String imagePath) {
        super("Coin Boom", x, y, imagePath, -5);
        this.description = "Skill: If you have 4 Development cards in hand. Gain 15 coins immediately.";
    }

    @Override
    public boolean isActive() {
        return ZhuzheeGame.POLICY_CARD_HAND != null
                && ZhuzheeGame.POLICY_CARD_HAND.containsCard(this)
                && ZhuzheeGame.DEVLOPMENT_CARD_HAND != null
                && ZhuzheeGame.DEVLOPMENT_CARD_HAND.getCards().size() == 4;
    }

    @Override
    public void update() {
        super.update();
        boolean active = isActive();
        if (active && !rewardGrantedAtCurrentThreshold
                && ZhuzheeGame.CLIENT != null
                && ZhuzheeGame.CLIENT.getLocalPlayer() != null) {
            int currentCoin = ZhuzheeGame.CLIENT.getLocalPlayer().getCoin();
            ZhuzheeGame.CLIENT.getLocalPlayer().setCoin(currentCoin + 15);
            rewardGrantedAtCurrentThreshold = true;
            System.out.println("Coin Boom activated: +15 coin");
        } else if (!active) {
            rewardGrantedAtCurrentThreshold = false;
        }
    }

    @Override
    public PoliticsStats calculateStats(ActionCard playedCard, City city) {
        return null;
    }

    @Override
    public void onActionCardPlayed(ActionCard playedCard, City city) {
        // no use.
    }
}
