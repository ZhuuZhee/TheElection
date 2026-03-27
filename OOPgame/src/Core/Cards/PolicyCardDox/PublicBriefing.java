package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.ZhuzheeGame;

public class PublicBriefing extends PolicyCard {
    public PublicBriefing(int x, int y, String imagePath) {
        super("Public Briefing", x, y, imagePath, -4);
        this.description = "Skill: Play a Dev Card with Facility >= 4 to gain 5 Coins.";
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
            System.out.println("----------------------------------");
            System.out.println("🎤 [PUBLIC BRIEFING] ทำงาน!");
            System.out.println("โฆษกชี้แจงโครงการขนาดเล็ก " + playedCard.getName() + " ให้มวลชนเข้าใจ!");
            System.out.println(">>> คุณได้รับ 5 Coin จากสปอนเซอร์! <<<");
            System.out.println("----------------------------------");

            if (ZhuzheeGame.CLIENT != null && ZhuzheeGame.CLIENT.getLocalPlayer() != null) {
                int currentCoin = ZhuzheeGame.CLIENT.getLocalPlayer().getCoin();
                ZhuzheeGame.CLIENT.getLocalPlayer().setCoin(currentCoin + 5);
            }
        }
    }
}