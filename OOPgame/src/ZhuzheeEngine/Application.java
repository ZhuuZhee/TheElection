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
    protected int screenWidth = 1280, screenHeight = 720;

    protected static Application Instance;

    public Application() {
        Instance = this;
    }

    public String getTitle() {
        return "ZhuzheeApp";
    }

    //----------------- screen configuration --------------------
    /// default is 1280 x 720
    public void setScreenSize(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public int getScreenHeight() {
        return screenHeight;
    }
    public int getScreenWidth(){
        return screenWidth;
    }
    // -------------------------------------------------------

    public static JFrame getMainFrame() {
        return Instance.mainFrame;
    }

    public static void LuchApp(Application app) {
        app.create();
        if (app.mainFrame == null) {
            throw new IllegalStateException("Main frame not set. Make sure your Application subclass calls super.create().");
        }
        startRenderLoop(app);
    }

    /**
     * Stops the render loop and calls Depose(). Call when exiting the application.
     */
    public static void KillApp(Application app) {
        if (app.renderTimer != null) {
            app.renderTimer.stop();
            app.renderTimer = null;
        }
        app.depose();
        app.mainFrame = null;
    }

    private static void startRenderLoop(Application app) {
        app.renderTimer = new Timer(
                FRAME_DELAY_MS,
                e -> {
                    if (app.mainFrame == null) {
                        return;
                    }
                    app.render();
                }
        );
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
    /// Create JFrame
    protected void create() {
        mainFrame = new JFrame(getTitle());
        mainFrame.setSize(screenWidth,screenHeight);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }

    protected void resize(int width, int height) {

    }

    /// Called every frame after LuchApp() succeeds.
    protected void render() {
        mainFrame.repaint();
    }

    /// Called when AppKill() is called
    protected void depose() {

    }
}
