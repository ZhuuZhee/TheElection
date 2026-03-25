package Core.UI;

import Core.Cards.PolicyCard;
import Core.Cards.PolicyCardRegistry;
import Core.ZhuzheeGame;
import ZhuzheeEngine.Scene.Scene2D;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import Core.Player.Player;

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

    private Scene2D scene;

    // ตัวแปร state
    private List<PolicyCard> shopCards = new ArrayList<>();
    private Player localPlayer;
    private boolean purchased = false;

    /** สร้าง Shop โดยสุ่มการ์ด 3 ใบจาก policy_cards.json อัตโนมัติ */
    public Shop(Scene2D scene) {
        this(scene, PolicyCardRegistry.rollCards(3));
    }

    public Shop(Scene2D scene, List<PolicyCard> allCards) {
        this.scene = scene;
        if (ZhuzheeGame.CLIENT != null) {
            this.localPlayer = ZhuzheeGame.CLIENT.getLocalPlayer();
        } else {
            this.localPlayer = Dummy.Tester.dummyPlayer;
        }

//        สุ่มการ์ดมา 3 ใบ
        shopCards = rollCards(allCards);

//        ซ่อนการ์ดทุกใบออกจาก scene ก่อน จะวาดเองใน buildCardArea
        for (PolicyCard card : shopCards) {
//            card.setEnable(false);
//            card.setVisible(false);
            card.setDraggable(false);
        }

//        สร้าง Core.UI
        setLayout(new BorderLayout());
        setBackground(BG_PANEL);
        setBorder(new LineBorder(CARD_BORDER, 2));
//        setPreferredSize(new Dimension(SHOP_WIDTH, SHOP_HEIGHT));
//        setSize(SHOP_WIDTH, SHOP_HEIGHT);

//        เพิ่มส่วนต่างๆ ของ Shop
        add(buildHeader(),   BorderLayout.NORTH);
        add(buildCardArea(), BorderLayout.CENTER);
        add(buildMoneyBar(), BorderLayout.SOUTH);

//        add เข้า scene และวางตรงกลาง
        scene.add(this);
        scene.setComponentZOrder(this, 0);
        scene.revalidate();
        setVisible(true);
//        scene.repaint();

        // ตั้งตำแหน่งและขนาดให้ Shop แสดงขึ้นมา (center ถ้า scene มีขนาด มิฉะนั้นใช้ default)
        int x = (scene.getWidth() > SHOP_WIDTH) ? (scene.getWidth() - SHOP_WIDTH) / 2 : 200;
        int y = (scene.getHeight() > SHOP_HEIGHT) ? (scene.getHeight() - SHOP_HEIGHT) / 2 : 150;
        setBounds(x, y, SHOP_WIDTH, SHOP_HEIGHT);

    }

    // สุ่มการ์ดจาก pool (ใช้เมื่อส่ง list เข้ามาเอง)
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
            ZhuzheeGame.MAIN_SCENE.remove(card);
            card.setVisible(true);
            card.setDraggable(false);
            area.add(buildCardPanel(card));
        }

        return area;
    }

    // สร้าง panel ของการ์ดแต่ละใบ
    private JPanel buildCardPanel(PolicyCard card) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setOpaque(false); // โปร่งใส
        wrapper.setPreferredSize(new Dimension(CARD_WIDTH + 10, CARD_HEIGHT + 65));

//        กล่อง placeholder โปร่งใส card จะถูกวาดทับโดย paintComponent
        JPanel cardBox = new JPanel(new BorderLayout());
        cardBox.setOpaque(false);
        cardBox.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        cardBox.setMaximumSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        cardBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardBox.add(card, BorderLayout.CENTER);
        wrapper.add(cardBox);

        wrapper.add(Box.createVerticalStrut(6));
//        ราคาการ์ด
        JLabel priceLabel = new JLabel("$ " + -1 * getPrice(card), SwingConstants.CENTER);
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        priceLabel.setForeground(Color.WHITE);
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        wrapper.add(priceLabel);
        wrapper.add(Box.createVerticalStrut(4));

//        ปุ่มซื้อ เขียวถ้าเงินพอ แดงถ้าเงินไม่พอ
        boolean canAfford = this.localPlayer.getCoin() >= getPrice(card);
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
    JLabel moneyLabel;
    private JPanel buildMoneyBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bar.setBackground(new Color(80, 80, 80));

        moneyLabel = new JLabel("total money : " + this.localPlayer.getCoin());
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
        return card.getCoin();
    }

    // จัดการการซื้อการ์ด → snap card เข้า slot แล้วปิด shop
    private void handleBuy(PolicyCard card) {
        if (this.localPlayer.getCoin() < getPrice(card)) return;

        this.localPlayer.setCoin(this.localPlayer.getCoin() + getPrice(card));
        System.out.println("ซื้อการ์ด " + card.getName() + " สำเร็จ! หักเงิน " +  -(getPrice(card)) + " เหลือ: " + localPlayer.getCoin());

        shopCards.remove(card);
        if (ZhuzheeGame.POLICY_CARD_UI != null) {
            ZhuzheeGame.POLICY_CARD_UI.addCard(card);
        } else {
            System.err.println("Warning: PolicyCardHolderUI is null!");
        }

        moneyLabel.setText("Total Money : " + this.localPlayer.getCoin());

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