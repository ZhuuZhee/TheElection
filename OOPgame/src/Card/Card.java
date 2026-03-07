package Card;

import ZhuzheeEngine.Scene2D.GameObject;

import java.awt.*;

public abstract class Card extends GameObject {
    protected String name;
    protected boolean enabled;
    protected boolean isGrabbed = false;
    protected Point offset = new Point(0, 0);

    public Card(String name, int x, int y, int width, int height, boolean enabled) {
        super(x, y, width, height);
        this.name = name;
        this.enabled = enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void grab(int mouseX, int mouseY) {
        if (!enabled) return;

        isGrabbed = true;
        offset.x = mouseX - position.x;
        offset.y = mouseY - position.y;
    }

    public void drag(int mouseX, int mouseY) {
        if (!enabled || !isGrabbed) return;

        position.x = mouseX - offset.x;
        position.y = mouseY - offset.y;
    }

    public void drop() {
        if (!enabled || !isGrabbed) return;

        isGrabbed = false;
    }

    protected abstract boolean isDroppable(Object bottom);

    @Override
    public void draw(Graphics g) {
        if (!enabled) g.setColor(Color.LIGHT_GRAY);
        else if (isGrabbed) g.setColor(new Color(255, 165, 0));
        else g.setColor(new Color(176, 255, 183));

        g.fillRect(position.x, position.y, size.x, size.y);

        g.setColor(Color.BLACK);
        g.drawRect(position.x, position.y, size.x, size.y);

        FontMetrics fm = g.getFontMetrics();
        int textX = position.x + (size.x - fm.stringWidth(name)) / 2;
        int textY = position.y + (size.y - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(name, textX, textY);
    }
}
