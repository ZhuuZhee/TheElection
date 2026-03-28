package Core.UI;

import Core.Player.Player;
import ZhuzheeEngine.Scene.Canvas;
import ZhuzheeEngine.Scene.Scene2D;

import javax.swing.*;
import java.awt.*;

public class InfoEndGame extends Canvas {
    private final Player player;
    private JLabel nameLabel;

    public InfoEndGame(Scene2D scene, Player player) {
        super(scene);
        this.player = player;
    }

}
