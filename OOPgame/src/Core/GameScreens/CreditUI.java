package Core.GameScreens;

import Core.ZhuzheeGame;
import ZhuzheeEngine.Audios.AudioManager;
import ZhuzheeEngine.Screen;
import ZhuzheeEngine.Scene.NineSliceCanvas;
import ZhuzheeEngine.Scene.NineSliceButton;
import Core.UI.UIButtonFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class CreditUI extends Screen implements ActionListener{

    NineSliceButton backBtn;
    JPanel creditPanel;
    Timer scrollTimer;
    int yOffset;
    int startOffset = 80;

    BufferedImage bgImage;
    BufferedImage btnNormalImg;
    BufferedImage btnHoverImg;
    NineSliceCanvas bgCanvas;

    public CreditUI() {
        setLayout(new BorderLayout());
        try {
            bgImage = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/test.png"));
            btnNormalImg = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/btn_normal.png"));
            btnHoverImg = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/btn_hover.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        bgCanvas = new NineSliceCanvas(bgImage, 25, 25, 25, 25) {};
        bgCanvas.setLayout(null);

        JLabel title = new JLabel("Credit");
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        bgCanvas.add(title, BorderLayout.NORTH);
        creditPanel = new JPanel();
        creditPanel.setLayout(new BoxLayout(creditPanel, BoxLayout.Y_AXIS));
        creditPanel.setOpaque(false);

        // เพิ่มข้อความเครดิต
        addCredit("The Election", 40, true, Color.ORANGE);
        addSpace(30);

        addCredit("68070006", 16, true, Color.BLACK);
        addCredit("Gawintep Chiangka", 14, false, Color.LIGHT_GRAY);
        addSpace(20);

        addCredit("68070062", 16, true, Color.BLACK);
        addCredit("Thana Mingboon", 14, false, Color.LIGHT_GRAY);
        addSpace(20);

        addCredit("68070106", 16, true, Color.BLACK);
        addCredit("Piyawat Supphaphontakorn", 14, false, Color.LIGHT_GRAY);
        addSpace(20);

        addCredit("68070111", 16, true, Color.BLACK);
        addCredit("Pongsapak Boonsonthi", 14, false, Color.LIGHT_GRAY);

        addCredit("68070143", 16, true, Color.BLACK);
        addCredit("Puri Ngadeesanguannam", 14, false, Color.LIGHT_GRAY);

        addCredit("68070145", 16, true, Color.BLACK);
        addCredit("Purin Leuprasert", 14, false, Color.LIGHT_GRAY);

        addCredit("68070149", 16, true, Color.BLACK);
        addCredit("Muninthon Donliken", 14, false, Color.LIGHT_GRAY);

        addCredit("68070152", 16, true, Color.BLACK);
        addCredit("Yossakorn Praharnphap", 14, false, Color.LIGHT_GRAY);

        addCredit("68070204", 16, true, Color.BLACK);
        addCredit("Aphiraks Noppakhrao", 14, false, Color.LIGHT_GRAY);

        backBtn = UIButtonFactory.createMenuButton("Back", btnNormalImg, btnHoverImg, this);
        backBtn.setBounds(20, 20, 90, 40);
        bgCanvas.add(backBtn);

        creditPanel.setBounds(200, yOffset, 400, 800);
        bgCanvas.add(creditPanel);

        add(bgCanvas, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            yOffset = getHeight() + startOffset;
        });

        startScroll();

    }
    private void addCredit(String text, int size, boolean bold, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font("Arial", bold ? Font.BOLD : Font.PLAIN, size));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        creditPanel.add(label);
    }
    private void addSpace(int height) {
        creditPanel.add(Box.createRigidArea(new Dimension(0, height)));
    }

    private void startScroll() {
        scrollTimer = new Timer(16, e -> {
            yOffset -= 1;

            Dimension size = creditPanel.getPreferredSize();
            int centerX = (getWidth() - size.width) / 2;

            creditPanel.setBounds(centerX, yOffset, size.width, size.height);

            if (yOffset < -size.height) {
                yOffset = getHeight() + startOffset;
            }
        });
        scrollTimer.start();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backBtn) {
            Screen.ChangeScreen(ZhuzheeGame.MAIN_MENU);
        }
        AudioManager.getInstance().playSound("click");
    }
}
