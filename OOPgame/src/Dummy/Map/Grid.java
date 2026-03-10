package Dummy.Map;

import Core.ZhuzheeGame;
import ZhuzheeEngine.Scene.GameObject;

import java.awt.Point;

public class Grid extends GameObject {
    private Point position;
    private float size;
    public Grid() {
        super(0, 0, 1280, 720, ZhuzheeGame.MAIN_SCENE);
    }
}
