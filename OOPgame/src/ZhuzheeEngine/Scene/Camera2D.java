package ZhuzheeEngine.Scene;

import Core.ZhuzheeGame;
import ZhuzheeEngine.Application;
import ZhuzheeEngine.MathZ;

import java.awt.Point;

public class Camera2D {
    private Point position;
    private double zoom;

    public Camera2D() {
        this.position = new Point(0, 0);
        this.zoom = 1.0;
    }

    // Getters and Setters
    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    /**
     * Moves the camera by a specific offset
     */
    public void translate(int dx, int dy) {
        position.translate(dx, dy);
    }

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


    /**
     * Shifts the camera by a specific amount from its current position.
     *
     * @param dx The amount to move on the X axis
     * @param dy The amount to move on the Y axis
     */
    private void TranslateCamera(int dx, int dy) {
        Point current = getPosition();
        setPosition(new Point(current.x + dx, current.y + dy));
    }

    public void centerCameraOn(GameObject target) {
        if (target != null) {
            // Assuming your SceneObject has a getPosition() returning a Point
            Point targetPos = target.getPosition();
            setPosition(new Point(targetPos.x, targetPos.y));
        }
    }
    private  Thread lerpThread = null;
    public void LerpCameraTo(Point worldPosition, float duration) {
        lerpThread =  new Thread(() -> {
            try {
                float dx = worldPosition.x - position.x, dy = worldPosition.y - position.y;
                float distance = MathZ.Length(dx, dy);
                float speed = distance / (duration * 1000);//1000 millisec
                speed *= Application.getDeltaTime();
                while (distance > 2) {
                    dx = worldPosition.x - position.x;
                    dy = worldPosition.y - position.y;
                    distance = MathZ.Length(dx, dy);
                    int x = (int)((dx > 0? 1 : -1) * Math.ceil(Math.abs(dx) * speed)),
                            y = (int)((dy > 0? 1 : -1) * Math.ceil(Math.abs(dy) * speed));
                    TranslateCamera(x, y);
                    //1000 -> 1 seconds
                    //0.1 seconds -> 100
                    long sleepTime = (long)Math.round(Application.getDeltaTime()*1000);
                    System.out.println("distance " + distance);
                    System.out.println("speed " + speed);
                    System.out.println("speed x y " + x + "," + y);
                    System.out.println("dx x y " + dx + "," + dy);
                    System.out.println("sleep " + sleepTime);
                    Thread.sleep(sleepTime);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });

        lerpThread.start();
    }
}