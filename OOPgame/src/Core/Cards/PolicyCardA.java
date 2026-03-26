/**
 * @Xynezter 10/3/2026 19:30
 */
// create policycard for test
package Core.Cards;

import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.ZhuzheeGame;

// สืบทอด PassiveCard และใช้ Interface PolicyCard
public class PolicyCardA extends PolicyCard {

    public PolicyCardA(String name, int x, int y, String imagePath, int coin) {
        super("A", 0, 100, imagePath, -10);
    }

    @Override
    public boolean isActive() {
        if (ZhuzheeGame.POLICY_CARD_UI != null) {
            return ZhuzheeGame.POLICY_CARD_UI.containsCard(this);
        }
        return false;
    }

    // Business Logic
    @Override
    public void onActionCardPlayed(ActionCard playedCard, City city) {
        if (isActive()) {
            PoliticsStats cardStats = playedCard.getStats();
            if (cardStats != null) {
                int cardFacility = cardStats.getStats(PoliticsStats.FACILITY);

                if (cardFacility > 0) {
                    int bonusFacility = cardFacility * 10;
                    cardStats.setStats(PoliticsStats.FACILITY, bonusFacility);
                }
            }
        }
    }

}