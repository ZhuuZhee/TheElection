/** @Munin 10/3/25 - 16:28 - edited : เพิ่มการคำนวนตำแหน่งจาก GameObject.position
 * @Jeng {มาใส่วันที่ด้วย} created
 */
package Dummy.Maps;

import Core.ZhuzheeGame;
import ZhuzheeEngine.Scene.GameObject;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.Random;

public class Map extends GameObject {
    /// กำหนดค่าความกว้างของ map ได้ใน attribute นี้เลย
    private final int rows = 10; // ความกว้าง
    private final int cols = 10; // ความสูง
    private final District district;
    private final City city;
    private final District[][] board; // array ของช่องแต่ละช่องว่าเป็น city หรือ water

    public Map() {
        super(0, 0, 1280, 720, ZhuzheeGame.MAIN_SCENE);
        district = new District(10, 10);
        city = new City(1, 1, 1, 2, 10);
        board = GenerateMap();
    }

    private District[][] GenerateMap() {
        District[][] grid = new District[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = district;
            }
        }
        int currentX = rows / 2;
        int currentY = cols / 2;

        int tilesCreated = 1;
        Random random = new Random();

        int x = 0, y = 0;

        // สามารถเปลี่ยนจำนวนช่องที่เป็น water ได้ตรงนี้
        while (tilesCreated < 15) {
            int direction = random.nextInt(4);
            if (direction == 0) { y = currentY - 1; }
            else if (direction == 1) {  y = currentY + 1; }
            else if (direction == 2) { x = currentX - 1; }
            else { x = currentX + 1; }

            if (x >= 0 && x < rows && y >= 0 && y < cols) {
                currentX = x;
                currentY = y;
                if (grid[x][y] != null) {
                    grid[x][y] = null;
                    tilesCreated++;
                }
            }
        }
        return grid;
    }

    private Path2D.Double createHexagon(double x, double y, double radius) {
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

    @Override
    public void render(Graphics g) {
        super.render(g);
        int radius = 25; // ใช้รัศมี 25 (เท่ากับ cellSize / 2 เดิม)

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // คำนวณระยะห่างทางคณิตศาสตร์สำหรับหกเหลี่ยมแบบรังผึ้ง
        double hexWidth = Math.sqrt(3) * radius; // ระยะห่างแนวนอนระหว่างชิ้น
        double vertSpacing = 1.5 * radius;       // ระยะห่างแนวตั้งระหว่างแถว


        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                // 1. คำนวณจุดศูนย์กลาง x, y พื้นฐาน
                double x = j * hexWidth;
                double y = i * vertSpacing;

                // @Munin 10/3/25 - 16:28 - edited
                //บวกตำแหน่งของ GameObject นี้ เพื่อใช้เป็นจุดอ้างอิง
                // ex. เมื่อขยับ position gameObject นี้เป็น Point(20,5) -> ตำแหน่งของ grid แรกจะเป็น 20, 5
                // (ถ้าไม่บวก GameObject.position จะเป็น 0,0 ทั้งที่ตำแหน่ง GameObject ขยับเป็น 20,5 แล้ว)
                x += position.x;
                y += position.y;

                // 2. สลับฟันปลา: ถ้าเป็นแถวคี่ ให้ขยับแกน x ถอยไปทางขวาครึ่งช่อง
                if (i % 2 == 0) {
                    x += hexWidth / 2;
                }

                // 3. บวกค่าชดเชยเริ่มต้น (Offset) ไม่ให้หกเหลี่ยมตกขอบซ้ายบนของจอ
                int cx = (int) (x + radius);
                int cy = (int) (y + radius);

                Path2D.Double hexagon = createHexagon(cx, cy, radius);

                // 4. วาดรูปหกเหลี่ยม
                if (board[i][j] == null) {
                    g2d.setColor(Color.BLUE);
                } else {
                    g2d.setColor(Color.GREEN);
                }
                g2d.fill(hexagon);
                if (board[i][j] != null) {
                    g2d.setColor(Color.BLACK);
                } else {
                    g2d.setColor(Color.RED);
                }
                g2d.setStroke(new BasicStroke(3));
                g2d.draw(hexagon);
            }
        }
    }
}
