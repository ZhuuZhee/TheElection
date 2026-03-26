package ZhuzheeEngine;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

public final class Application {
    private static int TARGET_FPS = 60;

    /// frame time delay in milliseconds
    private static int FRAME_DELAY_MS = 1000 / TARGET_FPS;
    /// time between each rendered frame in seconds
    public static final float DELTA_TIME = FRAME_DELAY_MS / 1000f;
    public static final long DELTA_TIME_MS = 16;

    private JFrame mainFrame;
    private Timer renderTimer;

    private int screenWidth = 1280, screenHeight = 720;

    /// main application class for running in Application
    private ApplicationAdapter rootAdapter;

    /// for other adapters usually for in case of add new Engine-Class
    private ArrayList<ApplicationAdapter> adapters = new ArrayList<ApplicationAdapter>();

    /// Singletons
    private static Application instance;
    public static Application getInstance(){
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
    public static void setMainFrameTitle(String s){
        if(instance == null){
            throw new IllegalStateException("Main frame not set. Make sure your Application subclass calls super.create().");
        }
        instance.mainFrame.setTitle(s);
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
        return instance.mainFrame;
    }
    public static void addAdapter(ApplicationAdapter adapter){
        if(instance == null){
            throw  new IllegalStateException("Make sure your Application subclass calls super.create().");
        }
        if(instance.adapters.contains(adapter)){
            System.err.println("Application already add this adapter.");
            throw new RuntimeException("Application already add this adapter.");
        }
        instance.adapters.add(adapter);
    }
    public static void removeAdapter(ApplicationAdapter adapter){
        instance.adapters.remove(adapter);
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

    public static float getDeltaTime() {
        return DELTA_TIME;
    }

    public static void setRootApplicationAdapter(ApplicationAdapter adapter){
        if(instance == null){
            throw new IllegalStateException("Application: Have to Instance Yet! Can't Set Root Adapter.");
        }
        instance.rootAdapter = adapter;
    }
    /// Called when LaunchApp() is invoked.
    /// Create JFrame
    private void create() {
        mainFrame = new JFrame(getTitle());
        mainFrame.setSize(screenWidth,screenHeight);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                KillApp(instance);
                super.windowClosing(e);
            }
        });

        rootAdapter.create();

        for(ApplicationAdapter adapter : adapters){
            adapter.create();
        }
        if(Screen.currentScreen != null)
            Screen.currentScreen.create();
    }

    private void resize(int width, int height) {
        rootAdapter.resize(width,height);
        for(ApplicationAdapter adapter : adapters){
            adapter.resize(width,height);
        }
        if(Screen.currentScreen != null)
            Screen.currentScreen.resize(width,height);
    }

    /// Called every frame after LaunchApp() succeeds.
    private void render() {
        mainFrame.repaint();
        rootAdapter.render();
        for(ApplicationAdapter adapter : adapters){
            adapter.render();
        }
        if(Screen.currentScreen != null)
            Screen.currentScreen.render();
    }

    /// Called when AppKill() is called
    private void dispose() {
        mainFrame.dispose();
        rootAdapter.dispose();
        for(ApplicationAdapter adapter : adapters){
            adapter.dispose();
        }
        if(Screen.currentScreen != null)
            Screen.currentScreen.dispose();
    }
}
