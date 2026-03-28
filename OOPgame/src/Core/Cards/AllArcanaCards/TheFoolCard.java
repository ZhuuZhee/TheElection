package Core.Cards.AllArcanaCards;

import Core.Cards.ActionCard;
import Core.Cards.ArcanaCard;
import Core.Cards.PolicyCard;
import Core.Cards.Stream.CardBufferObject;
import Core.Cards.Stream.CardReader;
import Core.Network.Client.ClientAdapter;
import Core.UI.PolicyCardHolderUI;
import Core.ZhuzheeGame;
import ZhuzheeEngine.Scene.GameObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import Core.UI.UINotificationToast;

public class TheFoolCard extends ArcanaCard {
    private ArrayList<ActionCard> temporaryActionCards = new ArrayList<>();
    public static final int MAX_DEVLOPMENT_CARD_COUNT = 10;

    public TheFoolCard(int x, int y) {
        // สามารถกำหนด maxCooldown ได้จาก contractor ตรงนี้ได้เลย
        super("TheFoolCard", x, y, 2, "OOPgame/Assets/ImageForCards/Arcana Card/TheFool.png");
        this.description = "Skill: Increase Development Card hand capacity to 10 and draw random Development Cards until full.";
        ZhuzheeGame.CLIENT.addClientListener(new ClientAdapter() {
            @Override
            public void onEndTurn() {
                onTurnEnded();
            }
        });
    }

    @Override
    protected void activateSkill() {
        UINotificationToast.showNotification("The Fool activate!");
        if (ZhuzheeGame.DEVLOPMENT_CARD_HAND != null) {
            ZhuzheeGame.DEVLOPMENT_CARD_HAND.setMaxCard(MAX_DEVLOPMENT_CARD_COUNT);
            int maxCards = ZhuzheeGame.DEVLOPMENT_CARD_HAND.getMaxCard();
            int currentCardsCount = ZhuzheeGame.DEVLOPMENT_CARD_HAND.getCards().size();
            int cardsToDraw = maxCards - currentCardsCount;

            if (cardsToDraw > 0) {
                // เรียกใช้ระบบสุ่ม Policy Card ที่มีอยู่แล้วในโปรเจกต์
                List<CardBufferObject> rolledCards = CardReader.readActionCards();
                Collections.shuffle(rolledCards);

                int actualCardsToDraw = Math.min(cardsToDraw, rolledCards.size());
                for (int i = 0; i < actualCardsToDraw; i++) {
                    CardBufferObject card = rolledCards.get(i);
                    ZhuzheeGame.DEVLOPMENT_CARD_HAND.addCard(new ActionCard(card, 0, 0));
                }
            } else {
                System.out.println("Policy Hand is full. No cards drawn.");
            }
        }
    }

    public void onTurnEnded() {
        if (ZhuzheeGame.DEVLOPMENT_CARD_HAND != null) {
            for (ActionCard tempCard : temporaryActionCards) {
                // ตรวจสอบว่าการ์ดยังอยู่ในมือ (เผื่อถูกใช้ออกไปก่อน) แล้วลบโดยอ้างอิง Object ตรงๆ
                if (ZhuzheeGame.DEVLOPMENT_CARD_HAND.getCards().contains(tempCard)) {
                    ZhuzheeGame.DEVLOPMENT_CARD_HAND.removeCard(tempCard);
                    GameObject.Destroy(tempCard);
                }
            }
            temporaryActionCards.clear();
            System.out.println("The Fool effect ended. Temporary development cards removed.");

            // คืนค่าจำนวนการ์ดสูงสุดกลับเป็นค่า Default
            ZhuzheeGame.DEVLOPMENT_CARD_HAND.setMaxCard(5); // เปลี่ยนเป็นค่าคงที่ Default ของ Development Card ถ้ามี
        }
    }
}
