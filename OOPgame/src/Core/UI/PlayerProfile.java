package Core.UI;

import Core.ZhuzheeGame;
import Core.Player.Player;
import ZhuzheeEngine.Scene.*;
import ZhuzheeEngine.Scene.Canvas;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;

public class PlayerProfile extends Canvas {
    private final Player player;
    private Image profileImage;
    private JLabel nameLabel;

    private final Color colorBorder = new Color(200, 180, 150);
    private final Color colorBorder2 = new Color(100, 90, 75);
    private final Color colorBg = new Color(20, 20, 20, 240);
    private final int arcSize = 15;

    public PlayerProfile(Scene2D scene, Player player) {
        super(scene);
        this.player = player;

        setPanelSize(180, 224);
        setAnchorTop(false);
        setAnchorRight(true);
        setAnchorLeft(false);
        setMargins(16, 16, 16, 16);
        setOpaque(false);

        this.setLayout(new BorderLayout());

        String playerName = player != null ? player.getPlayerName() : "Name";
        nameLabel = new JLabel(playerName);
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0)); // เว้นขอบล่าง 15px

        this.add(nameLabel, BorderLayout.SOUTH);

        updateProfile();
        setVisible(true);

        onResize(scene.getWidth(), scene.getHeight());
    }

    public void updateProfile() {
        if (player == null) return;

        File imageFile = player.getProfileImageFile();
        if (imageFile != null && imageFile.exists()) {
            ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
            this.profileImage = icon.getImage();
        }
        nameLabel.setText(player.getPlayerName());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // วาดพื้นหลังกล่องโปรไฟล์
        g2d.setColor(new Color(50, 50, 50, 220));
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arcSize, arcSize);

        setBorder(new LineBorder(new Color(150, 150, 150), 2));

        int boxX = 15;
        int boxY = 15;
        int boxWidth = 150;
        int boxHeight = 150;

        Shape clipShape = new RoundRectangle2D.Float(boxX, boxY, boxWidth, boxHeight, 10, 10);
        g2d.setClip(clipShape);

        if (profileImage != null) {
            g2d.drawImage(profileImage, boxX, boxY, boxWidth, boxHeight, null);
        } else {
            g2d.setColor(player != null && player.getColor() != null ? player.getColor() : Color.GRAY);
            g2d.fillRect(boxX, boxY, boxWidth, boxHeight);
        }
        g2d.setClip(null);

        g2d.dispose();
    }
}