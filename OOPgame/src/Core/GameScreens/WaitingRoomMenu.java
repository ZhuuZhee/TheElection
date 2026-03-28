package Core.GameScreens;

import Core.Cards.Stream.ArcanaCardName;
import Core.Player.Player;
import Core.ZhuzheeGame;
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
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.File;
import java.util.ArrayList;

import static Core.Network.PacketBuilder.createStartPacket;

import Core.UI.UITool;

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

    // --- Character Selection Variables ---
    private String selectedColor = "";
    // ลบ private JLabel selectedProfileImagePreview; ออกไป
    private String selectedProfileFilepath = "";
    private String selectedArcanaName = ArcanaCardName.THE_FOOL;
    private JPanel activeTooltipUI = null;
    private Color[] getColors() {
        return Player.COLOR_MAP.values().toArray(new Color[0]);
    }

    private String[] getColorNames() {
        return Player.COLOR_MAP.keySet().toArray(new String[0]);
    }

    public WaitingRoomMenu() {
        setLayout(new BorderLayout());

        try {
            bgImage = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/test.png"));
            btnNormalImg = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/btn_normal.png"));
            btnHoverImg = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/btn_hover.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        bgCanvas = new NineSliceCanvas(bgImage, 25, 25, 25, 25) {
        };
        bgCanvas.setLayout(new BorderLayout());

        JLabel title = UITool.createLabel("Game Lobby", 40f);
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        bgCanvas.add(title, BorderLayout.NORTH);

        // ==========================================
        // MAIN PANEL: แบ่งซ้าย-ขวา
        // ==========================================
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.fill = GridBagConstraints.BOTH;
        mainGbc.weighty = 1.0;

        // ==========================================
        // LEFT PANEL: รายชื่อผู้เล่น
        // ==========================================
        JPanel leftPanel = createLeftPanel();
        mainGbc.gridx = 0;
        mainGbc.gridy = 0;
        mainGbc.weightx = 0.6; // ให้พื้นที่ฝั่งซ้าย 60%
        mainGbc.insets = new Insets(0, 0, 0, 20);
        mainPanel.add(leftPanel, mainGbc);

        // ==========================================
        // RIGHT PANEL: เลือกรูปและสี
        // ==========================================
        JPanel rightPanel = createRightPanel();
        mainGbc.gridx = 1;
        mainGbc.gridy = 0;
        mainGbc.weightx = 0.4; // ให้พื้นที่ฝั่งขวา 40%
        mainGbc.insets = new Insets(0, 20, 0, 0);
        mainPanel.add(rightPanel, mainGbc);

        bgCanvas.add(mainPanel, BorderLayout.CENTER);

        // ==========================================
        // BOTTOM PANEL: ปุ่มต่างๆ
        // ==========================================
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        startBtn = UIButtonFactory.createMenuButton("Start Game", btnNormalImg, btnHoverImg, this);
        leaveBtn = UIButtonFactory.createMenuButton("Leave Lobby", btnNormalImg, btnHoverImg, this);

        MouseAdapter mouseHover = ZhuzheeGame.MOUSE_HOVER_SFX;
        startBtn.addMouseListener(mouseHover);
        leaveBtn.addMouseListener(mouseHover);

        btnPanel.add(startBtn);
        btnPanel.add(leaveBtn);

        bgCanvas.add(btnPanel, BorderLayout.SOUTH);
        add(bgCanvas, BorderLayout.CENTER);

        // Initial setup for character selection
        File[] profileFiles = loadImageFiles(ZhuzheeGame.PROFILE_FILE_PATH);
        if (profileFiles != null && profileFiles.length > 0) {
            selectedProfileFilepath = profileFiles[0].getName();
        }
        String[] names = getColorNames();
        if (names.length > 0) selectedColor = names[0];
        refreshTimer = new Timer(1000, e -> refreshPlayerList());
    }


    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        ipLabel = UITool.createLabel("Your IP: Loading...", 18f);
        ipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        ipLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        leftPanel.add(ipLabel);

        JLabel subTitle = UITool.createLabel("Connected Players", 25f);
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        leftPanel.add(subTitle);

        playersPanel = new JPanel();
        playersPanel.setOpaque(false);
        playersPanel.setLayout(new BoxLayout(playersPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(playersPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        leftPanel.add(scrollPane);
        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        rightPanel.setOpaque(false); // ต้องปิด Opaque เสมอเมื่อใช้สีโปร่งแสง
        rightPanel.setBackground(new Color(230, 230, 230, 200)); // สีพื้นหลังโปร่งแสงเล็กน้อย
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints rGbc = new GridBagConstraints();
        rGbc.fill = GridBagConstraints.BOTH;
        rGbc.weightx = 1.0;
        rGbc.gridx = 0;

        // 1. Color Selection
        JPanel colorPanel = createColorSelectionPanel();
        rGbc.gridy = 0;
        rGbc.weighty = 0.1;
        rightPanel.add(colorPanel, rGbc);

        // 2. Profile Selection Grid
        File[] profileFiles = loadImageFiles(ZhuzheeGame.PROFILE_FILE_PATH);
        JPanel profileGrid = createGridPanel(profileFiles, 3, new Dimension(100, 100), new Dimension(10, 10), "PROFILE");

        JScrollPane profileScroll = new JScrollPane(profileGrid);
        profileScroll.setOpaque(false);
        profileScroll.getViewport().setOpaque(false);
        profileScroll.setBorder(BorderFactory.createTitledBorder("Select Profile"));

        rGbc.gridy = 1;
        rGbc.weighty = 0.45;
        rGbc.insets = new Insets(10, 0, 0, 0);
        rightPanel.add(profileScroll, rGbc);

        // 3. Arcana Selection Grid
        File[] arcanaFiles = loadImageFiles(ZhuzheeGame.CARD_IMAGES_FILE_PATH);
        JPanel arcanaGrid = createGridPanel(arcanaFiles, 3, new Dimension(80, 120), new Dimension(10, 10), "ARCANA");

        JScrollPane arcanaScroll = new JScrollPane(arcanaGrid);
        arcanaScroll.setOpaque(false);
        arcanaScroll.getViewport().setOpaque(false);
        arcanaScroll.setBorder(BorderFactory.createTitledBorder("Select Arcana"));

        rGbc.gridy = 2;
        rGbc.weighty = 0.45;
        rGbc.insets = new Insets(10, 0, 0, 0);
        rightPanel.add(arcanaScroll, rGbc);

        return rightPanel;
    }

    private JPanel createColorSelectionPanel() {
        JPanel colorGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        colorGrid.setOpaque(false);
        colorGrid.add(UITool.createLabel("Color: ", 20f));

        ArrayList<JPanel> colorBoxes = new ArrayList<>();
        Color[] colors = getColors();
        String[] colorNames = getColorNames();

        for (int i = 0; i < colors.length; i++) {
            final int index = i;
            JPanel colorBox = new JPanel();
            colorBox.setPreferredSize(new Dimension(40, 40)); // ขยายขนาดช่องเลือกสี
            colorBox.setBackground(colors[i]);

            if (i == 0) colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
            else colorBox.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

            colorBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
            colorBox.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectedColor = colorNames[index];
                    AudioManager.getInstance().playSound("click");
                    for (JPanel box : colorBoxes) box.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                    colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
                    sendPlayerDataUpdate();
                }
            });
            colorBoxes.add(colorBox);
            colorGrid.add(colorBox);
        }
        return colorGrid;
    }

    private JPanel createGridPanel(File[] files, int cols, Dimension imageScale, Dimension gap, String type) {
        JPanel grid = new JPanel(new GridLayout(0, cols, gap.width, gap.height));
        grid.setOpaque(false);

        if (files != null) {
            for (File file : files) {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(imageScale.width, imageScale.height));
                btn.setContentAreaFilled(false);
                btn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

                ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                Image scaledImg = icon.getImage().getScaledInstance(imageScale.width, imageScale.height, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(scaledImg));
                // เพิ่มเอฟเฟกต์ Hover ทั้งเสียงและขอบ
                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        ZhuzheeGame.MOUSE_HOVER_SFX.mouseEntered(e); // เล่นเสียง Hover
                        if (((javax.swing.border.LineBorder) btn.getBorder()).getLineColor() == Color.GRAY) {
                            // เปลี่ยนสี Hover ให้เข้มขึ้นเป็น DARK_GRAY
                            btn.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));
                        }
                        if (type.equals("ARCANA")) {
                            String cleanName = file.getName().replace(".png", "").replace(".jpg", "");
                            Core.Cards.ArcanaCard dummyCard = Core.Cards.Stream.ArcanaCardRegistry.createCard(cleanName);

                            if (dummyCard != null) {

                                String currentDesc = dummyCard.getDescription();
                                if (currentDesc == null || currentDesc.isEmpty()) {
                                    dummyCard.setDescription("Cooldown: " + dummyCard.getMaxCooldown() + " Rounds");
                                } else {
                                    dummyCard.setDescription(currentDesc + "\n\nCooldown: " + dummyCard.getMaxCooldown() + " Rounds");
                                }

                                // ดึง panel ชั้นบนสุดของจอเกมมาใช้
                                JLayeredPane layeredPane = SwingUtilities.getRootPane(btn).getLayeredPane();

                                // ถ้ามีอันเก่าค้างอยู่ เคลียร์ทิ้งก่อน
                                if (activeTooltipUI != null) {
                                    layeredPane.remove(activeTooltipUI);
                                    layeredPane.repaint();
                                }

                                // สร้าง Tooltip
                                activeTooltipUI = dummyCard.new SmartTooltipUI(dummyCard);
                                activeTooltipUI.setSize(activeTooltipUI.getPreferredSize());

                                // แปลงพิกัดเมาส์ให้เป็นพิกัดของจอเกม
                                Point p = SwingUtilities.convertPoint(btn, e.getPoint(), layeredPane);

                                int x = p.x + 15;
                                int y = p.y + 15;

                                // เช็คไม่ให้ล้นขอบจอ
                                if (x + activeTooltipUI.getWidth() > layeredPane.getWidth()) {
                                    x = p.x - activeTooltipUI.getWidth() - 15;
                                }
                                if (y + activeTooltipUI.getHeight() > layeredPane.getHeight()) {
                                    y = p.y - activeTooltipUI.getHeight() - 15;
                                }

                                activeTooltipUI.setLocation(x, y);

                                // แปะลงเลเยอร์ POPUP_LAYER (บนสุดเสมอ)
                                layeredPane.add(activeTooltipUI, JLayeredPane.POPUP_LAYER);
                                layeredPane.repaint();
                            }
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (((javax.swing.border.LineBorder) btn.getBorder()).getLineColor() == Color.DARK_GRAY) {
                            btn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                        }
                        if (activeTooltipUI != null) {
                            JLayeredPane layeredPane = SwingUtilities.getRootPane(btn).getLayeredPane();
                            layeredPane.remove(activeTooltipUI);
                            layeredPane.repaint();
                            activeTooltipUI = null;
                        }
                    }
                });

                btn.addActionListener(e -> {
                    if (type.equals("PROFILE")) {
                        selectedProfileFilepath = file.getName();
                    } else if (type.equals("ARCANA")) {
                        selectedArcanaName = file.getName().replace(".png", "").replace(".jpg", "");
                    }

                    AudioManager.getInstance().playSound("click");

                    for (Component c : grid.getComponents()) {
                        if (c instanceof JButton)
                            ((JButton) c).setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                    }
                    Color highlightColor = type.equals("ARCANA") ? Color.ORANGE : Color.BLACK;
                    btn.setBorder(BorderFactory.createLineBorder(highlightColor, 4));
                    sendPlayerDataUpdate();
                });
                grid.add(btn);
            }
        }
        return grid;
    }

    File[] loadImageFiles(String path) {
        File folder = new File(path);
        return folder.listFiles((dir, name) -> name.endsWith(".png") || name.endsWith(".jpg"));
    }

    private synchronized boolean sendPlayerDataUpdate() {
        if (ZhuzheeGame.CLIENT != null && ZhuzheeGame.CLIENT.getLocalPlayer() != null) {
            Player localPlayer = ZhuzheeGame.CLIENT.getLocalPlayer();
            org.json.JSONObject playerPacket = Core.Network.PacketBuilder.createPlayerDataPacket(
                    localPlayer.getPlayerId(),
                    localPlayer.getPlayerName(),
                    localPlayer.getCoin(),
                    selectedColor,
                    selectedProfileFilepath,
                    selectedArcanaName
            );
            ZhuzheeGame.CLIENT.sendAction(playerPacket);
            return true;
        }
        System.err.println("No Local Player in here");
        return false;
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
            java.util.List<Player> players = new ArrayList<>(ZhuzheeGame.CLIENT.getConnectedPlayers());
            playersPanel.removeAll();

            for (Player p : players) {
                JPanel playerRow = new JPanel(new BorderLayout(15, 0));
                playerRow.setOpaque(true);
                playerRow.setBackground(new Color(255, 255, 255, 150));

                // กำหนดขอบกล่องให้เป็นสีของผู้เล่น
                Color pColor = p.getColor() != null ? p.getColor() : Color.GRAY;
                playerRow.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(pColor, 3),
                        BorderFactory.createEmptyBorder(10, 10, 10, 20)
                ));
                playerRow.setMaximumSize(new Dimension(500, 80));

                // 1. เพิ่มรูปโปรไฟล์ทางซ้าย
                JLabel profileIconLabel = new JLabel();
                profileIconLabel.setPreferredSize(new Dimension(60, 60));
                profileIconLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));


                    File imgFile = p.getProfileImageFile();
