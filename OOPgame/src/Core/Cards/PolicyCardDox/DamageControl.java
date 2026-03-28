package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.UI.UINotificationToast;
import Core.ZhuzheeGame;

public class DamageControl extends PolicyCard {
    public DamageControl(int x, int y, String imagePath) {
        super("Damage Control", x, y, imagePath, -6);
        this.description = "Skill: Any negative stats on played Development Card  change - to +.";
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
        boolean isTriggered = false; // ตัวแปรเช็คว่ามีการกลับค่าสแตทหรือไม่

        int currentFac = stats.getStats(PoliticsStats.FACILITY);
        if (currentFac < 0) {
            stats.setStats(PoliticsStats.FACILITY, Math.abs(currentFac));
            isTriggered = true;
        }

        int currentEnv = stats.getStats(PoliticsStats.ENVIRONMENT);
        if (currentEnv < 0) {
            stats.setStats(PoliticsStats.ENVIRONMENT, Math.abs(currentEnv));
            isTriggered = true;
        }

        int currentEco = stats.getStats(PoliticsStats.ECONOMY);
        if (currentEco < 0) {
            stats.setStats(PoliticsStats.ECONOMY, Math.abs(currentEco));
            isTriggered = true;
        }

        if (isTriggered) {
            UINotificationToast.showNotification("📰 [DAMAGE CONTROL] พลิกวิกฤตเป็นบวกให้ " + playedCard.getName() + " แล้ว!");
        }
    }
}