package ZhuzheeEngine.Scene;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 9-slice button extending JButton.
 * Ensures the background image is drawn 9-sliced in paintComponent.
 */
public class NineSliceButton extends JButton {
    protected BufferedImage sourceImage;
    
    // Borders for slicing (in pixels) from the edges of the source image
    protected int leftSlice;
    protected int rightSlice;
    protected int topSlice;
    protected int bottomSlice;

    public NineSliceButton(String text, BufferedImage sourceImage, int left, int right, int top, int bottom) {
        super(text);
        this.sourceImage = sourceImage;
        this.leftSlice = left;
        this.rightSlice = right;
        this.topSlice = top;
        this.bottomSlice = bottom;
        
        // Remove standard button styling to allow the 9-slice image to be the sole background
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
    }
    
    public void setSourceImage(BufferedImage sourceImage) {
        this.sourceImage = sourceImage;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (sourceImage != null) {
            Graphics2D g2d = (Graphics2D) g;
            
            int width = getWidth();
            int height = getHeight();
            
            int imgWidth = sourceImage.getWidth();
            int imgHeight = sourceImage.getHeight();
            
            // Dest coordinates
            int[] dx = {0, leftSlice, width - rightSlice, width};
            int[] dy = {0, topSlice, height - bottomSlice, height};
            
            // Source coordinates
            int[] sx = {0, leftSlice, imgWidth - rightSlice, imgWidth};
            int[] sy = {0, topSlice, imgHeight - bottomSlice, imgHeight};
            
            // Overlap prevention
            if (width < leftSlice + rightSlice) {
                dx[1] = width / 2;
                dx[2] = width / 2;
            }
            if (height < topSlice + bottomSlice) {
                dy[1] = height / 2;
                dy[2] = height / 2;
            }

            // Draw the 9 segments
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    g2d.drawImage(sourceImage,
                            dx[i], dy[j], dx[i + 1], dy[j + 1], // Dest
                            sx[i], sy[j], sx[i + 1], sy[j + 1], // Source
                            null);
                }
            }
        }
        
        // Draw the text (and icon if any) on top of the background
        super.paintComponent(g);
    }
}
