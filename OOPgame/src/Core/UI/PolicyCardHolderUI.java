package Core.UI;

import Core.Cards.Card;
import Core.Cards.Stream.PolicyCardRegistry;
import ZhuzheeEngine.Scene.*;

public class PolicyCardHolderUI extends CardHolderUI {

    public static final int SPACING = 64;

    public PolicyCardHolderUI(Scene2D scene){
        super(scene);
        setAnchorTop(true);
        setAnchorLeft(true);
        setPanelSize(164,224);
        setMargins(16, 0, 16, 16);
        setSetLabel("Your Policies");
        setMaxCard(5);
    }

    private void updateSize(){
        setPanelSize((Card.DEFAULT_CARD_WIDTH * Math.max(1,cards.size())) + SPACING,224);
    }

    @Override
    public boolean addCard(Card card) {
        boolean success = super.addCard(card);
        if (success) {
            // ใช้ชื่อ Class แทนชื่อการ์ด (เช่น "GreenPolicy" แทน "Green Policy") เพื่อให้ตรงกับ JSON
            PolicyCardRegistry.markAsUsed(card.getClass().getName());
            card.setDraggable(false);
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
