package Core.UI;

// Button สำเร็จรูปแบบ NineSliceButton ใช้ได้โดยแบบนี่
// nameBtn = UIButtonFactory.createMenuButton("ชื่อปุ่ม", btnNormalImg, btnHoverImg, listener);
import ZhuzheeEngine.Scene.NineSliceButton;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class UIButtonFactory {
    
    public static NineSliceButton createMenuButton(String text, BufferedImage normalImg, BufferedImage hoverImg, ActionListener listener) {
        NineSliceButton btn = new NineSliceButton(text, normalImg, 6, 6, 6, 6);
        btn.setPreferredSize(new Dimension(150, 35));
        
        if (listener != null) {
            btn.addActionListener(listener);
        }

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setSourceImage(hoverImg);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setSourceImage(normalImg);
            }
        });
        
        return btn;
    }
}
