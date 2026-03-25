package Core.GameScreens;

import Core.ZhuzheeGame;
import ZhuzheeEngine.Audios.AudioManager;
import ZhuzheeEngine.Screen;
import ZhuzheeEngine.Scene.NineSliceCanvas;
import ZhuzheeEngine.Scene.NineSliceButton;
import Core.UI.UIButtonFactory;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class CharacterSelectUI extends Screen implements ActionListener {

    // เปลี่ยนจากชื่อเป็นพาธของรูปภาพตัวละคร (แก้ไขพาธและไฟล์ให้ตรงกับที่คุณมีในโฟลเดอร์ Assets นะครับ)
    private static final String[] CHAR_IMAGE_PATHS = {
            "OOPgame/Assets/ImageForProfile/best.jpg", "OOPgame/Assets/ImageForProfile/jeng.jpg", "OOPgame/Assets/ImageForProfile/sega.jpg", "OOPgame/Assets/ImageForProfile/pong.jpg",
            "OOPgame/Assets/ImageForProfile/lee.jpg", "OOPgame/Assets/ImageForProfile/monkey.jpg", "OOPgame/Assets/ImageForProfile/Maloch.png", "OOPgame/Assets/ImageForProfile/ShockWave.png",
            "OOPgame/Assets/ImageForProfile/black.png"
    };

    // Dynamic card data
    private static final String[] CARD_NAMES = {
            "Card 1", "Card 2", "Card 3", "Card 4", "Card 5", "Card 6"
    };

    // Team colors
    private static final Color[] TEAM_COLORS = {
            new Color(231, 76, 60),  // Soft Red
            new Color(230, 126, 34), // Orange
            new Color(241, 196, 15), // Yellow
            new Color(46, 204, 113), // Green
            new Color(52, 152, 219), // Blue
            new Color(155, 89, 182), // Purple
            new Color(253, 121, 168),// Pink
            new Color(52, 73, 94)    // Dark Gray
    };

    // Modern Color Palette
    private final Color BG_MAIN = new Color(245, 247, 250);
    private final Color PANEL_BG = Color.WHITE;
    private final Color BORDER_NORMAL = new Color(220, 225, 230);
    private final Color BORDER_SELECTED = new Color(41, 128, 185); // Active Blue
    private final Color HOVER_BG = new Color(236, 240, 241);

    // State
    private int   selChar  = -1;
    private int   selCard  = -1;
    private Color selColor = null;

    // UI refs
    private List<JPanel> charCells  = new ArrayList<>();
    private List<JPanel> cardCells  = new ArrayList<>();
    private List<JPanel> colorCells = new ArrayList<>();
    private JPanel     previewBox;
    private JLabel     previewLbl;
    private JTextField nameField;

    private NineSliceButton confirmBtn;
    private BufferedImage bgImage;
    private BufferedImage btnNormalImg;
    private BufferedImage btnHoverImg;
    private NineSliceCanvas bgCanvas;

    public CharacterSelectUI() {
        setLayout(new BorderLayout());

        try {
            bgImage = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/test.png"));
            btnNormalImg = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/btn_normal.png"));
            btnHoverImg = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/btn_hover.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        bgCanvas = new NineSliceCanvas(bgImage, 25, 25, 25, 25) {};
        bgCanvas.setLayout(new BorderLayout());
        bgCanvas.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30)); // ขอบนอกสุด

        // Title
        JLabel title = new JLabel("Choose Your Character", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(new Color(44, 62, 80));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        bgCanvas.add(title, BorderLayout.NORTH);

        // Body: left + right
        JPanel body = new JPanel(new GridLayout(1, 2, 30, 0)); // เพิ่มช่องว่างตรงกลางระหว่าง 2 ฝั่ง
        body.setOpaque(false);
        body.add(buildCharGrid());
        body.add(buildRightPanel());

        // นำ body มาใส่ใน wrapper แบบ GridBagLayout เพื่อให้กล่องอยู่ตรงกลางและไม่ถูกยืดจนเต็มหน้าจอ
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(body);

        bgCanvas.add(wrapper, BorderLayout.CENTER);
        add(bgCanvas, BorderLayout.CENTER);
    }

    // Left: dynamic character grid
    private JPanel buildCharGrid() {
        int cols = 3; // 4 คอลัมน์ตามรูปอ้างอิง
        int rows = (int) Math.ceil((double) CHAR_IMAGE_PATHS.length / cols);
        JPanel grid = new JPanel(new GridLayout(rows, cols, 15, 15));
        grid.setOpaque(false);

        for (int i = 0; i < CHAR_IMAGE_PATHS.length; i++) {
            final int idx = i;
            JPanel cell = new JPanel(new BorderLayout());
            cell.setBackground(PANEL_BG);
            cell.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // พยายามโหลดรูปภาพมาแสดงผล ถ้าหาไฟล์ไม่เจอ ให้แสดงชื่อตัวละครแทน
            JLabel lbl = new JLabel("", SwingConstants.CENTER);
            try {
                // อ่านไฟล์รูป
                BufferedImage img = javax.imageio.ImageIO.read(new java.io.File(CHAR_IMAGE_PATHS[i]));
                // ย่อรูปภาพให้พอดีกับช่อง (ขนาด 80x80)
                Image scaledImg = img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                lbl.setIcon(new ImageIcon(scaledImg));
            } catch (Exception ex) {
                // ถ้ายังไม่มีรูป ให้แสดงชื่อแทนเป็นการชั่วคราว
                lbl.setText("None");
                lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
                lbl.setForeground(new Color(50, 50, 50));
            }

            cell.add(lbl, BorderLayout.CENTER);

            // Hover Effect
            cell.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    if (selChar != idx) cell.setBackground(HOVER_BG);
                }
                public void mouseExited(MouseEvent e) {
                    if (selChar != idx) cell.setBackground(PANEL_BG);
                }
                public void mouseClicked(MouseEvent e) {
                    selChar = idx;
                    refreshChars();
                    updatePreview();
                }
            });
            charCells.add(cell);
            grid.add(cell);
        }
        refreshChars(); // Set initial borders
        return grid;
    }

    // Right panel
    private JPanel buildRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_NORMAL, 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Top Row: Preview + Name/Color
        JPanel topRow = new JPanel(new BorderLayout(20, 0));
        topRow.setOpaque(false);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        // Preview Box
        previewBox = new JPanel(new BorderLayout());
        previewBox.setPreferredSize(new Dimension(80, 80));
        previewBox.setBackground(BG_MAIN);
        previewBox.setBorder(BorderFactory.createLineBorder(BORDER_NORMAL, 2));
        previewLbl = new JLabel("?", SwingConstants.CENTER);
        previewLbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        previewLbl.setForeground(Color.GRAY);
        previewBox.add(previewLbl, BorderLayout.CENTER);
        topRow.add(previewBox, BorderLayout.WEST);

        // Name + Color
        JPanel nameColorCol = new JPanel();
        nameColorCol.setLayout(new GridLayout(2, 1, 0, 10));
        nameColorCol.setOpaque(false);

        // Name Input
        JPanel nameRow = new JPanel(new BorderLayout(10, 0));
        nameRow.setOpaque(false);
        JLabel nameLbl = new JLabel("Player Name:");
        nameLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameField = new JTextField();
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_NORMAL),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        nameRow.add(nameLbl, BorderLayout.WEST);
        nameRow.add(nameField, BorderLayout.CENTER);
        nameColorCol.add(nameRow);

        // Colors
        JPanel colorRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        colorRow.setOpaque(false);
        for (int i = 0; i < TEAM_COLORS.length; i++) {
            final int idx = i;
            JPanel sw = new JPanel();
            sw.setPreferredSize(new Dimension(30, 30));
            sw.setBackground(TEAM_COLORS[i]);
            sw.setCursor(new Cursor(Cursor.HAND_CURSOR));

            sw.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    selColor = TEAM_COLORS[idx];
                    refreshColors();
                }
            });
            colorCells.add(sw);
            colorRow.add(sw);
        }
        refreshColors();
        nameColorCol.add(colorRow);

        topRow.add(nameColorCol, BorderLayout.CENTER);
        panel.add(topRow);

        panel.add(Box.createVerticalStrut(20));

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_NORMAL);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(sep);

        panel.add(Box.createVerticalStrut(20));

        // Arcana Card
        JLabel arcLbl = new JLabel("Select Arcana Card");
        arcLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        arcLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(arcLbl);
        panel.add(Box.createVerticalStrut(15));

        // Card Grid
        int cardCols = 3;
        int cardRows = (int) Math.ceil((double) CARD_NAMES.length / cardCols);
        JPanel cardGrid = new JPanel(new GridLayout(cardRows, cardCols, 10, 10));
        cardGrid.setOpaque(false);
        cardGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, cardRows * 130));

        for (int i = 0; i < CARD_NAMES.length; i++) {
            final int idx = i;
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(PANEL_BG);
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel clbl = new JLabel(CARD_NAMES[i], SwingConstants.CENTER);
            clbl.setFont(new Font("SansSerif", Font.BOLD, 13));
            card.add(clbl, BorderLayout.CENTER);

            // Hover Effect
            card.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    if (selCard != idx) card.setBackground(HOVER_BG);
                }
                public void mouseExited(MouseEvent e) {
                    if (selCard != idx) card.setBackground(PANEL_BG);
                }
                public void mouseClicked(MouseEvent e) {
                    selCard = idx;
                    refreshCards();
                }
            });
            cardCells.add(card);
            cardGrid.add(card);
        }
        refreshCards();
        panel.add(cardGrid);

        panel.add(Box.createVerticalGlue()); // ดันปุ่ม Confirm ลงไปล่างสุด

        // Confirm Button
        confirmBtn = UIButtonFactory.createMenuButton("Confirm Selection", btnNormalImg, btnHoverImg, this);
        confirmBtn.addMouseListener(ZhuzheeGame.MOUSE_HOVER_SFX);
        confirmBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(confirmBtn);

        return panel;
    }

    //Helper Methods
    //Border Methods สร้างสไตล์ของเส้นขอบ (Border) เพื่อเอาไปแปะให้กล่องตัวละคร/การ์ด
    //getNormalBorde สร้างขอบสำหรับสถานะ "ปกติ (ยังไม่ได้ถูกเลือก)" จะเป็นเส้นขอบบางๆ
    private Border getNormalBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_NORMAL, 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );
    }
    //getSelectedBorder สร้างขอบสำหรับสถานะ "ถูกเลือกแล้ว" จะเปลี่ยนเป็นเส้นขอบที่หนาขึ้น
    private Border getSelectedBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_SELECTED, 3, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        );
    }
    //ถ้าเลือกสีนี้อยู่: เส้นขอบจะเป็นสีดำ / ถ้าไม่ได้เลือก: เส้นขอบจะเป็นสีปกติ
    private void refreshChars() {
        for (int i = 0; i < charCells.size(); i++) {
            boolean isSel = (i == selChar);
            charCells.get(i).setBorder(isSel ? getSelectedBorder() : getNormalBorder());
            charCells.get(i).setBackground(isSel ? new Color(235, 245, 255) : PANEL_BG);
        }
    }
    //ถ้าเลือกสีนี้อยู่: เส้นขอบจะเป็นสีดำ / ถ้าไม่ได้เลือก: เส้นขอบจะเป็นสีปกติ
    private void refreshCards() {
        for (int i = 0; i < cardCells.size(); i++) {
            boolean isSel = (i == selCard);
            cardCells.get(i).setBorder(isSel ? getSelectedBorder() : getNormalBorder());
            cardCells.get(i).setBackground(isSel ? new Color(235, 245, 255) : PANEL_BG);
        }
    }
    //ถ้าเลือกสีนี้อยู่: เส้นขอบจะเป็นสีดำ / ถ้าไม่ได้เลือก: เส้นขอบจะเป็นสีปกติ
    private void refreshColors() {
        for (int i = 0; i < colorCells.size(); i++) {
            boolean isSel = TEAM_COLORS[i].equals(selColor);
            colorCells.get(i).setBorder(BorderFactory.createLineBorder(isSel ? Color.BLACK : BORDER_NORMAL, isSel ? 3 : 1));
        }
    }

    private void updatePreview() {
        if (selChar >= 0) {
            // อัพเดตฝั่งขวาบน ให้แสดงเป็นรูปภาพเหมือนกัน
            try {
                BufferedImage img = javax.imageio.ImageIO.read(new java.io.File(CHAR_IMAGE_PATHS[selChar]));
                Image scaledImg = img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                previewLbl.setText(""); // ลบข้อความออก
                previewLbl.setIcon(new ImageIcon(scaledImg));
            } catch (Exception ex) {
                // ถ้าไม่มีรูป ให้กลับไปแสดงเป็นข้อความ
                previewLbl.setIcon(null);
                previewLbl.setText("None");
                previewLbl.setForeground(new Color(44, 62, 80));
            }
            previewBox.repaint();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == confirmBtn) {
            // TODO: จัดการเมื่อกดปุ่ม Confirm เช่น การเปลี่ยนหน้าหรือบันทึกข้อมูล
            // Screen.ChangeScreen(ZhuzheeGame.MAIN_MENU);
        }
        AudioManager.getInstance().playSound("click");
    }

}