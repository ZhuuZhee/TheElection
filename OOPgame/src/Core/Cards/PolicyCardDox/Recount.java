package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.CardSlot;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.ZhuzheeGame;

public class Recount extends PolicyCard {
    public Recount(int x, int y, String imagePath) {
        super("Recount", x, y, imagePath, -5);
    }

    @Override
    public boolean isActive() {
        return true;
    }
    @Override
    public void onActionCardPlayed(ActionCard playedCard, City city) {
        if (isActive()) {
            PoliticsStats stats = playedCard.getStats();

            if (stats != null) {
                // ดึงค่า Environment,Facility,Economy ของการ์ดที่เพิ่งเล่นออกมาเช็ค
                int envValue = stats.getStats(PoliticsStats.ENVIRONMENT);
                int facValue = stats.getStats(PoliticsStats.FACILITY);
                int ecoValue = stats.getStats(PoliticsStats.ECONOMY);
                // ถ้าการ์ดใบนั้นมีค่า Environment เป็นบวก
                if (envValue > 0) {
                    System.out.println("----------------------------------");
                    System.out.println("PolicyCard [Local Campaign] ทำงาน!");
                    System.out.println(">>> คุณได้รับ +3 ในทุกstat <<<");
                    System.out.println("----------------------------------");

                    // ดึง Environment,Facility,Economy มาบวก 3
                    stats.addStats(PoliticsStats.ENVIRONMENT, 3);
                    stats.addStats(PoliticsStats.FACILITY, 3);
                    stats.addStats(PoliticsStats.ECONOMY, 3);
                }
            }
        }
    }
}
