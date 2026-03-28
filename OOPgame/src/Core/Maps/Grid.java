package Core.Maps;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.json.JSONArray;

public class Grid {
    private final Core.Maps.Map map;
    private City city;
    private float x, y;
    private int gridX, gridY;
    private final float radius, xOffset;
    private boolean isHovered = false;
    private float currentScale = 1.0f;
    private static final float TARGET_SCALE = 1.25f; // เวลาชี้เมาส์ให้ใหญ่ขึ้น
    private static final float NORMAL_SCALE = 1.0f;
    private static final float LERP_SPEED = 15.0f;
    private Path2D.Double hexagon;

    private static java.util.Map<String, Image> profileImageCache = new HashMap<>();

    public Grid(Core.Maps.Map map, City city, int gridX, int gridY, float radius, float xOffset) {
        setCity(city);
        this.map = map;
        this.xOffset = xOffset;
        this.radius = radius;
        setGridPosition(gridX, gridY);
    }

    public void setHovered(boolean hovered) { isHovered = hovered; }
    public boolean isHovered() { return isHovered; }
    public float getX() { return x; }
    public float getY() { return y; }

    public boolean contains(Point p) {
        if (hexagon == null) return false;
        return hexagon.contains(p.x, p.y);
    }

    public void setGridPosition(int x, int y) {
        this.gridX = x;
        this.gridY = y;
        Point center = new Point(map.getWidth() / 2, map.getHeight() / 2);
        float halfSizeX = map.getGridMapWidth() / 2;
        float halfSizeY = map.getGridMapHeight() / 2;
        float scaledOffset = this.xOffset * map.getScaleRatio();
        float xPos = halfSizeX - x * map.getGridWidth() + scaledOffset + center.x;
        float yPos = halfSizeY - y * map.getGridHeight() + map.getGridHeight() + center.y;
        this.x = xPos;
        this.y = yPos;
    }

