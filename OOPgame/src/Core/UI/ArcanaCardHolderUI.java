package Core.UI;

import Core.Cards.Card;
import ZhuzheeEngine.Scene.*;
import ZhuzheeEngine.Scene.Canvas;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ArcanaCardHolderUI extends Canvas {
    private Card currentCard = null;
    // ขนาดของช่องใส่ Arcana Card (เล็กลงหน่อยเพราะใส่ใบเดียว)
    private final int panelWidth = 150;
    private final int panelHeight = 220;
    private final JPanel cardContainer;
    private final Scene2D scene;

    public ArcanaCardHolderUI(Scene2D scene){
        super(scene);
        this.scene = scene;
        setLayout(new BorderLayout());

        // กำหนดดีไซน์พื้นหลังและขอบ (เลียนแบบ PolicyCardHolderUI)
        setBackground(new Color(50, 50, 50, 220)); // สีเทาเข้มโปร่งแสง
        setBorder(new LineBorder(new Color(150, 150, 150), 2));
        setOpaque(true);

        // ส่วนหัวข้อ
        JLabel titleLabel = new JLabel("Arcana Card");
        titleLabel.setForeground(Color.LIGHT_GRAY);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Container สำหรับใส่การ์ด
        cardContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        cardContainer.setOpaque(false);
        add(cardContainer, BorderLayout.CENTER);

        onResize(scene.getWidth(), scene.getHeight());

        scene.revalidate();
        setVisible(true);
    }

    @Override
    protected void onResize(int width, int height) {
        // ย้ายไปไว้ที่มุมซ้ายล่าง ถัดจากขอบหน้าจอเล็กน้อย
        // ปรับความห่างได้จากการเปลี่ยนค่า margin
        int margin = 10;
        setBounds(margin, height - panelHeight - margin, panelWidth, panelHeight);
        revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // วาดสีพื้นหลังแบบโปร่งแสงเอง
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    public void setCard(Card card){
        // ถ้ามีการ์ดใบเดิมอยู่ ให้เอาออกก่อน
        if (currentCard != null) {
            removeCard();
        }

        if (card == null) return;

        scene.remove(card);
        cardContainer.add(card);
        currentCard = card;

        // ปรับขนาดการ์ดให้พอดีกับ Container
        int height = panelHeight - 60;
        float ratio = (float) height / card.getHeight();
        card.setBounds(0, 0, (int)(card.getWidth() * ratio), height);

        System.out.println("Arcana Card set: " + card.getName());
        cardContainer.revalidate();
        cardContainer.repaint();
    }

    public void removeCard(){
        if (currentCard == null) return;

        Point location = currentCard.getLocationOnScreen();
        cardContainer.remove(currentCard);
        
        scene.add(currentCard);
        currentCard.setPosition(scene.Screen2WorldPoint(location));

        System.out.println("Arcana Card removed: " + currentCard.getName());
        currentCard = null;
        
        cardContainer.revalidate();
        cardContainer.repaint();
    }

    public Card getCard() {
        return currentCard;
    }
}
