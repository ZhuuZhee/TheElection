/**
 * @Xynezter 23/3/2026 16:54
 */
package Core.UI;

import Core.Cards.Card;
import ZhuzheeEngine.Scene.*;
import ZhuzheeEngine.Scene.Canvas;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;

public class CardHolderUI extends Canvas {
    protected final ArrayList<Card> cards = new ArrayList<>();
    private static final int DEFAULT_HEIGHT = 220;
    private static final int DEFAULT_WIDTH = 400;
    private final JPanel cardContainer;
    private final Scene2D scene;
    private final JLabel titleLabel;
    private int maxCard = 5;
    //100 = fit
    public CardHolderUI(Scene2D scene) {
        this(scene, 50, 50);
    }

    public CardHolderUI(Scene2D scene, int x, int y) {
        this(scene, DEFAULT_WIDTH, DEFAULT_HEIGHT, x, y);
    }

    public CardHolderUI(Scene2D scene, int width, int height, int x, int y) {
        super(scene);
        this.scene = scene;
        setLayout(new BorderLayout());

        setPanelSize(width, height);
        setScreenPos(x, y);
        setMargins(0, 20, 20, 20);
        setAnchors(-1, 1); // Default: Left-Top

        // กำหนดดีไซน์พื้นหลังและขอบ
        setBackground(new Color(50, 50, 50, 220)); // สีเทาเข้มโปร่งแสง
        setBorder(new LineBorder(new Color(150, 150, 150), 2));
        setOpaque(true); // สำคัญมาก: ป้องกัน Swing วาดพื้นหลังทึบทับกันซ้ำซ้อนจนกระพริบ

        // ส่วนหัวข้อ
        titleLabel = UITool.createLabel("Card Holder UI", 16f);
        titleLabel.setForeground(Color.LIGHT_GRAY);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        titleLabel.setForeground(Color.LIGHT_GRAY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Container สำหรับใส่การ์ด
        cardContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        cardContainer.setOpaque(false); // โปร่งใสเพื่อให้เห็นพื้นหลังของ UI หลัก
        add(cardContainer, BorderLayout.CENTER);

        onResize(scene.getWidth(), scene.getHeight());
        if (strechToFit) {
            this.marginLeft = (int) (width * 0.1f);
            this.marginRight = (int) (width * 0.125f);
        }
        scene.revalidate();
        setVisible(true);
    }

    @Override
    protected void onResize(int width, int height) {
        super.onResize(width, height);
    }

//    /**
//     * กำหนดจุดยึดของ UI เมื่อใช้โหมด Stretch
//     *
//     * @param anchorTop true ยึดขอบบน (ใช้ marginTop), false ยึดขอบล่าง (ใช้ marginBottom)
//     */

    public void setSetLabel(String label){
        titleLabel.setText(label);
    }

    public int getMaxCard() {
        return maxCard;
    }

    public void setMaxCard(int maxCard) {
        this.maxCard = maxCard;
    }

    public boolean isFull(){
        return cards.size() >= maxCard;
    }

    public boolean isEmpty(){
        return cards.isEmpty();
    }

    public boolean containsCard(Card card) {
        return cards.contains(card);
    }

    @Override
    protected void paintComponent(Graphics g) {
        // วาดสีพื้นหลังแบบโปร่งแสงเอง (เพื่อไม่ให้บั๊กกระพริบตอนแอนิเมชันรันรัวๆ)
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    public void updateCards() {
        for (Card card : cards) {
            card.update();
        }
    }

    public boolean addCard(Card card) {
        if (cards.contains(card) || cards.size() >= maxCard) {
            System.err.println("Can not add card, this card holder is full!");
            GameObject.Destroy(card);
            return false;
        }
        scene.remove(card);

        cardContainer.add(card);
        cards.add(card);

        int height = panelHeight - 60;
        float ratio = (float) height / card.getHeight();
        card.setBounds(0, 0, (int) (card.getWidth() * ratio), height);

        ZhuzheeEngine.Debug.GameLogger.logInfo("Card added to hand: " + card.getName());
        cardContainer.revalidate();
        cardContainer.repaint();
        return true;
    }

    public void removeCard(Card card) {
        if (!cards.contains(card)) return;

        Point location = card.getLocationOnScreen();
        cardContainer.remove(card);
        cards.remove(card);

        scene.add(card);

        card.setPosition(scene.Screen2WorldPoint(location));

        System.out.println("Card removed to hand: " + card.getName());
        cardContainer.revalidate();
        cardContainer.repaint();
    }
    public ArrayList<Card> removeAllCards(){
        ArrayList<Card> tempCard = new ArrayList<Card>(cards);
        for(Card card : new ArrayList<Card>(cards)){
            removeCard(card);
        }
        return tempCard;
    }
    public ArrayList<Card> getCards() {
        return cards;
    }
}
