package Core.UI;

import Core.ZhuzheeGame;
import Core.Cards.Card;
import Core.Player.Player;
import ZhuzheeEngine.Scene.*;
import ZhuzheeEngine.Scene.Canvas;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class PlayerProfile extends Canvas {
    private final Player player;
    private final JLabel profileImageLabel;
    private final JLabel playerNameLabel;
    private final JLabel playerCoinsLabel;
    private final int panelWidth = 450;
    private final int panelHeight = 160;
    private final int imageWidth = 140;
    private final int imageHeight = 140;

    public PlayerProfile(Scene2D scene, Player player) {
        super(scene);
        this.player = player;

        // 1. ตั้งค่าตำแหน่งและขนาด (ขวาล่าง)
        setPanelSize(panelWidth, panelHeight);
        setAnchors(1, -1);
        setMargins(0, 20, 0, 20);

        // 2. ตั้งค่ารูคลักษณ์ (สีเทาเข้มโปร่งแสง)
        setLayout(new BorderLayout(15, 0));
        setBackground(new Color(30, 30, 30, 220));
        setOpaque(false);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(player.getColor(), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // 3. ส่วนของรูปโปรไฟล์ (ซ้าย)
        profileImageLabel = new JLabel();
        profileImageLabel.setPreferredSize(new Dimension(imageWidth, imageHeight));
        profileImageLabel.setHorizontalAlignment(JLabel.CENTER);
        profileImageLabel.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 50), 1));

        // 4. ส่วนข้อมูล (ขวา)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        // ชื่อผู้เล่น
        playerNameLabel = new JLabel(player.getPlayerName());
        playerNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        playerNameLabel.setForeground(Color.WHITE);
        playerNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // จำนวนเหรียญ
        playerCoinsLabel = new JLabel("Coins: $ " + player.getCoin());
        playerCoinsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        playerCoinsLabel.setForeground(new Color(255, 215, 0)); // สีทอง
        playerCoinsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(Box.createVerticalGlue());
        infoPanel.add(playerNameLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(playerCoinsLabel);
        infoPanel.add(Box.createVerticalGlue());

        // 5. นำเข้าสู่หน้าจอ
        add(profileImageLabel, BorderLayout.WEST);
        add(infoPanel, BorderLayout.CENTER);

        updateProfile();
        setVisible(true);
    }

    public void updateProfile() {
        if (player == null) return;

        // อัปเดตรูปภาพ (ถ้ามีการเปลี่ยนแปลงหรือตอนเริ่ม)
        File imageFile = player.getProfileImageFile();
        if (imageFile != null && imageFile.exists()) {
            // 2. โหลดรูปภาพด้วย ImageIcon
            ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());

            // 3. ตรวจสอบว่าไฟล์รูปภาพมีอยู่จริงและโหลดได้
            if (icon.getIconWidth() > 0) {
                // 4. ทำการ Re-size (Scale) รูปภาพให้พอดีกับกรอบ 140x140 พิกเซล
                Image img = icon.getImage().getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH);

                // 5. นำรูปที่ปรับขนาดแล้วไปใส่ใน JLabel
                profileImageLabel.setIcon(new ImageIcon(img));
                profileImageLabel.setText(""); // ล้างข้อความ N/A ออก
            } else {
                fallbackToColor(); // ถ้าโหลดรูปไม่ได้ ให้ใช้สีประจำตัวแทน
            }
        } else {
            fallbackToColor();
        }

        // อัปเดตข้อความ
        playerNameLabel.setText(player.getPlayerName());
        playerCoinsLabel.setText("Coins: $ " + player.getCoin());

        // อัปเดตสีขอบตามสีผู้เล่น
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(player.getColor(), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    }

    private void fallbackToColor() {
        profileImageLabel.setIcon(null);
        profileImageLabel.setOpaque(true);
        profileImageLabel.setBackground(player.getColor());
        profileImageLabel.setText("N/A");
        profileImageLabel.setForeground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        updateProfile();
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        g2d.dispose();
    }
}
