/**
 * @Xynezter 10/3/2026 19:30
 */
package Core.Cards;

import Core.Player.Player;
import Core.ZhuzheeGame;
import Core.Maps.City;
import Core.Maps.PoliticsStats;

import java.awt.*;

public abstract class PolicyCard extends Card {
    protected boolean isInSlot = false;

    public PolicyCard(String name, int x, int y, int coin) {
        super(name, x, y, 100, 150);
        this.coin = coin;
    }

    public PolicyCard(String name, int x, int y, String imagePath, int coin) {
        super(name, x, y, 100, 150, imagePath);
        this.coin = coin;
    }

    public abstract boolean isActive();

    public boolean isInSlot() {
        return isInSlot;
    }

    public abstract void onActionCardPlayed(ActionCard playedCard, City city);
}
