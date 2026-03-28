/**
 * @Munin 10/3/25 - 16:28 - edited : เพิ่มการคำนวนตำแหน่งจาก GameObject.position
 * @Jeng {มาใส่วันที่ด้วย} created
 * @Update : ระบบ Render น้ำเป็น Pre-calculated Heightmap แบบ Seamless (ไร้รอยต่อ + ลื่นที่สุด)
 */
package Core.Maps;

import Core.Player.Player;
import Core.ZhuzheeGame;
import ZhuzheeEngine.Scene.GameObject;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.HashMap;
import java.util.Random;
import java.util.ArrayList;

public class Map extends GameObject {
    private final int rows; 
    private final int cols; 
    private final int citiesCount;
    private final int numPlayers;
    private static final int DEFAULT_ROWS = 12;
    private static final int DEFAULT_COLS = 12;
    private static final int DEFAULT_CITIES_COUNT = 8;
    private final int maxGridPerCties = 12;
    private final int minStats = 1;
    private final int maxStats = 5;
    private final int minPopulation = 50000;
    private final int maxPopulation = 1000001;
    private final int gap = 4;
    private final float radius = 84;
    private float scaleRatio = 1;
    private final Point startSize;
    private final Grid[][] gridMap;
    private Grid currentHoveredGrid = null;
    private Grid currentClickedGrid = null;

    // --- ระบบวาดน้ำแบบ Pre-calculated Heightmap (ลื่นที่สุด) ---
    private BufferedImage waterBackground = null;
    private int[] waterPixels;
    private int renderWidth, renderHeight;
    private final int PIXEL_SIZE = 1;
    private float timeElapsed = 0f; 
    private final float WATER_SPEED = 20f; 

    // --- Heightmap Data ---
    private float[][] heightMap;
    private final int HEIGHTMAP_SIZE = 1024; // ขนาดของแผนที่ความสูง

    // --- ชุดสีน้ำทะเลสไตล์ Pixel Art Shader ---
    private final int DEEP_WATER    = 0xFF1A4770;
    private final int MID_WATER     = 0xFF246A9C;
    private final int CAUSTIC_LINE  = 0xFF3EA3D9;
    private final int SPARKLE_FOAM  = 0xFFE5F8FF;

    public Map() { this(DEFAULT_ROWS, DEFAULT_COLS, DEFAULT_CITIES_COUNT, new Random().nextLong(), 4); }
    public Map(long seed) { this(DEFAULT_ROWS, DEFAULT_COLS, DEFAULT_CITIES_COUNT, seed, 4); }
    public Map(long seed, int numPlayers) { this(DEFAULT_ROWS, DEFAULT_COLS, DEFAULT_CITIES_COUNT, seed, numPlayers); }
    public Map(int rows, int cols, int citiesCount, long seed) { this(rows, cols, citiesCount, seed, 4); }

