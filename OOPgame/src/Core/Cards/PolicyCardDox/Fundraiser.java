package Core.Cards.PolicyCardDox;

import Core.Cards.ActionCard;
import Core.Cards.PolicyCard;
import Core.Maps.City;
import Core.Player.Player;
import Core.UI.UINotificationToast;
import Core.ZhuzheeGame;

public class Fundraiser extends PolicyCard {
    public Fundraiser(int x, int y, String imagePath) {
        super("Fundraiser", x, y, imagePath, -4);
        this.description = "Skill: Gain 2 Coins every time you play a Develop Card.";
    }

    @Override
    public boolean isActive() {
        if (ZhuzheeGame.POLICY_CARD_HAND != null) {
            return ZhuzheeGame.POLICY_CARD_HAND.containsCard(this);
        }
        return false;
    }

    @Override
    public void onActionCardPlayed(ActionCard playedCard, City city) {
        if (!isActive()) return;
        if (ZhuzheeGame.CLIENT == null) return;
        Player localPlayer = ZhuzheeGame.CLIENT.getLocalPlayer();
        if (localPlayer == null) return;
        UINotificationToast.showNotification("💰 [FUNDRAISER] ระดมทุนจาก " + playedCard.getName() + " ได้รับ 2 Coin!");
        int currentCoin = localPlayer.getCoin();
        localPlayer.setCoin(currentCoin + 20);
    }
}