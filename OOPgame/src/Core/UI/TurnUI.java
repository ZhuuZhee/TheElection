package Core.UI;

import Core.ZhuzheeGame;
import ZhuzheeEngine.Scene.Scene2D;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TurnUI extends CardHolderUI{
    private static BufferedImage btnHoverImg;
    static {
        try {
            btnHoverImg = ImageIO.read(new File("OOPgame/Assets/UI/btn_hover.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TurnUI(Scene2D scene) {
        super(scene);
        setAnchorTop(true);
        setAnchorLeft(false);
        setAnchorRight(false);
        setMargins(16, 16, 16, 16);
        
        setPanelSize(180, 30);
        
        if (btnHoverImg != null) {
            setNineSliceImage(btnHoverImg, 16, 16, 16, 16);
        } else {
            enableNineSliceBackground(false);
        }

        setMaxCard(0);

        updateTurnDisplay();
    }

    public void updateTurnDisplay() {
        int currentTurn = 1;
        int currentRound = 1;

        if (ZhuzheeGame.CLIENT != null) {
            currentTurn = ZhuzheeGame.CLIENT.getTurnCounter();

            int playerCount = Math.max(1, ZhuzheeGame.CURRENT_PLAYERS.size());

            if (currentTurn > 0) {
                currentRound = ((currentTurn - 1) / playerCount) + 1;
            }
        }

        setSetLabel("Round: " + currentRound + "   |   Turn: " + currentTurn);

        repaint();
    }
    @Override
    protected void onResize(int width, int height) {
        // ให้มันคำนวณระยะขอบบน-ล่าง ตามปกติก่อน
        super.onResize(width, height);

        // คำนวณหาจุดกึ่งกลางของหน้าจอเกมแนวนอน
        int centerX = (width - this.getWidth()) / 2;

        // เซ็ตตำแหน่ง X ให้อยู่ตรงกลาง ส่วน Y ช้ค่าเดิม (ที่ยึดขอบบนไว้)
        this.setLocation(centerX, this.getY());
    }
}
