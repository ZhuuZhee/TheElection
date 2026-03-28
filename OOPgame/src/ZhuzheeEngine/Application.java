package ZhuzheeEngine;

import Core.ZhuzheeGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * The core Engine class responsible for the application lifecycle.
 * It manages the main window (JFrame), the render loop (Timer),
 * and coordinates ApplicationAdapters for game logic and rendering.
 */
public final class Application {
    private static int TARGET_FPS = 60;

    /// frame time delay in milliseconds
    private static int FRAME_DELAY_MS = 1000 / TARGET_FPS;
    /// time between each rendered frame in seconds
    public static final float DELTA_TIME = FRAME_DELAY_MS / 1000f;
    public static final long DELTA_TIME_MS = 16;

    private JFrame mainFrame;
    private Timer renderTimer;

    private int screenWidth = 1920, screenHeight = 1080;

    /// main application class for running in Application
    private ApplicationAdapter rootAdapter;

    /// for other adapters usually for in case of add new Engine-Class
    private ArrayList<ApplicationAdapter> adapters = new ArrayList<ApplicationAdapter>();

    /** Singleton instance of the Application. */
    private static Application instance;

    public static Application getInstance() {
        return instance;
    }

    private Application(ApplicationAdapter rootAdapter) {
        instance = this;
        this.rootAdapter = rootAdapter;
    }

    /// JFrame Title
    private String getTitle() {
        return "Zhuzhee Application";
    }

    /**
     * Updates the title of the main application window.
     */
    public static void setMainFrameTitle(String s) {
        if (instance == null) {
            throw new IllegalStateException(
                    "Main frame not set. Make sure your Application subclass calls super.create().");
        }
        instance.mainFrame.setTitle(s);
    }

    // ----------------- screen configuration --------------------
    public int getScreenHeight() {
        return screenHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }
    // -------------------------------------------------------

    public static JFrame getMainFrame() {
        return instance.mainFrame;
    }

    /**
     * Internal method to initialize and start the application.
     * 
     * @param app The application instance to launch.
     */
    private static void LaunchApp(Application app) {
        app.create();
        if (app.mainFrame == null) {
            throw new IllegalStateException(
                    "Main frame not set. Make sure your Application subclass calls super.create().");
        }
        startRenderLoop(app);
    }

    /**
     * Public entry point to start the engine with a specific root adapter.
     * 
     * @param appAdapter The main game/logic adapter.
     */
    public static void LaunchApp(ApplicationAdapter appAdapter) {
        LaunchApp(new Application(appAdapter));
    }

    /**
     * Stops the render loop and disposes of resources. Call when exiting.
     */
    public static void KillApp(Application app) {
        if (app.renderTimer != null) {
            app.renderTimer.stop();
            app.renderTimer = null;
        }
        app.dispose();
        app.mainFrame = null;
    }

    /** Starts the Swing Timer that drives the render() calls. */
    private static void startRenderLoop(Application app) {
        app.renderTimer = new Timer(
                FRAME_DELAY_MS,
                e -> {
                    if (app.mainFrame == null) {
                        return;
                    }
                    app.render();
                });
        app.renderTimer.setRepeats(true);
        app.renderTimer.start();
    }

    /** @return The time elapsed between frames in seconds. */
    public static float getDeltaTime() {
        return DELTA_TIME;
    }

    /** Updates the primary adapter responsible for the application logic. */
    public static void setRootApplicationAdapter(ApplicationAdapter adapter) {
        if (instance == null) {
            throw new IllegalStateException("Application: Have to Instance Yet! Can't Set Root Adapter.");
        }
        instance.rootAdapter = adapter;
    }

    /// Called when LaunchApp() is invoked.
    /// Create JFrame
    private void create() {
        mainFrame = new JFrame(getTitle());
        mainFrame.setUndecorated(true); // ทำเป็น Borderless
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        mainFrame.getContentPane().setBackground(Color.BLACK); // แก้บั๊ก OpenGL วาดฉากหลังไม่ติด

        // หาขนาดหน้าจอทั้งหมดแล้วตั้งค่าให้เต็มจอ (Borderless Fullscreen)
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setSize(screenSize.width, screenSize.height);
        mainFrame.setLocation(0, 0);

        mainFrame.setVisible(true);
        GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(mainFrame);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                KillApp(instance);
                super.windowClosing(e);
            }
        });

        rootAdapter.create();

        for (ApplicationAdapter adapter : adapters) {
            adapter.create();
        }
        if (Screen.currentScreen != null)
            Screen.currentScreen.create();
    }

    /// Called every frame after LaunchApp() succeeds.
    private void render() {
        rootAdapter.render();
        for (ApplicationAdapter adapter : adapters) {
            adapter.render();
        }
        if (Screen.currentScreen != null)
            Screen.currentScreen.render();
    }

    /// Called when AppKill() is called
    private void dispose() {
        mainFrame.dispose();
        rootAdapter.dispose();
        for (ApplicationAdapter adapter : adapters) {
            adapter.dispose();
        }
        if (Screen.currentScreen != null)
            Screen.currentScreen.dispose();
    }
}
