import Core.ZhuzheeGame;
import ZhuzheeEngine.Application;

/// Main Entry point of Game
public class Main {
    public static void main(String[] args) {
        var myGame = new ZhuzheeGame();
        Application.LaunchApp(myGame);
    }
}
