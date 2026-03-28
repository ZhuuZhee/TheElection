package Core.UI;

import Core.ZhuzheeGame;
import Core.Player.Player;
import ZhuzheeEngine.Scene.*;
import ZhuzheeEngine.Scene.Canvas;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;

public class PlayerProfile extends Canvas {
    private final Player player;
    private final int panelWidth = 180;
    private final int panelHeight = 220;
    private Image profileImage;

    // Dummy value for percentage for now
    private float partyListPercentage = 0.45f;

    public PlayerProfile(Scene2D scene, Player player) {
        super(scene);
        this.player = player;

        // ขวาล่าง (เว้นที่ให้ GameEventLog ด้านล่าง)
        setPanelSize(panelWidth, panelHeight);
        setAnchors(1, -1);
        setMargins(0, 20, 230, 20); // bottom margin 230 เพื่อให้ลอยอยู่เหนือ GameLogUI
        setOpaque(false);

        updateProfile();
        setVisible(true);
    }

    public void updateProfile() {
        if (player == null) return;

        File imageFile = player.getProfileImageFile();
        if (imageFile != null && imageFile.exists()) {
            ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
            if (icon.getIconWidth() > 0) {
                profileImage = icon.getImage();
            } else {
                profileImage = null;
            }
        } else {
            profileImage = null;
        }

        repaint();
    }

    public void setPartyListPercentage(float percentage) {
        this.partyListPercentage = Math.max(0f, Math.min(1f, percentage));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Sizes and Positions
        int barWidth = 15;
        int barHeight = 170; // Height covering image + name
        int barX = 0;
        int barY = 25; // Top alignment

        int boxX = barWidth + 12;
        int boxY = 25;
        int boxWidth = width - boxX - 10;
        int boxHeight = 140; // Square-ish
        int arcSize = 25;

        // 1. Draw Vertical Bar (PartyList %)
        // Background of bar (White)
        g2d.setColor(Color.WHITE);
        g2d.fillRect(barX, barY, barWidth, barHeight);
        
        // Fill of bar (Blue) based on percentage (from bottom up)
        int fillHeight = (int) (barHeight * partyListPercentage);
        g2d.setColor(new Color(0, 51, 153)); // Dark blue
        g2d.fillRect(barX, barY + barHeight - fillHeight, barWidth, fillHeight);

        // 2. Draw Profile Box (White background with rounded corners)
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(boxX, boxY, boxWidth, boxHeight, arcSize, arcSize);

        // Draw Profile Image inside the box
        Shape clip = new RoundRectangle2D.Float(boxX, boxY, boxWidth, boxHeight, arcSize, arcSize);
        g2d.setClip(clip);
        if (profileImage != null) {
            // Draw image maintaining aspect ratio or scaling to fit
            g2d.drawImage(profileImage, boxX, boxY, boxWidth, boxHeight, null);
        } else {
            g2d.setColor(player != null ? player.getColor() : Color.GRAY);
            g2d.fillRect(boxX, boxY, boxWidth, boxHeight);
        }
        g2d.setClip(null); // Remove clip

        // 3. Draw Name (Outside, below the image box)
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 20)); // Adjusted font to look like the design
        String name = player != null ? player.getPlayerName() : "Name";
        FontMetrics fm = g2d.getFontMetrics();
        // Position name slightly below the image box, aligned to the left of the image box
        int nameY = boxY + boxHeight + 25; 
        g2d.drawString(name, boxX, nameY);

        // 4. Draw Bottom Right Oval Button/Badge
        int badgeWidth = 55;
        int badgeHeight = 35;
        // Positioned overlapping the bottom right corner of the profile box
        int badgeX = boxX + boxWidth - badgeWidth + 10; 
        int badgeY = boxY + boxHeight - badgeHeight + 15;
        
        // Draw oval (badge)
        g2d.setColor(new Color(150, 150, 150, 200)); // Grey semi-transparent like the image
        g2d.fillRoundRect(badgeX, badgeY, badgeWidth, badgeHeight, badgeHeight, badgeHeight);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(badgeX, badgeY, badgeWidth, badgeHeight, badgeHeight, badgeHeight);

        g2d.dispose();
    }
}
