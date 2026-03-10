package ZhuzheeEngine;

import javax.swing.*;

public final class Application {
    private static int TARGET_FPS = 60;

    /// frame time delay in milliseconds
    private static int FRAME_DELAY_MS = 1000 / TARGET_FPS;
    /// time between each rendered frame in seconds
    private static float DELTA_TIME = FRAME_DELAY_MS / 1000f;

    private JFrame mainFrame;
    private Timer renderTimer;

    private int screenWidth = 1280, screenHeight = 720;

    /// main application class for running in Application
    private ApplicationAdapter rootAdapter;

    /// Singletons
    private static Application Instance;
    public static Application getInstance(){
        return Instance;
    }
    private Application(ApplicationAdapter rootAdapter) {
        Instance = this;
        this.rootAdapter = rootAdapter;
    }

    /// JFrame Title
    private String getTitle() {
        return "Zhuzhee Application";
    }
    public static  void setMainFrameTitle(String s){
        if(Instance == null){
            throw new IllegalStateException("Main frame not set. Make sure your Application subclass calls super.create().");
        }
        Instance.mainFrame.setTitle(s);
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

    private static void LaunchApp(Application app) {
        app.create();
        if (app.mainFrame == null) {
            throw new IllegalStateException("Main frame not set. Make sure your Application subclass calls super.create().");
        }
        startRenderLoop(app);
    }
    public static void LaunchApp(ApplicationAdapter appAdapter) {
        LaunchApp(new Application(appAdapter));
    }
    /**
     * Stops the render loop and calls Depose(). Call when exiting the application.
     */
    public static void KillApp(Application app) {
        if (app.renderTimer != null) {
            app.renderTimer.stop();
            app.renderTimer = null;
        }
        app.dispose();
        app.mainFrame = null;
    }

    /// render looping
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
            throw new NumberFormatException("Application: Target FPS must be greater than zero.");
        }
    }

    public static float getDeltaTime() {
        return DELTA_TIME;
    }

    public static void SetRootApplicationAdapter(ApplicationAdapter adapter){
        if(Instance == null){
            throw new IllegalStateException("Application: Have to Instance Yet! Can't Set Root Adapter.");
        }
        Instance.rootAdapter = adapter;
    }
    /// Called when LaunchApp() is invoked.
    /// Create JFrame
    private void create() {
        mainFrame = new JFrame(getTitle());
        mainFrame.setSize(screenWidth,screenHeight);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
        rootAdapter.create();
        if(Screen.currentScreen != null)
            Screen.currentScreen.create();
    }

    private void resize(int width, int height) {
        rootAdapter.resize(width,height);
        if(Screen.currentScreen != null)
            Screen.currentScreen.resize(width,height);
    }

    /// Called every frame after LaunchApp() succeeds.
    private void render() {
        mainFrame.repaint();
        rootAdapter.render();
        if(Screen.currentScreen != null)
            Screen.currentScreen.render();
    }

    /// Called when AppKill() is called
    private void dispose() {
        mainFrame.dispose();
        rootAdapter.dispose();
        if(Screen.currentScreen != null)
            Screen.currentScreen.dispose();
    }
}
