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

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            boolean isActive = (i == 0);

            // แปลงและแสดงผลสีตามที่ผู้เล่นตั้งไว้
            Color playerColor = p.getColor();
            listContainer.add(new PlayerItemUI(p, playerColor, isActive));
            listContainer.add(Box.createVerticalStrut(10));
        }
        revalidate();
        repaint();
    }

    @Override
    protected void onResize(int width, int height) {
        this.panelHeight = height - 100;
        super.onResize(width, height);
    }

    private static class PlayerItemUI extends JPanel {
        public PlayerItemUI(Player player, Color teamColor, boolean isActive) {
            setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            setOpaque(false);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

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

            JLabel nameLabel = UITool.createLabel(player.getPlayerName(), 22f);
            nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
            nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            nameTag.add(nameLabel, BorderLayout.CENTER);

            add(nameTag);
        }
    }
}
