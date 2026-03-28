package Core.Cards.AllArcanaCards;

import Core.Cards.ArcanaCard;
import Core.UI.UINotificationToast;

public class Judgement extends ArcanaCard {

    public Judgement(int x, int y) {
        super("Judgement", x, y, 5, "OOPgame/Assets/ImageForCards/Arcana Card/Judgement.png");
    }

    @Override
    protected void activateSkill() {
        UINotificationToast.showNotification("Judgement activate!");
        // Business Logic
    }
}
