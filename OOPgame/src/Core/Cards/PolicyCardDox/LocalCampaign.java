package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.CardSlot;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.ZhuzheeGame;

public class LocalCampaign extends PolicyCard {
    public LocalCampaign(int x, int y, String imagePath) {
        super("Local Campaign", x, y, imagePath, -3);
        this.description = "Skill: If you play a Development card with +5 Economic. Gain 6 coins.";
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
        if (isActive()) {
            PoliticsStats stats = playedCard.getStats();

            if (stats != null) {
                // ดึงค่า Environment ของการ์ดที่เพิ่งเล่นออกมาเช็ค
                int envValue = stats.getStats(PoliticsStats.ENVIRONMENT);
                // ถ้าการ์ดใบนั้นมีค่า Environment เป็นบวก
                if (envValue > 5) {
                    System.out.println("----------------------------------");
                    System.out.println("PolicyCard [Local Campaign] ทำงาน!");
                    System.out.println("เนื่องจากคุณเล่นการ์ด " + playedCard.getName() + " ที่มี +Environment");
                    System.out.println(">>> คุณได้รับ 3 Coin จากธนาคาร! <<<");
                    System.out.println("----------------------------------");

                    // ดึง coin มาบวก 6
                    int currentCoin = ZhuzheeGame.CLIENT.getLocalPlayer().getCoin();
                    ZhuzheeGame.CLIENT.getLocalPlayer().setCoin(currentCoin + 6);
                }
            }
        }
    }

}