    private Path2D.Double createHexagon(float x, float y, float currentRadius) {
        Path2D.Double path = new Path2D.Double();
        for (int i = 0; i < 6; i++) {
            double angle = Math.PI / 3 * i + Math.PI / 6;
            double px = x + currentRadius * Math.cos(angle);
            double py = y + currentRadius * Math.sin(angle);

            if (i == 0) path.moveTo(px, py);
            else path.lineTo(px, py);
        }
        path.closePath();
        return path;
    }

    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }

    public void animation(float deltaTime) {
        float target = isHovered ? TARGET_SCALE : NORMAL_SCALE;

        if (Math.abs(currentScale - target) > 0.001f) {
            currentScale += (target - currentScale) * LERP_SPEED * deltaTime;
        } else {
            currentScale = target;
        }
    }

    private Core.Player.Player getDominatingPlayer() {
        if (city == null || city.playerScores == null) return null;
        java.util.List<Core.Player.Player> players = Core.ZhuzheeGame.CURRENT_PLAYERS;
        if (players == null || players.isEmpty()) return null;

        double totalScore = 0;
        for (double score : city.playerScores) {
            totalScore += score;
        }
        if (totalScore == 0) return null;

        double highestPct = -1;
        double secondHighestPct = -1;
        int leaderId = -1;

        for (int i = 0; i < city.playerScores.length; i++) {
            double pct = (city.playerScores[i] / totalScore) * 100.0;
            if (pct > highestPct) {
                secondHighestPct = highestPct;
                highestPct = pct;
                leaderId = i;
            } else if (pct > secondHighestPct) {
                secondHighestPct = pct;
            }
        }

        if (highestPct - secondHighestPct <= 1.0) {
            return null; // สีเทาถ้าสูสีกันไม่เกิน 1%
        }

        if (leaderId >= 0 && leaderId < players.size()) {
            return players.get(leaderId);
        }
        return null;
    }

    private Image getPlayerProfileImage(Core.Player.Player player) {
        if (player == null) return null;
        String path = player.getProfileImagePath();
        if (path == null) return null;

        if (profileImageCache.containsKey(path)) {
            return profileImageCache.get(path);
        }

        try {
            Image img = ImageIO.read(player.getProfileImageFile());
            profileImageCache.put(path, img);
            return img;
        } catch (Exception e) {
            System.err.println("Cannot load profile image: " + path);
            profileImageCache.put(path, null);
            return null;
        }
    }

    public void render(Graphics2D g2d) {
        setGridPosition(gridX, gridY);

        float finalRadius = radius * map.getScaleRatio() * currentScale;
        if (finalRadius <= 0) return;

        hexagon = createHexagon(x, y, finalRadius);

        // --- 1. วาด Drop Shadow ของตาราง ---
        float shadowOffset = isHovered ? (8f * map.getScaleRatio()) : (3f * map.getScaleRatio());
        Path2D.Double shadowHex = createHexagon(x, y + shadowOffset, finalRadius);
        g2d.setColor(new Color(0, 0, 0, 90));
        g2d.fill(shadowHex);

        // --- 2. วาดพื้นผิวตัวหลักแบบไล่สี ---
        Core.Player.Player owner = getDominatingPlayer();
        Color baseColor = (owner != null && owner.getColor() != null) ? owner.getColor() : new Color(120, 120, 120); // สีเทาถ้าสูสีหรือไม่มีเจ้าของ

        Color lightColor = getLighterColor(baseColor, 0.3f);
        Color darkColor = getDarkerColor(baseColor, 0.2f);

        GradientPaint gradient = new GradientPaint(
                x, y - finalRadius, lightColor,
                x, y + finalRadius, darkColor
        );
        g2d.setPaint(gradient);
        g2d.fill(hexagon);

        // --- 3. วาดรูปภาพเจ้าของเมืองจางๆ (ถ้ามี) ---
        if (owner != null) {
            Image profileImg = getPlayerProfileImage(owner);
            if (profileImg != null) {
                Shape oldClip = g2d.getClip();
                g2d.setClip(hexagon);

                Composite oldComposite = g2d.getComposite();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f)); // opacity จางๆ

                float imgSize = finalRadius * 1.8f; // ขนาดรูปเกือบเต็มหกเหลี่ยม
                g2d.drawImage(profileImg, (int)(x - imgSize/2), (int)(y - imgSize/2), (int)imgSize, (int)imgSize, null);

                g2d.setComposite(oldComposite);
                g2d.setClip(oldClip);
            }
        }

        // --- 4. วาด Inner Bevel (ขอบแสงสันนูนด้านใน) ---
        Path2D.Double innerHex = createHexagon(x, y, finalRadius * 0.9f);
        g2d.setStroke(new BasicStroke(finalRadius * 0.08f));
        g2d.setColor(new Color(255, 255, 255, 40));
        g2d.draw(innerHex);

        // --- 5. วาดเส้นขอบนอกสุด ---
        if (isHovered) {
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(4 * map.getScaleRatio()));
        } else {
            g2d.setColor(getDarkerColor(baseColor, 0.6f));
            g2d.setStroke(new BasicStroke(2 * map.getScaleRatio()));
        }
        g2d.draw(hexagon);
    }

    private Color getLighterColor(Color color, float factor) {
        int r = Math.min(255, (int)(color.getRed() + (255 - color.getRed()) * factor));
        int g = Math.min(255, (int)(color.getGreen() + (255 - color.getGreen()) * factor));
        int b = Math.min(255, (int)(color.getBlue() + (255 - color.getBlue()) * factor));
        return new Color(r, g, b, color.getAlpha());
    }

    private Color getDarkerColor(Color color, float factor) {
        int r = Math.max(0, (int)(color.getRed() * (1 - factor)));
        int g = Math.max(0, (int)(color.getGreen() * (1 - factor)));
        int b = Math.max(0, (int)(color.getBlue() * (1 - factor)));
        return new Color(r, g, b, color.getAlpha());
    }

    public JSONArray toJsonPosition() {
        return new JSONArray(new int[]{gridX, gridY});
    }
}