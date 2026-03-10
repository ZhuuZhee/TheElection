package Dummy.Maps;

public class City {
    public PoliticsStats stats;
    public int population;
    public Grid[] Grids;

    public City(int facility,
                int military,
                int economy,
                int population,
                int size) {
        this.stats = new PoliticsStats(facility, military, economy);
        this.population = population;
        Grids = new Grid[size];
    }
}
