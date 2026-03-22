/**
 * @Xynezter 9/3/2026 18:50
 */
package Core.Cards;

import Core.ZhuzheeGame;
import Dummy.Maps.City;
import ZhuzheeEngine.Scene.GameObject;
import java.awt.*;

public class CardSlot extends GameObject {
    private static final int Z_INDEX_BACKGROUND = -1;
    private static final float[] DASH_PATTERN = {5.0f}; // ความห่างของเส้นประ
    private static final float STROKE_DASHED_WIDTH = 2.0f; // ความหนาเส้นประ
    private static final float STROKE_NORMAL_WIDTH = 1.0f; // ความหนาเส้นปกติ (สำหรับข้อความ)
    private static final Color SLOT_COLOR = Color.GRAY; // สีของช่อง
    private static final String SLOT_TEXT = "Drop Here";
    // เพิ่ม Attributes city เพื่อเอาไว้อางอิง เมือง
    private final City city;
    // setup Constructor รับค่า city มาตอนสร้าง slot
    public CardSlot(int x, int y, int width, int height, City city) {
        super(x, y, width, height, ZhuzheeGame.MAIN_SCENE);
        this.setZIndex(Z_INDEX_BACKGROUND);
        this.city = city;
    }

    public City getCity() {
        return city;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setStroke(new BasicStroke(
            STROKE_DASHED_WIDTH,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            10.0f,
            DASH_PATTERN,
            0.0f
        ));
        g2d.setColor(SLOT_COLOR);
        g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1); // Draw local at 0,0
        g2d.setStroke(new BasicStroke(STROKE_NORMAL_WIDTH));
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(SLOT_TEXT)) / 2;
        int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

        g2d.drawString(SLOT_TEXT, textX, textY);
    }
}
