package Core.GameScreens;

import Core.ZhuzheeGame;
import ZhuzheeEngine.Audios.AudioManager;
import ZhuzheeEngine.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import ZhuzheeEngine.Scene.NineSliceCanvas;
import ZhuzheeEngine.Scene.NineSliceButton;
import Core.UI.UIButtonFactory;

public class MainMenu extends Screen implements ActionListener {

    NineSliceButton startBtn;
    NineSliceButton optionBtn;
    NineSliceButton creditBtn;
    NineSliceButton exitBtn;

    BufferedImage bgImage;
    BufferedImage btnNormalImg;
    BufferedImage btnHoverImg;
    NineSliceCanvas bgCanvas;

    public MainMenu() {
        setLayout(new BorderLayout());

        // คำแนะนำ: โหลดรูปภาพ 9-slice ของคุณตรงนี้ (ใช้ try-catch เพื่อป้องกัน error)
        try {
            // ตัวอย่างการโหลดรูป:
            bgImage = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/test.png"));
            // TODO: ใส่ที่อยู่รูปปุ่มของคุณ (ถ้ายังไม่มี
            // ให้หาหรือสร้างรูปปุ่มมาใส่แทนที่อยู่ตรงนี้)
            btnNormalImg = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/btn_normal.png"));
            btnHoverImg = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/btn_hover.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // สร้าง NineSliceCanvas (เนื่องจากไม่ได้ใช้ใน Scene2D จึงใส่ null
        // ได้เลยหลังจากอัปเดตระบบแล้ว)
        // อย่าลืมกำหนดระยะตัดขอบ (ซ้าย, ขวา, บน, ล่าง) ตามความเหมาะสมของรูปภาพ
        bgCanvas = new NineSliceCanvas(bgImage, 25, 25, 25, 25) {
        };
        bgCanvas.setLayout(new BorderLayout());

        JPanel topTextGroup = new JPanel(new BorderLayout());
        topTextGroup.setOpaque(false);
        topTextGroup.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));

        JLabel title = new JLabel("The Election");
        title.setFont(title.getFont().deriveFont(50f));
        title.setForeground(Color.BLACK);
        title.setHorizontalAlignment(JLabel.CENTER);

        JLabel bankImageLabel = new JLabel();
        bankImageLabel.setHorizontalAlignment(JLabel.CENTER);
        bankImageLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        try {
            ImageIcon originalIcon = new ImageIcon("OOPgame/Assets/ImageForMapBackground/realbank.jpg");
            if (originalIcon.getIconWidth() > 0) {
                int scaledWidth = 400;
                int scaledHeight = (originalIcon.getIconHeight() * scaledWidth) / originalIcon.getIconWidth();
                Image scaledImage = originalIcon.getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                bankImageLabel.setIcon(new ImageIcon(scaledImage));
            }
        } catch (Exception e) {
            System.err.println("Load bank image error: " + e.getMessage());
        }

        topTextGroup.add(title, BorderLayout.NORTH);
        topTextGroup.add(bankImageLabel, BorderLayout.SOUTH);
        bgCanvas.add(topTextGroup, BorderLayout.NORTH);

        startBtn = UIButtonFactory.createMenuButton("START GAME", btnNormalImg, btnHoverImg, this);
        optionBtn = UIButtonFactory.createMenuButton("OPTIONS", btnNormalImg, btnHoverImg, this);
        creditBtn = UIButtonFactory.createMenuButton("CREDITS", btnNormalImg, btnHoverImg, this);
        exitBtn = UIButtonFactory.createMenuButton("EXIT", btnNormalImg, btnHoverImg, this);

        MouseAdapter mouseHover = ZhuzheeGame.MOUSE_HOVER_SFX;
        startBtn.addMouseListener(mouseHover);
        optionBtn.addMouseListener(mouseHover);
        creditBtn.addMouseListener(mouseHover);
        exitBtn.addMouseListener(mouseHover);

        JPanel buttonBox = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonBox.setOpaque(false);
        buttonBox.add(startBtn);
        buttonBox.add(optionBtn);
        buttonBox.add(creditBtn);
        buttonBox.add(exitBtn);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weighty = 1.0;

        gbc.insets = new Insets(30, 0, 0, 0);

        wrapper.add(buttonBox, gbc);
        bgCanvas.add(wrapper, BorderLayout.CENTER);

        add(bgCanvas, BorderLayout.CENTER);

        AudioManager.getInstance().loadSound("click", "click.WAV");
        AudioManager.getInstance().loadSound("hover", "hover.WAV");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startBtn) {
            Screen.ChangeScreen(ZhuzheeGame.LOBBY_MENU);
        }
        if (e.getSource() == optionBtn) {
            Screen.ChangeScreen(ZhuzheeGame.OPTION_MENU);
        }
        if (e.getSource() == creditBtn) {
            Screen.ChangeScreen(ZhuzheeGame.CREDIT_UI);
        }
        if (e.getSource() == exitBtn) {
            AudioManager.getInstance().playSound("click");
            System.exit(0);
        }
        AudioManager.getInstance().playSound("click");
    }

//    private void addImage(String imagePath) {
//        try {
//            ImageIcon originalIcon = new ImageIcon(imagePath);
//            if (originalIcon.getIconWidth() > 0) {
//                int targetWidth = 400;
//                int targetHeight = (originalIcon.getIconHeight() * targetWidth) / originalIcon.getIconWidth();
//
//                Image scaledImage = originalIcon.getImage().getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
//                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
//
//                imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//                creditPanel.add(imageLabel);
//            }
//        } catch (Exception e) {
//            System.err.println("หาภาพเครดิตไม่เจอ: " + imagePath);
//        }
//    }
}
