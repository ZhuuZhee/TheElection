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

    @Override
    public boolean addCard(Card card) {
        boolean success = super.addCard(card);
        setPanelSize((Card.DEFAULT_CARD_WIDTH * Math.max(1,cards.size())) + SPACING,224);
        return success;
    }

    @Override
    public void removeCard(Card card) {
        super.removeCard(card);
        setPanelSize((164 + SPACING) * Math.max(1,cards.size()),224);
    }
}
