/**
 * @Xynezter 14/3/2026 15:03
 */
package Core.Cards;

import Core.ZhuzheeGame;
import java.awt.*;

public abstract class ArcanaCard extends Card {
    protected final int maxCooldown;
    protected int currentCooldown;
    private int lastUsedTurn = -9999;

    public ArcanaCard(String name, int x, int y, int maxCooldown, String imagePath) {
        super(name, x, y, 100, 150, imagePath);
        this.maxCooldown = maxCooldown;
        this.currentCooldown = 0;
    }

    protected abstract void activateSkill();

    public int getRemainingCooldown() {
        if (lastUsedTurn == -9999) return 0;

        int currentTurn = 0;
        int playerCount = 4;

        if (ZhuzheeGame.CLIENT != null) {
            currentTurn = ZhuzheeGame.CLIENT.getTurnCounter();
            playerCount = Math.max(1, ZhuzheeGame.CLIENT.getConnectedPlayers().size());
        }

        int turnsPerRound = playerCount * 4;
        int turnsPassed = currentTurn - lastUsedTurn;
        int roundsPassed = turnsPassed / turnsPerRound;
        int remainingCooldown = this.maxCooldown - roundsPassed;

        return Math.max(0, remainingCooldown);
    }

    @Override
    public void onMousePressed(int mouseX, int mouseY) {
        int remainingCooldown = getRemainingCooldown();

        if (remainingCooldown <= 0) {
            System.out.println("Used skill : " + this.name);
            activateSkill();

            if (ZhuzheeGame.CLIENT != null) {
                lastUsedTurn = ZhuzheeGame.CLIENT.getTurnCounter();
            } else {
                lastUsedTurn = 0;
            }
            this.currentCooldown = this.maxCooldown;

        } else {
            System.out.println(this.name + " Can't use! wait for " + remainingCooldown + " round(s)");
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int remainingCooldown = getRemainingCooldown();

        // ถ้ามี Cooldown ค่อยวาดทับ
        if (remainingCooldown > 0) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            try {
                // วาดฟิล์มดำคลุม
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // วาดข้อความให้กึ่งกลางเสมอ
                g2d.setColor(Color.WHITE);

                String text = remainingCooldown + " ROUNDS";
                FontMetrics fm = g2d.getFontMetrics();

                // คำนวณจาก getWidth() / getHeight() ตอนนั้นๆ เลย จะขยายแค่ไหนก็อยู่ตรงกลาง
                int textWidth = fm.stringWidth(text);
                int textX = (getWidth() - textWidth) / 2;
                int textY = (getHeight() / 2) + (fm.getAscent() / 4);

                g2d.drawString(text, textX, textY);
            } finally {
                g2d.dispose();
            }
        }
    }
}