package Core.GameScreens;

import Core.ZhuzheeGame;
import ZhuzheeEngine.Audios.AudioManager;
import ZhuzheeEngine.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import ZhuzheeEngine.Scene.NineSliceCanvas;
import ZhuzheeEngine.Scene.NineSliceButton;
import Core.UI.UIButtonFactory;

public class MainMenu extends Screen implements ActionListener {

    GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

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
            bgImage = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/ImageForMapBackground/MainReal.png"));
            // TODO: ใส่ที่อยู่รูปปุ่มของคุณ (ถ้ายังไม่มี
            // ให้หาหรือสร้างรูปปุ่มมาใส่แทนที่อยู่ตรงนี้)
            btnNormalImg = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/btn_normal.png"));
            btnHoverImg = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/btn_hover.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        bgCanvas = new NineSliceCanvas(bgImage, 0, 0, 0, 0) {
        };
        bgCanvas.setLayout(new GridBagLayout());

        JLabel LogoLabel = new JLabel();
        LogoLabel.setHorizontalAlignment(JLabel.CENTER);
        LogoLabel.setBorder(null);

        try {
            ImageIcon originalIcon = new ImageIcon("OOPgame/Assets/ImageForMapBackground/The_Elction_Logo.png");
            if (originalIcon.getIconWidth() > 0) {
                int scaledWidth = 600;
                int scaledHeight = (originalIcon.getIconHeight() * scaledWidth) / originalIcon.getIconWidth();
                Image scaledImage = originalIcon.getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                LogoLabel.setIcon(new ImageIcon(scaledImage));
            }
        } catch (Exception e) {
            System.err.println("Load bank image error: " + e.getMessage());
        }

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
        buttonBox.setPreferredSize(new Dimension(350, 320));
        buttonBox.add(startBtn);
        buttonBox.add(optionBtn);
        buttonBox.add(creditBtn);
        buttonBox.add(exitBtn);

        GridBagConstraints gbcLogo = new GridBagConstraints();
        gbcLogo.gridx = 0;
        gbcLogo.gridy = 0;
        gbcLogo.anchor = GridBagConstraints.PAGE_START;
        gbcLogo.weightx = 1.0;
        gbcLogo.weighty = 0.0;
        gbcLogo.insets = new Insets(100, 0, 0, 0);
        bgCanvas.add(LogoLabel, gbcLogo);

        GridBagConstraints gbcButtons = new GridBagConstraints();
        gbcButtons.gridx = 0;
        gbcButtons.gridy = 1;
        gbcButtons.anchor = GridBagConstraints.CENTER;
        gbcButtons.weightx = 1.0;
        gbcButtons.weighty = 1.0;
        gbcButtons.insets = new Insets(20, 0, 50, 0);
        bgCanvas.add(buttonBox, gbcButtons);

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

    private void enableFullscreen() {
        dispose();
        // device.setFullScreenWindow();
        setVisible(true);
    }

    private void disableFullscreen() {
        device.setFullScreenWindow(null);
        dispose();
        setVisible(true);
    }
}
