package Core.UI;

import Core.Player.Player;
import Core.ZhuzheeGame;
import ZhuzheeEngine.Scene.Scene2D;

public class PlayerCoinUI extends CardHolderUI {

    private Player localPlayer;

    public PlayerCoinUI(Scene2D scene) {
        super(scene);

        if (ZhuzheeGame.CLIENT != null) {
            this.localPlayer = ZhuzheeGame.CLIENT.getLocalPlayer();
        } else {
            this.localPlayer = Dummy.Tester.dummyPlayer;
        }

        // ตั้งให้อยู่มุมขวาบน
        setAnchorTop(false);
        setAnchorRight(true);
        setAnchorLeft(false);

        // เว้นระยะขอบ (Top, Right, Bottom, Left)
        setMargins(0, 16, 0, 250);

        setPanelSize(180, 30);
        setMaxCard(0);

        updateCoinDisplay();

    }

    public void updateCoinDisplay() {
        if (localPlayer != null) {
            setSetLabel("Your Money: $ " + localPlayer.getCoin());
        }
        repaint();
    }
}