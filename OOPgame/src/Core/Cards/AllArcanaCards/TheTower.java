package Core.Cards.AllArcanaCards;

import Core.Cards.ArcanaCard;
import Core.UI.UINotificationToast;

public class TheTower extends ArcanaCard {

    public TheTower(int x, int y) {
        super("The Tower", x, y, 4, "OOPgame/Assets/ImageForCards/Arcana Card/Tower.png");
        this.description = "Skill: Destroy all cards in other players' hands. They draw 2 cards next turn.";
    }

    @Override
    protected void activateSkill() {
        if (Core.ZhuzheeGame.CLIENT != null) {
            UINotificationToast.showNotification("The Tower activate!", 5000, true);
            Core.ZhuzheeGame.CLIENT.sendDestroyHandSkill();
        }
    }
}
