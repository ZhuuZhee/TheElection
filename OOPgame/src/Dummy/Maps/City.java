package Dummy.Maps;

public class City {
    public PoliticsStats stats;
    public int population;
    public Grid[] Grids;

    public City(int facility,
                int environment,
                int economy,
                int population,
                int size) {
        this.stats = new PoliticsStats(facility, environment, economy);
        this.population = population;
        Grids = new Grid[size];
    }
}
