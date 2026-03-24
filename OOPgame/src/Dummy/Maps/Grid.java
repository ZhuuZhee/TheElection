package Dummy.Maps;

import java.awt.*;
import java.awt.geom.Path2D;

public class Grid{
    private final Map map;
    private City city;
    private float x,y;
    private int gridX, gridY;
    private final float radius, xOffset;
    public Grid(Map map, City city, int gridX, int gridY, float radius, float xOffset) {
        setCity(city);
        this.map = map;
        this.xOffset = xOffset;
        this.radius = radius;
        setGridPosition(gridX,gridY);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setGridPosition(int x, int y){
        this.gridX = x;
        this.gridY = y;
        Point center = new Point(map.getWidth()/2,map.getHeight()/2);
        float halfSizeX = map.getGridMapWidth()/2;
        float halfSizeY = map.getGridMapHeight()/2;
        float xOffset = this.xOffset * map.getScaleRatio();
        float xPos = halfSizeX - x * map.getGridWidth() + xOffset + center.x;
        float yPos = halfSizeY - y * map.getGridHeight() + map.getGridHeight() + center.y;
        this.x = xPos;
        this.y = yPos;
    }

    private Path2D.Double createHexagon(float x, float y, float radius) {
        // ส่วนนี้ให้ AI ทำ
        Path2D.Double path = new Path2D.Double();
        for (int i = 0; i < 6; i++) {
            double angle = Math.PI / 3 * i + Math.PI / 6;
            // คำนวณพิกัดแบบทศนิยม (ไม่ต้อง cast เป็น int แล้ว)
            double px = x + radius * Math.cos(angle);
            double py = y + radius * Math.sin(angle);

            if (i == 0) {
                path.moveTo(px, py); // จุดเริ่มต้น
            } else {
                path.lineTo(px, py); // ลากเส้นไปยังจุดต่อไป
            }
        }
        path.closePath(); // ลากเส้นปิดกลับมาจุดเริ่มต้น
        return path;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public void render(Graphics2D g2d){
        setGridPosition(gridX, gridY);
        g2d.setColor(city.getColor());
        Path2D.Double hexagon = createHexagon(x, y,radius * map.getScaleRatio());

        g2d.fill(hexagon);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(3));
        g2d.draw(hexagon);
    }
}
