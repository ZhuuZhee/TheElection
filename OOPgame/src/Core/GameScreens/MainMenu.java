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

        // นี่คือส่วนชข้อความชื่อเกม
        JLabel title = new JLabel("The Election");
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        bgCanvas.add(title, BorderLayout.NORTH); // เพิ่ม title ลงใน bgCanvas

        // นี่คือส่วนปุ่มต่างๆ
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // ทำให้ปุ่มโปร่งใสเพื่อเห็นพื้นหลัง
        buttonPanel.setLayout(new GridLayout(4, 1, 10, 10));

        // สร้างปุ้มโดยใช้ UIButtonFactory
        startBtn = UIButtonFactory.createMenuButton("Start Game", btnNormalImg, btnHoverImg, this);
        optionBtn = UIButtonFactory.createMenuButton("Options", btnNormalImg, btnHoverImg, this);
        creditBtn = UIButtonFactory.createMenuButton("Credits", btnNormalImg, btnHoverImg, this);
        exitBtn = UIButtonFactory.createMenuButton("Exit", btnNormalImg, btnHoverImg, this);

        MouseAdapter mouseHover = ZhuzheeGame.MOUSE_HOVER_SFX;

        startBtn.addMouseListener(mouseHover);
        optionBtn.addMouseListener(mouseHover);
        creditBtn.addMouseListener(mouseHover);
        exitBtn.addMouseListener(mouseHover);

        buttonPanel.add(startBtn);
        buttonPanel.add(optionBtn);
        buttonPanel.add(creditBtn);
        buttonPanel.add(exitBtn);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false); // ทำให้ wrapper โปร่งใส
        wrapper.add(buttonPanel);

        bgCanvas.add(wrapper, BorderLayout.CENTER); // เพิ่มปุ่มลงใน bgCanvas ตรงกลาง

        // นำ bgCanvas เพิ่มลงในหน้าจอหลัก
        add(bgCanvas, BorderLayout.CENTER);

        // audio test
        AudioManager.getInstance().loadSound("click","click.WAV");
        AudioManager.getInstance().loadSound("hover","hover.WAV");

//        System.out.println("Main Menu is Created");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startBtn) {
            // JOptionPane.showMessageDialog(this);
            Screen.ChangeScreen(ZhuzheeGame.LOBBY_MENU);
        }
        if (e.getSource() == optionBtn) {
            // JOptionPane.showMessageDialog(this);
            // Tester.audioManagerTester.setVisible(true);
            Screen.ChangeScreen(ZhuzheeGame.OPTION_MENU);
        }
        if (e.getSource() == exitBtn) {
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
