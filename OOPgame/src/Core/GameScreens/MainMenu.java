package Core.GameScreens;

import Core.ZhuzheeGame;
import Dummy.Taro;
import Dummy.Tester;
import ZhuzheeEngine.Audios.AudioManager;
import ZhuzheeEngine.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends Screen implements ActionListener {

    GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

    JButton startBtn;
    JButton optionBtn;
    JButton creditBtn;
    JButton exitBtn;

    public MainMenu() {
        setLayout(new BorderLayout());
//        นี่คือส่วนชข้อความชื่อเกม
        JLabel title = new JLabel("The Election");
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(40,0,20,0));
        add(title, BorderLayout.NORTH);
//        นี่คือส่วนปุ่มต่างๆ
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4,1,10,10));
        startBtn = new JButton("Start Game");
        startBtn.setPreferredSize(new Dimension(120,30));//เอาไว้กำหนดขนาดของปุ่ม
        startBtn.addActionListener(this);
        HoverButton(startBtn);
        optionBtn = new JButton("Option");
        optionBtn.setPreferredSize(new Dimension(120,30));//เอาไว้กำหนดขนาดของปุ่ม
        optionBtn.addActionListener(this);
        HoverButton(optionBtn);
        creditBtn = new JButton("Credit");
        creditBtn.setPreferredSize(new Dimension(120,30));//เอาไว้กำหนดขนาดของปุ่ม
        creditBtn.addActionListener(this);
        HoverButton(creditBtn);
        exitBtn = new JButton("Exit");
        exitBtn.setPreferredSize(new Dimension(120,30));//เอาไว้กำหนดขนาดของปุ่ม
        exitBtn.addActionListener(this);
        HoverButton(exitBtn);

        buttonPanel.add(startBtn);
        buttonPanel.add(optionBtn);
        buttonPanel.add(creditBtn);
        buttonPanel.add(exitBtn);
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.add(buttonPanel);

        add(wrapper, BorderLayout.CENTER);

        // audio test
        AudioManager.getInstance().loadSound("Kuy","guntrum.WAV");
        AudioManager.getInstance().playLoop("Kuy");

        System.out.println("Main Menu is Created");
    }
    private void HoverButton(JButton b) {
        b.setFocusPainted(false);// ปิดเส้นกรอบ focus ของปุ่ม (เส้นที่ขึ้นตอนปุ่มถูกเลือก)
        // เพิ่มตัวดักจับ event ของเมาส์ให้กับปุ่ม
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            // ทำงานเมื่อเมาส์เคลื่อนเข้ามาอยู่บนปุ่ม
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                b.setBackground(Color.GRAY);// เปลี่ยนสีพื้นหลังของปุ่ม
                b.setForeground(Color.GREEN);// เปลี่ยนสีตัวอักษรบนปุ่ม
            }
            // ทำงานเมื่อเมาส์ออกจากพื้นที่ของปุ่ม
            public void mouseExited(java.awt.event.MouseEvent evt) {
                b.setBackground(Color.WHITE);// เปลี่ยนสีพื้นหลังของปุ่ม
                b.setForeground(Color.BLACK);// เปลี่ยนสีตัวอักษรบนปุ่ม
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == startBtn){
//            JOptionPane.showMessageDialog(this);
            Screen.ChangeScreen(ZhuzheeGame.MAIN_SCENE);
        }
        if(e.getSource() == optionBtn){
//            JOptionPane.showMessageDialog(this);
//            Tester.audioManagerTester.setVisible(true);
            Taro.option.setVisible(true);
        }
        if(e.getSource() == exitBtn){
            System.exit(0);
        }
    }
    private void enableFullscreen() {
        dispose();
//        device.setFullScreenWindow();
        setVisible(true);
    }

    private void disableFullscreen() {
        device.setFullScreenWindow(null);
        dispose();
        setVisible(true);
    }

//        JFrame frame = new JFrame();
//        MainMenu menu = new MainMenu();
//        frame.setTitle("The Election");
//        frame.add(menu);
//        frame.setSize(800,600);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);
}

