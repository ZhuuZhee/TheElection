/**
 * @Xynezter 10/3/2026 19:30
 */
// create policycard for test
package Core.Cards;

import Dummy.Maps.City;
import Dummy.Maps.PoliticsStats;

// สืบทอด PassiveCard และใช้ Interface PolicyCard
public class PolicyCardA extends PolicyCard {

    public PolicyCardA(String name, int x, int y, int coin) {
        super(name, x, y,coin); // โยนค่าไปให้ PassiveCard จัดการ
    }

    public PolicyCardA(String name, int x, int y, String imagePath, int coin) {
        super(name, x, y, imagePath, coin);
    }

    @Override
    public boolean IsActivate() {
        return true;
    }

    // Business Logic
    @Override
    public void onActionCardPlayed(ActionCard playedCard, City city) {
        if (IsActivate()) {
            System.out.println("----------------------------------");
            System.out.println("PolicyCardA: " + this.name + " Buff for " + playedCard.name);
            System.out.println("----------------------------------");
            PoliticsStats stats = playedCard.getStats();
            if (stats != null) {
                // business logic confix stat # ตัวอย่าง Business Logic: คูณค่า Facility เดิมด้วย 10
                int currentFacility = stats.getStats(PoliticsStats.Facility);
                stats.setStats(PoliticsStats.Facility, currentFacility * 10);
            }
        }
    }

    @Override
    protected boolean isDroppable(Object bottom) {
        // ให้วางทับ CardSlot ได้
        return bottom instanceof CardSlot;
    }
}