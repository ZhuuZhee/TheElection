/**
 * @Munin 10/3/25 - 16:28 - edited : เพิ่มการคำนวนตำแหน่งจาก GameObject.position
 * @Jeng {มาใส่วันที่ด้วย} created
 */
package Core.Maps;

import Core.ZhuzheeGame;
import ZhuzheeEngine.Scene.GameObject;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.ArrayList;
//import java.util.;

public class Map extends GameObject {
    /// กำหนดค่าความกว้างของ map ได้ใน attribute นี้เลย
    private final int rows; // ความกว้าง
    private final int cols; // ความสูง
    private final int citiesCount;
    private static final int DEFAULT_ROWS = 12; // ความสูง
    private static final int DEFAULT_COLS = 12; // ความสูง
    private static final int DEFAULT_CITIES_COUNT = 8; // ความสูง
    private final int maxGridPerCties = 12;
    private final int minStats = 1;
    private final int maxStats = 5;
    private final int minPopulation = 50000;
    private final int maxPopulation = 1000001;
    private final int gap = 4;
    private final float radius = 84;
    private float scaleRatio = 1;
    private final Point startSize;
    private final Grid[][] gridMap;// array ของช่องแต่ละช่องว่าเป็น city หรือ water
    private Grid currentHoveredGrid = null;
    private Grid currentClickedGrid = null;

    public Map() {
        this(DEFAULT_ROWS, DEFAULT_COLS, DEFAULT_CITIES_COUNT, new Random().nextLong());
    }

    public Map(long seed) {
        this(DEFAULT_ROWS, DEFAULT_COLS, DEFAULT_CITIES_COUNT, seed);
    }

