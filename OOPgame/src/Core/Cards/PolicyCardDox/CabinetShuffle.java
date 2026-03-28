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
        PoliticsStats stats = new PoliticsStats(playedCard.getStats());
        if (!isActive()) {
            return null;
        }
        if (stats == null) {
            return null;
        }
        int facValue = stats.getStats(PoliticsStats.FACILITY);
        int envValue = stats.getStats(PoliticsStats.ENVIRONMENT);
        int ecoValue = stats.getStats(PoliticsStats.ECONOMY);

        stats.setStats(PoliticsStats.FACILITY, -facValue);
        stats.setStats(PoliticsStats.ENVIRONMENT, -envValue);
        stats.setStats(PoliticsStats.ECONOMY, -ecoValue);

        UINotificationToast.showNotification("[Cabinet Shuffle] Activate! " + playedCard.getName() +"positive stats become negative and negative stats become positive!");
        return stats;
    }

    @Override
    public void onActionCardPlayed(ActionCard playedCard, City city) {
//        if (!isActive()) {
//            return;
//        }
//
//        PoliticsStats stats = playedCard.getStats();
//        if (stats == null) {
//            return;
//        }
//
//        int facValue = stats.getStats(PoliticsStats.FACILITY);
//        int envValue = stats.getStats(PoliticsStats.ENVIRONMENT);
//        int ecoValue = stats.getStats(PoliticsStats.ECONOMY);
//
//        stats.setStats(PoliticsStats.FACILITY, -facValue);
//        stats.setStats(PoliticsStats.ENVIRONMENT, -envValue);
//        stats.setStats(PoliticsStats.ECONOMY, -ecoValue);
//
//        System.out.println("----------------------------------");
//        System.out.println("PolicyCard [Cabinet Shuffle] ทำงาน!");
//        System.out.println("ค่าสถานะของการ์ด " + playedCard.getName() + " ถูกสลับเครื่องหมายทั้งหมด");
//        System.out.println("----------------------------------");
//
        return;
    }
}
