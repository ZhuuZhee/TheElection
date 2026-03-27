package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.Player.Player;
import Core.ZhuzheeGame;

public class TradeAgreement extends PolicyCard {
    public TradeAgreement(int x, int y, String imagePath) {
        super("Trade Agreement", x, y, imagePath, -4);
        this.description = "Skill: Negative stats on your played Development card become 0. Lose 1 coin per fixed stat.";
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

        int fixedStatCount = 0;

        if (stats.getStats(PoliticsStats.FACILITY) < 0) {
            stats.setStats(PoliticsStats.FACILITY, 0);
            fixedStatCount++;
        }
        if (stats.getStats(PoliticsStats.ENVIRONMENT) < 0) {
            stats.setStats(PoliticsStats.ENVIRONMENT, 0);
            fixedStatCount++;
        }
        if (stats.getStats(PoliticsStats.ECONOMY) < 0) {
            stats.setStats(PoliticsStats.ECONOMY, 0);
            fixedStatCount++;
        }

        if (fixedStatCount <= 0) {
            return;
        }

        Player player = null;
        if (ZhuzheeGame.CLIENT != null) {
            player = ZhuzheeGame.CLIENT.getLocalPlayer();
        } else {
            player = Dummy.Tester.dummyPlayer;
        }

        if (player != null) {
            player.setCoin(player.getCoin() - fixedStatCount);
        }

        System.out.println("----------------------------------");
        System.out.println("PolicyCard [Trade Agreement] ทำงาน!");
        System.out.println("สแตทติดลบของการ์ด " + playedCard.getName() + " ถูกเปลี่ยนเป็น 0 จำนวน " + fixedStatCount + " ค่า");
        System.out.println(">>> คุณเสีย " + fixedStatCount + " Coin <<<");
        System.out.println("----------------------------------");
    }
}