    public Map(int rows, int cols, int citiesCount, long seed, int numPlayers) {
        super(-2000, -1500, 4000, 3000, ZhuzheeGame.MAIN_SCENE);
        this.rows = rows;
        this.cols = cols;
        this.citiesCount = citiesCount;
        this.numPlayers = Math.max(1, numPlayers);
        
        startSize = new Point(Math.max(1, getWidth()), Math.max(1, getHeight()));

        // สร้าง Heightmap เก็บไว้ใน RAM รอบเดียวตอนเริ่มเกม
        generateHeightMap(seed);

        initWaterBuffer();
        gridMap = generateMap(seed);

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Grid clicked = getGridAtPoint(e.getPoint());
                if (currentHoveredGrid != clicked) {
                    if (currentHoveredGrid != null) currentHoveredGrid.setHovered(false);
                    currentHoveredGrid = clicked;
                    if (currentHoveredGrid != null) currentHoveredGrid.setHovered(true);
                }
            }
        });
        
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Grid clicked = getGridAtPoint(e.getPoint());
                if (clicked != null) {
                    currentClickedGrid = clicked;
                    clicked.getCity().printStats();
                    clicked.getCity().getVotingResults();
                } else {
                    currentClickedGrid = null;
                }
            }
        });
    }

    /**
     * คำนวณ Noise ทั้งหมดล่วงหน้า แล้วเก็บเป็น Array 2 มิติ (ใช้เป็น Heightmap)
     */
    private void generateHeightMap(long seed) {
        heightMap = new float[HEIGHTMAP_SIZE][HEIGHTMAP_SIZE];
        Random random = new Random(seed);
        float offsetX = random.nextFloat() * 1000f;
        float offsetY = random.nextFloat() * 1000f;

        for (int y = 0; y < HEIGHTMAP_SIZE; y++) {
            for (int x = 0; x < HEIGHTMAP_SIZE; x++) {
                // ใช้ Seamless Noise เพื่อไม่ให้เกิดรอยต่อสี่เหลี่ยม
                float noise1 = seamlessNoise(x, y, 0.015f, offsetX, offsetY);
                float noise2 = seamlessNoise(x, y, 0.04f, -offsetX, -offsetY) * 0.5f;
                
                // เก็บค่าความสูงไว้ใน Array
                heightMap[x][y] = Math.clamp(noise1 + noise2, 0f, 1f);
            }
        }
    }

    /**
     * สร้าง Noise แบบเนียนกริ๊บ ไม่มีรอยต่อ (Seamless Tiling)
     */
    private float seamlessNoise(int x, int y, float scale, float offsetX, float offsetY) {
        float s = (float) x / HEIGHTMAP_SIZE;
        float t = (float) y / HEIGHTMAP_SIZE;

        float dx = HEIGHTMAP_SIZE * scale;
        float dy = HEIGHTMAP_SIZE * scale;

        float x1 = x * scale + offsetX;
        float y1 = y * scale + offsetY;

        float val00 = simpleNoise(x1, y1);
        float val10 = simpleNoise(x1 - dx, y1);
        float val01 = simpleNoise(x1, y1 - dy);
        float val11 = simpleNoise(x1 - dx, y1 - dy);

        float blendX = s * s * (3.0f - 2.0f * s);
        float blendY = t * t * (3.0f - 2.0f * t);

        float top = val00 * (1.0f - blendX) + val10 * blendX;
        float bottom = val01 * (1.0f - blendX) + val11 * blendX;
        
        return top * (1.0f - blendY) + bottom * blendY;
    }
    public ArrayList<City> getAllCities() {
        ArrayList<City> uniqueCities = new ArrayList<>();
        if (gridMap != null) {
            for (Grid[] col : gridMap) {
                for (Grid grid : col) {
                    if (grid != null && grid.getCity() != null) {
                        if (!uniqueCities.contains(grid.getCity())) {
                            uniqueCities.add(grid.getCity());
                        }
                    }
                }
            }
        }
        return uniqueCities;
    }

    public int getOwnedCitiesCount(String playerId) {
        int count = 0;
        for (City city : getAllCities()) {
            if (city.getOwnerId() == playerId) {
                count++;
            }
        }
        return count;
    }
    /**
     * ฟังก์ชันพื้นฐานสำหรับสร้าง Noise
     */
    private float simpleNoise(float x, float y) {
        int xi = (int) Math.floor(x);
        int yi = (int) Math.floor(y);
        float xf = x - xi;
        float yf = y - yi;
        float u = xf * xf * (3.0f - 2.0f * xf);
        float v = yf * yf * (3.0f - 2.0f * yf);
        float a = randomHash(xi, yi);
        float b = randomHash(xi + 1, yi);
        float c = randomHash(xi, yi + 1);
        float d = randomHash(xi + 1, yi + 1);
        float x1 = a + u * (b - a);
        float x2 = c + u * (d - c);
        return x1 + v * (x2 - x1);
    }

    private float randomHash(int x, int y) {
        int n = x + y * 57;
        n = (n << 13) ^ n;
        float res = (float) (1.0 - ((n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0);
        return (res + 1.0f) / 2.0f; 
    }

    /**
     * ฟังก์ชันอ่านค่าจาก Heightmap แบบ Seamless (ไหลวนลูปไม่มีที่สิ้นสุด)
     */
    private float sampleHeightMap(float x, float y) {
        int ix = ((int) x) & (HEIGHTMAP_SIZE - 1);
        int iy = ((int) y) & (HEIGHTMAP_SIZE - 1);
        return heightMap[ix][iy];
    }

    private void initWaterBuffer() {
        // ใช้ความละเอียดแบบเต็มหน้าจอเพื่อ Render เฉพาะส่วนที่มองเห็น
        int w = 1920; 
        int h = 1080;
        try {
            if (ZhuzheeEngine.Application.getInstance() != null) {
                w = Math.max(w, ZhuzheeEngine.Application.getInstance().getScreenWidth());
                h = Math.max(h, ZhuzheeEngine.Application.getInstance().getScreenHeight());
            }
        } catch (Exception e) {}
        
        renderWidth = Math.max(1, w / PIXEL_SIZE) + 2; 
        renderHeight = Math.max(1, h / PIXEL_SIZE) + 2;
        
        waterBackground = new BufferedImage(renderWidth, renderHeight, BufferedImage.TYPE_INT_RGB);
        waterPixels = ((DataBufferInt) waterBackground.getRaster().getDataBuffer()).getData();
    }

    private void drawWaterFrame(Rectangle clip) {
        if (waterPixels == null) return;
        
        float currentOffsetX = timeElapsed * WATER_SPEED;
        float currentOffsetY = timeElapsed * WATER_SPEED * 0.5f;

        float safeScale = Math.max(0.01f, scaleRatio); 
        float safeScaleInv = 1.0f / safeScale;
        
        float offset1X = currentOffsetX;
        float offset1Y = currentOffsetY;
        float offset2X = -(currentOffsetX * 1.2f);
        float offset2Y = currentOffsetY * 0.8f;
        
        int startX = clip.x;
        int startY = clip.y;
        
        int rw = Math.min(renderWidth, (clip.width / PIXEL_SIZE) + 2);
        int rh = Math.min(renderHeight, (clip.height / PIXEL_SIZE) + 2);

        for (int y = 0; y < rh; y++) {
            float mapY = startY + (y * PIXEL_SIZE);
            float worldY = mapY * safeScaleInv;
            float wy1 = worldY + offset1Y;
            float wy2 = worldY + offset2Y;
            
            int rowOffset = y * renderWidth;
            for (int x = 0; x < rw; x++) {
                float mapX = startX + (x * PIXEL_SIZE);
                float worldX = mapX * safeScaleInv;

                float baseHeight = sampleHeightMap(worldX + offset1X, wy1);
                float causticHeight = sampleHeightMap(worldX + offset2X, wy2);

                int color = DEEP_WATER;
                
                if (baseHeight > 0.7f) {
                    color = MID_WATER;
                }
                
                float edge = causticHeight - 0.5f;
                if (edge < 0) edge = -edge; 
                
                if (edge < 0.04f) { 
                    if (baseHeight > 0.85f) {
                        color = SPARKLE_FOAM; 
                    } else {
                        color = CAUSTIC_LINE; 
                    }
                }
                
                waterPixels[rowOffset + x] = color;
            }
        }
    }

    public void clearHoveredGrid() {
        if (currentHoveredGrid != null) {
            currentHoveredGrid.setHovered(false);
            currentHoveredGrid = null;
        }
    }

    public HashMap<String, Float> getAllPlayerPercentages(){
        HashMap<String,Float> playerScores = new HashMap<>();
        float totalScore = 0f;

        //รวมคะแนนสะสมของผู้เล่นแต่ละคนจากทุกเมือง
        for (City city : getAllCities()) {
            for (java.util.Map.Entry<String, Float> entry : city.playerScores.entrySet()) {
                String pId = entry.getKey();
                float score = entry.getValue();
                
                playerScores.put(pId, playerScores.getOrDefault(pId, 0f) + score);
                totalScore += score;
            }
        }

        //แปลงเป็น %
        HashMap<String, Float> playerPercentages = new HashMap<>();
        if (totalScore > 0) {
            for (java.util.Map.Entry<String, Float> entry : playerScores.entrySet()) {
                playerPercentages.put(entry.getKey(), (entry.getValue() / totalScore) * 100f);
            }
        }
        return playerPercentages;
    }

    public void setHoveredGrid(Grid grid) {
        if (currentHoveredGrid != grid) {
            if (currentHoveredGrid != null) currentHoveredGrid.setHovered(false);
            currentHoveredGrid = grid;
            if (currentHoveredGrid != null) currentHoveredGrid.setHovered(true);
        }
    }

    public Grid getGridAtPoint(Point p) {
        for (Grid[] col : gridMap) {
            for (Grid grid : col) {
                if (grid != null && grid.contains(p)) return grid;
            }
        }
        return null;
    }

    public Grid getGrid(int x, int y) {
        if (x >= 0 && x < rows && y >= 0 && y < cols) {
            return gridMap[x][y];
        }
        return null;
    }

    public City getCityByName(String name) {
        if (name == null || gridMap == null) return null;
        for (Grid[] col : gridMap) {
            for (Grid grid : col) {
                if (grid != null && grid.getCity() != null) {
                    if (grid.getCity().getCityName().equals(name)) return grid.getCity();
                }
            }
        }
        return null;
    }

    private Grid[][] generateMap(long seed) {
        Grid[][] grid = new Grid[rows][cols];
        Random random = new Random(seed);
        Color newColor;
        boolean isDuplicate;
        ArrayList<City> cities = new ArrayList<>();

        Point startPosition = new Point(rows / 2, cols / 2);

        for (int i = 0; i < citiesCount; i++) {
            City city = new City("City Test : " + i,
                    random.nextInt(minStats, maxStats), random.nextInt(minStats, maxStats),
                    random.nextInt(minStats, maxStats), random.nextInt(minPopulation, maxPopulation),
                    (ArrayList<Player>) ZhuzheeGame.CLIENT.getConnectedPlayers());
            do {
                isDuplicate = false;
                int r = random.nextInt(1, 5) * 255 / 5;
                int g = random.nextInt(1, 5) * 255 / 5;
                int b = random.nextInt(1, 5) * 255 / 5;
                newColor = new Color(r, g, b, 230); 

                for (City c : cities) {
                    if (newColor.equals(c.getColor())) {
                        isDuplicate = true;
                        break;
                    }
                }
            } while (isDuplicate); 

            city.setColor(newColor);
            cities.add(city);

            setCityOnGridMapByRandomWalk(grid, city, random.nextInt(6, maxGridPerCties), startPosition, random);
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
        if (filledTiles.isEmpty()) return new Point(rows / 2, cols / 2);
        return filledTiles.get(random.nextInt(filledTiles.size()));
    }

    public float getScaleRatio() { return scaleRatio; }

    private void setCityOnGridMapByRandomWalk(Grid[][] grid, City city, int gridCount, Point startPosition, Random random) {
        ArrayList<Point> cityTiles = new ArrayList<>();
        ArrayList<Point> candidates = new ArrayList<>();

        int startX = Math.abs(startPosition.x) % grid.length;
        int startY = Math.abs(startPosition.y) % grid[0].length;

        while (grid[startX][startY] != null) {
            startX = random.nextInt(grid.length);
            startY = random.nextInt(grid[0].length);
        }

        addTileToCity(grid, city, startX, startY, cityTiles, candidates);
        int tilesCreated = 1;

        while (tilesCreated < gridCount && !candidates.isEmpty()) {
            int index = random.nextInt(candidates.size());
            Point next = candidates.get(index);

            if (grid[next.x][next.y] == null) {
                addTileToCity(grid, city, next.x, next.y, cityTiles, candidates);
                tilesCreated++;
            } else {
                candidates.remove(index);
            }
        }
    }

    private void addTileToCity(Grid[][] grid, City city, int x, int y, ArrayList<Point> cityTiles, ArrayList<Point> candidates) {
        float xOffset = y % 2 == 0 ? getGridWidth() : getGridWidth() / 2;
        grid[x][y] = new Grid(this, city, x, y, radius, xOffset);
        cityTiles.add(new Point(x, y));
        candidates.removeIf(p -> p.x == x && p.y == y);

        int[][] neighbors = { { 0, -1 }, { 1, 0 }, { 0, 1 }, { -1, 0 }, { (y % 2 == 0 ? -1 : 1), -1 }, { (y % 2 == 0 ? -1 : 1), 1 } };

        for (int[] offset : neighbors) {
            int nx = Math.clamp(x + offset[0], 0, grid.length - 1);
            int ny = Math.clamp(y + offset[1], 0, grid[0].length - 1);
            if (grid[nx][ny] == null) {
                Point p = new Point(nx, ny);
                if (!candidates.contains(p)) candidates.add(p);
            }
        }
    }

    public float getGridWidth() { return ((float) Math.sqrt(3) * radius + gap) * scaleRatio; }
    public float getGridHeight() { return ((float) 1.5 * radius + gap) * scaleRatio; }
    public float getGridMapWidth() { return cols * getGridWidth(); }
    public float getGridMapHeight() { return rows * getGridHeight(); }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gridMap == null) return;
        
        Graphics2D g2d = (Graphics2D) g.create();
        
        if (waterBackground != null) {
            Rectangle clip = g2d.getClipBounds();
            if (clip == null) {
                clip = new Rectangle(0, 0, getWidth(), getHeight());
            }
            drawWaterFrame(clip);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            
            // Draw exactly the generated piece at the correct scaled location
            int drawW = renderWidth * PIXEL_SIZE;
            int drawH = renderHeight * PIXEL_SIZE;
            g2d.drawImage(waterBackground, clip.x, clip.y, clip.x + drawW, clip.y + drawH,
                          0, 0, renderWidth, renderHeight, null);
        }

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Grid[] col : gridMap) {
            for (Grid grid : col) {
                if (grid != null && !grid.isHovered()) grid.render(g2d);
            }
        }

        if (currentHoveredGrid != null) {
            currentHoveredGrid.render(g2d);
        }

        if (currentClickedGrid != null) {
            drawCityStatsUI(g2d, currentClickedGrid.getCity());
        }

        g2d.dispose();
    }

    private void drawCityStatsUI(Graphics2D g2d, City city) {
        if (city == null || currentClickedGrid == null) return;

        java.util.List<Core.Player.Player> players = Core.ZhuzheeGame.CURRENT_PLAYERS;
        int availablePlayers = (players != null) ? players.size() : 0;
        int scorePlayers = (city.playerScores != null) ? city.playerScores.size() : 0;
        int playersToShow = Math.max(0, Math.min(availablePlayers, scorePlayers));

        int padding = 15;
        int boxWidth = 220;
        int legendLines = Math.max(1, playersToShow);
        int boxHeight = 150 + (legendLines * 30);

        int x = (int) currentClickedGrid.getX() + 30;
        int y = (int) currentClickedGrid.getY() + 30;

        if (x + boxWidth > getWidth()) x = (int) currentClickedGrid.getX() - boxWidth - 30;
        if (y + boxHeight > getHeight()) y = (int) currentClickedGrid.getY() - boxHeight - 30;

        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRoundRect(x + 3, y + 3, boxWidth, boxHeight, 15, 15);
        g2d.setColor(new Color(245, 245, 245, 240));
        g2d.fillRoundRect(x, y, boxWidth, boxHeight, 15, 15);

        g2d.setColor(city.getColor() != null ? city.getColor() : Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(x, y, boxWidth, boxHeight, 15, 15);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2d.drawString(city.getCityName(), x + padding, y + padding + 15);

        g2d.setFont(new Font("SansSerif", Font.PLAIN, 14));
        int startY = y + padding + 40;
        int lineSpacing = 20;

        drawStatLine(g2d, "Facility: ", city.stats.getStats(PoliticsStats.FACILITY), x + padding, startY);
        drawStatLine(g2d, "Environment: ", city.stats.getStats(PoliticsStats.ENVIRONMENT), x + padding, startY + lineSpacing);
        drawStatLine(g2d, "Economy: ", city.stats.getStats(PoliticsStats.ECONOMY), x + padding, startY + lineSpacing * 2);

        int barX = x + padding;
        int barY = startY + lineSpacing * 3 - 7;
        int barWidth = boxWidth - (padding * 2);
        int barHeight = 15;
        int rowGap = 6;

        g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString("Vote share:", barX, barY - 4);

        int yCursor = barY + 6;
        for (int i = 0; i < playersToShow; i++) {
            Core.Player.Player p = players.get(i);
            String name = (p != null) ? p.getPlayerName() : ("Player " + i);
            String pId = (p != null) ? p.getPlayerId() : "";
            Color c = (p != null && p.getColor() != null) ? p.getColor() : Color.GRAY;
            double percent = city.getPlayerPercentage(pId);

            g2d.setColor(Color.DARK_GRAY);
            g2d.drawString(String.format("%s: %.2f%%", name, percent), barX, yCursor + 10);

            int bgY = yCursor + 14;
            g2d.setColor(new Color(220, 220, 220));
            g2d.fillRoundRect(barX, bgY, barWidth, barHeight, 6, 6);

            int fillWidth = (int) Math.round(barWidth * (percent / 100.0));
            fillWidth = Math.max(0, Math.min(fillWidth, barWidth));
            g2d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 220));
            if (fillWidth > 0) g2d.fillRoundRect(barX, bgY, fillWidth, barHeight, 6, 6);

            g2d.setColor(new Color(0, 0, 0, 90));
            g2d.drawRoundRect(barX, bgY, barWidth, barHeight, 6, 6);

            yCursor = bgY + barHeight + rowGap;
        }

        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2d.drawString("Population: " + String.format("%,d", city.population), x + padding, yCursor + 10);
    }

    private void drawStatLine(Graphics2D g2d, String label, int value, int x, int y) {
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString(label, x, y);
        g2d.setColor(new Color(0, 102, 204)); 
        g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2d.drawString(String.valueOf(value), x + 100, y);
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 14));
    }

    @Override
    public void update() {
        super.update();
        float deltaTime = ZhuzheeEngine.Application.getDeltaTime();
        timeElapsed += deltaTime;

        if (startSize != null && startSize.x > 0) {
            scaleRatio = (float) getWidth() / (float) startSize.x;
        }

        if (waterBackground == null) {
            initWaterBuffer();
        }

        if (gridMap != null) {
            for (Grid[] col : gridMap) {
                for (Grid grid : col) {
                    if (grid != null) grid.animation(deltaTime);
                }
            }
        }
    }
}