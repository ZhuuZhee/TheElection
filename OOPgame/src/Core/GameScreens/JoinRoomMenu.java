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

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class JoinRoomMenu extends Screen implements ActionListener {

    JTextField ipInput;
    JTextField nameInput;
    NineSliceButton connectBtn;
    NineSliceButton backBtn;
    
    JList<String> serverList;
    DefaultListModel<String> listModel;
    Thread listenerThread;
    boolean listening = false;

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
        title.setFont(title.getFont().deriveFont(40f));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        bgCanvas.add(title, BorderLayout.NORTH);

        JPanel Panel = new JPanel();
        Panel.setOpaque(false);
        Panel.setLayout(new BoxLayout(Panel, BoxLayout.Y_AXIS));
        
        JLabel nameTitle = new JLabel("Enter Your Name:");
        nameTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        nameInput = new JTextField();
        nameInput.setMaximumSize(new Dimension(250, 35));
        nameInput.setPreferredSize(new Dimension(250, 35));
        nameInput.setFont(new Font("Arial", Font.PLAIN, 16));
        nameInput.setHorizontalAlignment(JTextField.CENTER);
        nameInput.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitle = new JLabel("Enter Host IP:");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        ipInput = new JTextField();
        ipInput.setMaximumSize(new Dimension(250, 35));
        ipInput.setPreferredSize(new Dimension(250, 35));
        ipInput.setHorizontalAlignment(JTextField.CENTER);
        ipInput.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel btnRow = new JPanel();
        btnRow.setOpaque(false);
        btnRow.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnRow.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        listModel = new DefaultListModel<>();
        serverList = new JList<>(listModel);
        serverList.setFont(new Font("Arial", Font.PLAIN, 14));
        serverList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(serverList);
        scrollPane.setPreferredSize(new Dimension(250, 100));
        scrollPane.setMaximumSize(new Dimension(250, 100));
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        serverList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && serverList.getSelectedValue() != null) {
                String selected = serverList.getSelectedValue();
                String[] parts = selected.split(":");
                if (parts.length > 0) {
                    ipInput.setText(parts[0]);
                }
            }
        });

        connectBtn = UIButtonFactory.createMenuButton("Connect", btnNormalImg, btnHoverImg, this);
        backBtn = UIButtonFactory.createMenuButton("Back to Lobby", btnNormalImg, btnHoverImg, this);

        MouseAdapter mouseHover = ZhuzheeGame.MOUSE_HOVER_SFX;

        connectBtn.addMouseListener(mouseHover);
        backBtn.addMouseListener(mouseHover);
        
        btnRow.add(connectBtn);
        btnRow.add(backBtn);

        Panel.add(Box.createVerticalGlue());
        Panel.add(nameTitle);
        Panel.add(Box.createRigidArea(new Dimension(0, 10)));
        Panel.add(nameInput);
        Panel.add(subtitle);
        Panel.add(Box.createRigidArea(new Dimension(0, 10)));
        Panel.add(ipInput);
        Panel.add(Box.createRigidArea(new Dimension(0, 10)));
        Panel.add(scrollPane);
        Panel.add(btnRow);
        Panel.add(Box.createVerticalGlue());

        bgCanvas.add(Panel, BorderLayout.CENTER);
        add(bgCanvas, BorderLayout.CENTER);
    }



    @Override
    public void onScreenEnter() {
        super.onScreenEnter();
        listModel.clear();
        listening = true;
        listenerThread = new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(8888)) {
                socket.setSoTimeout(2000);
                byte[] buffer = new byte[256];
                while (listening) {
                    try {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);
                        String message = new String(packet.getData(), 0, packet.getLength());
                        if (message.startsWith("ZHUZHEE_GAME_SERVER:")) {
                            String ip = packet.getAddress().getHostAddress();
                            String port = message.split(":")[1];
                            String entry = ip + ":" + port;
                            SwingUtilities.invokeLater(() -> {
                                if (!listModel.contains(entry)) {
                                    listModel.addElement(entry);
                                }
                            });
                        }
                    } catch (java.net.SocketTimeoutException e) {
                        // timeout to check listening flag
                    } catch (Exception e) {
                        if (listening) e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        listenerThread.start();
    }

    @Override
    public void onScreenExit() {
        super.onScreenExit();
        listening = false;
        if (listenerThread != null) {
            listenerThread.interrupt();
        }
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
        AudioManager.getInstance().playSound("click");
    }
}
