package Dummy.Maps;

import java.util.ArrayList;
import java.awt.Color;

public class District {
    public ArrayList<City> cities;
    public EncounterEvent[] event;
    public String districtName;
    private Color color;

    public District(ArrayList<City> cities, int event, String districtName) {
        this.cities = cities;
        this.event = new EncounterEvent[event];
        this.districtName = districtName;
    }

    public District(String districtName) {
        this.districtName = districtName;
        cities = new ArrayList<>();
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void addCity(City city) {
        cities.add(city);
    }

    public City getCity(String cityName) {
        for (City city : cities) {
            if (city.getCityName().equals(cityName)) {
                return city;
            }
        }
        return null;
    }

    public ArrayList<City> getCities() {
        return cities;
    }
}
