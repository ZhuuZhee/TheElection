package Core.UI;

import Core.Cards.ActionCard;
import ZhuzheeEngine.Scene.Scene2D;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ActionCardHolderUI extends JPanel {
    private ArrayList<ActionCard> cards = new ArrayList<>();
    public ActionCardHolderUI(Scene2D scene){

        add(new JLabel("Test For Card Holder UI"));

        setSize(400,200);
        setPreferredSize(new Dimension(400,200));
        setBackground(Color.CYAN);

        scene.add(this, BorderLayout.SOUTH);

        setVisible(true);
    }
}
