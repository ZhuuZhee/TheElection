package Core.Cards.AllArcanaCards;

import Core.Cards.ArcanaCard;
import Core.Cards.PolicyCard;
import Core.Cards.Stream.PolicyCardRegistry;
import Core.Network.Client.ClientAdapter;
import Core.UI.PolicyCardHolderUI;
import Core.UI.UINotificationToast;
import Core.ZhuzheeGame;
import ZhuzheeEngine.Scene.GameObject;

import java.util.ArrayList;
import java.util.List;

public class TheWheelOfFortune extends ArcanaCard {

    // เก็บรายการ Policy Card ที่สุ่มมาในเทิร์นนี้ เพื่อรอเคลียร์ตอนจบเทิร์น
    private final ArrayList<PolicyCard> temporaryPolicyCards = new ArrayList<>();
    public static final int MAX_POLICY_CARD_COUNT = 8;
    public TheWheelOfFortune(int x, int y) {
        super("The Wheel of Fortune", x, y, 2, "OOPgame/Assets/ImageForCards/Arcana Card/WOF.png");
        this.description = "Skill: fill up your Policy Hand with random Policy Card until end of turn.";

        ZhuzheeGame.CLIENT.addClientListener(new ClientAdapter(){
            @Override
            public void onEndTurn() {
                onTurnEnded();
            }
        });
    }

    @Override
    protected void activateSkill() {
        UINotificationToast.showNotification("The Wheel Of Fortune Activate!");
        // เช็กก่อนว่ามีที่วาง Policy Card ไหม
        if (ZhuzheeGame.POLICY_CARD_HAND != null) {
            ZhuzheeGame.POLICY_CARD_HAND.setMaxCard(MAX_POLICY_CARD_COUNT);
            int maxCards = ZhuzheeGame.POLICY_CARD_HAND.getMaxCard();
            int currentCardsCount = ZhuzheeGame.POLICY_CARD_HAND.getCards().size();
            int cardsToDraw = maxCards - currentCardsCount;
            
            if (cardsToDraw > 0) {
                // เรียกใช้ระบบสุ่ม Policy Card ที่มีอยู่แล้วในโปรเจกต์
                List<PolicyCard> rolledCards = PolicyCardRegistry.rollCards(cardsToDraw);
                
                for (PolicyCard card : rolledCards) {
                    // ยัดลงไปใน Slot ของผู้เล่น
                    ZhuzheeGame.POLICY_CARD_HAND.addCard(card);
                    
                    // เก็บประวัติไว้ลบออกตอนจบเทิร์น
                    temporaryPolicyCards.add(card);
                }
            } else {
                System.out.println("Policy Hand is full. No cards drawn.");
            }
        }
    }

    // !! สำคัญ: อย่าลืมหาที่เรียกใช้ onTurnEnded() เมื่อผู้เล่นจบเทิร์น !!
    public void onTurnEnded() {
        if (ZhuzheeGame.POLICY_CARD_HAND != null) {
            for (PolicyCard tempCard : temporaryPolicyCards) {
                // ตรวจสอบว่าการ์ดยังอยู่ในมือ (เผื่อถูกใช้ออกไปก่อน) แล้วลบโดยอ้างอิง Object ตรงๆ
                if (ZhuzheeGame.POLICY_CARD_HAND.getCards().contains(tempCard)) {
                    ZhuzheeGame.POLICY_CARD_HAND.removeCard(tempCard);
                    GameObject.Destroy(tempCard);
                }
            }
            temporaryPolicyCards.clear();
            UINotificationToast.showNotification("The Wheel Of Fortune effect ended. Temporary policy cards removed.", 5000, true);
        }
        ZhuzheeGame.POLICY_CARD_HAND.setMaxCard(PolicyCardHolderUI.DEFAULT_MAX_CARD);
    }
}
