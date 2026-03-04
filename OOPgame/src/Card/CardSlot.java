package Card;

import javax.swing.*;
import java.awt.*;

public class CardSlot extends JPanel {

    public CardSlot(int x, int y, int width, int height) {
        setBounds(x, y, width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        float[] dash = {5.0f};
        g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
        g2d.setColor(Color.GRAY);

        g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        g2d.setStroke(new BasicStroke(1.0f));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "Drop Here";
        int textX = (getWidth() - fm.stringWidth(text)) / 2;
        int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(text, textX, textY);
    }
}
