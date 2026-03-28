package ZhuzheeEngine.Scene;

import ZhuzheeEngine.Application;

import java.awt.Point;

public class Camera2D {
    private Point position;
    private float zoom;

    public Camera2D() {
        this.position = new Point(0, 0);
        this.zoom = 1.0f;
    }

    // Getters and Setters
    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    /**
     * Moves the camera by a specific offset
     */


    /**
     * Logic for Screen to World transformation
     */
    public Point screenToWorld(Point screenPos, int viewWidth, int viewHeight) {
        int centerX = viewWidth / 2;
        int centerY = viewHeight / 2;

        int worldX = (int) ((screenPos.x - centerX) / zoom + position.x);
        int worldY = (int) ((screenPos.y - centerY) / zoom + position.y);

        return new Point(worldX, worldY);
    }

    /**
     * Logic for World to Screen transformation
     */
    public Point worldToScreen(Point worldPos, int viewWidth, int viewHeight) {
        int centerX = viewWidth / 2;
        int centerY = viewHeight / 2;

        int screenX = (int) ((worldPos.x - position.x) * zoom + centerX);
        int screenY = (int) ((worldPos.y - position.y) * zoom + centerY);

        return new Point(screenX, screenY);
    }
}