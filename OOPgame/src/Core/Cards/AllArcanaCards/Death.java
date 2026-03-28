package Core.Cards.AllArcanaCards;

import Core.Cards.ArcanaCard;
import Core.Network.PacketBuilder;
import Core.ZhuzheeGame;

public class Death extends ArcanaCard {
    public Death(int x, int y) {
        super("Death", x, y, 1, "OOPgame/Assets/ImageForCards/Arcana Card/Death.png");
    }

    @Override
    protected void activateSkill() {
        System.out.println("Death activate!");
        // Business Logic
        ZhuzheeGame.CLIENT.getConnectedPlayers();//คนอื่นติดลบ
    }
}
