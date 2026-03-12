package Dummy.Maps;

import java.awt.*;
import java.util.ArrayList;

public class City {
    private final String cityName;
    public PoliticsStats stats;
    public int population;
    public ArrayList<Grid> Grids = new ArrayList<Grid>();
    private Color color;

    public City(String cityName, int facility, int environment, int economy, int population) {
        this.stats = new PoliticsStats(facility, environment, economy);
        this.population = population;
        this.cityName = cityName;
    }

    public String getCityName() {
        return this.cityName;
    }
    /** Xynezter 11/3/2026 17:42 : fix method stat**/
    public void applyStats(PoliticsStats cardStats) {
        if (cardStats != null) {
            this.stats.addStats(PoliticsStats.Economy, cardStats.getStats(PoliticsStats.Economy));
            this.stats.addStats(PoliticsStats.Facility, cardStats.getStats(PoliticsStats.Facility));
            this.stats.addStats(PoliticsStats.Environment, cardStats.getStats(PoliticsStats.Environment));
            System.out.println(cityName);
            printStats();
        }
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    // wait for business logic
    public void updatePopulation(PoliticsStats cardStats) {
        return;
    }

    public void printStats() {
        System.out.println("----------------------------------");
        System.out.println("City: " + this.getCityName());
        System.out.println("Economic: " + stats.getStats(PoliticsStats.Economy));
        System.out.println("Facility: " + stats.getStats(PoliticsStats.Facility));
        System.out.println("Environment: " + stats.getStats(PoliticsStats.Environment));
        System.out.println("Population: " + population);
        System.out.println("----------------------------------");
    }
}
