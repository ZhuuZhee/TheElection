package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.UI.UINotificationToast;
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
//        if (!isActive()) return;
//        PoliticsStats stats = playedCard.getStats();
//        if (stats == null) return;
//        int fac = stats.getStats(PoliticsStats.FACILITY);
//        if (fac >= 4) {
//            stats.setStats(PoliticsStats.FACILITY, fac * 5);
//            UINotificationToast.showNotification("🎤 [PUBLIC BRIEFING] ชี้แจงโปรเจกต์ " + playedCard.getName() + " สำเร็จ! (x5 Facility)");
//        }
        return;
    }
    @Override
    public PoliticsStats calculateStats(ActionCard playedCard, City city) {
        if (!isActive()) return null;
        PoliticsStats stats = new PoliticsStats(playedCard.getStats());
        if (stats == null) return null;
        int fac = stats.getStats(PoliticsStats.FACILITY);
        if (fac >= 4) {
            stats.setStats(PoliticsStats.FACILITY, fac * 5);
            UINotificationToast.showNotification("[Public Briefing] Activate! " + playedCard.getName() + " Gain x5 Facility!");
        }
        return stats;
    }
}