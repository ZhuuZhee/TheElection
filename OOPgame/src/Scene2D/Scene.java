package Scene2D;

import Card.ActionCard;
import Card.Card;
import Card.CardSlot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
        initializeGameObjects();
    }

    public static void initialize(GameObject gameObject) {
        if (Instance != null) Instance.gameObjects.add(gameObject);
        else {
            System.out.println("Does not create scene object");
        }
    }
    // Getter เพื่อให้ Card สามารถเข้าถึง List ไปเช็ค Slot ได้
    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    //not globalize, for test only
    private void initializeGameObjects() {
        gameObjects.add(new CardSlot(50, 150, 100, 150));
        ActionCard card1 = new ActionCard("Red Dragon", 0, 0, true);
        gameObjects.add(card1);
        ActionCard card2 = new ActionCard("Blue Eyes", 150, 0, true);
        card2.setDraggable(false); // <--- setDraggable # default true
        gameObjects.add(card2);
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


    // for test (Dot not change now)
    public static void main(String[] args) {
        JFrame frame = new JFrame("Scene");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        var scene = new Scene(frame);
        frame.add(scene);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    //for test only
//    JFrame frame;
//    JPanel current;
//    public void ChangeScreen(JPanel newScreen){
//        frame.remove(current);
//        current = newScreen;
//        frame.add(current);
//    }
    //screeen
    //onDeposeScreen(){ } // called when current screen is close
    //onOpenScreen() { } // called when current screen is open
}
