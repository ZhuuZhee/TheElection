package Core.GameScreens;

import Core.Player.Player;
import Core.ZhuzheeGame;
import Core.Network.NetworkProtocol;
import ZhuzheeEngine.Audios.AudioManager;
import ZhuzheeEngine.Screen;
import ZhuzheeEngine.Scene.NineSliceCanvas;
import ZhuzheeEngine.Scene.NineSliceButton;
import Core.UI.UIButtonFactory;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class WaitingRoomMenu extends Screen implements ActionListener {

    NineSliceButton startBtn;
    NineSliceButton leaveBtn;
    JPanel playersPanel;
    JLabel ipLabel;
    Timer refreshTimer;

    BufferedImage bgImage;
    BufferedImage btnNormalImg;
    BufferedImage btnHoverImg;
    NineSliceCanvas bgCanvas;

    public WaitingRoomMenu() {
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

        JLabel title = new JLabel("Waiting Room");
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(20, 50, 10, 0));
        
        ipLabel = new JLabel("Your IP: Loading...");
        ipLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        ipLabel.setHorizontalAlignment(JLabel.CENTER);
        ipLabel.setBorder(BorderFactory.createEmptyBorder(0, 50, 20, 0));

        JPanel northPanel = new JPanel();
        northPanel.setOpaque(false);
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.add(title);
        northPanel.add(ipLabel);

        bgCanvas.add(northPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel subTitle = new JLabel("Connected Players:");
        subTitle.setFont(new Font("Arial", Font.BOLD, 22));
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        centerPanel.add(subTitle);

        playersPanel = new JPanel();
        playersPanel.setOpaque(false);
        playersPanel.setLayout(new BoxLayout(playersPanel, BoxLayout.Y_AXIS));
        centerPanel.add(playersPanel);

        bgCanvas.add(centerPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        startBtn = UIButtonFactory.createMenuButton("Start Game", btnNormalImg, btnHoverImg, this);
        btnPanel.add(startBtn);
        
        leaveBtn = UIButtonFactory.createMenuButton("Leave Room", btnNormalImg, btnHoverImg, this);
        btnPanel.add(leaveBtn);

        MouseAdapter mouseHover = ZhuzheeGame.MOUSE_HOVER_SFX;

        startBtn.addMouseListener(mouseHover);
        leaveBtn.addMouseListener(mouseHover);

        bgCanvas.add(btnPanel, BorderLayout.SOUTH);

        add(bgCanvas, BorderLayout.CENTER);

        refreshTimer = new Timer(1000, e -> refreshPlayerList());
    }

    @Override
    public void onScreenEnter() {
        super.onScreenEnter();
        refreshPlayerList();
    }

    @Override
    public void render() {
        super.render();
        if (!refreshTimer.isRunning()) {
            refreshTimer.start();
        }

        if (ZhuzheeGame.SERVER != null) {
            startBtn.setVisible(true);
        } else {
            startBtn.setVisible(false);
        }
        
        updateIpLabel();
        refreshPlayerList();
    }

    private void updateIpLabel() {
        try {
            String myIp = InetAddress.getLocalHost().getHostAddress();
            ipLabel.setText("Room IP: " + myIp);
        } catch (UnknownHostException ex) {
            ipLabel.setText("Room IP: 127.0.0.1 (Offline)");
        }
    }

    private void refreshPlayerList() {
        if (ZhuzheeGame.CLIENT != null) {
            List<Player> players = ZhuzheeGame.CLIENT.getConnectedPlayers();
            playersPanel.removeAll();
            
            for (Player p : players) {
                JLabel pLabel = new JLabel("Player: " + p.getPlayerName());
                pLabel.setFont(new Font("Arial", Font.PLAIN, 20));
                pLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                playersPanel.add(pLabel);
                playersPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
            
            playersPanel.revalidate();
            playersPanel.repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startBtn) {
            if (ZhuzheeGame.CLIENT != null) {
                JSONObject startReq = new JSONObject();
                startReq.put("actionType", NetworkProtocol.START_GAME.name());
                ZhuzheeGame.CLIENT.sendAction(startReq);
            }
        }
        else if (e.getSource() == leaveBtn) {
            refreshTimer.stop();
            if (ZhuzheeGame.SERVER != null) {
                ZhuzheeGame.SERVER.stopServer();
                ZhuzheeGame.SERVER = null;
            } else if (ZhuzheeGame.CLIENT != null) {
                ZhuzheeGame.CLIENT.disconnect();
            }
            ZhuzheeGame.CLIENT = null;
            Screen.ChangeScreen(ZhuzheeGame.LOBBY_MENU);
        }
        AudioManager.getInstance().playSound("click");
    }
}
