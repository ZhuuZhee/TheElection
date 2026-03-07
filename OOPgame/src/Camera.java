import java.awt.*;

public class Camera {
    private boolean isZoomed = false;
    private int offsetX = 0;
    private int offsetY = 0;
    private final int defaultCellSize = 25;
    private final int zoomedCellSize = 50;
    private int currentCellSize = defaultCellSize;

    /**
     * ดึงขนาดเซลล์ปัจจุบัน
     * @return ขนาดเซลล์ในหน่วย pixel
     */
    public int getCurrentCellSize() {
        return currentCellSize;
    }

    /**
     * ตั้งค่าขนาดเซลล์ปัจจุบัน
     * @param currentCellSize ขนาดเซลล์ใหม่ในหน่วย pixel
     */
    public void setCurrentCellSize(int currentCellSize) {
        this.currentCellSize = currentCellSize;
    }

    /**
     * ตรวจสอบว่ากล้องอยู่ในโหมดซูมหรือไม่
     * @return true ถ้าอยู่ในโหมดซูม, false ถ้าไม่
     */
    public boolean getIsZoomed() {
        return isZoomed;
    }

    /**
     * คำนวณตำแหน่งเริ่มต้นในการวาด grid บนหน้าจอ
     * @param panelWidth ความกว้างของ panel
     * @param panelHeight ความสูงของ panel
     * @param rows จำนวนแถวของ grid
     * @param cols จำนวนคอลัมน์ของ grid
     * @return จุดเริ่มต้นในการวาด (x, y)
     */
    public Point getDrawOffset(int panelWidth, int panelHeight, int rows, int cols) {
        if (isZoomed) {
            return new Point(offsetX, offsetY);
        } else {
            // สูตรที่ทำให้อยู่ตรงกลางจอ
            int x = (panelWidth - (cols * currentCellSize)) / 2;
            int y = (panelHeight - (rows * currentCellSize)) / 2;
            return new Point(x, y);
        }
    }

    /**
     * ซูมเข้าไปที่เซลล์ที่ระบุ หรือซูมออกถ้าอยู่ในโหมดซูมอยู่แล้ว
     * @param col คอลัมน์ของเซลล์ที่ต้องการซูม
     * @param row แถวของเซลล์ที่ต้องการซูม
     * @param width ความกว้างของหน้าจอ
     * @param height ความสูงของหน้าจอ
     */
    public void zoomToCell(int col, int row, int width, int height) {
        if (!isZoomed) {
            isZoomed = true;
            currentCellSize = zoomedCellSize; // เปลี่ยนเป็นไซส์ใหญ่

            // --- 1. หาจุดกึ่งกลาง (Center) ของช่อง Grid นั้นๆ ---
            int relativeCenterX;
            // แกน Y: จุดกลางคือ (row * size) + (size / 2) เสมอ
            int relativeCenterY = (row * currentCellSize) + (currentCellSize / 2);

            // แกน X: ต้องแยกเคสละเอียด
            if (row % 2 == 0) {
                // >> แถวคู่ <<
                if (col == 0) {
                    // ช่องแรกของแถวคู่ มันถูกวาดแค่ครึ่งเดียว (Half-block)
                    // จุดกึ่งกลางจึงอยู่ที่ 1/4 ของขนาดเต็ม
                    relativeCenterX = currentCellSize / 4;
                } else {
                    // ช่องอื่นๆ ถูกวาดเยื้องซ้าย (-size/2)
                    // จุดกึ่งกลางจะตรงกับเส้น Grid พอดีเป๊ะ
                    relativeCenterX = col * currentCellSize;
                }
            } else {
                // >> แถวคี่ << วาดปกติ จุดกลางคือ (col * size) + (size/2)
                relativeCenterX = (col * currentCellSize) + (currentCellSize / 2);
            }

            // --- 2. คำนวณ Offset เพื่อดันให้จุดกึ่งกลางนั้น มาอยู่ที่กลางจอ ---
            // สูตร: จุดกลางจอ - จุดกลางวัตถุ
            offsetX = (width / 2) - relativeCenterX;
            offsetY = (height / 2) - relativeCenterY;

        } else {
            resetZoom();
        }
    }
    /**
     * รีเซ็ตการซูมกลับไปเป็นค่าปกติ
     */
    public void resetZoom() {
        isZoomed = false;
        currentCellSize = defaultCellSize;
        offsetX = 0;
        offsetY = 0;
    }
    /**
     * แปลงตำแหน่งเมาส์บนหน้าจอเป็นตำแหน่งเซลล์ใน grid
     * @param mouseX ตำแหน่ง X ของเมาส์
     * @param mouseY ตำแหน่ง Y ของเมาส์
     * @param width ความกว้างของหน้าจอ
     * @param height ความสูงของหน้าจอ
     * @param rows จำนวนแถวของ grid
     * @param cols จำนวนคอลัมน์ของ grid
     * @param cameraX ตำแหน่ง X ของกล้อง (สำหรับการเลื่อน)
     * @param cameraY ตำแหน่ง Y ของกล้อง (สำหรับการเลื่อน)
     * @return จุดของเซลล์ที่เมาส์ชี้อยู่ (col, row)
     */

    //-----------------------------------------------------------------------------------
    //---เป็น method ที่เอาไว้ใช้หาตำแหน่ง mouse ความกว้างยาวของจอ และจำนวนของ rows และ cols-----
    //-----------------------------------------------------------------------------------
    public Point getGridPoint(int mouseX, int mouseY, int width, int height, int rows, int cols, int cameraX, int cameraY) {
        Point start = getDrawOffset(width, height, rows, cols);

        int row = (mouseY - start.y - cameraY) / currentCellSize;
        int relativeX = mouseX - start.x - cameraX;
        int col;

        if (row >= 0 && row < rows && row % 2 == 0) {
            col = (relativeX + (currentCellSize / 2)) / currentCellSize;
        } else {
            col = relativeX / currentCellSize;
        }
        return new Point(col, row);
    }
}
