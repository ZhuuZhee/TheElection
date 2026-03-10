package UI;

import Core.Cards.PolicyCard;
import ZhuzheeEngine.Scene.Canvas;
import ZhuzheeEngine.Scene.Scene2D;
import ZhuzheeEngine.Scene.SceneObject;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Shop extends Canvas {

    // ==================== Config ====================
    private static final int SHOP_WIDTH    = 620;
    private static final int SHOP_HEIGHT   = 400;
    private static final int CARD_WIDTH    = 150;
    private static final int CARD_HEIGHT   = 190;

    // ==================== Colors ====================
    private static final Color BG_PANEL    = new Color(100, 100, 100);
    private static final Color HEADER_BG   = new Color(180, 180, 180);
    private static final Color CARD_BG     = Color.WHITE;
    private static final Color CARD_BORDER = new Color(150, 150, 150);
    private static final Color BUY_OK      = new Color(70, 150, 70);
    private static final Color BUY_FAIL    = new Color(160, 80,  80);
    private static final Color MONEY_BG    = new Color(195, 195, 195);

    // ==================== State ====================
    /**
     * การ์ดที่สุ่มมาแสดงในร้าน
     * เก็บเป็น PassiveCard เพราะ PolicyCard เป็นแค่ marker interface
     * แต่ทุกใบจะต้อง implement PolicyCard ด้วย
     */
    private List<PolicyCard> shopCards = new ArrayList<>();
    private int playerMoney;
    private boolean purchased = false;

    // ==================== Listener ====================
    public interface ShopListener {
        /**
         * ถูกเรียกเมื่อผู้เล่นซื้อการ์ดสำเร็จ
         * @param card           การ์ดที่ซื้อ (สามารถ cast เป็น PolicyCard ได้)
         * @param remainingMoney เงินที่เหลือหลังซื้อ
         */
        void onCardPurchased(PolicyCard card, int remainingMoney);

        /** ถูกเรียกเมื่อร้านปิด */
        void onShopClosed();
    }
    private final ShopListener shopListener;

    // ==================== Constructor ====================
    /**
     * @param scene       Scene2D ที่ Canvas นี้สังกัดอยู่
     * @param allCards    Policy card ทั้งหมดในเกม (PassiveCard ที่ implement PolicyCard)
     * @param playerMoney จำนวนเงินของผู้เล่นตอนนี้
     * @param listener    callback เมื่อซื้อ / ปิดร้าน (nullable)
     */
    public Shop(Scene2D scene,
                List<PolicyCard> allCards,
                int playerMoney,
                ShopListener listener) {
        super(scene);
        this.playerMoney  = playerMoney;
        this.shopListener = listener;

        rollCards(allCards);
        buildUI();

        setPreferredSize(new Dimension(SHOP_WIDTH, SHOP_HEIGHT));
        setSize(SHOP_WIDTH, SHOP_HEIGHT);

        // ต้องเปลี่ยน layout เป็น null ก่อน ถึงจะ setLocation ได้
        scene.setLayout(null);
        scene.add(this);

        // รอให้ Swing layout เสร็จก่อนแล้วค่อยวางตรงกลาง
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

    // ==================== SceneObject lifecycle ====================
    @Override
    public void start() { }

    /** Canvas render ผ่าน Swing — ไม่ต้องวาดเองใน render() */
    @Override
    public void render(Graphics g) { }

    @Override
    public void onDestroy() { }

    @Override
    public Dimension getSize() {
        return new Dimension(SHOP_WIDTH, SHOP_HEIGHT);
    }

    // ==================== Card Rolling ====================
    /** กรองเฉพาะ PassiveCard ที่ implement PolicyCard แล้วสุ่ม 3 ใบ */
    private void rollCards(List<PolicyCard> allCards) {
        List<PolicyCard> pool = new ArrayList<>();
        for (PolicyCard c : allCards) {
            if (c instanceof PolicyCard) pool.add(c);
        }
        Collections.shuffle(pool);
        shopCards = new ArrayList<>(pool.subList(0, Math.min(3, pool.size())));
    }

    // ==================== UI Building ====================
    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(BG_PANEL);
        setBorder(new LineBorder(CARD_BORDER, 2));

        add(buildHeader(),   BorderLayout.NORTH);
        add(buildCardArea(), BorderLayout.CENTER);
        add(buildMoneyBar(), BorderLayout.SOUTH);
    }

    /** แถบ "Shop" ด้านบน */
    private JPanel buildHeader() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
        header.setBackground(HEADER_BG);
        header.setPreferredSize(new Dimension(SHOP_WIDTH, 42));

        JLabel title = new JLabel("Shop");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.DARK_GRAY);
        header.add(title);
        return header;
    }

    /** พื้นที่แสดง card 3 ใบ */
    private JPanel buildCardArea() {
        JPanel area = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 16));
        area.setBackground(BG_PANEL);
        for (PolicyCard card : shopCards) {
            area.add(buildCardPanel(card));
        }
        return area;
    }

    /** สร้าง panel ของการ์ดแต่ละใบ */
    private JPanel buildCardPanel(PolicyCard card) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(BG_PANEL);
        wrapper.setPreferredSize(new Dimension(CARD_WIDTH + 10, CARD_HEIGHT + 65));

        // ── กล่องการ์ด — ชื่อและ detail จะแสดงบน Art ของการ์ดเอง ──
        JPanel cardBox = new JPanel();
        cardBox.setBackground(CARD_BG);
        cardBox.setBorder(new LineBorder(CARD_BORDER, 2));
        cardBox.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        cardBox.setMaximumSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        cardBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        wrapper.add(cardBox);
        wrapper.add(Box.createVerticalStrut(6));

        // ── ราคา ──
        JLabel priceLabel = new JLabel("฿" + getPrice(card), SwingConstants.CENTER);
        priceLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        priceLabel.setForeground(Color.WHITE);
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        wrapper.add(priceLabel);
        wrapper.add(Box.createVerticalStrut(4));

        // ── ปุ่ม Buy ──
        boolean canAfford = playerMoney >= getPrice(card);
        JButton buyBtn = new JButton("Buy");
        buyBtn.setBackground(canAfford ? BUY_OK : BUY_FAIL);
        buyBtn.setForeground(Color.WHITE);
        buyBtn.setFocusPainted(false);
        buyBtn.setBorderPainted(false);
        buyBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        buyBtn.setMaximumSize(new Dimension(CARD_WIDTH, 28));
        buyBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        buyBtn.setEnabled(canAfford && !purchased);
        buyBtn.addActionListener(e -> handleBuy(card));
        wrapper.add(buyBtn);

        return wrapper;
    }

    /** แถบเงินด้านล่างขวา */
    private JPanel buildMoneyBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bar.setBackground(new Color(80, 80, 80));

        JLabel moneyLabel = new JLabel("total money : " + playerMoney);
        moneyLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        moneyLabel.setForeground(Color.DARK_GRAY);
        moneyLabel.setOpaque(true);
        moneyLabel.setBackground(MONEY_BG);
        moneyLabel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        bar.add(moneyLabel);
        return bar;
    }

    // ==================== Helpers ====================
    private JButton styledButton(Color bg) {
        JButton btn = new JButton("Buy");
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        return btn;
    }

    /**
     * ราคาการ์ด
     * TODO: แก้ให้ตรงกับ field/method จริงใน PassiveCard เช่น return card.price;
     */
    private int getPrice(PolicyCard card) {
        return 100; // placeholder
    }

    // ==================== Buy Logic ====================
    private void handleBuy(PolicyCard card) {
        if (purchased) return;
        if (playerMoney < getPrice(card)) return;

        purchased    = true;
        playerMoney -= getPrice(card);

        if (shopListener != null) shopListener.onCardPurchased(card, playerMoney);
        closeShop();
    }

    /**
     * ปิดร้าน: ซ่อน + ลบออกจาก scene
     * Scene2D.remove(SceneObject) มีอยู่แล้วใน engine
     */
    private void closeShop() {
        setVisible(false);
        scene.remove((Component) this);   // remove ออกจาก Swing
        scene.remove((SceneObject) this); // remove ออกจาก sceneObjects list
        scene.revalidate();
        scene.repaint();
        if (shopListener != null) shopListener.onShopClosed();
    }
}