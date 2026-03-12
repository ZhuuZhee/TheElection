package Dummy.Maps;

import java.util.ArrayList;

public class District {
    public ArrayList<City> cities;
    public EncounterEvent[] event;

    public District(int cities, int event) {
        this.event = new EncounterEvent[event];
    }
}
