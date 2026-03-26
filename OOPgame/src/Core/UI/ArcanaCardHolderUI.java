package Core.UI;

import Core.Cards.Card;
import ZhuzheeEngine.Scene.*;

import javax.swing.*;
import java.awt.*;

public class ArcanaCardHolderUI extends CardHolderUI {
    public static final int SPACING = 64;

    public ArcanaCardHolderUI(Scene2D scene){
        super(scene);
        setAnchorTop(false);
        setAnchorLeft(true);
        setPanelSize(164,224);
        setMargins(16, 0, 16, 16);
        setSetLabel("Your Arcana Cards");
        setMaxCard(1);
    }

    private void updateSize(){
        setPanelSize((Card.DEFAULT_CARD_WIDTH * Math.max(1,cards.size())) + SPACING,224);
    }

    @Override
    protected void onResize(int width, int height) {
        super.onResize(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        // วาดสีพื้นหลังแบบโปร่งแสงเอง
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    @Override
    public boolean addCard(Card arcanaCard) {
        boolean success = super.addCard(arcanaCard);
        arcanaCard.setDraggable(false);
        updateSize();
        return success;
    }

    @Override
    public void removeCard(Card card) {
        super.removeCard(card);
        updateSize();
    }
}
