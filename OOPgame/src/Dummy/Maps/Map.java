/**
 * @Munin 10/3/25 - 16:28 - edited : เพิ่มการคำนวนตำแหน่งจาก GameObject.position
 * @Jeng {มาใส่วันที่ด้วย} created
 */
package Dummy.Maps;

import Core.ZhuzheeGame;
import ZhuzheeEngine.Scene.GameObject;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.Random;
import java.util.ArrayList;

public class Map extends GameObject {
    /// กำหนดค่าความกว้างของ map ได้ใน attribute นี้เลย
    private final int rows = 10; // ความกว้าง
    private final int cols = 10; // ความสูง
    private final int citiesCount = 9;
    private final int districtCount = 2;
//    private int maxCitiesPerDistrict = 6;
    private final int maxGridPerCties = 12;
    private City[] cities;
    private final Grid[][] board; // array ของช่องแต่ละช่องว่าเป็น city หรือ water

    public Map() {
        super(0, 0, 1280, 720, ZhuzheeGame.MAIN_SCENE);
        board = GenerateMap();
    }

    private Grid[][] GenerateMap() {
        Grid[][] grid = new Grid[rows][cols];

        Random random = new Random();
        ArrayList<District> districts = new ArrayList<>(districtCount);

        while (districts.size() < districtCount) {
            int r = random.nextInt(1, 5) * 255 / 5;
            int g = random.nextInt(1, 5) * 255 / 5;
            int b = random.nextInt(1, 5) * 255 / 5;
            District district = new District("District Test : " + districts.size());
            district.setColor(new Color(r, g, b));
            districts.add(district);
        }

        //generate the city on grid
        for (int i = 0; i < citiesCount; i++) {
            City city = new City("City Test : " + i, random.nextInt(1, 5),
                                                            random.nextInt(1, 5),
                                                            random.nextInt(1, 5),
                                                            random.nextInt(1, 5));
            //random color
            int r = random.nextInt(1, 5) * 255 / 5;
            int g = random.nextInt(1, 5) * 255 / 5;
            int b = random.nextInt(1, 5) * 255 / 5;
            city.setColor(new Color(r, g, b));

            District district = districts.get(random.nextInt(districtCount));
            district.addCity(city);
            //random start position inside map
            Point startPosition = new Point(random.nextInt(grid.length), random.nextInt(grid[0].length));
            setCityOnGridMapByRandomWalk(grid, city, district, random.nextInt(1, maxGridPerCties), startPosition);
        }

        // check District and Cities in District
        for (District district : districts) {
            System.out.println(district.getDistrictName());
            for (City city : districts.get(districts.indexOf(district)).getCities()) {
                System.out.println(city.getCityName());
                city.printStats();
            }
        }

        return grid;
    }

    /**
     * set the `City` on grid map(2D Array) by random walk algorithm
     *
     * @param city          the city want to set on gridMap.
     * @param grid          grid Map.
     * @param gridCount     amount of grid on map that want to set city in.
     * @param startPosition start position of walker in grid map
     */
    private void setCityOnGridMapByRandomWalk(Grid[][] grid, City city, District district, int gridCount, Point startPosition) {

        int x = startPosition.x;
        int y = startPosition.y;

        Random random = new Random();
        int tilesCreated = 1;
        while (tilesCreated < gridCount) {

            // random direction
            int rX = random.nextInt(-1, 2), rY = random.nextInt(-1, 2);
            if (rX == 0) y += rY;
            else x += rX;
            //modulation for looping position for prevent walking out of map boundaries.
            //using Math.abs() for preventing negative numbers. ex. x = -1, y = 1 -> array have no index -1
            x = Math.abs(x) % grid.length;
            y = Math.abs(y) % grid[0].length;

            //if this grid doesn't have a city -> set the city to this grid
            if (grid[x][y] == null) {
                grid[x][y] = new Grid(city, district);

                //notify that this grid is have set city on.
                tilesCreated++;
            }
        }
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

        // Guard against null board - prevent NPE
        if (board == null) {
            System.err.println("Warning: Map.board is null, cannot render");
            return;
        }

        int radius = 25; // ใช้รัศมี 25 (เท่ากับ cellSize / 2 เดิม)

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
                // set color of grid
                if (board[i][j] == null) {
                    g2d.setColor(Color.WHITE);
                } else {
                    g2d.setColor(board[i][j].getCity().getColor());
                }

                // drawing
                g2d.fill(hexagon);
                if (board[i][j] != null) {
                    g2d.setColor(board[i][j].getDistrict().getColor());
                } else {
                    g2d.setColor(Color.BLACK);
                }
                g2d.setStroke(new BasicStroke(3));
                g2d.draw(hexagon);
            }
        }
        g2d.dispose();
    }
}
