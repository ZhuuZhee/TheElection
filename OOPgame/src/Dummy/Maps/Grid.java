package Dummy.Maps;

import Core.ZhuzheeGame;
import ZhuzheeEngine.Scene.GameObject;
import ZhuzheeEngine.Scene.Scene2D;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Grid extends GameObject{
    private Point position;
    private float size;
    private District district;
    private City city;
    public Grid(City city, District district) {
        super(0, 0, 10, 10, ZhuzheeGame.MAIN_SCENE);
        setCity(city);
        setDistrict(district);
        Scene2D scene = getScene();
        scene.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("Pressed");
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                System.out.println("Move");
            }
        });
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
