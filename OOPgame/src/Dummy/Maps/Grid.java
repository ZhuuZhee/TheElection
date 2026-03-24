package Dummy.Maps;

import java.awt.*;
import java.awt.geom.Path2D;

public class Grid {
    private District district;
    private City city;
    private final int rows;
    private final int cols;

    public Grid(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
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

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public void render(Graphics g, int panelWidth, int panelHeight) {
        // คำนวณ radius ให้พอดีกับขนาด panel
        // แนวนอน: cols * sqrt(3) * radius <= panelWidth
        // แนวตั้ง: rows * 1.5 * radius <= panelHeight
        int radiusFromWidth  = (int) (panelWidth  / (cols * Math.sqrt(3)));
        int radiusFromHeight = (int) (panelHeight / (rows * 1.5));
        int radius = Math.min(radiusFromWidth, radiusFromHeight); // เลือกอันเล็กกว่า ไม่ให้ล้น

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // คำนวณระยะห่างทางคณิตศาสตร์สำหรับหกเหลี่ยมแบบรังผึ้ง
        double hexWidth = Math.sqrt(3) * radius; // ระยะห่างแนวนอนระหว่างชิ้น
        double vertSpacing = 1.5 * radius;       // ระยะห่างแนวตั้งระหว่างแถว

        double halfSizeX = cols * hexWidth / 2;
        double halfSizeY = rows * vertSpacing / 2;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                // 1. คำนวณจุดศูนย์กลาง x, y พื้นฐาน
                // ตำแน่งปัจจุุบัน - ขนาดครึ่งนึงของ Map
                // ex. ตำแหน่งแรกก่อนลบ ครึ่งนึง = 0,0 ซึ่งอยู่ตรงกลาง ตำแหน่งถัดไปจะเป็น 1,0 ซึ่งไปทางขาวและลงไปเรื่อยๆ
                // แต่เราอยากให้ตำแหน่งตรงกลางคือ ครึ่งนึง ดังนั้นจึงเอา ครึ่งนึง มาลบ
                double x = j * hexWidth - halfSizeX;
                double y = i * vertSpacing - halfSizeY;

                // @Munin 10/3/25 - 16:28 - edited
                // บวกตำแหน่งของ GameObject นี้ เพื่อใช้เป็นจุดอ้างอิง
                // ex. เมื่อขยับ position gameObject นี้เป็น Point(20,5) -> ตำแหน่งของ grid แรกจะเป็น 20, 5
                // (ถ้าไม่บวก GameObject.position จะเป็น 0,0 ทั้งที่ตำแหน่ง GameObject ขยับเป็น 20,5 แล้ว)
//                x += position.x;
//                y += position.y;

                // 2. สลับฟันปลา: ถ้าเป็นแถวคี่ ให้ขยับแกน x ถอยไปทางขวาครึ่งช่อง
                if (i % 2 == 0) {
                    x += hexWidth / 2;
                }

                // 3. บวกค่าชดเชยเริ่มต้น (Offset) ไม่ให้หกเหลี่ยมตกขอบซ้ายบนของจอ
                int cx = (int) x;
                int cy = (int) y;

                Path2D.Double hexagon = createHexagon(cx, cy, radius);

                // 4. วาดรูปหกเหลี่ยม
                // set color of grid
//                if (gridMap[i][j] == null) {
//                    g2d.setColor(Color.WHITE);
//                } else {
//                    g2d.setColor(gridMap[i][j].getCity().getColor());
//                }
//
//                // drawing
//                g2d.fill(hexagon);
//                if (gridMap[i][j] != null) {
//                    g2d.setColor(gridMap[i][j].getDistrict().getColor());
//                } else {
//                    g2d.setColor(Color.BLACK);
//                }
                g2d.setColor(Color.GREEN);
                g2d.fill(hexagon);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(hexagon);
            }
        }
        g2d.dispose();
    }
}
