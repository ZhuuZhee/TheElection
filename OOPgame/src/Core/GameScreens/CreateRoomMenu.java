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
import java.awt.image.BufferedImage;

public class CreateRoomMenu extends Screen implements ActionListener {

    NineSliceButton backBtn;
    NineSliceButton startClientBtn;
    JTextField nameInput;

    BufferedImage bgImage;
    BufferedImage btnNormalImg;
    BufferedImage btnHoverImg;
    NineSliceCanvas bgCanvas;

    public CreateRoomMenu() {
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

        JLabel title = new JLabel("Creating Room");
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
        
        JPanel btnRow = new JPanel();
        btnRow.setOpaque(false);
        btnRow.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnRow.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        startClientBtn = UIButtonFactory.createMenuButton("Create Room", btnNormalImg, btnHoverImg, this);
        backBtn = UIButtonFactory.createMenuButton("Back to Lobby", btnNormalImg, btnHoverImg, this);

        MouseAdapter mouseHover = ZhuzheeGame.MOUSE_HOVER_SFX;

        startClientBtn.addMouseListener(mouseHover);
        backBtn.addMouseListener(mouseHover);

        btnRow.add(startClientBtn);
        btnRow.add(backBtn);

        Panel.add(Box.createVerticalGlue());
        Panel.add(nameTitle);
        Panel.add(Box.createRigidArea(new Dimension(0, 5)));
        Panel.add(nameInput);
        Panel.add(btnRow);
        Panel.add(Box.createVerticalGlue());

        bgCanvas.add(Panel, BorderLayout.CENTER);
        add(bgCanvas, BorderLayout.CENTER);
    }

    public void executeServerStart() {
        if (ZhuzheeGame.SERVER == null) {
            new Thread(() -> {
                ZhuzheeGame.SERVER = new Core.Network.Server.GameServerManager();
                ZhuzheeGame.SERVER.startServer(9999);
            }).start();
        }
    }
    


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startClientBtn) {
            String meName = nameInput.getText();
            if(meName.isEmpty()) meName = "Host_Player";

            final String finalName = meName;
            
            executeServerStart();
            
            new Thread(() -> {
                try { Thread.sleep(500); } catch (InterruptedException ex) { ex.printStackTrace(); }

                System.out.println("Host connecting to own server as " + finalName);
                ZhuzheeGame.CLIENT = new Core.Network.Client.GameClientManager();
                ZhuzheeGame.CLIENT.connect("127.0.0.1", 9999, finalName);

                // 3) เปลี่ยน screen บน EDT
                javax.swing.SwingUtilities.invokeLater(() ->
                    Screen.ChangeScreen(ZhuzheeGame.WAITING_ROOM_MENU)
                );
            }).start();
        }
        else if (e.getSource() == backBtn) {
            Screen.ChangeScreen(ZhuzheeGame.LOBBY_MENU);
        }
        AudioManager.getInstance().playSound("click");
    }
}
