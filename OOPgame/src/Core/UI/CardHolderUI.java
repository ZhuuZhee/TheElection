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
    private ArrayList<Card> cards = new ArrayList<>();
    private int panelHeight = 220;
    private JPanel cardContainer;
    private Scene2D scene;

    public CardHolderUI(Scene2D scene){
        super(scene);
        this.scene = scene;
        setLayout(new BorderLayout());

        // กำหนดดีไซน์พื้นหลังและขอบ
        setBackground(new Color(50, 50, 50, 220)); // สีเทาเข้มโปร่งแสง
        setBorder(new LineBorder(new Color(150, 150, 150), 2));

        // ส่วนหัวข้อ
        JLabel titleLabel = new JLabel("Your Hand");
        titleLabel.setForeground(Color.LIGHT_GRAY);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Container สำหรับใส่การ์ด
        cardContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        cardContainer.setOpaque(false); // โปร่งใสเพื่อให้เห็นพื้นหลังของ UI หลัก
        add(cardContainer, BorderLayout.CENTER);

        onResize(scene.getWidth(),scene.getHeight());

        cardContainer.setOpaque(false);
        add(cardContainer, BorderLayout.CENTER);

        onResize(scene.getWidth(),scene.getHeight());

        scene.revalidate();
        setVisible(true);
    }

    @Override
    protected void onResize(int width, int height) {
        // ยึดตำแหน่งไว้ที่ด้านล่างของหน้าจอเสมอ
        setBounds(0, height - panelHeight, width, panelHeight);
        revalidate();
    }

    public void addCard(Card card){
        if(cards.contains(card)) return;
        scene.remove(card);

        cardContainer.add(card);
        cards.add(card);

        System.out.println("Card added to hand: " + card.getName());
        cardContainer.revalidate();
        cardContainer.repaint();
    }
    public void removeCard(Card card){
        if(!cards.contains(card)) return;

        Point location = card.getLocationOnScreen();
        cardContainer.remove(card);
        cards.remove(card);

        scene.add(card);

        card.setPosition(scene.Screen2WorldPoint(location));

        System.out.println("Card removed to hand: " + card.getName());
        cardContainer.revalidate();
        cardContainer.repaint();
    }
}
