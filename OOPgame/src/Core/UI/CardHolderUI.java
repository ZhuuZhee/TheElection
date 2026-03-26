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
    private final ArrayList<Card> cards = new ArrayList<>();
    private final int panelHeight = 220;
    private final JPanel cardContainer;
    private final Scene2D scene;

    public CardHolderUI(Scene2D scene){
        super(scene);
        this.scene = scene;
        setLayout(new BorderLayout());

        // กำหนดดีไซน์พื้นหลังและขอบ
        setBackground(new Color(50, 50, 50, 220)); // สีเทาเข้มโปร่งแสง
        setBorder(new LineBorder(new Color(150, 150, 150), 2));
        setOpaque(true); // สำคัญมาก: ป้องกัน Swing วาดพื้นหลังทึบทับกันซ้ำซ้อนจนกระพริบ

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

        scene.revalidate();
        setVisible(true);
    }

    @Override
    protected void onResize(int width, int height) {
        // จำกัดความกว้างไม่ให้ยาวเกินไป (เช่น 70% ของจอ) และจัดให้อยู่ตรงกลาง
        int uiWidth = Math.min(width - 100, (int)(width * 0.7)); 
        int x = (width - uiWidth) / 2;
        
        // ยึดตำแหน่งไว้ที่ด้านล่างของหน้าจอเสมอ
        setBounds(x, height - panelHeight - 10, uiWidth, panelHeight); // ขยับขึ้นจากขอบล่าง 10px เพื่อความสวยงาม
        revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // วาดสีพื้นหลังแบบโปร่งแสงเอง (เพื่อไม่ให้บั๊กกระพริบตอนแอนิเมชันรันรัวๆ)
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
        for(Card card : cards){
            card.update();
        }
    }

    public void addCard(Card card){
        if(cards.contains(card)) return;
        scene.remove(card);

        cardContainer.add(card);
        cards.add(card);

        int height = panelHeight - 60;
        float ratio = (float) height /card.getHeight();
        card.setBounds(0,0,(int)(card.getWidth() * ratio), height);

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

    public ArrayList<Card> getCards(){
        return cards;
    }
}
