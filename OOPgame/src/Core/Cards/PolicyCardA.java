/**
 * @Xynezter 10/3/2026 19:30
 */
/// create policycard for test
package Core.Cards;

import Dummy.Citybanna;
import java.util.List;

// สืบทอด PassiveCard และใช้ Interface PolicyCard
public class PolicyCardA extends PassiveCard implements PolicyCard {

    public PolicyCardA(String name, int x, int y, boolean enabled) {
        super(name, x, y, enabled); // โยนค่าไปให้ PassiveCard จัดการ
    }

    @Override
    public boolean IsActivate() {
        return true;
    }

    // Business Logic
    @Override
    public void onActionCardPlayed(ActionCard playedCard, Citybanna city) {
        if (IsActivate()) {
            System.out.println("----------------------------------");
            System.out.println("PolicyCardA: " + this.name + " Buff for " + playedCard.name);
            System.out.println("----------------------------------");
            List<Integer> stats = playedCard.getStat();
            if (stats != null && !stats.isEmpty()) {
                // business logic confix stat
                stats.set(0, stats.get(0) * 10); // ex: add stat index[0]
            }
        }
    }

    @Override
    protected boolean isDroppable(Object bottom) {
        // ให้วางทับ CardSlot ได้
        if (bottom instanceof CardSlot) {
            return true;
        }
        return false;
    }
}