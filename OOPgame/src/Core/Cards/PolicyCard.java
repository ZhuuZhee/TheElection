/**
 * @Xynezter 10/3/2026 19:30
 */
package Core.Cards;

import Core.Maps.City;
import Core.Maps.PoliticsStats;

import java.awt.*;

/**
 * Abstract base class for Policy Cards.
 * Policy cards are persistent cards placed in slots that provide passive effects 
 * or react when Action Cards are played.
 */
public abstract class PolicyCard extends Card {
     /** Tracks if the card is currently active in a slot. */
    private boolean isShowHighlight = false;
    public PolicyCard(String name, int x, int y, int coin) {
        super(name, x, y, 100, 150);
        this.coin = coin;
    }

    public PolicyCard(String name, int x, int y, String imagePath, int coin) {
        super(name, x, y, 100, 150, imagePath);
        this.coin = coin;
    }

    /**
     * Determines if the policy's effects are currently active.
     * @return true if active.
     */
    public abstract boolean isActive();

    /**
     * Triggered when an Action Card is played in a city where this policy is active.
     * @param playedCard The action card being played.
     * @param city The city where the action is taking place.
     */
    public abstract void onActionCardPlayed(ActionCard playedCard, City city);
    public PoliticsStats calculateStats(ActionCard playedCard, City city){
        return null;
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (isShowHighlight) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            try {
                // วาดกรอบสีขาวหนา 4px เพื่อเน้นว่าการ์ดกำลังทำงาน (Active)
                g2d.setColor(Color.WHITE);
                float thickness = 4f;
                g2d.setStroke(new BasicStroke(thickness));
                // วาด Rect โดยขยับพิกัดเข้ามาครึ่งหนึ่งของความหนาเส้นเพื่อให้กรอบอยู่ภายในขอบการ์ดพอดี
                g2d.drawRect((int)(thickness/2), (int)(thickness/2), getWidth() - (int)thickness, getHeight() - (int)thickness);
            } finally {
                g2d.dispose();
            }
        }
    }

    public void setShowHighlight(boolean showHighlight) {
        isShowHighlight = showHighlight;
    }
}
