package Core.GameScreens;

import Core.ZhuzheeGame;
import ZhuzheeEngine.Audios.AudioManager;
import ZhuzheeEngine.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import ZhuzheeEngine.Scene.NineSliceCanvas;
import ZhuzheeEngine.Scene.NineSliceButton;

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
        bgCanvas = new NineSliceCanvas(null, bgImage, 25, 25, 25, 25) {
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
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));

        // สมมติระยะตัดขอบปุ่มเป็น 10 พิกเซล
        startBtn = new NineSliceButton("Start Game", btnNormalImg, 6, 6, 6, 6);
        startBtn.setPreferredSize(new Dimension(120, 30));// เอาไว้กำหนดขนาดของปุ่ม
        startBtn.addActionListener(this);
        HoverButton(startBtn);

        optionBtn = new NineSliceButton("Option", btnNormalImg, 6, 6, 6, 6);
        optionBtn.setPreferredSize(new Dimension(120, 30));// เอาไว้กำหนดขนาดของปุ่ม
        optionBtn.addActionListener(this);
        HoverButton(optionBtn);

        exitBtn = new NineSliceButton("Exit", btnNormalImg, 6, 6, 6, 6);
        exitBtn.setPreferredSize(new Dimension(120, 30));// เอาไว้กำหนดขนาดของปุ่ม
        exitBtn.addActionListener(this);
        HoverButton(exitBtn);

        buttonPanel.add(startBtn);
        buttonPanel.add(optionBtn);
        buttonPanel.add(exitBtn);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false); // ทำให้ wrapper โปร่งใส
        wrapper.add(buttonPanel);

        bgCanvas.add(wrapper, BorderLayout.CENTER); // เพิ่มปุ่มลงใน bgCanvas ตรงกลาง

        // นำ bgCanvas เพิ่มลงในหน้าจอหลัก
        add(bgCanvas, BorderLayout.CENTER);

        // audio test
        // AudioManager.getInstance().loadSound("Kuy","guntrum.WAV");
        // AudioManager.getInstance().playLoop("Kuy");

        System.out.println("Main Menu is Created");
    }

    private void HoverButton(NineSliceButton b) {
        // b.setFocusPainted(false); // ปิดเส้นกรอบ focus ของปุ่ม
        // (เส้นที่ขึ้นตอนปุ่มถูกเลือก)
        // เพิ่มตัวดักจับ event ของเมาส์ให้กับปุ่ม
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            // ทำงานเมื่อเมาส์เคลื่อนเข้ามาอยู่บนปุ่ม
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                b.setSourceImage(btnHoverImg); // เปลี่ยนเป็นรูป Hover
//                b.setForeground(Color.GREEN);// เปลี่ยนสีตัวอักษรบนปุ่ม
            }

            // ทำงานเมื่อเมาส์ออกจากพื้นที่ของปุ่ม
            public void mouseExited(java.awt.event.MouseEvent evt) {
                b.setSourceImage(btnNormalImg); // กลับมาเป็นรูปดั้งเดิม
                //b.setForeground(Color.BLACK);// เปลี่ยนสีตัวอักษรบนปุ่ม
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startBtn) {
            // JOptionPane.showMessageDialog(this);
            Screen.ChangeScreen(ZhuzheeGame.MAIN_SCENE);
        }
        if (e.getSource() == optionBtn) {
            // JOptionPane.showMessageDialog(this);
            // Tester.audioManagerTester.setVisible(true);
            Screen.ChangeScreen(ZhuzheeGame.OPTION_MENU);
        }
        if (e.getSource() == exitBtn) {
            System.exit(0);
        }
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

    // JFrame frame = new JFrame();
    // MainMenu menu = new MainMenu();
    // frame.setTitle("The Election");
    // frame.add(menu);
    // frame.setSize(800,600);
    // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // frame.setLocationRelativeTo(null);
    // frame.setVisible(true);
}
