package Core.Cards;

import javax.swing.*;
import java.awt.*;

public class CardStatTooltip extends JPanel {
    private String name;
    private int eco, fac, env;

    public CardStatTooltip(String name, int eco, int fac, int env) {
        this.name = name;
        this.eco = eco;
        this.fac = fac;
        this.env = env;

        // ตั้งค่าขนาดหน้าต่าง Tooltip
        setPreferredSize(new Dimension(200, 120));
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // วาดพื้นหลังขาวโปร่งแสง
        g2d.setColor(new Color(255, 255, 255, 240));
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

        // วาดเส้นขอบ
        g2d.setColor(new Color(200, 200, 200));
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

        // วาดข้อมูล (เลียนแบบ Font ในรูป)
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2d.drawString(name, 15, 25);

        g2d.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g2d.setColor(new Color(80, 80, 80));
        g2d.drawString("Economic:", 15, 55);
        g2d.drawString("Facility:", 15, 80);
        g2d.drawString("Environment:", 15, 105);

        // วาดตัวเลขสีน้ำเงิน
        g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2d.setColor(new Color(0, 102, 204));
        g2d.drawString(String.valueOf(eco), 160, 55);
        g2d.drawString(String.valueOf(fac), 160, 80);
        g2d.drawString(String.valueOf(env), 160, 105);

        g2d.dispose();
    }
}