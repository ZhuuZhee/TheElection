package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.UI.UINotificationToast;
import Core.ZhuzheeGame;

/**
 * IronWill:  ถ้า Facility บนการ์ดเป็นลบ → ป้องกันเปลี่ยนให้เป็น 0
 * ถ้า Facility เป็นบวกอยู่แล้ว → บวกเพิ่มอีก +5
 */
public class  IronWill extends PolicyCard {
    public IronWill(int x, int y, String imagePath) {
        super("Iron Will", x, y, imagePath, -6);
        this.description = "Skill: If Development card has Facility < 0 then change to 0 but if development card has Facility > 0 then Facility +5";
    }

    @Override
    public boolean isActive() {
        if (ZhuzheeGame.POLICY_CARD_HAND != null) {
        return ZhuzheeGame.POLICY_CARD_HAND.containsCard(this);
    }
        return false; }

    @Override
    public void onActionCardPlayed(ActionCard playedCard, City city) {}

    @Override
    public PoliticsStats calculateStats(ActionCard playedCard, City city) {
        if (!isActive()) return null;
        PoliticsStats stats = new PoliticsStats(playedCard.getStats());

        int fac = stats.getStats(PoliticsStats.FACILITY);
        if (fac < 0) {
            UINotificationToast.showNotification("[Iron Will] Activate! " + playedCard.getName() + " Change to 0!");
            stats.setStats(PoliticsStats.FACILITY, 0);
        } else if (fac > 0) {
            UINotificationToast.showNotification("[Iron Will] Activate! " + playedCard.getName() + " Gain Facility +5!");
            stats.addStats(PoliticsStats.FACILITY, fac + 5);
        }
        return stats;
    }
}
