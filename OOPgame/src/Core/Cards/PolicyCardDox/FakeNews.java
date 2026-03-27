package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.Player.Player;
import Core.ZhuzheeGame;

public class FakeNews extends PolicyCard {
    public FakeNews(int x, int y, String imagePath) {
        super("Fake News", x, y, imagePath, -5);
        this.description = "Skill: If play Dev Card with 0 Facility gain +3 Coins and +1 Environment.";
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
        if (ZhuzheeGame.CLIENT == null) return;
        Player localPlayer = ZhuzheeGame.CLIENT.getLocalPlayer();
        if (localPlayer == null) return;

        PoliticsStats stats = playedCard.getStats();
        if (stats == null) return;

        int fac = stats.getStats(PoliticsStats.FACILITY);
        if (fac == 0) {
            int currentCoin = localPlayer.getCoin();
            localPlayer.setCoin(currentCoin + 3);

            int currentEnv = stats.getStats(PoliticsStats.ENVIRONMENT);
            stats.setStats(PoliticsStats.ENVIRONMENT, currentEnv + 1);

            System.out.println("----------------------------------");
            System.out.println("📰 [FAKE NEWS] ทำงาน!");
            System.out.println("ปั่นข่าวโคมลอย! การ์ด " + playedCard.getName() + " ได้รับ +1 Environment");
            System.out.println("และคุณได้รับเงินสนับสนุน 3 Coin!");
            System.out.println("----------------------------------");
        }
    }
}