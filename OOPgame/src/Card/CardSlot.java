package Card;

import Scene2D.GameObject;

import java.awt.*;

public class CardSlot extends GameObject {
    private static final float Z_INDEX_BACKGROUND = -1f;
    private static final float[] DASH_PATTERN = {5.0f}; // ความห่างของเส้นประ
    private static final float STROKE_DASHED_WIDTH = 2.0f; // ความหนาเส้นประ
    private static final float STROKE_NORMAL_WIDTH = 1.0f; // ความหนาเส้นปกติ (สำหรับข้อความ)
    private static final Color SLOT_COLOR = Color.GRAY; // สีของช่อง
    private static final String SLOT_TEXT = "Drop Here";
    public CardSlot(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.setzIndex(Z_INDEX_BACKGROUND);
    }

    @Override
    public void draw(Graphics g) {
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
        g2d.drawRect(position.x, position.y, size.x, size.y);
        g2d.setStroke(new BasicStroke(STROKE_NORMAL_WIDTH));
        FontMetrics fm = g2d.getFontMetrics();
        int textX = position.x + (size.x - fm.stringWidth(SLOT_TEXT)) / 2;
        int textY = position.y + (size.y - fm.getHeight()) / 2 + fm.getAscent();

        g2d.drawString(SLOT_TEXT, textX, textY);
    }
}
