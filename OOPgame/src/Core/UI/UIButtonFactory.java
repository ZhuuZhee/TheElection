package Core.UI;

// Button สำเร็จรูปแบบ NineSliceButton ใช้ได้โดยแบบนี่
// nameBtn = UIButtonFactory.createMenuButton("ชื่อปุ่ม", btnNormalImg, btnHoverImg, listener);
import ZhuzheeEngine.Scene.NineSliceButton;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class UIButtonFactory {
    
    /**
     * สร้าง NineSliceButton โดยกำหนดขนาดขอบแต่ละด้านได้
     * @param text ข้อความปุ่ม
     * @param normalImg รูปปกติ
     * @param hoverImg รูป hover
     * @param listener ActionListener
     * @param leftSlice ขอบซ้าย (px)
     * @param rightSlice ขอบขวา (px)
     * @param topSlice ขอบบน (px)
     * @param bottomSlice ขอบล่าง (px)
     * @return NineSliceButton
     */
    public static NineSliceButton createMenuButton(String text, BufferedImage normalImg, BufferedImage hoverImg, ActionListener listener,
                                                   int leftSlice, int rightSlice, int topSlice, int bottomSlice) {
        NineSliceButton btn = new NineSliceButton(text, normalImg, leftSlice, rightSlice, topSlice, bottomSlice);
        btn.setPreferredSize(new Dimension(150, 35));

        if (listener != null) {
            btn.addActionListener(listener);
        }

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            private Color originalColor = btn.getForeground();
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setSourceImage(hoverImg);
                btn.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setSourceImage(normalImg);
                btn.setForeground(originalColor);
            }
        });

        return btn;
    }
    
    // เมธอดเดิม (default) เพื่อไม่ให้โค้ดเก่าพัง
    public static NineSliceButton createMenuButton(String text, BufferedImage normalImg, BufferedImage hoverImg, ActionListener listener) {
        // ค่า default: left=10, right=10, top=6, bottom=6
        return createMenuButton(text, normalImg, hoverImg, listener, 10, 10, 6, 6);
    }
}
