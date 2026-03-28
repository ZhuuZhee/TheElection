package Core.UI;

import Core.Player.Player;
import Core.ZhuzheeGame;
import ZhuzheeEngine.Scene.Canvas;
import ZhuzheeEngine.Scene.Scene2D;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import ZhuzheeEngine.Scene.NineSliceCanvas;
import java.io.File;
import java.io.IOException;

public class PlayerListUI extends Canvas {
    private final List<Player> players;
    private final JPanel listContainer;
    private final Map<String, PlayerItemUI> playerItemMap = new ConcurrentHashMap<>();

    private static BufferedImage frameImage;
    static {
        try {
            frameImage = ImageIO.read(new File("OOPgame/Assets/UI/frame.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PlayerListUI(Scene2D scene, List<Player> players) {
        super(scene);
        this.players = players;

        setLayout(new BorderLayout());
        setOpaque(false);

        // ใช้ระบบ Layout ของ Canvas
        setPanelSize(350, 300); // กำหนดความสูงให้เล็กลง เพื่อให้อยู่ตรงกลางจริงๆ
        setMargins(0, 0, 0, 0);
        setAnchors(1, 0); // Right side, use ratio/fixed Y
        setScreenPos(0, 3); // 0 = default X behavior, 3 = Y is height/3 (ประมาณช่วงกลางจอค่อนไปทางบนนิดๆ)

        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setOpaque(false);
        add(listContainer, BorderLayout.NORTH);

        updatePlayerList();
        onResize(scene.getWidth(), scene.getHeight());
    }

    public void updatePlayerList() {
        // ล้างข้อมูลใน Container เพื่อจัดลำดับใหม่ตามคะแนน/Rank
        listContainer.removeAll();

        // 1. Get city counts for each player
        int[] cityCounts = new int[players.size()];
        for (int i = 0; i < players.size(); i++) {
            if (Core.ZhuzheeGame.MAP != null) {
                cityCounts[i] = Core.ZhuzheeGame.MAP.getOwnedCitiesCount(players.get(i).getPlayerId());
            }
        }

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            
            // ตรวจสอบค่า Null เพื่อป้องกัน Error กรณี Client ยังโหลดไม่เสร็จ
            String currentPlayerId = (ZhuzheeGame.CLIENT != null) ? ZhuzheeGame.CLIENT.getCurrentPlayerId() : "";
            boolean isActive = p.getPlayerId().equals(currentPlayerId);
            boolean isMe = p.isLocal();
            Color playerColor = p.getColor();

            // 2. คำนวณอันดับ (Rank)
            int Rank = 1;
            for (int k = 0; k < players.size(); k++) {
                if (k == i) {
                    continue;
                }
                if (cityCounts[k] > cityCounts[i] || (cityCounts[k] == cityCounts[i] && k < i)) {
                    Rank++;
                }
            }

            // แปลงและแสดงผลสีตามที่ผู้เล่นตั้งไว้
//            System.out.println(p.toString());
            
            // 3. ตรวจสอบว่ามี UI เดิมอยู่หรือไม่ ถ้าไม่มีให้สร้างใหม่ ถ้ามีให้ดึงมาอัปเดต
            int finalRank = Rank;
            PlayerItemUI item = playerItemMap.computeIfAbsent(p.getPlayerId(),
                id -> new PlayerItemUI(p, finalRank, playerColor, isActive, isMe));
            
            item.updateState(Rank, isActive, playerColor);
            
            listContainer.add(item);
            listContainer.add(Box.createVerticalStrut(10));
        }
        listContainer.revalidate();
        repaint();
    }

    @Override
    protected void onResize(int width, int height) {
        // ให้ขนาดคงที่ตามที่ตั้งไว้ เพื่อให้อยู่ตรงกลางจอได้
        super.onResize(width, height);
    }

    private static class PlayerItemUI extends JPanel {
        int rank = 1;
        private final static int margin = 12; // เพิ่ม margin พื้นฐาน
        private final static int padding = 24; // เพิ่ม margin พื้นฐาน
        public Color teamColor;
        public boolean isActive;
        public boolean isMe;

        private final JLabel rankLabel;
        private final JLabel nameLabel;
        private final JPanel nameTag;
        private final JLabel imgLabel;

        public PlayerItemUI(Player player, int calculatedRank, Color teamColor, boolean isActive, boolean isMe) {
            this.rank = calculatedRank;
            this.teamColor = teamColor;
            this.isActive = isActive;
            this.isMe = isMe;

            setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0)); // เพิ่มช่องว่างแนวตั้งเล็กน้อย
            setOpaque(false);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

            nameTag = getJPanel();
            JPanel center = new JPanel(new GridBagLayout());
            center.setOpaque(false);

            nameLabel = createPlayerNameLabel(player.getPlayerName(), center);
            rankLabel = createRankLabel(center);
            if (isMe) {
                createYouLabel(center);
            }

            nameTag.add(center);

            // เพิ่มรูปโปรไฟล์พร้อมระยะห่าง
            File imgFile = player.getProfileImageFile();
            ImageIcon icon = createImageIcon(imgFile, 50);
            
            if (icon == null) {
                System.err.println("[DEBUG] PlayerListUI: Failed to load image for '" + player.getPlayerName() + "' at: " + imgFile.getAbsolutePath());
            }

            imgLabel = new JLabel(icon);
            imgLabel.setPreferredSize(new Dimension(50, 50));
            imgLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 0, 0, 0), // ระยะห่างซ้ายขวา
                BorderFactory.createLineBorder(teamColor, 2)
            ));
            nameTag.add(imgLabel, BorderLayout.WEST);

            add(nameTag);
        }

        /**
         * อัปเดตสถานะของ UI แทนการสร้าง Object ใหม่
         */
        public void updateState(int rank, boolean isActive, Color teamColor) {
            this.rank = rank;
            this.isActive = isActive;
            this.teamColor = teamColor;

            rankLabel.setText(Integer.toString(rank));
            int width = isActive ? 300 : 250;
            nameTag.setPreferredSize(new Dimension(width, 50));
            imgLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(0, 0, 0, 0),
                    BorderFactory.createLineBorder(teamColor, 2)
            ));
            revalidate();
            repaint();
        }

        private static ImageIcon createImageIcon(File file, int size) {
            ImageIcon icon = new ImageIcon(file.getAbsolutePath());
            return new ImageIcon(icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
        }

        private JLabel createRankLabel(JPanel container) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, margin,0, padding); // เพิ่มช่องว่างด้านขวาของอันดับ
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.ipady = -padding;

            JLabel rankLabel = UITool.createLabel(Integer.toString(rank), 24f);
            rankLabel.setFont(rankLabel.getFont().deriveFont(Font.ITALIC));
            rankLabel.setHorizontalAlignment(SwingConstants.LEFT);
            container.add(rankLabel, gbc);
            return rankLabel;
        }

        private JLabel createPlayerNameLabel(String playerName, Container container) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, padding, 0, margin);
            gbc.anchor = GridBagConstraints.WEST; // เปลี่ยนเป็นชิดซ้าย
            gbc.gridx = 1; // แยกมาอยู่คอลัมน์ที่ 2
            gbc.gridy = 0;
            gbc.ipady = -padding;

            JLabel nameLabel = UITool.createLabel(playerName, 22f);
            nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
            nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            container.add(nameLabel, gbc);
            return nameLabel;
        }

        private void createYouLabel(Container container) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 0, 0, margin);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.gridx = 2;
            gbc.gridy = 0;
            gbc.ipady = -padding + 30;

            JLabel youLabel = new JLabel("you");
            youLabel.setFont(getFont().deriveFont(Font.BOLD, 12f));
            youLabel.setForeground(Color.WHITE);
            youLabel.setBackground(new Color(50, 50, 80));
            youLabel.setOpaque(true);
            youLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
            youLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            container.add(youLabel, gbc);
        }

        private JPanel getJPanel() {
            JPanel nameTag = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g); // Draws the 9-slice background
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // วาดแถบสีบ่งบอกว่าเป็นผู้เล่นทีมไหน
                    int borderSize = 4;
                    g2d.setColor(teamColor);
                    g2d.fillRect(10, 5, getWidth() - 20, borderSize);
                    g2d.fillRect(10, getHeight() - borderSize - 5, getWidth() - 20, borderSize);
                }
            };
            nameTag.setLayout(new BorderLayout());
            int width = isActive ? 300 : 250;
            nameTag.setPreferredSize(new Dimension(width, 50));
            return nameTag;
        }
    }
}
