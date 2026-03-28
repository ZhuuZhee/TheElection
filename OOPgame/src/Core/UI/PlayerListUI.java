package Core.UI;

import Core.Player.Player;
import ZhuzheeEngine.Scene.Canvas;
import ZhuzheeEngine.Scene.Scene2D;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class PlayerListUI extends Canvas {
    private final List<Player> players;
    private final JPanel listContainer;

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
        listContainer.removeAll();

        System.out.println("PlayerlistUI : \n ");
        
        // 1. Get city counts for each player
        int[] cityCounts = new int[players.size()];
        for (int i = 0; i < players.size(); i++) {
            if (Core.ZhuzheeGame.MAP != null) {
                cityCounts[i] = Core.ZhuzheeGame.MAP.getOwnedCitiesCount(i);
            }
        }

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            boolean isActive = (i == 0);
            boolean isMe = p.isLocal();

            int Rank = 1;
            for (int k = 0; k < players.size(); k++) {
                if (k == i) continue;
                if (cityCounts[k] > cityCounts[i] || (cityCounts[k] == cityCounts[i] && k < i)) {
                    Rank++;
                }
            }

            // แปลงและแสดงผลสีตามที่ผู้เล่นตั้งไว้
            System.out.println(p.toString());
            Color playerColor = p.getColor();
            listContainer.add(new PlayerItemUI(p, Rank, playerColor, isActive, isMe));
            listContainer.add(Box.createVerticalStrut(10));
        }
        System.out.println("-----------------------");
        revalidate();
        repaint();
    }

    @Override
    protected void onResize(int width, int height) {
        // ให้ขนาดคงที่ตามที่ตั้งไว้ เพื่อให้อยู่ตรงกลางจอได้
        super.onResize(width, height);
    }

    private static class PlayerItemUI extends JPanel {
        int rank = 1;
        private static int margin = 12; // เพิ่ม margin พื้นฐาน
        private static int padding = 24; // เพิ่ม margin พื้นฐาน

        public PlayerItemUI(Player player, int calculatedRank, Color teamColor, boolean isActive, boolean isMe) {
            this.rank = calculatedRank;
            setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0)); // เพิ่มช่องว่างแนวตั้งเล็กน้อย
            setOpaque(false);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

            JPanel nameTag = getJPanel(teamColor, isActive);
            JPanel center = new JPanel(new GridBagLayout());
            center.setOpaque(false);

            createPlayerNameLabel(player.getPlayerName(),center);
            createRankLabel(center);
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

            JLabel imgLabel = new JLabel(icon);
            imgLabel.setPreferredSize(new Dimension(50, 50));
            imgLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 0, 0, 0), // ระยะห่างซ้ายขวา
                BorderFactory.createLineBorder(teamColor, 2)
            ));
            nameTag.add(imgLabel, BorderLayout.WEST);

            add(nameTag);
        }

        private static ImageIcon createImageIcon(File file, int size) {
            ImageIcon icon = new ImageIcon(file.getAbsolutePath());
            System.out.println("image path is {%s}".formatted(file.getAbsolutePath()));
            return new ImageIcon(icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
        }

        private void createRankLabel(JPanel container) {
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
        }

        private void createPlayerNameLabel(String playerName, Container container) {
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

        private JPanel getJPanel(Color teamColor, boolean isActive) {
            int width = isActive ? 300 : 250;
            JPanel nameTag = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(0, 0, getWidth(), getHeight());

                    int borderSize = 4;
                    g2d.setColor(teamColor);
                    g2d.fillRect(0, 0, getWidth(), borderSize);
                    g2d.fillRect(0, getHeight() - borderSize, getWidth(), borderSize);

                    g2d.setColor(new Color(50, 50, 80));
                    g2d.fillRect(0, 0, getWidth(), 1);
                    g2d.fillRect(0, getHeight() - 1, getWidth(), 1);
                }
            };
            nameTag.setPreferredSize(new Dimension(width, 50));
            return nameTag;
        }
    }
}
