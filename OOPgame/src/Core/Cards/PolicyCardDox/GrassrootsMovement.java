package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.ZhuzheeGame;

public class GrassrootsMovement extends PolicyCard {
    public GrassrootsMovement(int x, int y, String imagePath) {
        super("Grassroots Movement", x, y, imagePath, -3);
        this.description = "Skill: Play Dev Card with Environment <= 0 change Environment to +20.";
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
        PoliticsStats stats = playedCard.getStats();
        if (stats == null) return;
        int currentEnv = stats.getStats(PoliticsStats.ENVIRONMENT);
        if (currentEnv <= 0) {
            stats.setStats(PoliticsStats.ENVIRONMENT, 20);
            System.out.println("----------------------------------");
            System.out.println("🌱 [GRASSROOTS MOVEMENT] ทำงาน!");
            System.out.println("พลังรากหญ้า! พลิกฟื้นสิ่งแวดล้อมให้การ์ด " + playedCard.getName());
            System.out.println(">>> ค่า Environment เปลี่ยนเป็น +20 ทันที! <<<");
            System.out.println("----------------------------------");
        }
    }
}