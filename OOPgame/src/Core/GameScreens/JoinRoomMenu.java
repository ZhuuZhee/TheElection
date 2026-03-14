package Core.GameScreens;

import Core.ZhuzheeGame;
import ZhuzheeEngine.Screen;
import ZhuzheeEngine.Scene.NineSliceCanvas;
import ZhuzheeEngine.Scene.NineSliceButton;
import Core.UI.UIButtonFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class JoinRoomMenu extends Screen implements ActionListener {

    JTextField ipInput;
    JTextField nameInput;
    NineSliceButton connectBtn;
    NineSliceButton backBtn;

    BufferedImage bgImage;
    BufferedImage btnNormalImg;
    BufferedImage btnHoverImg;
    NineSliceCanvas bgCanvas;

    public JoinRoomMenu() {
        setLayout(new BorderLayout());

        try {
            bgImage = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/test.png"));
            btnNormalImg = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/btn_normal.png"));
            btnHoverImg = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/btn_hover.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        bgCanvas = new NineSliceCanvas(bgImage, 25, 25, 25, 25) {};
        bgCanvas.setLayout(new BorderLayout());

        JLabel title = new JLabel("Join Game");
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        bgCanvas.add(title, BorderLayout.NORTH);

        JPanel Panel = new JPanel();
        Panel.setOpaque(false);
        Panel.setLayout(new BoxLayout(Panel, BoxLayout.Y_AXIS));
        
        JLabel nameTitle = new JLabel("Enter Your Name:");
        nameTitle.setFont(new Font("Arial", Font.BOLD, 18));
        nameTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        nameInput = new JTextField();
        nameInput.setMaximumSize(new Dimension(250, 35));
        nameInput.setPreferredSize(new Dimension(250, 35));
        nameInput.setFont(new Font("Arial", Font.PLAIN, 16));
        nameInput.setHorizontalAlignment(JTextField.CENTER);
        nameInput.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel("Enter Host IP:");
        subtitle.setFont(new Font("Arial", Font.BOLD, 18));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        ipInput = new JTextField();
        ipInput.setMaximumSize(new Dimension(250, 35));
        ipInput.setPreferredSize(new Dimension(250, 35));
        ipInput.setFont(new Font("Arial", Font.PLAIN, 16));
        ipInput.setHorizontalAlignment(JTextField.CENTER);
        ipInput.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel btnRow = new JPanel();
        btnRow.setOpaque(false);
        btnRow.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnRow.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        connectBtn = UIButtonFactory.createMenuButton("Connect", btnNormalImg, btnHoverImg, this);
        backBtn = UIButtonFactory.createMenuButton("Back to Lobby", btnNormalImg, btnHoverImg, this);
        
        btnRow.add(connectBtn);
        btnRow.add(backBtn);

        Panel.add(Box.createVerticalGlue());
        Panel.add(nameTitle);
        Panel.add(Box.createRigidArea(new Dimension(0, 10)));
        Panel.add(nameInput);
        Panel.add(subtitle);
        Panel.add(Box.createRigidArea(new Dimension(0, 10)));
        Panel.add(ipInput);
        Panel.add(btnRow);
        Panel.add(Box.createVerticalGlue());

        bgCanvas.add(Panel, BorderLayout.CENTER);
        add(bgCanvas, BorderLayout.CENTER);
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == connectBtn) {
            String targetIp = ipInput.getText();
            String pName = nameInput.getText();
            if(pName.isEmpty()) pName = "Player";

            ZhuzheeGame.CLIENT = new Core.Network.Client.GameClientManager();
            ZhuzheeGame.CLIENT.connect(targetIp, 9999, pName);

            Screen.ChangeScreen(ZhuzheeGame.WAITING_ROOM_MENU);
        }
        else if (e.getSource() == backBtn) {
            Screen.ChangeScreen(ZhuzheeGame.LOBBY_MENU);
        }
    }
}
