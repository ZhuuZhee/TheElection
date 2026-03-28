package Core.Maps;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Ellipse2D; // เพิ่ม Import สำหรับวาดวงกลมแบบทศนิยม (สมูทกว่า)
import java.awt.geom.Point2D;
import org.json.JSONArray;

public class Grid {
    private final Map map;
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

    public Grid(Map map, City city, int gridX, int gridY, float radius, float xOffset) {
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

    /**
     * ฟังก์ชันหาว่า Player คนไหนมีคะแนนนำ (ครองพื้นที่นี้อยู่)
     * @return สีของ Player ที่นำอยู่ (ถ้าคะแนน 0 หมด จะคืนค่า null)
     */
    private Color getLeadingPlayerColor() {
        if (city == null || city.playerScores == null) return null;
        java.util.List<Core.Player.Player> players = Core.ZhuzheeGame.CURRENT_PLAYERS;
        if (players == null || players.isEmpty()) return null;

        int leadingPlayerIndex = -1;
        int maxScore = 0;

        // วนหาคนที่คะแนนมากที่สุด (และต้องมากกว่า 0)
        for (int i = 0; i < city.playerScores.length; i++) {
            if (i < players.size() && city.playerScores[i] > maxScore) {
                maxScore = (int) city.playerScores[i];
                leadingPlayerIndex = i;
            }
        }

        // ถ้าเจอคนนำ ให้ส่งสีของคนนั้นกลับไป
        if (leadingPlayerIndex != -1 && players.get(leadingPlayerIndex) != null) {
            return players.get(leadingPlayerIndex).getColor();
        }
        return null; // ยังไม่มีใครยึดเมืองนี้
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
        Color baseColor = city.getColor();
        Color lightColor = getLighterColor(baseColor, 0.3f);
        Color darkColor = getDarkerColor(baseColor, 0.2f);

        GradientPaint gradient = new GradientPaint(
                x, y - finalRadius, lightColor,
                x, y + finalRadius, darkColor
        );
        g2d.setPaint(gradient);
        g2d.fill(hexagon);

        // --- 3. วาด Inner Bevel (ขอบแสงสันนูนด้านใน) ---
        Path2D.Double innerHex = createHexagon(x, y, finalRadius * 0.9f);
        g2d.setStroke(new BasicStroke(finalRadius * 0.08f));
        g2d.setColor(new Color(255, 255, 255, 40));
        g2d.draw(innerHex);

        // --- 4. วาดเส้นขอบนอกสุด ---
        if (isHovered) {
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(4 * map.getScaleRatio()));
        } else {
            g2d.setColor(getDarkerColor(baseColor, 0.6f));
            g2d.setStroke(new BasicStroke(2 * map.getScaleRatio()));
        }
        g2d.draw(hexagon);


        // ========================================================
        // 5. วาดจุดบอกสถานะการยึดครอง (Owner Marker) ตรงกลางหกเหลี่ยม
        // ========================================================
        Color ownerColor = getLeadingPlayerColor();
        if (ownerColor != null) {
            // ขนาดของจุด (ตั้งไว้ที่ 25% ของขนาด Grid)
            float dotRadius = finalRadius * 0.25f;
            float dotSize = dotRadius * 2;

            // 5.1 เงาของจุด (ทำให้ดูเหมือนเม็ดนูนขึ้นมา)
            Ellipse2D.Double shadowDot = new Ellipse2D.Double(x - dotRadius + 2, y - dotRadius + 2, dotSize, dotSize);
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.fill(shadowDot);

            // 5.2 สีหลักของจุด (สีของ Player ที่ยึด)
            Ellipse2D.Double mainDot = new Ellipse2D.Double(x - dotRadius, y - dotRadius, dotSize, dotSize);
            g2d.setColor(ownerColor);
            g2d.fill(mainDot);

            // 5.3 แสงตกกระทบ (Highlight) ให้ดูเป็นอัญมณี
            Ellipse2D.Double highlightDot = new Ellipse2D.Double(x - dotRadius * 0.5f, y - dotRadius * 0.7f, dotRadius, dotRadius);
            g2d.setColor(new Color(255, 255, 255, 120));
            g2d.fill(highlightDot);

            // 5.4 เส้นขอบของจุด
            g2d.setColor(getDarkerColor(ownerColor, 0.5f));
            g2d.setStroke(new BasicStroke(1.5f * map.getScaleRatio()));
            g2d.draw(mainDot);
        }
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