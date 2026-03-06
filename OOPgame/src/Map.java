//import Card.*;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.util.*;
//
//public class Map extends JPanel {
//    private final int rows = 20;
//    private final int cols = 20;
//    private final int[][] map = new int[rows][cols];
//    private int hoverRow = -1;
//    private int hoverCol = -1;
//    private int cameraX = 0;
//    private int cameraY = 0;
//    private int lastMouseX;
//    private int lastMouseY;
//    private boolean rightDragging = false;
//    private final Camera camera = new Camera();
//
//    public Map() {
//        this.setBackground(Color.BLACK);
//        generateRandomMap(); // เรียกใช้ฟังก์ชันสุ่มตอนสร้าง Object
//
//        add(new CardSlot(400, 200, 100, 150));
//
//        add(new Card("Red Dragon", 50, 1100, 150, 225, true, false));
//        add(new Card("Blue Eyes", 225, 1100, 150, 225, true, false));
//        add(new Card("Rock Golem", 400, 1100, 150, 225, true, false));
//        this.addMouseWheelListener(new MouseWheelListener() {
//            @Override
//            public void mouseWheelMoved(MouseWheelEvent e) {
//                // ยอมให้เปลี่ยนขนาดเฉพาะตอนที่ไม่ได้ Lock เป้าหมาย (ไม่ได้ Click Zoom)
//                // หรือถ้าอยากให้ซูมต่อจากที่ Click แล้วได้ ก็เอา !camera.isZoomed() ออก
//                if (!camera.getIsZoomed()) {
//                    int newSize = camera.getCurrentCellSize();
//                    if (e.getWheelRotation() < 0) {
//                        if (newSize < 150) newSize += 5;
//                    } else {
//                        if (newSize > 10) newSize -= 5;
//                    }
//                    camera.setCurrentCellSize(newSize);
//                    repaint();
//                }
//            }
//        });
//        this.addMouseMotionListener(new MouseMotionAdapter() {
//            @Override
//            public void mouseMoved(MouseEvent e) {
//                super.mouseMoved(e);
//                // ส่งค่า width/height ปัจจุบันให้ camera คำนวณ
//                Point cell = camera.getGridPoint(e.getX(), e.getY(), getWidth(), getHeight(), rows, cols, cameraX, cameraY);
//
//                int row = cell.y;
//                int col = cell.x;
//
//                if (row >= 0 && row < rows && col >= 0 && col < cols) {
//                    if (hoverRow != row || hoverCol != col) {
//                        hoverRow = row;
//                        hoverCol = col;
//                        repaint();
//                    }
//                } else {
//                    if (hoverRow != -1) {
//                        hoverRow = -1;
//                        hoverCol = -1;
//                        repaint();
//                    }
//                }
//            }
//            @Override
//            public void mouseDragged(MouseEvent e) {
//                super.mouseDragged(e);
//                if (rightDragging) {
//                    int dx = e.getX() - lastMouseX;
//                    int dy = e.getY() - lastMouseY;
//                    cameraX += dx;
//                    cameraY += dy;
//                    lastMouseX = e.getX();
//                    lastMouseY = e.getY();
//                    repaint();
//                } else {
//                    // Update hover position while dragging cards
//                    Point cell = camera.getGridPoint(e.getX(), e.getY(), getWidth(), getHeight(), rows, cols, cameraX, cameraY);
//                    int row = cell.y;
//                    int col = cell.x;
//
//                    if (row >= 0 && row < rows && col >= 0 && col < cols) {
//                        if (hoverRow != row || hoverCol != col) {
//                            hoverRow = row;
//                            hoverCol = col;
//                            repaint();
//                        }
//                    } else {
//                        if (hoverRow != -1) {
//                            hoverRow = -1;
//                            hoverCol = -1;
//                            repaint();
//                        }
//                    }
//                }
//            }
//        });
//        this.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent e) {
//                super.mousePressed(e);
//                    if (SwingUtilities.isRightMouseButton(e)) {
//                        rightDragging = true;
//                        lastMouseX = e.getX();
//                        lastMouseY = e.getY();
//                        return;
//                    }
//                // 1. หาตำแหน่ง Grid
//                Point cell = camera.getGridPoint(e.getX(), e.getY(), getWidth(), getHeight(), rows, cols, cameraX, cameraY);
//
//                // 2. ถ้า Click ใน Grid ให้สลับโหมดซูม
//                if (cell.y >= 0 && cell.y < rows && cell.x >= 0 && cell.x < cols) {
//                    // เรียกฟังก์ชันใน Camera ให้คำนวณ Offset ใหม่
//                    camera.zoomToCell(cell.x, cell.y, getWidth(), getHeight());
//                } else if (camera.getIsZoomed()) {
//                    // ถ้าคลิกพื้นที่ว่างๆ ตอนซูมอยู่ ให้ Reset
//                    camera.resetZoom();
//                }
//
//                // 3. สั่งวาดใหม่
//                repaint();
//            }
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                if (SwingUtilities.isRightMouseButton(e)) {
//                    rightDragging = false;
//                }
//            }
//        });
//    }
//
//    private Polygon createHexagon(int x, int y, int radius) {
//        int[] xPoints = new int[6];
//        int[] yPoints = new int[6];
//
//        for (int i = 0; i < 6; i++) {
//            double angle = Math.PI / 3 * i + Math.PI / 6;  // Rotate 30 degrees for flat-top
//            xPoints[i] = (int) (x + radius * Math.cos(angle));
//            yPoints[i] = (int) (y + radius * Math.sin(angle));
//        }
//
//        return new Polygon(xPoints, yPoints, 6);
//    }
//
//    private void generateRandomMap() {
//        Random rand = new Random();
//        double obstacleChance = 0.2; // กำหนดโอกาสพื้นที่
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < cols; j++) {
//                // ถ้าสุ่มได้ค่าน้อยกว่าค่า obstacleChance ให้เป็น 1 นอกนั้นเป็น 0
//                if (rand.nextDouble() < obstacleChance) {
//                    map[i][j] = 1;
//                } else {
//                    map[i][j] = 0;
//                }
//            }
//        }
//    }
//
//    @Override
//    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        Point startPt = camera.getDrawOffset(getWidth(), getHeight(), rows, cols);
//        int cellSize = camera.getCurrentCellSize();
//
//        Graphics2D g2d = (Graphics2D) g;
//        // เปิด Anti-aliasing ให้เส้นคมชัดขึ้น
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < cols; j++) {
//                int x = startPt.x + j * cellSize + cameraX;
//                int y = startPt.y + i * cellSize + cameraY;
//                int drawX = x;
//                int drawW = cellSize;
//
//                // Logic การวาดแบบเยื้อง
//                if (i % 2 == 0) {
//                    if (j == 0) {
//                        drawW = cellSize / 2;
//                    } else {
//                        drawX = x - cellSize / 2;
//                    }
//                }
//
//                if (j == 0 && i % 2 == 0) continue;
//
//                // วาดสีพื้น
//                if (map[i][j] == 1) {
//                    g2d.setColor(Color.BLUE);
//                } else {
//                    g2d.setColor(Color.GREEN);
//                }
//                Polygon hexagon = createHexagon(drawX, y, cellSize / 2);
//                g2d.fillPolygon(hexagon);
//
//                // วาดเส้นขอบ (Hover หรือ ปกติ)
//                if (i == hoverRow && j == hoverCol) {
//                    g2d.setColor(Color.WHITE);
//                    g2d.setStroke(new BasicStroke(3)); // หนาขึ้นตอน Hover
//                } else {
//                    g2d.setColor(Color.BLACK);
//                    g2d.setStroke(new BasicStroke(1));
//                }
//                g2d.drawPolygon(hexagon);
//            }
//        }
//    }
//    public static void main() {
//        JFrame frame = new JFrame("Test Map");
//        frame.add(new Map(), BorderLayout.CENTER);
//        frame.setSize(1280, 720);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setVisible(true);
//    }
//}
