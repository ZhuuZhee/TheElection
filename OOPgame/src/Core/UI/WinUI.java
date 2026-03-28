package Core.UI;

import Core.ZhuzheeGame;
import ZhuzheeEngine.Audios.AudioManager;
import ZhuzheeEngine.Scene.Canvas;
import ZhuzheeEngine.Scene.Scene2D;
import ZhuzheeEngine.Screen;

import javax.swing.*;
import java.awt.*;

/**
 * UI สำหรับแสดงเมื่อผู้เล่นชนะการเลือกตั้ง (เป็นผู้รอดชีวิตคนสุดท้าย)
 */
public class WinUI extends Canvas {
    public WinUI(Scene2D scene) {
        super(scene);
        
        // ตั้งค่า Z-Index ให้สูงเพื่อให้แสดงทับทุกอย่างบนหน้าจอ
        setZIndex(3000);
        
        setPanelSize(600, 400);
        setAnchors(0, 0); // กึ่งกลางหน้าจอ
        
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // พื้นหลังโปร่งแสงสีเข้ม
                g2d.setColor(new Color(0, 0, 0, 220));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                
                // วาดขอบสีทองเรืองแสง
                g2d.setColor(new Color(255, 215, 0));
                g2d.setStroke(new BasicStroke(8));
                g2d.drawRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 30, 30);
                
                g2d.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));

        JLabel winLabel = new JLabel("VICTORY!");
        winLabel.setFont(new Font("SansSerif", Font.BOLD, 80));
        winLabel.setForeground(new Color(255, 215, 0)); // สีทอง
        winLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("Congratulations! You have won the election.");
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        subLabel.setForeground(Color.WHITE);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton backBtn = new JButton("Back to Waiting Room");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> {
            AudioManager.getInstance().playSound("click");
            // ย้ายผู้เล่นที่ชนะกลับไปยังหน้า Waiting Room
            Screen.ChangeScreen(ZhuzheeGame.WAITING_ROOM_MENU);
        });

        mainPanel.add(winLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(subLabel);
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(backBtn);

        add(mainPanel, BorderLayout.CENTER);
        
        setVisible(true);
    }
}