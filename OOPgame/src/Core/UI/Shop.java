package Core.UI;

import Core.Cards.PolicyCard;
import Core.ZhuzheeGame;
import ZhuzheeEngine.Scene.Scene2D;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Shop extends JPanel {

    // ขนาดของ Shop และ Card
    private static final int SHOP_WIDTH  = 620;
    private static final int SHOP_HEIGHT = 400;
    private static final int CARD_WIDTH  = 150;
    private static final int CARD_HEIGHT = 190;

    // สีต่างๆ
    private static final Color BG_PANEL    = new Color(100, 100, 100);
    private static final Color HEADER_BG   = new Color(180, 180, 180);
    private static final Color CARD_BG     = Color.WHITE;
    private static final Color CARD_BORDER = new Color(150, 150, 150);
    private static final Color BUY_OK      = new Color(70, 150, 70);
    private static final Color BUY_FAIL    = new Color(160, 80, 80);
    private static final Color MONEY_BG    = new Color(195, 195, 195);

    // ตัวแปร state
    private List<PolicyCard> shopCards = new ArrayList<>();
    private int playerMoney;
    private boolean purchased = false;

    public Shop(Scene2D scene, List<PolicyCard> allCards, int playerMoney) {
        this.playerMoney  = playerMoney;

//        สุ่มการ์ดมา 3 ใบ
        shopCards = rollCards(allCards);

//        ซ่อนการ์ดทุกใบออกจาก scene ก่อน จะวาดเองใน buildCardArea
        for (PolicyCard card : shopCards) {
            card.setEnable(false);
            card.setVisible(false);
            card.setDraggable(false);
        }

//        สร้าง Core.UI
        setLayout(new BorderLayout());
        setBackground(BG_PANEL);
        setBorder(new LineBorder(CARD_BORDER, 2));
        setPreferredSize(new Dimension(SHOP_WIDTH, SHOP_HEIGHT));
        setSize(SHOP_WIDTH, SHOP_HEIGHT);

//        เพิ่มส่วนต่างๆ ของ Shop
        add(buildHeader(),   BorderLayout.NORTH);
        add(buildCardArea(), BorderLayout.CENTER);
        add(buildMoneyBar(), BorderLayout.SOUTH);

//        add เข้า scene และวางตรงกลาง
        scene.setLayout(null);
        scene.add(this);

        SwingUtilities.invokeLater(() -> {
            setLocation(
                    (scene.getWidth()  - SHOP_WIDTH)  / 2,
                    (scene.getHeight() - SHOP_HEIGHT) / 2
            );
            scene.revalidate();
            scene.repaint();
        });

        setVisible(true);
    }

    // สุ่มการ์ดจาก pool
    private ArrayList<PolicyCard> rollCards(List<PolicyCard> allCards) {
        List<PolicyCard> pool = new ArrayList<>(allCards);
        Collections.shuffle(pool);
        return new ArrayList<>(pool.subList(0, Math.min(3, pool.size())));
    }

    // ส่วนหัว "Shop"
    private JPanel buildHeader() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
        header.setBackground(HEADER_BG);
        header.setPreferredSize(new Dimension(SHOP_WIDTH, 42));

        JLabel title = new JLabel("Shop");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.DARK_GRAY);
        header.add(title);

        return header;
    }

    // ส่วนแสดงการ์ด 3 ใบ
    private JPanel buildCardArea() {
        JPanel area = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 16));
        area.setOpaque(false); // โปร่งใส เพื่อให้ paintComponent วาดการ์ดทะลุได้

        for (PolicyCard card : shopCards) {
            area.add(buildCardPanel(card));
        }

        return area;
    }

    // วาด card ทับบน Shop โดยตรงผ่าน paintComponent
    // paintComponent ของ JPanel วาดหลัง Scene2D ทำให้การ์ดอยู่บนสุด
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (shopCards == null || shopCards.isEmpty()) return;

        Graphics2D g2d = (Graphics2D) g.create();
        try {
//            header สูง 42, FlowLayout vgap=16
            int cardY = 42 + 16;

//            คำนวณ x เริ่มต้น (FlowLayout CENTER, hgap=24)
            int totalWidth = shopCards.size() * (CARD_WIDTH + 24) - 24;
            int startX = (SHOP_WIDTH - totalWidth) / 2;

            System.out.println("Panel size: " + getWidth() + ", " + getHeight());

            for (int i = 0; i < shopCards.size(); i++) {
                int cardX = startX + i * (CARD_WIDTH + 24);
                System.out.println("Card " + i + " drawing at: " + cardX + ", " + cardY);

//                สร้าง graphics ที่ translate ไปที่ตำแหน่ง card แล้ววาดที่ 0,0
                Graphics2D cardG = (Graphics2D) g2d.create();
                cardG.translate(cardX - shopCards.get(i).getPosition().x,
                        cardY - shopCards.get(i).getPosition().y);
                shopCards.get(i).render(cardG);
                cardG.dispose();
            }
        } finally {
            g2d.dispose();
        }
    }

    // สร้าง panel ของการ์ดแต่ละใบ
    private JPanel buildCardPanel(PolicyCard card) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false); // โปร่งใส
        wrapper.setPreferredSize(new Dimension(CARD_WIDTH + 10, CARD_HEIGHT + 65));

