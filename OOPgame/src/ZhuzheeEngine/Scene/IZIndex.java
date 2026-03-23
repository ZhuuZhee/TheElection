package ZhuzheeEngine.Scene;

import java.awt.Component;

public interface IZIndex {
    int getZIndex();
    void setZIndex(int index);
    Component asComponent(); // เพื่อให้ Scene2D ดึง Component ไปจัดลำดับได้
}
