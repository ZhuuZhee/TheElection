package Core.GameScreens;

import Core.ZhuzheeGame;
import Dummy.Tester;
import ZhuzheeEngine.Audios.AudioManager;
import ZhuzheeEngine.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends Screen implements ActionListener {
    JButton startBtn;
    JButton optionBtn;
    JButton exitBtn;
    public MainMenu() {
        setLayout(new BorderLayout());
        JLabel title = new JLabel("The Election");
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(40,0,20,0));
        add(title, BorderLayout.NORTH);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3,1,10,10));
        startBtn = new JButton("Start Game");
        startBtn.setPreferredSize(new Dimension(120,30));
        startBtn.addActionListener(this);
        optionBtn = new JButton("Option");
        optionBtn.setPreferredSize(new Dimension(120,30));
        optionBtn.addActionListener(this);
        exitBtn = new JButton("Exit");
        exitBtn.setPreferredSize(new Dimension(120,30));
        exitBtn.addActionListener(this);
        buttonPanel.add(startBtn);
        buttonPanel.add(optionBtn);
        buttonPanel.add(exitBtn);
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.add(buttonPanel);
        add(wrapper, BorderLayout.CENTER);

        // audio test
        AudioManager.getInstance().loadSound("Kuy","guntrum.WAV");
        AudioManager.getInstance().playLoop("Kuy");
    }
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == startBtn){
//            JOptionPane.showMessageDialog(this,"Game Started!");
            Screen.ChangeScreen(ZhuzheeGame.MAIN_SCENE);
        }
        if(e.getSource() == optionBtn){
//            JOptionPane.showMessageDialog(this,"Settings Menu");
            Tester.audioManagerTester.setVisible(true);
        }
        if(e.getSource() == exitBtn){
            System.exit(0);
        }
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

