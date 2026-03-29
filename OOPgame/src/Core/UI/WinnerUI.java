package Core.UI;

import Core.ZhuzheeGame;
import ZhuzheeEngine.Audios.AudioManager;
import ZhuzheeEngine.Scene.Canvas;
import ZhuzheeEngine.Scene.NineSliceButton;
import ZhuzheeEngine.Scene.Scene2D;
import ZhuzheeEngine.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class WinnerUI extends Canvas {
    public WinnerUI(Scene2D scene) {
        super(scene);

        setZIndex(3000);
        setPanelSize(600, 300);
        setAnchors(0, 0);

        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(40, 35, 10, 240));

        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.YELLOW, 5),
                BorderFactory.createEmptyBorder(50, 40, 50, 40)
        ));

        JLabel winLabel = new JLabel("VICTORY!");
        winLabel.setFont(winLabel.getFont().deriveFont(Font.BOLD, 40f));
        winLabel.setForeground(Color.YELLOW);
        winLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("CONGRATULATIONS!");
        subLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        subLabel.setForeground(Color.WHITE);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("You have won the election.");
        descLabel.setFont(new Font("Monospaced", Font.PLAIN, 18));
        descLabel.setForeground(Color.LIGHT_GRAY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        BufferedImage btnNormalImg = null;
        BufferedImage btnHoverImg = null;
        try {
            btnNormalImg = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/btn_normal.png"));
            btnHoverImg = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/btn_hover.png"));
        } catch (Exception ignored) {}

        JButton backBtn;
        if (btnNormalImg != null && btnHoverImg != null) {
            NineSliceButton nineSliceBtn = UIButtonFactory.createMenuButton(
                    "BACK TO WAITING ROOM",
                    btnNormalImg,
                    btnHoverImg,
                    e -> {
                        if (ZhuzheeGame.CLIENT != null) {
                            ZhuzheeGame.CLIENT.resetClientState();
                        }
                        AudioManager.getInstance().playSound("click");
                        Screen.ChangeScreen(ZhuzheeGame.WAITING_ROOM_MENU);
                    }
            );
            nineSliceBtn.setFont(new Font("Monospaced", Font.BOLD, 20));
            nineSliceBtn.setPreferredSize(new Dimension(320, 55));
            nineSliceBtn.setMaximumSize(new Dimension(320, 55));
            nineSliceBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            backBtn = nineSliceBtn;
        } else {
            JButton basicBtn = new JButton(" BACK TO WAITING ROOM ");
            basicBtn.setFont(new Font("Monospaced", Font.BOLD, 20));
            basicBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            basicBtn.setFocusPainted(false);
            basicBtn.setBackground(Color.WHITE);
            basicBtn.setForeground(Color.BLACK);
            basicBtn.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 3));
            basicBtn.addActionListener(e -> {
                // ล้างค่าสถานะการเล่นก่อนกลับหน้า Waiting Room
                if (ZhuzheeGame.CLIENT != null) {
                    ZhuzheeGame.CLIENT.resetClientState();
                }

                AudioManager.getInstance().playSound("click");
                Screen.ChangeScreen(ZhuzheeGame.WAITING_ROOM_MENU);
            });
            backBtn = basicBtn;
        }

        mainPanel.add(winLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(subLabel);
        mainPanel.add(descLabel);
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(backBtn);

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    @Override
    protected void updateBounds(int w, int h) {
        setBounds((w - 600) / 2, (h - 450) / 2, 600, 300);
    }
}
