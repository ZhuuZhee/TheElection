package Core.UI;

import Core.Cards.PolicyCard;
import Core.Cards.Stream.PolicyCardRegistry;
import Core.ZhuzheeGame;
import ZhuzheeEngine.Audios.AudioManager;
import ZhuzheeEngine.Scene.GameObject;
import ZhuzheeEngine.Scene.Scene2D;
import ZhuzheeEngine.Scene.Canvas;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import Core.Player.Player;

public class Shop extends Canvas {

    private final Player localPlayer;
    private List<PolicyCard> shopCards = new ArrayList<>();
    private Scene2D scene;
    private JLabel headerLabel;
    private JPanel cardContainer;

    public Shop(Scene2D scene) {
        this(scene, PolicyCardRegistry.rollCards(3));
    }

    public Shop(Scene2D scene, List<PolicyCard> allCards) {
        super(scene);
        this.scene = scene;

        if (ZhuzheeGame.CLIENT != null) {
            this.localPlayer = ZhuzheeGame.CLIENT.getLocalPlayer();
        } else {
            this.localPlayer = Dummy.Tester.dummyPlayer;
        }

        // ตั้งค่า Layout ของ Canvas ร้านค้า
        setLayout(new BorderLayout());
        setPanelSize(520, 300); // เพิ่มความสูงให้พอดีกับปุ่ม
        setAnchors(0, 0); // กึ่งกลางหน้าจอ

        // ตกแต่งพื้นหลังและขอบ (สไตล์เดียวกับ CardHolderUI)
        setBackground(new Color(50, 50, 50, 220));
        setBorder(new LineBorder(new Color(150, 150, 150), 2));
        setOpaque(true);

        // สร้าง Header ไว้ด้านบน
        headerLabel = UITool.createLabel("Shop | Your Money: $ " + localPlayer.getCoin(), 16f);
        headerLabel.setForeground(Color.LIGHT_GRAY);
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        updateHeader();
        add(headerLabel, BorderLayout.NORTH);

        // สร้าง Container แนวนอน สำหรับเรียงกล่องการ์ด
        cardContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        cardContainer.setOpaque(false);
        add(cardContainer, BorderLayout.CENTER);

        // สุ่มการ์ดและจัดวางลงในร้าน
        shopCards = rollCards(allCards);

        for (PolicyCard card : shopCards) {
            scene.remove(card);
            card.setVisible(true);
            card.setDraggable(false);

            // สร้างกล่อง Wrapper (JPanel) เพื่อจับการ์ดและปุ่มมาอยู่ด้วยกัน
            JPanel wrapper = new JPanel(new BorderLayout(0, 10));
            wrapper.setOpaque(false);

            wrapper.add(card, BorderLayout.CENTER);

            JButton buyBtn = new JButton("Buy $" + getPrice(card));
            buyBtn.setBackground(new Color(39, 174, 96));
            buyBtn.setForeground(Color.WHITE);
            buyBtn.setFocusPainted(false);
            buyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            buyBtn.addActionListener(e -> {
                handleBuy(card, wrapper); // ส่งกล่อง Wrapper ไปให้ระบบเพื่อทำลายทิ้งตอนซื้อ
            });

            // ใส่ปุ่มไว้ด้านล่างสุดของกล่อง Wrapper
            wrapper.add(buyBtn, BorderLayout.SOUTH);

            // นำกล่อง Wrapper (ที่มีทั้งการ์ดและปุ่ม) ไปวางเรียงในร้าน
            cardContainer.add(wrapper);
        }

        scene.add(this);
        scene.setComponentZOrder(this, 0);
        scene.revalidate();
        setVisible(true);
    }

    private ArrayList<PolicyCard> rollCards(List<PolicyCard> allCards) {
        List<PolicyCard> pool = new ArrayList<>(allCards);
        Collections.shuffle(pool);
        return new ArrayList<>(pool.subList(0, Math.min(3, pool.size())));
    }

    private void updateHeader() {
        headerLabel.setText("Shop | Your Money: $ " + localPlayer.getCoin());
    }

    private int getPrice(PolicyCard card) {
        return Math.abs(card.getCoin());
    }

    private void handleBuy(PolicyCard card, JPanel wrapper) {
        int price = getPrice(card);

        if (this.localPlayer.getCoin() < price) {
            System.out.println("เงินไม่พอ! ขาดอีก: " + (price - this.localPlayer.getCoin()));
            return;
        }

        this.localPlayer.setCoin(this.localPlayer.getCoin() - price);
        System.out.println("ซื้อการ์ด " + card.getName() + " สำเร็จ!");
        if(AudioManager.getInstance() != null) {
            AudioManager.getInstance().playSound("click");
        }

        if (ZhuzheeGame.PLAYER_COIN_UI != null) {
            ZhuzheeGame.PLAYER_COIN_UI.updateCoinDisplay();
        }

        // ถอดกล่อง Wrapper ทั้งกล่องออกจากหน้าร้าน (ปุ่ม Buy จะปลิวหายไปพร้อมกล่อง)
        cardContainer.remove(wrapper);
        shopCards.remove(card);

        // ดึงเอา การ์ด ออกจากกล่อง Wrapper
        wrapper.remove(card);
        card.setDraggable(false);

        // ส่งเข้ามือผู้เล่น
        if (ZhuzheeGame.POLICY_CARD_HAND != null) {
            if (!ZhuzheeGame.POLICY_CARD_HAND.addCard(card)) {
                GameObject.Destroy(card);
            }
        } else {
            System.err.println("Warning: POLICY_CARD_HAND is null!");
        }

        updateHeader(); // อัปเดตเงิน
        cardContainer.revalidate();
        cardContainer.repaint();
//        closeShop();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    public void closeShop() {
        setVisible(false);
        scene.remove((Component) this);
        scene.revalidate();
        scene.repaint();
    }

    @Override
    protected void onResize(int width, int height) {
        super.onResize(width, height);
        int centerX = (width - getWidth()) / 2;
        int centerY = (height - getHeight()) / 2;
        setLocation(centerX, centerY - 50);
    }
}