//                  System.out.println("Waiting Room : Image File Path of {%s} is %s".formatted(p.getPlayerName(),imgFile.getAbsolutePath()));
                    if (imgFile.exists()) {
                        ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
                        profileIconLabel.setIcon(new ImageIcon(icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
                    }


                playerRow.add(profileIconLabel, BorderLayout.WEST);
                JLabel nameLabel = UITool.createLabel(p.getPlayerName(), 24f);
                playerRow.add(nameLabel, BorderLayout.CENTER);
                playersPanel.add(playerRow);
                playersPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                playersPanel.revalidate();
                playersPanel.repaint();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startBtn) {
            if (ZhuzheeGame.CLIENT != null) {
                JSONObject startReq = createStartPacket();
                ZhuzheeGame.CLIENT.sendAction(startReq);
                sendPlayerDataUpdate();
            }
        } else if (e.getSource() == leaveBtn) {
            refreshTimer.stop();
            if (ZhuzheeGame.SERVER != null) {
                ZhuzheeGame.SERVER.stopServer();
                ZhuzheeGame.SERVER = null;
            }
            if (ZhuzheeGame.CLIENT != null) {
                ZhuzheeGame.CLIENT.disconnect();
                ZhuzheeGame.CLIENT = null;
            }
            Screen.ChangeScreen(ZhuzheeGame.LOBBY_MENU);
        }
        AudioManager.getInstance().playSound("click");
    }
}
