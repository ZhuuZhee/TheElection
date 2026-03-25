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
        super(name, x, y, 100, 150, imagePath, true);
        this.coin = coin;
    }

    public abstract boolean IsActivate();

    public boolean isInSlot() {
        return isInSlot;
    }

    public abstract void onActionCardPlayed(ActionCard playedCard, City city);

    @Override
    protected void onDroppedInSlot(CardSlot slot) {
        System.out.println(name + " was dropped into a PASSIVE slot!");
        this.isInSlot = true;
        this.isDraggable = false;

        Player playercoin = null;
        if (ZhuzheeGame.CLIENT != null) {
            playercoin = ZhuzheeGame.CLIENT.getLocalPlayer();
        } else {
            // ใช้กระเป๋า Dummy ตัวเดียวกับ Shop และ ActionCard
            playercoin = Dummy.Tester.dummyPlayer;
        }

        // ทำการหักเงินตามมูลค่าของการ์ดใบนั้นๆ (this.coin)
        if (playercoin != null) {
            playercoin.setCoin(playercoin.getCoin() + this.coin);
            System.out.println("หักเงินค่า Policy: " + this.coin + " | กระเป๋าเงินเหลือ: " + playercoin.getCoin());
        }
    }
}