//        กล่อง placeholder โปร่งใส card จะถูกวาดทับโดย paintComponent
        JPanel cardBox = new JPanel();
        cardBox.setOpaque(false); // โปร่งใส
        cardBox.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        cardBox.setMaximumSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        cardBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        wrapper.add(cardBox);
        wrapper.add(Box.createVerticalStrut(6));

//        ราคาการ์ด
        JLabel priceLabel = new JLabel("฿" + getPrice(card), SwingConstants.CENTER);
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        priceLabel.setForeground(Color.WHITE);
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        wrapper.add(priceLabel);
        wrapper.add(Box.createVerticalStrut(4));

//        ปุ่มซื้อ เขียวถ้าเงินพอ แดงถ้าเงินไม่พอ
        boolean canAfford = playerMoney >= getPrice(card);
        JButton buyBtn = new JButton("Buy");
        buyBtn.setPreferredSize(new Dimension(CARD_WIDTH, 30));
        buyBtn.setMaximumSize(new Dimension(CARD_WIDTH, 28));
        buyBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        buyBtn.setBackground(canAfford ? BUY_OK : BUY_FAIL);
        buyBtn.setForeground(Color.WHITE);
        buyBtn.setFocusPainted(false);
        buyBtn.setBorderPainted(false);
        buyBtn.setFont(new Font("Arial", Font.BOLD, 12));
        buyBtn.setEnabled(canAfford && !purchased);
        buyBtn.addActionListener(e -> handleBuy(card));
        wrapper.add(buyBtn);

        return wrapper;
    }

    // แถบเงินด้านล่างขวา
    private JPanel buildMoneyBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bar.setBackground(new Color(80, 80, 80));

        JLabel moneyLabel = new JLabel("total money : " + playerMoney);
        moneyLabel.setFont(new Font("Arial", Font.BOLD, 13));
        moneyLabel.setForeground(Color.DARK_GRAY);
        moneyLabel.setOpaque(true);
        moneyLabel.setBackground(MONEY_BG);
        moneyLabel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        bar.add(moneyLabel);

        return bar;
    }

    // ราคาการ์ด TODO: แก้ให้ตรงกับ field จริง เช่น return card.price;
    private int getPrice(PolicyCard card) {
        return 100;
    }

    // จัดการการซื้อการ์ด → snap card เข้า slot แล้วปิด shop
    private void handleBuy(PolicyCard card) {
        if (purchased) return;
        if (playerMoney < getPrice(card)) return;

        purchased    = true;
        playerMoney -= getPrice(card);

//        ซ่อนการ์ดที่ไม่ได้ซื้อออกจาก scene
        for (PolicyCard c : shopCards) {
            if (c != card) {
                PolicyCard other = (PolicyCard) c;
                ZhuzheeGame.MAIN_SCENE.remove(other);
            }
        }

//      show bought card
        card.setPosition(new Point(0,200));
        card.setVisible(true);
        card.setEnable(true);

        closeShop();
    }

    // ปิดร้านและลบออกจาก scene
    private void closeShop() {
        setVisible(false);
        ZhuzheeGame.MAIN_SCENE.remove((Component) this);
        ZhuzheeGame.MAIN_SCENE.remove( this);
        ZhuzheeGame.MAIN_SCENE.revalidate();
        ZhuzheeGame.MAIN_SCENE.repaint();
    }

}