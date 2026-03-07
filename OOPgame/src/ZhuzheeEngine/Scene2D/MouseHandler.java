package ZhuzheeEngine.Scene2D;

import Core.Card.Card;

import java.util.List;

public class MouseHandler {
    private Scene scene;

    public MouseHandler(Scene scene) {
        this.scene = scene;
    }

    public void handleMouseMoved(int mouseX, int mouseY) {
        boolean foundHover = false;
        List<GameObject> gameObjects = scene.getGameObjects();

        for (int i = gameObjects.size() - 1; i >= 0; i--) {
            GameObject obj = gameObjects.get(i);
            if (obj instanceof Card) {
                Card card = (Card) obj;
                if (!foundHover && GameObject.isInsideBoundaries(mouseX, mouseY, card)) {
                    card.setHovered(true);
                    foundHover = true;
                } else {
                    card.setHovered(false);
                }
            }
        }
        scene.repaint();
    }

    public void handleMousePressed(int mouseX, int mouseY) {
        scene.sortGameObjects();
        List<GameObject> gameObjects = scene.getGameObjects();

        for (int i = gameObjects.size() - 1; i >= 0; i--) {
            GameObject obj = gameObjects.get(i);
            if (obj instanceof Card) {
                Card card = (Card) obj;
                if (card.onMousePressed(mouseX, mouseY)) {
                    scene.sortGameObjects();
                    scene.repaint();
                    return;
                }
            }
        }
    }

    public void handleMouseDragged(int mouseX, int mouseY) {
        for (GameObject obj : scene.getGameObjects()) {
            if (obj instanceof Card) {
                ((Card) obj).onMouseDragged(mouseX, mouseY);
            }
        }
        scene.repaint();
    }

    public void handleMouseReleased() {
        for (GameObject obj : scene.getGameObjects()) {
            if (obj instanceof Card) {
                ((Card) obj).onMouseReleased();
            }
        }
        scene.sortGameObjects();
        scene.repaint();
    }
}