package Core.UI;

import Core.Player.Player;
import Core.ZhuzheeGame;
import ZhuzheeEngine.Scene.Scene2D;

public class PlayerCoinUI extends CardHolderUI {

    private final Player localPlayer;

    public PlayerCoinUI(Scene2D scene) {
        super(scene);

        if (ZhuzheeGame.CLIENT != null) {
            this.localPlayer = ZhuzheeGame.CLIENT.getLocalPlayer();
        } else {
            this.localPlayer = null;
        }

        // ตั้งให้อยู่มุมขวาบน
        setAnchorTop(false);
        setAnchorRight(true);
        setAnchorLeft(false);

        // เว้นระยะขอบ (Top, Right, Bottom, Left)
        setMargins(0, 16, 0, 250);

        setPanelSize(180, 30);
        setMaxCard(0);

        try {
            java.awt.image.BufferedImage customFrame = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/btn_hover.png"));
            setNineSliceImage(customFrame, 10, 10, 6, 6); 
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitleColor(java.awt.Color.WHITE);

        updateCoinDisplay();

    }

    public void updateCoinDisplay() {
        if (localPlayer != null) {
            setSetLabel("Money: $ " + localPlayer.getCoin());
            try {
                javax.swing.ImageIcon coinIcon = new javax.swing.ImageIcon("OOPgame/Assets/UI/Coin.png");
                java.awt.Image image = coinIcon.getImage();
                java.awt.Image newimg = image.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH); 
                coinIcon = new javax.swing.ImageIcon(newimg);
                setTitleIcon(coinIcon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        repaint();
    }
}