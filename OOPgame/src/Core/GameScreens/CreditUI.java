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
    JPanel maskPanel;
    int yOffset;
    private final int RESET_POSITION_FLAG = 9999;

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
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        bgCanvas.add(title);
        creditPanel = new JPanel();
        creditPanel.setLayout(new BoxLayout(creditPanel, BoxLayout.Y_AXIS));
        creditPanel.setOpaque(false);

        // เพิ่มข้อความเครดิต
        addCredit("DIVIDE AND CONQUER", 40, true, Color.ORANGE);
        addSpace(30);

        addCredit("68070006", 16, true, Color.BLACK);
        addCredit("Gawintep Chiangka", 14, false, Color.GRAY);
        addImage("OOPgame/Assets/ImageForProfile/4Pro.png");
        addSpace(20);

        addCredit("68070062", 16, true, Color.BLACK);
        addCredit("Thana Mingboon", 14, false, Color.GRAY);
        addImage("OOPgame/Assets/ImageForProfile/3Pro.png");
        addSpace(20);

        addCredit("68070106", 16, true, Color.BLACK);
        addCredit("Piyawat Supphaphontakorn", 14, false, Color.GRAY);
        addImage("OOPgame/Assets/ImageForProfile/2Pro.png");
        addSpace(20);

        addCredit("68070111", 16, true, Color.BLACK);
        addCredit("Pongsapak Boonsonthi", 14, false, Color.GRAY);
        addImage("OOPgame/Assets/ImageForProfile/6Pro.png");
        addSpace(20);

        addCredit("68070143", 16, true, Color.BLACK);
        addCredit("Puri Ngadeesanguannam", 14, false, Color.GRAY);
        addImage("OOPgame/Assets/ImageForProfile/1Pro.png");
        addSpace(20);

        addCredit("68070145", 16, true, Color.BLACK);
        addCredit("Purin Leuprasert", 14, false, Color.GRAY);
        addImage("OOPgame/Assets/ImageForProfile/5Pro.png");
        addSpace(20);

        addCredit("68070149", 16, true, Color.BLACK);
        addCredit("Muninthon Donliken", 14, false, Color.GRAY);
        addImage("OOPgame/Assets/ImageForProfile/8Pro.png");
        addSpace(20);

        addCredit("68070152", 16, true, Color.BLACK);
        addCredit("Yossakorn Praharnphap", 14, false, Color.GRAY);
        addImage("OOPgame/Assets/ImageForProfile/9Pro.png");
        addSpace(20);

        addCredit("68070204", 16, true, Color.BLACK);
        addCredit("Aphiraks Noppakhrao", 14, false, Color.GRAY);
        addImage("OOPgame/Assets/ImageForProfile/7Pro.png");
        addSpace(20);

        backBtn = UIButtonFactory.createMenuButton("Back", btnNormalImg, btnHoverImg, this);
        backBtn.setBounds(20, 20, 90, 40);
        bgCanvas.add(backBtn);

        maskPanel = new JPanel(null);
        maskPanel.setOpaque(false);
        bgCanvas.add(maskPanel);

        // เอาข้อความไปใส่ในกรอบล่องหนแทน bgCanvas โดยตรง
        maskPanel.add(creditPanel);

        add(bgCanvas, BorderLayout.CENTER);

        startScroll();

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                title.setBounds(0, 0, getWidth(), 100);
                maskPanel.setBounds(50, 100, getWidth() - 100, getHeight() - 140);
            }
        });

        this.addAncestorListener(new javax.swing.event.AncestorListener() {
            @Override
            public void ancestorAdded(javax.swing.event.AncestorEvent event) {

                // ส่งสัญญาณให้ Timer รู้ว่าเพิ่งเปิดจอใหม่ (โยนค่า ให้ไปอยู่ล่าง jpanel ไปให้)
                yOffset = RESET_POSITION_FLAG;

                if (scrollTimer != null && !scrollTimer.isRunning()) {
                    scrollTimer.start();
                }
            }

            @Override
            public void ancestorRemoved(javax.swing.event.AncestorEvent event) {
                if (scrollTimer != null) scrollTimer.stop();
            }

            @Override
            public void ancestorMoved(javax.swing.event.AncestorEvent event) {}
        });

    }
    private void addCredit(String text, int size, boolean bold, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(label.getFont().deriveFont(bold ? Font.BOLD : Font.PLAIN, (float) size));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        creditPanel.add(label);
    }
    private void addSpace(int height) {
        creditPanel.add(Box.createRigidArea(new Dimension(0, height)));
    }

    private void startScroll() {
        scrollTimer = new Timer(16, e -> {

            // ถ้าเพิ่งเปิดหน้าจอใหม่ (ได้รับค่า 9999 พิกัด) ให้ดึงเครดิตกลับมาที่ขอบล่างสุดพอดีเป๊ะ
            if (yOffset == RESET_POSITION_FLAG) {
                yOffset = maskPanel.getHeight();
                creditPanel.revalidate(); // บังคับให้กล่องเครดิตประมวลผลขนาดตัวเองให้เสร็จ
            }

            yOffset -= 2; // สั่งเลื่อนขึ้น เอาไว้บังคับความเร็วนะจ้ะ

            Dimension size = creditPanel.getPreferredSize();
            int maskWidth = maskPanel.getWidth() > 0 ? maskPanel.getWidth() : getWidth();
            int centerX = (maskWidth - size.width) / 2;

            creditPanel.setBounds(centerX, yOffset, size.width, size.height);

            if (yOffset < -size.height) {
                yOffset = maskPanel.getHeight();
            }

            repaint();
        });
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backBtn) {
            Screen.ChangeScreen(ZhuzheeGame.MAIN_MENU);
        }
        AudioManager.getInstance().playSound("click");
    }

    private void addImage(String imagePath) {
        try {
            ImageIcon originalIcon = new ImageIcon(imagePath);
            if (originalIcon.getIconWidth() > 0) {
                int targetWidth = 400;
                int targetHeight = (originalIcon.getIconHeight() * targetWidth) / originalIcon.getIconWidth();

                Image scaledImage = originalIcon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));

                imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                creditPanel.add(imageLabel);
            }
        } catch (Exception e) {
            System.err.println("หาภาพเครดิตไม่เจอ: " + imagePath);
        }
    }
}
