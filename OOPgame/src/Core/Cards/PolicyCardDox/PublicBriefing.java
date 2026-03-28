package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.ZhuzheeGame;

public class PublicBriefing extends PolicyCard {
    public PublicBriefing(int x, int y, String imagePath) {
        super("Public Briefing", x, y, imagePath, -4);
        this.description = "Skill: Play a Dev Card with Facility >= 4 gain x5 Facility.";
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
        int fac = stats.getStats(PoliticsStats.FACILITY);

        if (fac >= 4) {
            stats.setStats(PoliticsStats.FACILITY, fac * 5);
            System.out.println("----------------------------------");
            System.out.println("🎤 [PUBLIC BRIEFING] ทำงาน!");
            System.out.println("โฆษกชี้แจงโปรเจกต์ยักษ์ใหญ่ " + playedCard.getName() + " จนมวลชนมั่นใจ!");
            System.out.println(">>> ค่า Facility ของการ์ดใบนี้ถูกคูณ 5 ทันที! (กลายเป็น " + (fac * 5) + ") <<<");
            System.out.println("----------------------------------");
        }
    }
}