package Core.Cards.AllArcanaCards;

import Core.Cards.ArcanaCard;
import Core.Cards.PolicyCard;
import Core.Cards.Stream.PolicyCardRegistry;
import Core.ZhuzheeGame;

import java.util.ArrayList;
import java.util.List;

public class TheWheelOfFortune extends ArcanaCard {

    // เก็บรายการ Policy Card ที่สุ่มมาในเทิร์นนี้ เพื่อรอเคลียร์ตอนจบเทิร์น
    private final ArrayList<PolicyCard> temporaryPolicyCards = new ArrayList<>();

    public TheWheelOfFortune(int x, int y) {
        super("The Wheel of Fortune", x, y, 2, "OOPgame/Assets/ImageForCards/Arcana Card/WOF.png");
        this.description = "Skill: fill up your Policy Hand with random Policy Card until end of turn.";
    }

    @Override
    protected void activateSkill() {
        System.out.println("The Wheel Of Fortune activate!");
        
        // เช็กก่อนว่ามีที่วาง Policy Card ไหม
        if (ZhuzheeGame.POLICY_CARD_HAND != null) {
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
        if (ZhuzheeGame.POLICY_CARD_HAND != null && !temporaryPolicyCards.isEmpty()) {
            // แก้ไขประเภทตัวแปรให้ตรงกับที่ getCards() คืนค่ามา
            java.util.List<Core.Cards.Card> currentHand = ZhuzheeGame.POLICY_CARD_HAND.getCards();
            
            for (PolicyCard tempCard : temporaryPolicyCards) {
                // วนลูปหาการ์ดในมือที่อ้างอิงถึง Object เดียวกันเป๊ะๆ (ป้องกันการลบการ์ดอื่นที่ชื่อซ้ำกัน)
                for (int i = 0; i < currentHand.size(); i++) {
                    // เปลี่ยนเป็น Card ธรรมดา
                    Core.Cards.Card cardInHand = currentHand.get(i);
                    if (cardInHand == tempCard) {
                        ZhuzheeGame.POLICY_CARD_HAND.removeCard(cardInHand);
                        break; // เจอแล้วลบออก แล้วข้ามไปหาใบต่อไป
                    }
                }
            }
            temporaryPolicyCards.clear();
            System.out.println("The Wheel Of Fortune effect ended. Temporary policy cards removed.");
        }
    }
}
