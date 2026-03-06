package Scene2D;

import Card.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import Card.*;

public class Scene extends JPanel {
    private static final float Z_INDEX_TOP = 999f;
    private static final float Z_INDEX_NORMAL = 0f;
    private static final int SNAP_MARGIN = 15;
    private static final double ZOOM_OFFSET = 20.0;

    private List<GameObject> gameObjects;

    //move to card class or create new class for handle drag and drop
    private Card draggedCard;
    private Card hoveredCard;

    //sigelton
    public static Scene Instance;

    public Scene() {
        gameObjects = new ArrayList<>();
        initializeGameObjects();
        setupMouseListener();
        Instance = this;
    }

    public static void initialize(GameObject gameObject) {
        if (Instance != null) Instance.gameObjects.add(gameObject);
        else {
            System.out.println("Does not create scene object");
        }
    }

    //not globalize, for test only
    private void initializeGameObjects() {
        gameObjects.add(new CardSlot(50, 150, 100, 150));
        gameObjects.add(new ActionCard("Red Dragon", 0, 0, true));
        gameObjects.add(new ActionCard("Blue Eyes", 150, 0, true));
    }

    //move to card class or create new class for handle drag and drop
    private void setupMouseListener() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            private int getWorldX(MouseEvent e) {
                return e.getX() - (getWidth() / 2);
            }

            private int getWorldY(MouseEvent e) {
                return e.getY() - (getHeight() / 2);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMoved(getWorldX(e), getWorldY(e));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(getWorldX(e), getWorldY(e));
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(getWorldX(e), getWorldY(e));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseReleased();
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    // ---------------------------------------------------------------------------------------------
    // Mouse Action Handlers (Single Responsibility & Guard Clauses)
    // ---------------------------------------------------------------------------------------------

    private void handleMouseMoved(int mouseX, int mouseY) {
        Card newlyHovered = null;

        for (int i = gameObjects.size() - 1; i >= 0; i--) {
            GameObject obj = gameObjects.get(i);
            if (!(obj instanceof Card)) continue; // Early Return via continue

            if (GameObject.isInsideBoundaries(mouseX, mouseY, obj)) {
                newlyHovered = (Card) obj;
                break;
            }
        }

        if (hoveredCard != newlyHovered) {
            hoveredCard = newlyHovered;
            repaint();
        }
    }

    private void handleMousePressed(int mouseX, int mouseY) {
        for (int i = gameObjects.size() - 1; i >= 0; i--) {
            GameObject obj = gameObjects.get(i);
            if (!(obj instanceof Card)) continue;

            Card card = (Card) obj;
            if (GameObject.isInsideBoundaries(mouseX, mouseY, card)) {
                card.grab(mouseX, mouseY);
                draggedCard = card;

                // 💡 แก้จาก setzIndex เป็น setZIndex (ตัว I ต้องพิมพ์ใหญ่)
                card.setzIndex(Z_INDEX_TOP);

                sortGameObjects();
                repaint();
                return;
            }
        }
    }

    private void handleMouseDragged(int mouseX, int mouseY) {
        if (draggedCard == null) return;

        draggedCard.drag(mouseX, mouseY);
        repaint();
    }

    private void handleMouseReleased() {
        if (draggedCard == null) return;

        draggedCard.drop();
        draggedCard.setzIndex(Z_INDEX_NORMAL);

        snapCardToSlot(draggedCard);

        sortGameObjects();
        draggedCard = null;
        repaint();
    }

    private void snapCardToSlot(Card card) {
        //  สร้างกล่องครอบตัวการ์ด
        Rectangle cardRect = new Rectangle(
                card.getPosition().x, card.getPosition().y,
                card.getSize().x, card.getSize().y
        );

        for (GameObject obj : gameObjects) {
            if (!(obj instanceof CardSlot)) continue;

            // สร้างกล่องครอบ CardSlot
            Rectangle slotMagneticField = new Rectangle(
                    obj.getPosition().x - SNAP_MARGIN,
                    obj.getPosition().y - SNAP_MARGIN,
                    obj.getSize().x + (SNAP_MARGIN * 2),
                    obj.getSize().y + (SNAP_MARGIN * 2)
            );

            // เช็คว่ากล่องทั้งสองเกยกันไหม
            if (cardRect.intersects(slotMagneticField)) {
                card.getPosition().setLocation(obj.getPosition().x, obj.getPosition().y);
                break;
            }
        }
    }

    /// sorting rendering squences of gameObjects by z index
    private void sortGameObjects() {
        gameObjects.sort((o1, o2) -> Float.compare(o1.getzIndex(), o2.getzIndex()));
    }

    //--------------------------------------------------------------------
    //------- this is the main update method for scene! (DO NOT CHANGE) ------------
    //--------------------------------------------------------------------
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //powerfull subclass pf Graphics -> can translate the origin of cordination system
        Graphics2D g2d = (Graphics2D) g;
        //set the origin of drawing any objects to center of component
        g2d.translate(getWidth() / 2, getHeight() / 2);
        //render object
        for (GameObject obj : gameObjects) {
            //obj.draw(g2d); // expected code

            //for test only
            if (obj == hoveredCard) {
                renderHoverEffect(g2d, obj);
            } else {
                obj.draw(g2d);
            }
        }
    }
//----------------------------------------

    /// /move to card class or create new class for handle drag and drop
    private void renderHoverEffect(Graphics2D g2d, GameObject obj) {
        int cx = obj.getPosition().x + (obj.getSize().x / 2);
        int cy = obj.getPosition().y + (obj.getSize().y / 2);

        double scaleX = (obj.getSize().x + ZOOM_OFFSET) / obj.getSize().x;
        double scaleY = (obj.getSize().y + ZOOM_OFFSET) / obj.getSize().y;

        AffineTransform oldTransform = g2d.getTransform();

        g2d.translate(cx, cy);
        g2d.scale(scaleX, scaleY);
        g2d.translate(-cx, -cy);

        obj.draw(g2d);
        g2d.setTransform(oldTransform);
    }
// for test (Dot not change now)
    public static void main(String[] args) {
        JFrame frame = new JFrame("Scene");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(new Scene());
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
