package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.Player.Player;
import Core.UI.UINotificationToast;
import Core.ZhuzheeGame;

public class CleanAudit extends PolicyCard {
    public CleanAudit(int x, int y, String imagePath) {
        super("Clean Audit", x, y, imagePath, -4);
        this.description = "Skill: Play Dev Card with all stats >= 0 gain 2 Coins.";
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
        int env = stats.getStats(PoliticsStats.ENVIRONMENT);
        int eco = stats.getStats(PoliticsStats.ECONOMY);
        if (fac >= 0 && env >= 0 && eco >= 0) {
            stats.setStats(PoliticsStats.ECONOMY, eco * 8);
            stats.setStats(PoliticsStats.ENVIRONMENT, env * 8);
            stats.setStats(PoliticsStats.FACILITY, fac * 8);
            UINotificationToast.showNotification("📄 [CLEAN AUDIT] โครงการ " + playedCard.getName() + " ไม่มีข้อเสีย! ได้รับโบนัส 2 Coin");
        }
    }
}