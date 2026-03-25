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

    private Thread lerpThread = null;

    /// smoothly move camera to target point in duration(time)
    public void LerpCameraTo(Point worldPosition, float duration) {
        // 1. ถ้ามี Thread เดิมทำงานอยู่ ให้สั่งหยุดก่อน
        if (lerpThread != null && lerpThread.isAlive()) {
            lerpThread.interrupt();
        }
        // เก็บตำแหน่งเริ่มต้นไว้
        final float startX = position.x;
        final float startY = position.y;
        final float targetX = worldPosition.x;
        final float targetY = worldPosition.y;
        final long durationMs = (long) (duration * 1000);

        lerpThread = new Thread(() -> {
            try {
                long startTime = System.currentTimeMillis();
                long elapsed = 0;

                while (elapsed < durationMs) {
                    elapsed = System.currentTimeMillis() - startTime;

                    // หาค่า t (0.0 ถึง 1.0) ว่าเวลาผ่านไปกี่เปอร์เซ็นต์แล้ว
                    float t = (float) elapsed / durationMs;
                    if (t > 1f) t = 1f; // กันเกิน

                    // สูตร Linear Interpolation: start + (target - start) * t
                    float nextX = startX + (targetX - startX) * t;
                    float nextY = startY + (targetY - startY) * t;

                    position.setLocation(nextX,nextY);

                    Thread.sleep(Application.DELTA_TIME_MS);
                }

                // ตบท้ายให้เป๊ะที่จุดหมาย
                TranslateCamera((int)(targetX - position.x), (int)(targetY - position.y));
                lerpThread = null;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        lerpThread.start();
    }
    private Thread lerpZoom = null;
    /// smoothly zoom camera to target zoom value in duration(time)
    public void LerpZoom(float targetZoom, float duration) {
        if (lerpZoom != null && lerpZoom.isAlive()) {
            lerpZoom.interrupt();
        }
        final long startTime = System.currentTimeMillis();
        final long durationMs = (long) duration * 1000;
        final float startZoom = zoom;
        new Thread(() -> {
            long elapsed = 0;
            try {
                while (elapsed < durationMs) {
                    elapsed = System.currentTimeMillis() - startTime;

                    // หาค่า t (0.0 ถึง 1.0) ว่าเวลาผ่านไปกี่เปอร์เซ็นต์แล้ว
                    float t = (float) elapsed / durationMs;
                    if (t > 1f) t = 1f; // กันเกิน
                    float nextZoom = startZoom + (targetZoom - startZoom) * t;
                    zoom = nextZoom;
                    Thread.sleep(Application.DELTA_TIME_MS);
                }
                zoom = targetZoom;
                lerpZoom = null;
                } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();
    }
}