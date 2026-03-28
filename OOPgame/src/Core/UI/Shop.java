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
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import Core.Player.Player;

public class Shop extends Canvas {

    private final Player localPlayer;
    private List<PolicyCard> shopCards = new ArrayList<>();
    private final Scene2D scene;
    private final JLabel headerLabel;
    private final JPanel cardContainer;

    public Shop(Scene2D scene) {
        this(scene, PolicyCardRegistry.rollCards(3));
    }

    public Shop(Scene2D scene, List<PolicyCard> allCards) {
        super(scene);
        this.scene = scene;
        ZhuzheeGame.SHOP_UI = this;

        if (ZhuzheeGame.CLIENT != null) {
            this.localPlayer = ZhuzheeGame.CLIENT.getLocalPlayer();
        } else {
            this.localPlayer = null;
        }

        // ตั้งค่า Layout ของ Canvas ร้านค้า
        setLayout(new BorderLayout());
        setPanelSize(520, 300); // เพิ่มความสูงให้พอดีกับปุ่ม
        setAnchors(0, 0); // กึ่งกลางหน้าจอ

        // ตกแต่งพื้นหลังและขอบ (สไตล์เดียวกับ CardHolderUI)
        enableNineSliceBackground(true);
        // setBackground(new Color(50, 50, 50, 220));
        // setBorder(new LineBorder(new Color(150, 150, 150), 2));
        // setOpaque(true);

        // สร้าง Header ไว้ด้านบน
        assert localPlayer != null;
        headerLabel = UITool.createLabel("Shop | Money: $ " + localPlayer.getCoin(), 16f);
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
            wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            wrapper.add(card, BorderLayout.CENTER);

            JButton buyBtn = new JButton("Buy");
            buyBtn.setBackground(new Color(39, 174, 96));
            buyBtn.setForeground(Color.WHITE);
            buyBtn.setFocusPainted(false);
            buyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            MouseAdapter mouseHover = ZhuzheeGame.MOUSE_HOVER_SFX;
            buyBtn.addMouseListener(mouseHover);

            buyBtn.addActionListener(e -> {
                handleBuy(card, wrapper); // ส่งกล่อง Wrapper ไปให้ระบบเพื่อทำลายทิ้งตอนซื้อ
            });

            // ใส่ปุ่มไว้ด้านล่างสุดของกล่อง Wrapper
            wrapper.add(buyBtn, BorderLayout.SOUTH);

            // นำกล่อง Wrapper (ที่มีทั้งการ์ดและปุ่ม) ไปวางเรียงในร้าน
            cardContainer.add(wrapper);
        }

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JButton closeBtn = new JButton("Close Shop");
        closeBtn.setBackground(new Color(231, 76, 60));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setPreferredSize(new Dimension(150, 30));
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        closeBtn.addMouseListener(ZhuzheeGame.MOUSE_HOVER_SFX);
        closeBtn.addActionListener(e -> {
            AudioManager.getInstance().playSound("click");
            closeShop(); // ปิดร้านค้า คืนการ์ดเข้ากอง
        });

        bottomPanel.add(closeBtn);
        add(bottomPanel, BorderLayout.SOUTH);
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
        headerLabel.setText("Shop | Money: $ " + localPlayer.getCoin());
        try {
            javax.swing.ImageIcon coinIcon = new javax.swing.ImageIcon("OOPgame/Assets/UI/Coin.png");
            java.awt.Image image = coinIcon.getImage();
            java.awt.Image newimg = image.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH); 
            coinIcon = new javax.swing.ImageIcon(newimg);
            headerLabel.setIcon(coinIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getPrice(PolicyCard card) {
        return Math.abs(card.getCoin());
    }

    private void handleBuy(PolicyCard card, JPanel wrapper) {
        int price = getPrice(card);

        if (this.localPlayer.getCoin() < price) {
            ZhuzheeEngine.Debug.GameLogger.logWarning("เงินไม่พอ! ขาดอีก: " + (price - this.localPlayer.getCoin()));
            return;
        }

        this.localPlayer.setCoin(this.localPlayer.getCoin() - price);
        ZhuzheeEngine.Debug.GameLogger.logInfo("ซื้อการ์ด " + card.getName() + " สำเร็จ!");
        AudioManager.getInstance().playSound("click");

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
        // เอาไว้เปิด shopUI
        closeShop();
    }

    public void closeShop() {
        // คืนค่าการ์ดที่ไม่ได้ซื้อกลับเข้ากองสุ่ม (ถ้าต้องการ)
        for (PolicyCard card : shopCards) {
            PolicyCardRegistry.markAsAvailable(card.getClass().getName());
            GameObject.Destroy(card);
        }
        shopCards.clear();
        if (ZhuzheeGame.SHOP_UI == this) ZhuzheeGame.SHOP_UI = null;

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
