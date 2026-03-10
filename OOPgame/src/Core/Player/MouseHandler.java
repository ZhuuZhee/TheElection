package Core.Player;

import Core.Card.Card;
import ZhuzheeEngine.Scene.SceneObject;
import ZhuzheeEngine.Scene.Scene2D;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MouseHandler {
    private final Scene2D scene2D;

    public MouseHandler(Scene2D scene2D) {
        this.scene2D = scene2D;

        //------ add event adapter for hook mouse event ------
        scene2D.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                Point worldPoint = scene2D.Screen2WorldPoint(new Point(e.getX(),e.getY()));
                handleMousePressed(worldPoint.x,worldPoint.y);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                handleMouseReleased();
            }
        });

        scene2D.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                Point worldPoint = scene2D.Screen2WorldPoint(new Point(e.getX(),e.getY()));
                handleMouseDragged(worldPoint.x,worldPoint.y);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                Point worldPoint = scene2D.Screen2WorldPoint(new Point(e.getX(),e.getY()));
                handleMouseMoved(worldPoint.x,worldPoint.y);
            }
        });
    }

    public void handleMouseMoved(int mouseX, int mouseY) {
        boolean foundHover = false;
        List<SceneObject> sceneObjects = scene2D.getGameObjects();

        for (int i = sceneObjects.size() - 1; i >= 0; i--) {
            SceneObject obj = sceneObjects.get(i);
            if (obj instanceof Card card) {
                if (!foundHover && card.isInsideBoundaries(mouseX, mouseY)) {
                    card.setHovered(true);
                    foundHover = true;
                } else {
                    card.setHovered(false);
                }
            }
        }
    }

    public void handleMousePressed(int mouseX, int mouseY) {
//        System.out.println("Pressed " + mouseX + "," + mouseY);
        List<SceneObject> sceneObjects = scene2D.getGameObjects();

        for (int i = sceneObjects.size() - 1; i >= 0; i--) {
            SceneObject obj = sceneObjects.get(i);
            if (obj instanceof Card card) {
                if (card.onMousePressed(mouseX, mouseY)) {
                    return;
                }
            }
        }

    }

    public void handleMouseDragged(int mouseX, int mouseY) {
        for (SceneObject obj : scene2D.getGameObjects()) {
            if (obj instanceof Card) {
                ((Card) obj).onMouseDragged(mouseX, mouseY);
            }
        }
    }

    public void handleMouseReleased() {
        for (SceneObject obj : scene2D.getGameObjects()) {
            if (obj instanceof Card) {
                ((Card) obj).onMouseReleased();
            }
        }
    }
}