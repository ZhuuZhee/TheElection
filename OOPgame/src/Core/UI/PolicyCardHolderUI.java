package Core.UI;

import Core.Cards.Card;
import ZhuzheeEngine.Scene.*;
import ZhuzheeEngine.Scene.Canvas;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;

public class PolicyCardHolderUI extends CardHolderUI {
    public PolicyCardHolderUI(Scene2D scene){
        super(scene);
        setAnchorTop(true);
        setAnchorLeft(true);
        setPanelSize(164,224);
        setMargins(16, 0, 16, 16);
        setSetLabel("Your Policies");
        setMaxCard(5);
    }
}
