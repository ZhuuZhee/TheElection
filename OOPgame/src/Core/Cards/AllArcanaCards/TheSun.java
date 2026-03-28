package Core.Cards.AllArcanaCards;

import Core.Cards.ActionCard;
import Core.Cards.ArcanaCard;
import Core.Cards.Card;
import Core.Maps.PoliticsStats;
import Core.ZhuzheeGame;

import java.util.Map;

public class TheSun extends ArcanaCard {

    public TheSun(int x, int y) {
        super("The Sun", x, y, 3, "OOPgame/Assets/ImageForCards/Arcana Card/Sun.png");
        this.description = "Skill: Multiply all your development cards by 10 if they have negative stats change to positive.";
    }

    @Override
    protected void activateSkill() {
        if (ZhuzheeGame.DEVLOPMENT_CARD_HAND != null) {
            for (Card card : ZhuzheeGame.DEVLOPMENT_CARD_HAND.getCards()) {
                if (card instanceof ActionCard playerCard) {
                    PoliticsStats stats = playerCard.getStats();
                    
                    if (stats != null && stats.stats != null) {
                        for (Map.Entry<Long, Integer> entry : stats.stats.entrySet()) {
                            long statType = entry.getKey();
                            int value = entry.getValue();

                            if (value < 0) {
                                value = Math.abs(value);
                            }

                            value *= 10;
                            stats.setStats(statType, value);
                        }
                    }
                }
            }
        }
    }
}
