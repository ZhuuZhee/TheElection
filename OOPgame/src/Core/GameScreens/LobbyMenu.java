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

public class LobbyMenu extends Screen implements ActionListener {

    JTextField nameInput;
    JTextField ipInput;
    NineSliceButton createBtn;
    NineSliceButton joinBtn;
    NineSliceButton backBtn;

    BufferedImage bgImage;
    BufferedImage btnNormalImg;
    BufferedImage btnHoverImg;
    NineSliceCanvas bgCanvas;

    public LobbyMenu() {
        setLayout(new BorderLayout());

        try {
            bgImage = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/ImageForMapBackground/LobbyReal.png"));
            btnNormalImg = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/btn_normal.png"));
            btnHoverImg = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/btn_hover.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        bgCanvas = new NineSliceCanvas(bgImage, 25, 25, 25, 25) {};
        bgCanvas.setLayout(new BorderLayout());

        JLabel title = new JLabel("Game Lobby");
        title.setFont(title.getFont().deriveFont(100f));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(100, 0, 0, 0));
        title.setForeground(new Color(195, 169, 82));
        bgCanvas.add(title, BorderLayout.NORTH);

        JPanel Panel = new JPanel();
        Panel.setOpaque(false);
        Panel.setLayout(new BoxLayout(Panel, BoxLayout.Y_AXIS));
        
        JLabel nameTitle = new JLabel("Enter Your Name:");
        nameTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        nameInput = new JTextField();
        nameInput.setMaximumSize(new Dimension(350, 50));
        nameInput.setPreferredSize(new Dimension(350, 50));
        nameInput.setHorizontalAlignment(JTextField.CENTER);
        nameInput.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel ipTitle = new JLabel("Enter Host IP (For Join):");
        ipTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        ipTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        ipInput = new JTextField();
        ipInput.setMaximumSize(new Dimension(350, 50));
        ipInput.setPreferredSize(new Dimension(350, 50));
        ipInput.setHorizontalAlignment(JTextField.CENTER);
        ipInput.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setPreferredSize(new Dimension(350, 220));
        btnPanel.setLayout(new GridLayout(3, 1, 10, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        createBtn = UIButtonFactory.createMenuButton("Create Room", btnNormalImg, btnHoverImg, this);
        joinBtn = UIButtonFactory.createMenuButton("Join Game",   btnNormalImg, btnHoverImg, this);
        backBtn = UIButtonFactory.createMenuButton("Back to Menu",btnNormalImg, btnHoverImg, this);

        MouseAdapter mouseHover = ZhuzheeGame.MOUSE_HOVER_SFX;

        createBtn.addMouseListener(mouseHover);
        joinBtn.addMouseListener(mouseHover);
        backBtn.addMouseListener(mouseHover);

        btnPanel.add(createBtn);
        btnPanel.add(joinBtn);
        btnPanel.add(backBtn);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        centerWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 150, 0));

        Panel.add(Box.createVerticalGlue());
        Panel.add(nameTitle);
        Panel.add(Box.createRigidArea(new Dimension(0, 5)));
        Panel.add(nameInput);
        Panel.add(ipTitle);
        Panel.add(Box.createRigidArea(new Dimension(0, 5)));
        Panel.add(ipInput);
        Panel.add(btnPanel);
        Panel.add(Box.createVerticalGlue());

        centerWrapper.add(Panel);

        bgCanvas.add(centerWrapper, BorderLayout.CENTER);
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
        if (e.getSource() == createBtn) {
            String meName = nameInput.getText();
            if(meName.isEmpty()) meName = "Host_Player";

            final String finalName = meName;
        
            executeServerStart();
        
            new Thread(() -> {
                try { Thread.sleep(500); } catch (InterruptedException ex) { ex.printStackTrace(); }

                System.out.println("Host connecting to own server as " + finalName);
                ZhuzheeGame.CLIENT = new Core.Network.Client.GameClientManager();
                ZhuzheeGame.CLIENT.connect("127.0.0.1", 9999, finalName);

                javax.swing.SwingUtilities.invokeLater(() ->
                    Screen.ChangeScreen(ZhuzheeGame.WAITING_ROOM_MENU)
                );
            }).start();
        }
        else if (e.getSource() == joinBtn) {
            String targetIp = ipInput.getText();
            String pName = nameInput.getText();
            if(pName.isEmpty()) pName = "Player";

            ZhuzheeGame.CLIENT = new Core.Network.Client.GameClientManager();
            ZhuzheeGame.CLIENT.connect(targetIp, 9999, pName);

            Screen.ChangeScreen(ZhuzheeGame.WAITING_ROOM_MENU);
        }
        else if (e.getSource() == backBtn) {
            Screen.ChangeScreen(ZhuzheeGame.MAIN_MENU);
        }
        AudioManager.getInstance().playSound("click");
    }
}
