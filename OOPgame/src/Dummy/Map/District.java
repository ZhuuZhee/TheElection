package Dummy.Map;

public class District {
    public City[] cities;
    public EncounterEvent[] event;

    public District(int cities, int event) {
        this.cities = new City[cities];
        this.event = new EncounterEvent[event];
    }
}
