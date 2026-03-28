package Core.UI;

import Core.ZhuzheeGame;
import ZhuzheeEngine.Debug.GameLogger;
import ZhuzheeEngine.Scene.Canvas;
import ZhuzheeEngine.Scene.Scene2D;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EndTurnButtonUI extends Canvas {

    private JButton endTurnBtn;

    public EndTurnButtonUI(Scene2D scene) {
        super(scene);

        setPanelSize(180, 50);

        setAnchorTop(false);
        setAnchorRight(true);
        setAnchorLeft(false);

        setMargins(0, 16, 0, 290);

        setOpaque(false);
        setLayout(new BorderLayout());

        endTurnBtn = new JButton("END TURN");
        endTurnBtn.setFont(endTurnBtn.getFont().deriveFont(20f));
        endTurnBtn.setForeground(Color.WHITE);

        endTurnBtn.setBackground(new Color(50, 50, 50));
        endTurnBtn.setBorder(new LineBorder(new Color(200, 180, 150), 2));

        endTurnBtn.setFocusPainted(false);
        endTurnBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // ==========================================
        // Hover Effect
        // ==========================================

        endTurnBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (endTurnBtn.isEnabled()) {
                    ZhuzheeGame.MOUSE_HOVER_SFX.mouseEntered(e); // เล่นเสียงของเกม
                    endTurnBtn.setBackground(new Color(70, 70, 70)); // สว่างขึ้นตอนเมาส์ชี้
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (endTurnBtn.isEnabled()) {
                    endTurnBtn.setBackground(new Color(50, 50, 50)); // กลับสีเดิม
                }
            }
        });

        endTurnBtn.addActionListener(e -> {
            // กันสแปมปุ่ม
            endTurnBtn.setEnabled(false);
            endTurnBtn.setBackground(new Color(30, 30, 30));

            if (ZhuzheeGame.CLIENT != null) {
                GameLogger.logInfo("Ending Turn...");
                // เรียกใช้คำสั่งเดียวกับใน Tester เดิม
                ZhuzheeGame.CLIENT.endTurn();
            }

            // หน่วงเวลาเปิดปุ่มใหม่
            Timer reenableTimer = new Timer(1500, event -> {
                endTurnBtn.setEnabled(true);
                endTurnBtn.setBackground(new Color(50, 50, 50));
            });
            reenableTimer.setRepeats(false);
            reenableTimer.start();
        });

        // แปะปุ่มลงใน Canvas ของเรา
        this.add(endTurnBtn, BorderLayout.CENTER);
    }
}