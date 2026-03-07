package ZhuzheeEngine;

import javax.swing.*;

public abstract class Application {
    private static int TARGET_FPS = 60;

    /// frame time delay in milliseconds
    private static int FRAME_DELAY_MS = 1000 / TARGET_FPS;
    /// time between each rendered frame in seconds
    private static float DELTA_TIME = FRAME_DELAY_MS / 1000f;

    private JFrame mainFrame;
    private Timer renderTimer;

    public JFrame getMainFrame() {
        return mainFrame;
    }

    protected void setMainFrame(JFrame frame) {
        this.mainFrame = frame;
    }

    public static void LuchApp(Application app) {
        app.Create();
        if (app.mainFrame == null) {
            throw new IllegalStateException("Main frame not set. Call setMainFrame(frame) in Create().");
        }
        startRenderLoop(app);
    }

    /** Stops the render loop and calls Depose(). Call when exiting the application. */
    public static void KillApp(Application app) {
        if (app.renderTimer != null) {
            app.renderTimer.stop();
            app.renderTimer = null;
        }
        app.Depose();
        app.mainFrame = null;
    }

    private static void startRenderLoop(Application app) {
        app.renderTimer = new Timer(FRAME_DELAY_MS, e -> {
            if (app.mainFrame == null) return;
            app.Render();
            app.mainFrame.repaint();
        });
        app.renderTimer.setRepeats(true);
        app.renderTimer.start();
    }

    public static void SetTargetFrameRate(int targetFPS) {
        if (targetFPS > 0) {
            TARGET_FPS = targetFPS;
            FRAME_DELAY_MS = 1000 / TARGET_FPS;
            DELTA_TIME = FRAME_DELAY_MS / 1000f;
        } else {
            System.err.println("Application: Target FPS must be greater than zero.");
        }
    }

    public static float getDeltaTime() {
        return DELTA_TIME;
    }
    /// Called when LuchApp() is invoked.
    protected void Create(){

    }
    protected void Resize(int width, int height){

    }
    /// Called every frame after LuchApp() succeeds.
    protected void Render(){

    }
    /// Called when AppKill() is called
    protected void Depose(){

    }
}
