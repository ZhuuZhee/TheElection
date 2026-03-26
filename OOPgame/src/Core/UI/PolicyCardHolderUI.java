package Core.UI;

import Core.Cards.Card;
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
        card.setDraggable(false);
        updateSize();
        return success;
    }

    @Override
    public void removeCard(Card card) {
        super.removeCard(card);
        updateSize();
    }
}
