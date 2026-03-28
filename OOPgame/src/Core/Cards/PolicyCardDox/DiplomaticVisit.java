package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.Player.Player;
import Core.UI.UINotificationToast;
import Core.ZhuzheeGame;

public class DiplomaticVisit extends PolicyCard {

    //ตัวแปรสำหรับจำว่าการ์ด ActionCard ใบไหนที่เพิ่งรับเงินไปแล้ว จะได้ไม่แจกเบิ้ล
    private ActionCard lastTriggeredCard = null;

    public DiplomaticVisit(int x, int y, String imagePath) {
        super("Diplomatic Visit", x, y, imagePath, -5);
        this.description = "Skill: Play Dev card to gain +5 coins. (If you have > 10 coins, gain +20 instead).";
    }

    @Override
    public boolean isActive() {
        return ZhuzheeGame.POLICY_CARD_HAND != null
                && ZhuzheeGame.POLICY_CARD_HAND.containsCard(this);
    }

    @Override
    public void onActionCardPlayed(ActionCard playedCard, City city) {
        //เช็คว่าการ์ด Policy ใบนี้อยู่ในช่องที่ทำงานได้หรือไม่
        if (!isActive()) return;

        //ป้องกันบั๊ก Engine ส่ง Event เบิ้ล: ถ้าเป็นการ์ดใบเดิมที่เพิ่งคิดเงินไปแล้ว ให้หยุดทำงานทันที
        if (playedCard == lastTriggeredCard) {
            return;
        }

        //ดึงข้อมูล Player และคำนวณเงิน
        if (ZhuzheeGame.CLIENT != null && ZhuzheeGame.CLIENT.getLocalPlayer() != null) {
            Player localPlayer = ZhuzheeGame.CLIENT.getLocalPlayer();

            //ใช้ getCoin() จากคลาส Player ของคุณ
            int currentCoin = localPlayer.getCoin();
            int bonusAmount;

            //เงื่อนไขถ้ามีเงิน > 7 ได้ +1, ถ้ามี <= 7 ได้ +2
            if (currentCoin > 10) {
                bonusAmount = 20;
            } else {
                bonusAmount = 5;
            }

            //ใช้ setCoin() จากคลาส Player เพื่ออัปเดตเงิน
            localPlayer.setCoin(currentCoin + bonusAmount);

            //บันทึกไว้ว่าการ์ดใบนี้ทำงานไปแล้วนะ รอบหน้าถ้า Engine ส่งมาอีกจะได้ไม่บวกเงินซ้ำ
            lastTriggeredCard = playedCard;

            UINotificationToast.showNotification("[Diplomatic Visit] Activate! " + "Gain +" + bonusAmount + " Coin!");
        }
    }
}