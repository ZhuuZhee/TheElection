package ZhuzheeEngine.Scene2D;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Scene extends JPanel {
    protected List<GameObject> gameObjects;
    private MouseHandler mouseHandler;
    public JFrame MainFrame; // for prototype

    /// can access this object by using Scene.Instance (this is called Sigelton)
    public static Scene Instance;

    public Scene(JFrame mainFrame) {
        Instance = this;
        MainFrame = mainFrame;
        gameObjects = new ArrayList<>();
        mouseHandler = new MouseHandler(this);
    }

    public static void register(GameObject gameObject) {
        if (Instance != null) Instance.gameObjects.add(gameObject);
        else {
            System.out.println("Does not create scene object");
        }
    }
    // Getter เพื่อให้ Card สามารถเข้าถึง List ไปเช็ค Slot ได้
    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    // sorting rendering squences of gameObjects by z index
    public void sortGameObjects() {
        gameObjects.sort((o1, o2) -> Float.compare(o1.getzIndex(), o2.getzIndex()));
    }

    //--------------------------------------------------------------------
    //------- this is the main update method for scene! (DO NOT CHANGE) ------------
    //--------------------------------------------------------------------
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        //set the origin of drawing any objects to center of component
        g2d.translate(getWidth() / 2, getHeight() / 2);

        // render object
        for (GameObject obj : gameObjects) {
            obj.draw(g2d);
        }
    }
}
