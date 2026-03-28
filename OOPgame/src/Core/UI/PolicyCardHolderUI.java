package Core.UI;

import Core.Cards.Card;
import Core.Cards.PolicyCard;
import Core.Cards.Stream.PolicyCardRegistry;
import ZhuzheeEngine.Scene.*;

public class PolicyCardHolderUI extends CardHolderUI {

    public static final int SPACING = 64;
    public static final int DEFAULT_MAX_CARD = 5;
    public PolicyCardHolderUI(Scene2D scene){
        super(scene);
        setAnchorTop(true);
        setAnchorLeft(true);
        setPanelSize(164,224);
        setMargins(16, 0, 16, 16);
        setSetLabel("Your Policies");
        setMaxCard(DEFAULT_MAX_CARD);
    }

    private void updateSize(){
        if (cards.isEmpty()) {
            setPanelSize(164, 224);
            return;
        }
        // การ์ดจะถูกปรับความสูงเป็น (panelHeight - 60) = 164px
        // อัตราส่วนความกว้างต่อความสูงของการ์ดคือ 100/150
        int actualCardWidth = (int) ((224 - 60) * (100.0 / 150.0));
        
        // 2. คำนวณความกว้างรวม: (ความกว้างการ์ด * จำนวน) + (Gap 10px * จำนวนช่องว่าง) + Padding เผื่อขอบ
        int hGap = 10; 
        int totalWidth = (actualCardWidth * cards.size()) + (hGap * (cards.size() + 1)) + 30;
        
        setPanelSize(Math.max(164, totalWidth), 224);
    }

    public void showActiveCards(){
        for(Card card : cards){
            PolicyCard policyCard = (PolicyCard) card;
            policyCard.setShowHighlight(policyCard.isActive());
        }
    }
    public void hideActiveCards(){
        for(Card card : cards){
            PolicyCard policyCard = (PolicyCard) card;
            policyCard.setShowHighlight(false);
        }
    }

    @Override
    public boolean addCard(Card card) {
        if(isFull())
            removeCard(cards.getFirst());//เอาใบแรกออกถ้าเต็ม

        boolean success = super.addCard(card);
        if (success) {
            // ใช้ชื่อ Class แทนชื่อการ์ด (เช่น "GreenPolicy" แทน "Green Policy") เพื่อให้ตรงกับ JSON
            PolicyCardRegistry.markAsUsed(card.getClass().getName());
            card.setDraggable(false);
            card.setGrabbed(false);
            updateSize();
        }
        return success;
    }

    @Override
    public void removeCard(Card card) {
        super.removeCard(card);
        updateSize();
    }
}
