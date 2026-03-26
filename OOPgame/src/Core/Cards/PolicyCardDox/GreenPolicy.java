package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.CardSlot;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;

/**
 * A specific Policy Card that protects the Environment stat.
 * If an Action Card would decrease the Environment stat, this policy negates the penalty.
 */
public class GreenPolicy extends PolicyCard {
    public GreenPolicy(int x, int y, String imagePath) {
        super("Green Policy", x, y, imagePath, -5);
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    /**
     * Intercepts Action Card stats. If Environment is negative, it sets it to 0
     * to prevent ecological damage.
     */
    public void onActionCardPlayed(ActionCard playedCard, City city) {
        // เช็คว่าการ์ดที่เล่นเป็น DevelopCard หรือไม่
        if (playedCard instanceof ActionCard) {
            PoliticsStats stats = playedCard.getStats();
            if (stats != null) {
                int env = stats.getStats(PoliticsStats.ENVIRONMENT);
                if (env < 0) {
                    System.out.println("----------------------------------");
                    System.out.println("PolicyCard [Green Policy] ทำงาน!");
                    System.out.println("ค่า Environmentที่ติดลบ ถูกป้องกัน");
                    System.out.println(">>> Environment ถูกเปลี่ยนเป็น 0 <<<");
                    System.out.println("----------------------------------");

                    stats.setStats(PoliticsStats.ENVIRONMENT, 0);
                }
            }
        }
    }
    @Override
    protected boolean isDroppable(Object bottom) {
        // กำหนดให้การ์ดใบนี้ลากไปวางใน CardSlot ได้เท่านั้น
        return bottom instanceof CardSlot;
    }
}
