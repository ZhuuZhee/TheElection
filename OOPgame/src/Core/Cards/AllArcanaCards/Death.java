package Core.Cards.AllArcanaCards;

import Core.Cards.ArcanaCard;
import Core.UI.UINotificationToast;

public class Death extends ArcanaCard {
    public Death(int x, int y) {
        super("Death", x, y, 1, "OOPgame/Assets/ImageForCards/Arcana Card/Death.png");
    }

    @Override
    protected void activateSkill() {
        UINotificationToast.showNotification("Death activate!");
        // Business Logic
    }
}
