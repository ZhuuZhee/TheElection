package Dummy.Maps;

public class City {
    private final String cityName;
    public PoliticsStats stats;
    public int population;
    public Grid[] Grids;

    public City(String cityName, int facility, int environment, int economy, int population, int size) {
        this.stats = new PoliticsStats(facility, environment, economy);
        this.population = population;
        Grids = new Grid[size];
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
