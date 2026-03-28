import Core.ZhuzheeGame;
import ZhuzheeEngine.Application;

/// Main Entry point of Game
public class Main {
    public static void main(String[] args) {
        // Force hardware acceleration for Java 2D to fix low FPS and CPU usage
//        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("sun.java2d.d3d", "true");
        System.setProperty("sun.java2d.noddraw", "false");

        var myGame = new ZhuzheeGame();
        Application.LaunchApp(myGame);
    }
}
