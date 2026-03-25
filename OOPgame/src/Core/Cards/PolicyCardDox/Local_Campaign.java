package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.CardSlot;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;

public class Local_Campaign extends PolicyCard {
    public Local_Campaign(int x, int y, String imagePath) {super("Local Campaign", x, y, imagePath, 3);}
    @Override
    public boolean IsActivate() {
        return true; // ให้การ์ดทำงานเสมอเมื่อถูกวางลงใน Slot
    }

    @Override
    public void onActionCardPlayed(ActionCard playedCard, City city) {
        if (IsActivate()) {
            PoliticsStats stats = playedCard.getStats();

            if (stats != null) {
                // ดึงค่า Environment ของการ์ดที่เพิ่งเล่นออกมาเช็ค
                int envValue = stats.getStats(PoliticsStats.Environment);
                // ถ้าการ์ดใบนั้นมีค่า Environment เป็นบวก
                if (envValue > 0) {
                    System.out.println("----------------------------------");
                    System.out.println("PolicyCard [Local Campaign] ทำงาน!");
                    System.out.println("เนื่องจากคุณเล่นการ์ด " + playedCard.getName() + " ที่มี +Environment");
                    System.out.println(">>> คุณได้รับ 1 Coin จากธนาคาร! <<<");
                    System.out.println("----------------------------------");

                    // TODO: รอใส่โค้ดสำหรับเพิ่มเงินให้ผู้เล่น 1 Coin ตรงนี้
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
