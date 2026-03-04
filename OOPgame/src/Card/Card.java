package Card;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Card extends JPanel {
    protected String idNameCard;
    protected boolean enabled;
    protected boolean isGrabbed;
    protected boolean isDroppedInSlot = false;
    protected int offsetX, offsetY;
    protected boolean isHovered = false;

    public Card(String idNameCard, int x, int y, int width, int height, boolean enabled, boolean isGrabbed){
        this.idNameCard = idNameCard;
        this.enabled = enabled;
        this.isGrabbed = isGrabbed;
        setBounds(x, y, width, height);

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!Card.this.enabled || Card.this.isHovered) return;

                isHovered = true;
                setBounds(getX() - 10, getY() - 10, getWidth() + 20, getHeight() + 20);
                getParent().setComponentZOrder(Card.this, 0);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!Card.this.enabled || !isHovered) return;

                isHovered = false;
                setBounds(getX() + 10, getY() + 10, getWidth() - 20, getHeight() - 20);
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (!Card.this.enabled) return;

                Card.this.isGrabbed = true;
                Card.this.isDroppedInSlot = false;

                offsetX = e.getX();
                offsetY = e.getY();

                getParent().setComponentZOrder(Card.this, 0);
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (!Card.this.enabled) return;

                int newX = getX() + e.getX() - offsetX;
                int newY = getY() + e.getY() - offsetY;
                setLocation(newX, newY);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!Card.this.enabled) return;

                Card.this.isGrabbed = false;

                // Reset hover state first if card was hovered
                if (isHovered) {
                    isHovered = false;
                    setBounds(getX() + 10, getY() + 10, getWidth() - 20, getHeight() - 20);
                }

                int centerX = getX() + (getWidth() / 2);
                int centerY = getY() + (getHeight() / 2);

                Container parent = getParent();
                if (parent != null) {
                    for (Component comp : parent.getComponents()) {
                        if (comp instanceof CardSlot) {
                            if (comp.getBounds().contains(centerX, centerY)) {
                                setLocation(comp.getX(), comp.getY());
                                Card.this.isDroppedInSlot = true;
                                break;
                            }
                        }
                    }
                }
                repaint();
            }
        };
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!enabled) {
            g.setColor(Color.LIGHT_GRAY);
        }
        else if (isGrabbed) {
            g.setColor(new Color(93, 255, 0));
        }
        else if (isDroppedInSlot) {
            g.setColor(new Color(150, 200, 255));
        }
        else {
            g.setColor(new Color(250, 25, 25));
        }

        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.BLACK);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        FontMetrics fm = g.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(idNameCard)) / 2;
        int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(idNameCard, textX, textY);
    }
}
