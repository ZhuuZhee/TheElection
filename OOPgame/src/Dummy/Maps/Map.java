/**
 * @Munin 10/3/25 - 16:28 - edited : เพิ่มการคำนวนตำแหน่งจาก GameObject.position
 * @Jeng {มาใส่วันที่ด้วย} created
 */
package Dummy.Maps;

import Core.ZhuzheeGame;
import ZhuzheeEngine.Scene.GameObject;

import java.awt.*;
import java.util.Random;
import java.util.ArrayList;

public class Map extends GameObject {
    /// กำหนดค่าความกว้างของ map ได้ใน attribute นี้เลย
    private final int rows = 10; // ความกว้าง
    private final int cols = 10; // ความสูง
    private final int citiesCount = 9;
    private final int districtCount = 4;
    private final int maxGridPerCties = 12;
    private City[] cities;
    private final Grid[][] gridMap;// array ของช่องแต่ละช่องว่าเป็น city หรือ water
    private final float radius = 25;
    private Point startSize;
    private float scaleRatio = 1;
    public Map() {
        super(-1280/2, -720/2, 1280, 720, ZhuzheeGame.MAIN_SCENE);
        startSize = new Point(getWidth(), getHeight());
        gridMap = GenerateMap();
//        setBackground(Color.CYAN);
//        setOpaque(true);
    }

    private Grid[][] GenerateMap() {
        Grid[][] grid = new Grid[rows][cols];

        Random random = new Random();
        ArrayList<District> districts = new ArrayList<>(districtCount);

        while (districts.size() < districtCount) {

            //random color of district
            int r = random.nextInt(1, 5) * 255 / 5;
            int g = random.nextInt(1, 5) * 255 / 5;
            int b = random.nextInt(1, 5) * 255 / 5;

            District district = new District("District Test : " + districts.size());
            district.setColor(new Color(r, g, b));
            districts.add(district);
        }
        int maxCitiesPerDistrict = citiesCount / districtCount + citiesCount % districtCount;
        int currCitiesPerDistrict = 0;
        District district = null;
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

            //randomly set city inside district
            if(currCitiesPerDistrict <= 0 && districts.size() > 1){
                districts.remove(district);
                currCitiesPerDistrict = random.nextInt(1,maxCitiesPerDistrict);
                district = districts.getFirst();
            }
            if(district == null){
                System.err.println("District is null");
                throw new NullPointerException();
            }
            district.addCity(city);
            currCitiesPerDistrict--;
            //random start position inside map
            Point startPosition = new Point(random.nextInt(grid.length), random.nextInt(grid[0].length));
            setCityOnGridMapByRandomWalk(grid, city, district, random.nextInt(1, maxGridPerCties), startPosition);
        }

        // check District and Cities in District
        for (District dt : districts) {
            System.out.println(dt.getDistrictName());
            for (City city : districts.get(districts.indexOf(dt)).getCities()) {
                System.out.println(city.getCityName());
                city.printStats();
            }
            System.out.println();
        }
        return grid;
    }
    public float getScaleRatio(){
        return scaleRatio;
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
                float xOffset = y % 2 == 0? getGridWidth() : getGridWidth()/2;
                grid[x][y] = new Grid(this,city, district, x,y, radius, xOffset);

                //notify that this grid is have set city on.
                tilesCreated++;
            }
        }
    }
    public float getGridWidth(){
        // คำนวณระยะห่างทางคณิตศาสตร์สำหรับหกเหลี่ยมแบบรังผึ้ง
        return (float) Math.sqrt(3) * radius * scaleRatio; // ระยะห่างแนวนอนระหว่างชิ้น
    }
    public float getGridHeight(){
        return (float) 1.5 * radius * scaleRatio;
    }
    public float getGridMapWidth(){
        return cols * getGridWidth();
    }
    public float getGridMapHeight(){
        return rows * getGridHeight();
    }


    @Override
    public void paintComponent(Graphics g) {
        scaleRatio = (float) getWidth()/(float)startSize.x;
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
