package Dummy.Maps;

import Core.ZhuzheeGame;
import ZhuzheeEngine.Scene.GameObject;

import java.awt.Point;

public class Grid extends GameObject {
    private Point position;
    private float size;
    private District district;
    private City city;
    public Grid(City city, District district) {
        super(0, 0, 10, 10, ZhuzheeGame.MAIN_SCENE);
        setCity(city);
        setDistrict(district);
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
