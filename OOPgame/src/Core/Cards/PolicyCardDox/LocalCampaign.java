package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.ZhuzheeGame;

public class LocalCampaign extends PolicyCard {
    public LocalCampaign(int x, int y, String imagePath) {
        super("Local Campaign", x, y, imagePath, -3);
        this.description = "Skill: If you play a Development card with +5 Economy. Gain 6 coins.";
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
        if (!isActive()) {
            return;
        }

        PoliticsStats stats = playedCard.getStats();
        if (stats == null) {
            return;
        }

        int ecoValue = stats.getStats(PoliticsStats.ECONOMY);
        if (ecoValue > 0) {
            System.out.println("----------------------------------");
            System.out.println("PolicyCard [Local Campaign] ทำงาน!");
            System.out.println("เนื่องจากคุณเล่นการ์ด " + playedCard.getName() + " ที่มี +Economy");
            System.out.println(">>> คุณได้รับ 6 Coin <<<");
            System.out.println("----------------------------------");

            if (ZhuzheeGame.CLIENT != null && ZhuzheeGame.CLIENT.getLocalPlayer() != null) {
                int currentCoin = ZhuzheeGame.CLIENT.getLocalPlayer().getCoin();
                ZhuzheeGame.CLIENT.getLocalPlayer().setCoin(currentCoin + 6);
            }
            stats.addStats(PoliticsStats.ECONOMY, 20);
        }
    }

}
