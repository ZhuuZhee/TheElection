package Core.UI;

import Core.Player.Player;
import ZhuzheeEngine.Scene.Canvas;
import ZhuzheeEngine.Scene.Scene2D;

import javax.swing.*;
import java.awt.*;
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
        setPanelSize(350, scene.getHeight() - 100);
        setMargins(0, 0, 150, 0); // Margin Top 150
        setAnchors(1, 0); // Right side, use ratio/fixed Y

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
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            boolean isActive = (i == 0);

            // แปลงและแสดงผลสีตามที่ผู้เล่นตั้งไว้
            System.out.println(p.toString());
            Color playerColor = p.getColor();
            listContainer.add(new PlayerItemUI(p, playerColor, isActive));
            listContainer.add(Box.createVerticalStrut(10));
        }
        System.out.println("-----------------------");
        revalidate();
        repaint();
    }

    @Override
    protected void onResize(int width, int height) {
        this.panelHeight = height - 100;
        super.onResize(width, height);
    }

    private static class PlayerItemUI extends JPanel {
        int rank = 1;
        private static int margin = 12; // เพิ่ม margin พื้นฐาน
        private static int padding = 24; // เพิ่ม margin พื้นฐาน

        public PlayerItemUI(Player player, Color teamColor, boolean isActive) {
            setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0)); // เพิ่มช่องว่างแนวตั้งเล็กน้อย
            setOpaque(false);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

            JPanel nameTag = getJPanel(teamColor, isActive);
            JPanel center = new JPanel(new GridBagLayout());
            center.setOpaque(false);

            createPlayerNameLabel(player.getPlayerName(),center);
            createRankLabel(center);

            nameTag.add(center);

            // เพิ่มรูปโปรไฟล์พร้อมระยะห่าง
            String path = player.getProfileImagePath();
            ImageIcon icon = loadImg(path, 50);
            
            if (icon == null) {
                System.err.println("[DEBUG] PlayerListUI: Failed to load image for '" + player.getPlayerName() + "' at: " + path);
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

        private static ImageIcon loadImg(String path, int size) {
            if (path == null || path.isEmpty() || !(new java.io.File(path).exists())) return null;
            ImageIcon icon = new ImageIcon(path);
            System.out.println("image path is {%s}".formatted(path));
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
