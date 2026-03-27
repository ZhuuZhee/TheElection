package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Maps.PoliticsStats;
import Core.ZhuzheeGame;

public class Propaganda extends PolicyCard {
    // ใช้ตัวแปรเช็คกันการทำงานซ้ำ
    private ActionCard lastProcessedCard = null;

    public Propaganda(int x, int y, String imagePath) {
        super("Propaganda", x, y, imagePath, -4);
        this.description = "Skill: All played Dev Cards gain +3 stats, but you lose 2 coin per card.";
    }

    @Override
    public boolean isActive() {
        return ZhuzheeGame.POLICY_CARD_HAND != null
                && ZhuzheeGame.POLICY_CARD_HAND.containsCard(this);
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void onActionCardPlayed(ActionCard playedCard, City city) {
        boolean active = isActive();

        if (active && playedCard != lastProcessedCard
                && ZhuzheeGame.CLIENT != null
                && ZhuzheeGame.CLIENT.getLocalPlayer() != null) {

            //เพิ่ม Stats +3 ให้การ์ดที่กำลังจะลง
            PoliticsStats stats = playedCard.getStats();
            if (stats != null) {
                stats.setStats(PoliticsStats.FACILITY, stats.getStats(PoliticsStats.FACILITY) + 3);
                stats.setStats(PoliticsStats.ENVIRONMENT, stats.getStats(PoliticsStats.ENVIRONMENT) + 3);
                stats.setStats(PoliticsStats.ECONOMY, stats.getStats(PoliticsStats.ECONOMY) + 3);
            }

            //หักเงิน Player -2 Coin
            int currentCoin = ZhuzheeGame.CLIENT.getLocalPlayer().getCoin();
            ZhuzheeGame.CLIENT.getLocalPlayer().setCoin(currentCoin - 2);

            //ปักธงว่าการ์ดใบนี้ประมวลผลไปแล้ว (เหมือนตอนเซ็ต rewardGranted = true)
            lastProcessedCard = playedCard;

            System.out.println("Propaganda activated: +3 Stats to " + playedCard.getName() + " and -2 coin");
        }
    }
}