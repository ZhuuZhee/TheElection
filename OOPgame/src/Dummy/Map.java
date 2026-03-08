package Dummy;

import ZhuzheeEngine.Scene.GameObject;
import ZhuzheeEngine.Scene.Scene2D;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;

public class Map extends GameObject {
    public Map() {
        super(0, 0, 1280, 720);

    }

    private Path2D.Double createHexagon(double x, double y, double radius) {
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

    public void GenerateMap() {

    }

    @Override
    public void render(Graphics g) {
        super.render(g);
        int radius = 25; // ใช้รัศมี 25 (เท่ากับ cellSize / 2 เดิม)
        int rows = 10;
        int cols = 10;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // คำนวณระยะห่างทางคณิตศาสตร์สำหรับหกเหลี่ยมแบบรังผึ้ง
        double hexWidth = Math.sqrt(3) * radius; // ระยะห่างแนวนอนระหว่างชิ้น
        double vertSpacing = 1.5 * radius;       // ระยะห่างแนวตั้งระหว่างแถว

        for (int i = 0; i < rows; i++) {
            if (i % 2 != 0) { cols = 11; }
            else { cols = 10; }
            for (int j = 0; j < cols; j++) {

                // 1. คำนวณจุดศูนย์กลาง x, y พื้นฐาน
                double x = j * hexWidth;
                double y = i * vertSpacing;

                // 2. สลับฟันปลา: ถ้าเป็นแถวคี่ ให้ขยับแกน x ถอยไปทางขวาครึ่งช่อง
                if (i % 2 == 0) {
                    x += hexWidth / 2;
                }

                // 3. บวกค่าชดเชยเริ่มต้น (Offset) ไม่ให้หกเหลี่ยมตกขอบซ้ายบนของจอ
                int cx = (int) (x + radius);
                int cy = (int) (y + radius);

                Path2D.Double hexagon = createHexagon(cx, cy, radius);

                // 4. วาดรูปหกเหลี่ยม
                g2d.setColor(Color.GREEN);
                g2d.fill(hexagon);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(3));
                g2d.draw(hexagon);
            }
        }
    }
}
