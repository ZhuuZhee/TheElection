package Core.UI;

import ZhuzheeEngine.Scene.Canvas;
import ZhuzheeEngine.Scene.Scene2D;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * UINotificationToast สำหรับแสดงการแจ้งเตือนสั้นๆ (Toast) บนหน้าจอ
 * จะแสดงอยู่บริเวณกึ่งกลางด้านบนและหายไปเองหลังจากเวลาที่กำหนด
 */
public class UINotificationToast extends Canvas {
    private final JLabel messageLabel;
    private Timer hideTimer;
    private static UINotificationToast instance;
    private final Scene2D scene;

    public UINotificationToast(Scene2D scene) {
        super(scene);
        this.scene = scene;
        instance = this;
        
        // ตั้งค่า Z-Index ให้อยู่เหนือ UI ปกติและเหนือของที่ลากอยู่
        setZIndex(Scene2D.Layer.DRAGGED + 10);

        // กำหนดขนาดและตำแหน่งเริ่มต้น
        setPanelSize(400, 50);
        setAnchors(0, 1); // กึ่งกลางบน (0 ใช้ screenPos, 1 คือ Top)
        setScreenPos(2, 0); // x = width / 2
        setMargins(0, 0, 50, 0); // ห่างจากขอบบน 50px

        setLayout(new BorderLayout());
        setOpaque(false);

        // สร้าง Label สำหรับข้อความ
        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        messageLabel.setBorder(new EmptyBorder(5, 20, 5, 20));
        add(messageLabel, BorderLayout.CENTER);

        setVisible(false);
    }

    /**
     * แสดงข้อความแจ้งเตือน
     * @param message ข้อความที่จะแสดง
     * @param durationMs ระยะเวลาที่แสดง (มิลลิวินาที)
     */
    public void show(String message, int durationMs) {
        messageLabel.setText(message);
        
        // ปรับขนาดแผงตามความกว้างของข้อความ
        FontMetrics metrics = messageLabel.getFontMetrics(messageLabel.getFont());
        int textWidth = metrics.stringWidth(message) + 60;
        setPanelSize(Math.max(300, textWidth), 50);
        
        setVisible(true);
        
        // ถ้ามี Timer เดิมอยู่ให้หยุดก่อน
        if (hideTimer != null && hideTimer.isRunning()) {
            hideTimer.stop();
        }

        // สร้าง Timer ใหม่เพื่อซ่อน
        hideTimer = new Timer(durationMs, _ -> setVisible(false));
        hideTimer.setRepeats(false);
        hideTimer.start();
        
        // บังคับให้จัดตำแหน่งใหม่
        onResize(scene.getWidth(), scene.getHeight());
    }

    /**
     * เรียกใช้งาน Toast จากที่ไหนก็ได้ (ถ้ามีการสร้าง instance ไว้ใน Scene ปัจจุบัน)
     * @param message ข้อความที่ต้องการแจ้งเตือน
     */
    public static void showNotification(String message) {
        showNotification(message, 5000);
    }

    /**
     * เรียกใช้งาน Toast จากที่ไหนก็ได้ พร้อมระบุระยะเวลา
     * @param message ข้อความที่ต้องการแจ้งเตือน
     * @param durationMs ระยะเวลาที่แสดง (มิลลิวินาที)
     */
    // กำหนดระยะเวลาแสดงผลของ Toast ได้
    public static void showNotification(String message, int durationMs) {
        if (instance != null) {
            instance.show(message, durationMs);
        } else {
            System.out.println("[Toast] " + message);
        }
    }

    @Override
    protected void onResize(int width, int height) {
        super.onResize(width, height);
        
        // ปรับตำแหน่ง X ให้กึ่งกลางหน้าจอแนวนอนเสมอ
        int centerX = (width - this.getWidth()) / 2;
        this.setLocation(centerX, this.getY());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // วาดพื้นหลังโค้งมน สีดำโปร่งแสง
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        
        // วาดเส้นขอบสีขาวจางๆ
        g2.setColor(new Color(255, 255, 255, 100));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
        
        g2.dispose();
        super.paintComponent(g);
    }
}
