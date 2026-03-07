package ZhuzheeEngine.Scene2D;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Scene extends JPanel {
    protected List<GameObject> gameObjects;
    private MouseHandler mouseHandler;
    public JFrame MainFrame; // for prototype
    private Point origin = new Point(0,0);
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
    public static void SetSceneOrigin(Point newPoint){
        if(Instance != null){
            Instance.origin = newPoint;
        }
        else System.out.println("Scene Object is not created, can't set origin");
    }
    // Getter เพื่อให้ Core.Card สามารถเข้าถึง List ไปเช็ค Slot ได้
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
        Point centerPoint = new Point(getWidth() / 2, getHeight() / 2);
        g2d.translate(centerPoint.x + origin.x, centerPoint.y + origin.y);

        // render object
        for (GameObject obj : gameObjects) {
            obj.draw(g2d);
        }
    }
}