    public Map(int rows, int cols, int citiesCount, long seed) {
        super(-1500, -1500, 3000, 3000, ZhuzheeGame.MAIN_SCENE);
        this.rows = rows;
        this.cols = cols;
        this.citiesCount = citiesCount;
        startSize = new Point(getWidth(), getHeight());
        gridMap = GenerateMap(seed);

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Grid clicked = getGridAtPoint(e.getPoint());
                if (currentHoveredGrid != clicked) {
                    if (currentHoveredGrid != null) {
                        currentHoveredGrid.setHovered(false);
                    }
                    currentHoveredGrid = clicked;
                    if (currentHoveredGrid != null) {
                        currentHoveredGrid.setHovered(true);
                    }
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

    // เอาไว้ clear hover state ของ Grid ที่อยู่ใน Map
    public void clearHoveredGrid() {
        if (currentHoveredGrid != null) {
            currentHoveredGrid.setHovered(false);
            currentHoveredGrid = null;
        }
    }

    // เอาไว้ set hover state ของ Grid ที่อยู่ใน Map
    public void setHoveredGrid(Grid grid) {
        if (currentHoveredGrid != grid) {
            if (currentHoveredGrid != null) {
                currentHoveredGrid.setHovered(false);
            }
            currentHoveredGrid = grid;
            if (currentHoveredGrid != null) {
                currentHoveredGrid.setHovered(true);
            }
        }
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

    private Grid[][] GenerateMap(long seed) {
        Grid[][] grid = new Grid[rows][cols];
        Random random = new Random(seed);

        Point startPosition = new Point(rows / 2, cols / 2);

        // generate the city on grid
        for (int i = 0; i < citiesCount; i++) {
            City city = new City("City Test : " + i,
                    random.nextInt(minStats, maxStats),
                    random.nextInt(minStats, maxStats),
                    random.nextInt(minStats, maxStats),
                    random.nextInt(minPopulation, maxPopulation));
            // random color
            int r = random.nextInt(1, 5) * 255 / 5;
            int g = random.nextInt(1, 5) * 255 / 5;
            int b = random.nextInt(1, 5) * 255 / 5;
            city.setColor(new Color(r, g, b));

            // randomly set city inside district

            // random start position inside map
            setCityOnGridMapByRandomWalk(grid, city, random.nextInt(6, maxGridPerCties), startPosition, random);
            startPosition = getRandomFilledTile(grid, random);
        }
        return grid;
    }

    private Point getRandomFilledTile(Grid[][] grid, Random random) {
        ArrayList<Point> filledTiles = new ArrayList<>();
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                if (grid[x][y] != null)
                    filledTiles.add(new Point(x, y));
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
     * set the `City` on grid map(2D Array) by a cluster growth algorithm (instead
     * of random walk)
     * This makes the city more compact and connected.
     *
     * @param city          the city to set on gridMap.
     * @param grid          grid Map.
     * @param gridCount     number of tiles to create for this city.
     * @param startPosition start position in grid map.
     */
    private void setCityOnGridMapByRandomWalk(Grid[][] grid, City city, int gridCount, Point startPosition,
            Random random) {
        ArrayList<Point> cityTiles = new ArrayList<>();
        ArrayList<Point> candidates = new ArrayList<>();

        // Ensure start position is within bounds and doesn't already have a city
        int startX = Math.abs(startPosition.x) % grid.length;
        int startY = Math.abs(startPosition.y) % grid[0].length;

        // If start position is occupied, find the nearest empty one (simple approach:
        // random until empty)
        while (grid[startX][startY] != null) {
            startX = random.nextInt(grid.length);
            startY = random.nextInt(grid[0].length);
        }

        // Add the first tile
        addTileToCity(grid, city, startX, startY, cityTiles, candidates);
        int tilesCreated = 1;

        while (tilesCreated < gridCount && !candidates.isEmpty()) {
            // Pick a random candidate from the list of neighbors
            int index = random.nextInt(candidates.size());
            Point next = candidates.get(index);

            // Double check if it's still null (might have been filled by another city or
            // same city)
            if (grid[next.x][next.y] == null) {
                addTileToCity(grid, city, next.x, next.y, cityTiles, candidates);
                tilesCreated++;
            } else {
                // If occupied, just remove it from candidates
                candidates.remove(index);
            }
        }
    }

    private void addTileToCity(Grid[][] grid, City city, int x, int y, ArrayList<Point> cityTiles,
            ArrayList<Point> candidates) {
        float xOffset = y % 2 == 0 ? getGridWidth() : getGridWidth() / 2;
        grid[x][y] = new Grid(this, city, x, y, radius, xOffset);
        Point current = new Point(x, y);
        cityTiles.add(current);

        // Remove from candidates if it was there
        candidates.removeIf(p -> p.x == x && p.y == y);


        // Add neighbors to candidates
        int[][] neighbors;
        if (y % 2 == 0) {
            // Neighbors for even rows in offset-x hexagon grid
            neighbors = new int[][]{{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
        } else {
            // Neighbors for odd rows in offset-x hexagon grid
            neighbors = new int[][]{{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
        }

        for (int[] offset : neighbors) {
            int nx = x + offset[0];
            int ny = y + offset[1];

            // Wrap around or clamp
            nx = Math.clamp(nx, 0, grid.length - 1);
            ny = Math.clamp(ny, 0, grid[0].length - 1);

            if (grid[nx][ny] == null) {
                Point p = new Point(nx, ny);
                if (!candidates.contains(p)) {
                    candidates.add(p);
                }
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

        // Render all non-hovered grids first
        for (Grid[] col : gridMap) {
            for (Grid grid : col) {
                if (grid != null && !grid.isHovered())
                    grid.render(g2d);
            }
        }

        // Render the hovered grid last to ensure it is on top
        if (currentHoveredGrid != null) {
            currentHoveredGrid.render(g2d);
        }

        // Render the clicked city stats UI
        if (currentClickedGrid != null) {
            drawCityStatsUI(g2d, currentClickedGrid.getCity());
        }

        g2d.dispose();
    }

    private void drawCityStatsUI(Graphics2D g2d, City city) {
        if (city == null || currentClickedGrid == null)
            return;

        // Configuration for the UI box
        int padding = 15;
        int boxWidth = 220;
        int boxHeight = 165;

        // Use the grid position to anchor the UI
        int x = (int) currentClickedGrid.getX() + 30;
        int y = (int) currentClickedGrid.getY() + 30;

        // Keep the box within the map bounds
        if (x + boxWidth > getWidth()) {
            x = (int) currentClickedGrid.getX() - boxWidth - 30;
        }
        if (y + boxHeight > getHeight()) {
            y = (int) currentClickedGrid.getY() - boxHeight - 30;
        }

        // Draw background shadow
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRoundRect(x + 3, y + 3, boxWidth, boxHeight, 15, 15);

        // Draw background box
        g2d.setColor(new Color(245, 245, 245, 240));
        g2d.fillRoundRect(x, y, boxWidth, boxHeight, 15, 15);

        // Draw border
        g2d.setColor(city.getColor() != null ? city.getColor() : Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(x, y, boxWidth, boxHeight, 15, 15);

        // Draw Text
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2d.drawString(city.getCityName(), x + padding, y + padding + 15);

        g2d.setFont(new Font("SansSerif", Font.PLAIN, 14));
        int startY = y + padding + 40;
        int lineSpacing = 20;

        drawStatLine(g2d, "Economy: ", city.stats.getStats(PoliticsStats.Economy), x + padding, startY);
        drawStatLine(g2d, "Facility: ", city.stats.getStats(PoliticsStats.Facility), x + padding, startY + lineSpacing);
        drawStatLine(g2d, "Environment: ", city.stats.getStats(PoliticsStats.Environment), x + padding,
                startY + lineSpacing * 2);

        // Draw Player Votes Percentage Bar
        int barX = x + padding;
        int barY = startY + lineSpacing * 3 - 7;
        int barWidth = boxWidth - (padding * 2);
        int barHeight = 15;
        double playerPercent = city.getPlayerPercentage(0);

        // Bar background
        g2d.setColor(new Color(220, 220, 220));
        g2d.fillRoundRect(barX, barY, barWidth, barHeight, 8, 8);

        // Bar fill (Player 0)
        g2d.setColor(Color.BLUE);
        int fillWidth = (int) (barWidth * (playerPercent / 100.0));
        if (fillWidth > 0) {
            g2d.fillRoundRect(barX, barY, fillWidth, barHeight, 8, 8);
        }

        // Bar border
        g2d.setColor(new Color(150, 0, 0));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(barX, barY, barWidth, barHeight, 8, 8);

        // Bar text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 11));
        String percentText = String.format("Player: %.1f%%", playerPercent);
        FontMetrics fm = g2d.getFontMetrics();
        int textX = barX + (barWidth - fm.stringWidth(percentText)) / 2;
        int textY = barY + ((barHeight - fm.getHeight()) / 2) + fm.getAscent();

        // Shadow for text readability
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(percentText, textX + 1, textY + 1);
        g2d.setColor(Color.WHITE);
        g2d.drawString(percentText, textX, textY);

        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2d.drawString("Population: " + String.format("%,d", city.population), x + padding,
                startY + lineSpacing * 4 + 10);
    }

    private void drawStatLine(Graphics2D g2d, String label, int value, int x, int y) {
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString(label, x, y);
        g2d.setColor(new Color(0, 102, 204)); // Dark blue for stats
        g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2d.drawString(String.valueOf(value), x + 100, y);
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 14));
    }

    @Override
    public void update() {
        super.update();
        if (gridMap != null) {
            float deltaTime = ZhuzheeEngine.Application.getDeltaTime();
            for (Grid[] col : gridMap) {
                for (Grid grid : col) {
                    if (grid != null) {
                        grid.animation(deltaTime);
                    }
                }
            }
            repaint(); // Re-render if there's any animation
        }
    }
}
