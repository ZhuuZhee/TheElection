/**
 * @Munin 10/3/25 - 16:28 - edited : เพิ่มการคำนวนตำแหน่งจาก GameObject.position
 * @Jeng {มาใส่วันที่ด้วย} created
 */
package Dummy.Maps;

import Core.ZhuzheeGame;
import ZhuzheeEngine.Scene.GameObject;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.ArrayList;
//import java.util.;

public class Map extends GameObject {
    /// กำหนดค่าความกว้างของ map ได้ใน attribute นี้เลย
    private final int rows = 10; // ความกว้าง
    private final int cols = 10; // ความสูง
    private final int citiesCount = 4;
    private final int maxGridPerCties = 20;
//    private City[] cities;
    private final Grid[][] gridMap;// array ของช่องแต่ละช่องว่าเป็น city หรือ water
    private final float radius = 100;
    private final Point startSize;
    private float scaleRatio = 1;
    private final int gap = 4;
    public Map() {
        super(-1000, -1000, 2000, 2000, ZhuzheeGame.MAIN_SCENE);
        startSize = new Point(getWidth(), getHeight());
        gridMap = GenerateMap();
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Grid clicked = getGridAtPoint(e.getPoint());
                if (clicked != null) {
                    clicked.getCity().printStats();
                    clicked.getCity().getVotingResults();
                }
            }
        });
//        this.addMouseMotionListener(new MouseMotionAdapter() {
//            @Override
//            public void mouseMoved(MouseEvent e) {
//                Grid clicked = getGridAtPoint(e.getPoint());
//                if (clicked != null) {
//                    System.out.println("I Luv this project");
//                }
//            }
//        });
    }

    public Grid getGridAtPoint(Point p) {
        for (Grid[] col : gridMap) {
            for (Grid grid : col) {
                if (grid != null && grid.contains(p)) {
                    return grid;
                }
            }
        }
        return null;
    }

    private Grid[][] GenerateMap() {
        Grid[][] grid = new Grid[rows][cols];
        Random random = new Random();

        Point startPosition = new Point(rows / 2, cols / 2);

        //generate the city on grid
        for (int i = 0; i < citiesCount; i++) {
            City city = new City("City Test : " + i, random.nextInt(1, 5),
                    random.nextInt(1, 5),
                    random.nextInt(1, 5),
                    random.nextInt(50000, 1000000));
            //random color
            int r = random.nextInt(1, 5) * 255 / 5;
            int g = random.nextInt(1, 5) * 255 / 5;
            int b = random.nextInt(1, 5) * 255 / 5;
            city.setColor(new Color(r, g, b));

            //randomly set city inside district

            //random start position inside map
            setCityOnGridMapByRandomWalk(grid, city, random.nextInt(15, maxGridPerCties), startPosition);
            startPosition = getRandomFilledTile(grid, random);
        }
        return grid;
    }

    private Point getRandomFilledTile(Grid[][] grid, Random random) {
        ArrayList<Point> filledTiles = new ArrayList<>();
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                if (grid[x][y] != null) filledTiles.add(new Point(x, y));
            }
        }
        if (filledTiles.isEmpty()) {
            return new Point(rows / 2, cols / 2);
        }
        return filledTiles.get(random.nextInt(filledTiles.size()));
    }

    public float getScaleRatio() {
        return scaleRatio;
    }
    /**
     * set the `City` on grid map(2D Array) by random walk algorithm
     *
     * @param city          the city want to set on gridMap.
     * @param grid          grid Map.
     * @param startPosition start position of walker in grid map
     */
    private void setCityOnGridMapByRandomWalk(Grid[][] grid, City city, int gridCount, Point startPosition) {

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
                float xOffset = y % 2 == 0? getGridWidth() : getGridWidth()/2;
                grid[x][y] = new Grid(this,city, x, y, radius, xOffset);

                //notify that this grid is have set city on.
                tilesCreated++;
            }
        }
    }

    public float getGridWidth() {
        return (float) Math.sqrt(3) * radius * scaleRatio + gap; // ระยะห่างแนวนอนระหว่างชิ้น
    }

    public float getGridHeight() {
        return (float) 1.5 * radius * scaleRatio + gap;
    }

    public float getGridMapWidth() {
        return cols * getGridWidth();
    }

    public float getGridMapHeight() {
        return rows * getGridHeight();
    }

    @Override
    public void paintComponent(Graphics g) {
        scaleRatio = (float) getWidth() / (float) startSize.x;
        super.paintComponent(g);
        // Guard against null board - prevent NPE
        if (gridMap == null) {
            System.err.println("Warning: Map.gridMap is null, cannot render");
            return;
        }
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for(Grid[] col : gridMap ){
            for(Grid grid : col){
                if(grid != null)
                    grid.render(g2d);
            }
        }
        g2d.dispose();
    }
}
