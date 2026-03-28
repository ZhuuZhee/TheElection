package Core.Cards.AllArcanaCards;

import Core.Cards.ArcanaCard;
import Core.UI.UINotificationToast;

public class Death extends ArcanaCard {
    public Death(int x, int y) {
        super("Death", x, y, 3, "OOPgame/Assets/ImageForCards/Arcana Card/Death.png");
        this.description = "Skill: Change all stats of development cards in other players' hands to negative.";
    }

    @Override
    protected void activateSkill() {
        if (Core.ZhuzheeGame.CLIENT != null) {
            UINotificationToast.showNotification("Death activate!");
            Core.ZhuzheeGame.CLIENT.sendNegativeHandStatsSkill();
        }
    }
}
