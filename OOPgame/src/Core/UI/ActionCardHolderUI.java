package Core.UI;

import Core.Cards.Card;
import ZhuzheeEngine.Scene.*;
import ZhuzheeEngine.Scene.Canvas;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ActionCardHolderUI extends Canvas {
    private ArrayList<Card> cards = new ArrayList<>();
    private int panelHeight = 220;
    private JPanel cardContainer;
    private Scene2D scene;

    public ActionCardHolderUI(Scene2D scene){
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

        // เพิ่ม Listener ที่ Scene เพื่อดักจับจังหวะ "ปล่อยเมาส์" (Drop)
        // เปลี่ยนมาใช้ Listener ที่ UI (this) เพื่อดักจับการเข้า/ออกพื้นที่ของเมาส์
        this.addMouseListener(new MouseAdapter() {
            private Card enteredCard;

            @Override
            public void mouseEntered(MouseEvent e) {
                // enter -> ใส่cardลงใน enteredCard ถ้ามีการลากอยู่
                System.out.println("Enter Card Holder");
                if (Card.CURRENT_GRABBED_CARD != null) {
                    enteredCard = Card.CURRENT_GRABBED_CARD;
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // exit -> เอาcardออก
                System.out.println("Exit Card Holder");
                enteredCard = null;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // เช็ค enteredCard แทนการเช็ค Bounds
                System.out.println("Drop at Card Holder");
                if (enteredCard != null) {
                    addCard(enteredCard);
                    Card.CURRENT_GRABBED_CARD = null;
                    enteredCard = null;
                }
            }
        });


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

        // 1. ลบการ์ดออกจาก GameObject List ของ Scene
        // เพื่อหยุด Scene ไม่ให้คำนวณตำแหน่ง World Position ทับตำแหน่งใน UI
        scene.remove(card);

        // 2. เพิ่มการ์ดเข้าสู่ Container ของ UI (Swing จะจัดการย้าย Parent ให้)
        cardContainer.add(card);
        cards.add(card);

        // 3. ปรับสถานะการ์ดให้เหมาะสมกับการแสดงผลใน UI
        card.setVisible(true);
        card.setEnable(true);
        // หากต้องการล็อกไม่ให้ลากต่อเมื่ออยู่ในมือ สามารถสั่ง card.setDraggable(false); ได้ที่นี่

        // 4. สั่งวาดใหม่
        System.out.println("Card added to hand: " + card.getName());
        cardContainer.revalidate();
        cardContainer.repaint();
    }
}
