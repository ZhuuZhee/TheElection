package Core.Cards.AllArcanaCards;

import Core.Cards.ArcanaCard;
import Core.UI.UINotificationToast;

public class Judgement extends ArcanaCard {

    public Judgement(int x, int y) {
        super("Judgement", x, y, 2, "OOPgame/Assets/ImageForCards/Arcana Card/Judgement.png");
        this.description = "Skill: Discard half of all other players' hand. They cannot draw next turn, and their Policy cards are silenced until the end of their turn.";
    }

    @Override
    protected void activateSkill() {
        UINotificationToast.showNotification("Judgement activate!", 5000, true);
        Core.ZhuzheeGame.CLIENT.sendJudgementSkill();
    }
}
