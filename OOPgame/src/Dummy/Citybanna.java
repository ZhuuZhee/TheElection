/**
 * @Xynezter 9/3/2026 18:50
 */

// เพิ่ม Attributes set Constructor เพิ่ม method applyStats # for test can Change to map
package Dummy;
import java.util.List;
import Dummy.Maps.PoliticsStats;
public class Citybanna {
    private String cityName;
    private PoliticsStats cityStats;

    public Citybanna(String cityName, int economic, int facility, int environment) {
        this.cityName = cityName;
        this.cityStats = new PoliticsStats(facility, environment, economic);
    }
    public String getCityName() {
        return this.cityName;
    }

    // เพิ่มค่า List จาก Cards follow Index add to city
//    public void applyStats(PoliticsStats cardStats) {
//        if (cardStats != null) {
//            this.cityStats.addStats(PoliticsStats.Economy, cardStats.getStats(PoliticsStats.Economy));
//            this.cityStats.addStats(PoliticsStats.Facility, cardStats.getStats(PoliticsStats.Facility));
//            this.cityStats.addStats(PoliticsStats.Environment, cardStats.getStats(PoliticsStats.Environment));
//            System.out.println(cityName);
//            printStats();
//        }
//    }

    // for debug in consol
    public void printStats() {
        System.out.println("----------------------------------");
        System.out.println("Economic: " + cityStats.getStats(PoliticsStats.Economy));
        System.out.println("Facility: " + cityStats.getStats(PoliticsStats.Facility));
        System.out.println("Environment: " + cityStats.getStats(PoliticsStats.Environment));
        System.out.println("----------------------------------");
    }
}
