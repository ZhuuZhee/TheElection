package Dummy;
import java.util.List;
public class Citybanna {
    private String cityName;
    private float economic;
    private float facility;
    private float environment;
    private float population;

    public Citybanna(String cityName, float economic, float facility, float environment, float population) {
        this.cityName = cityName;
        this.economic = economic;
        this.facility = facility;
        this.environment = environment;
        this.population = population;
    }
    public String getCityName() {
        return this.cityName;
    }

    public void applyStats(List<Integer> cardStats) {
        if (cardStats != null && cardStats.size() >= 4) {
            this.economic += cardStats.get(0);
            this.facility += cardStats.get(1);
            this.environment += cardStats.get(2);
            this.population += cardStats.get(3);
            System.out.println(cityName);
            printStats();
        }
    }

    // for debug in consol
    public void printStats() {
        System.out.println("----------------------------------");
        System.out.println("Economic: " + economic);
        System.out.println("Facility: " + facility);
        System.out.println("Environment: " + environment);
        System.out.println("Population: " + population);
        System.out.println("----------------------------------");
    }
}
