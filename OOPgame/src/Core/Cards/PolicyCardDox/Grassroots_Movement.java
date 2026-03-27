package Core.Cards.PolicyCardDox;


import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;

public class Grassroots_Movement extends PolicyCard {
    public Grassroots_Movement(String name, int x, int y, int coin) {
        super(name, x, y, coin);
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void onActionCardPlayed(ActionCard playedCard, City city) {

    }
}