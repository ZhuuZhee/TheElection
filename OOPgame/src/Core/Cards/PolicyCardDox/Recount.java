package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.ZhuzheeGame;

public class Recount extends PolicyCard {
    public Recount(int x, int y, String imagePath) {
        super("Recount", x, y, imagePath, -5);
        this.description = "Skill: If play Development card with at least 1 negative stat. Gain x3 to all stats.";
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
                int envValue = stats.getStats(PoliticsStats.ENVIRONMENT);
                int facValue = stats.getStats(PoliticsStats.FACILITY);
                int ecoValue = stats.getStats(PoliticsStats.ECONOMY);

                // ทำงานเมื่อการ์ด Develop ที่กำลังวางมี stat ติดลบอย่างน้อย 1 ค่า
                if (envValue < 0 || facValue < 0 || ecoValue < 0) {
                    System.out.println("----------------------------------");
                    System.out.println("PolicyCard [Recount] ทำงาน!");
                    System.out.println(">>> คุณได้รับ x3 ในทุกstat <<<");
                    System.out.println("----------------------------------");
                    stats.addStats(PoliticsStats.ENVIRONMENT, envValue * 3);
                    stats.addStats(PoliticsStats.FACILITY, facValue * 3);
                    stats.addStats(PoliticsStats.ECONOMY, ecoValue * 3);
                }
            }
        }
    }
}
