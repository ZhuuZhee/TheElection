package Core.GameScreens;

import Core.UI.UITool;
import Core.ZhuzheeGame;
import ZhuzheeEngine.Audios.AudioManager;
import ZhuzheeEngine.Screen;
import ZhuzheeEngine.Scene.NineSliceButton;
import ZhuzheeEngine.Scene.NineSliceCanvas;
import Core.UI.UIButtonFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class CharacterSelectMenu extends Screen implements ActionListener {

    private NineSliceButton confirmBtn;
    private NineSliceButton backBtn;

    private String selectedColor = "Pink"; // เก็บสีที่เลือกไว้

    private JLabel selectedProfileImage;
    private String selectedProfileFileName = "";
    private String selectedArcanaFileName = "";

    private BufferedImage bgImage;
    private BufferedImage btnNormalImg;
    private BufferedImage btnHoverImg;
    private NineSliceCanvas bgCanvas;

    String[] colorNames = {"Pink", "Red", "Blue", "Green", "Yellow", "Purple"};
    Color[] colors = {Color.PINK, Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, new Color(128, 0, 128)};

    public CharacterSelectMenu() {
        setLayout(new BorderLayout());

        loadImages();

        bgCanvas = new NineSliceCanvas(bgImage, 25, 25, 25, 25) {};
        bgCanvas.setLayout(new BorderLayout());

        JLabel title = UITool.createLabel("Custom Character",40f);
        bgCanvas.add(title, BorderLayout.NORTH);

        // ==========================================
        // MAIN PANEL: GridBagLayout for flexible resizing
        // ==========================================
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.fill = GridBagConstraints.BOTH; // ยืดขยายทั้งแนวตั้งแนวนอน
        mainGbc.weightx = 0.5; // แบ่งสัดส่วนความกว้างเป็น 50-50
        mainGbc.weighty = 1.0; // ใช้พื้นที่แนวตั้งให้เต็ม

        // ==========================================
        // LEFT PANEL: Profile Selection Grid
        // ==========================================
        JPanel leftPanel = new JPanel(new GridBagLayout()); // เปลี่ยนมาใช้ GridBagLayout เพิ่อจัดให้อยู่ตรงกลาง
        leftPanel.setOpaque(false);

        File profileFolder = new File("OOPgame/Assets/ImageForProfile");
        File[] profileFiles = profileFolder.listFiles((dir, name) -> name.endsWith(".png") || name.endsWith(".jpg"));

        int profileCols = 3; // กำหนดคอลัมน์เป็น 3 แบบตายตัว
        int profileRows = 3;
        if (profileFiles != null && profileFiles.length > 9) {
            profileRows = (int) Math.ceil((double) profileFiles.length / profileCols);
        }

        JPanel profileGrid = new JPanel(new GridLayout(profileRows, profileCols, 15, 15));
        profileGrid.setOpaque(false);

        if (profileFiles != null) {
            for (File file : profileFiles) {
                JButton profBtn = new JButton();
                // ปรับให้ปุ่มขยายขนาดตามได้ แต่ยังคงเป็นสี่เหลี่ยมจัตุรัส
                profBtn.setPreferredSize(new Dimension(100, 100));
                profBtn.setMinimumSize(new Dimension(80, 80));
                profBtn.setContentAreaFilled(false);
                profBtn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

                ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                Image scaledImg = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                profBtn.setIcon(new ImageIcon(scaledImg));

                profBtn.addActionListener(e -> {
                    selectedProfileFileName = file.getAbsolutePath();
                    selectedProfileImage.setIcon(new ImageIcon(icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH)));
                    AudioManager.getInstance().playSound("click");
                    
                    for (Component c : profileGrid.getComponents()) {
                        ((JButton) c).setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                    }
                    profBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
                });
                profileGrid.add(profBtn);
            }
        }

        JScrollPane leftScrollPane = new JScrollPane(profileGrid);
        leftScrollPane.setOpaque(false);
        leftScrollPane.getViewport().setOpaque(false);
        leftScrollPane.setBorder(null);
        leftScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.gridx = 0; leftGbc.gridy = 0;
        leftGbc.weightx = 1.0; leftGbc.weighty = 1.0;
        leftPanel.add(leftScrollPane, leftGbc);

        mainGbc.gridx = 0; mainGbc.gridy = 0;
        mainGbc.insets = new Insets(0, 0, 0, 20); // ระยะห่างจากตรงกลาง
        mainPanel.add(leftPanel, mainGbc);

        // ==========================================
        // RIGHT PANEL: Setting Details & Arcana Selection
        // ==========================================
        JPanel rightPanel = new JPanel(new GridBagLayout()); // เปลี่ยนมาใช้ GridBagLayout
        rightPanel.setBackground(new Color(230, 230, 230)); 
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints rGbc = new GridBagConstraints();
        rGbc.fill = GridBagConstraints.HORIZONTAL;
        rGbc.weightx = 1.0;
        rGbc.gridx = 0;

        // --- Top Right Part: Profile Preview, Name, Color ---
        JPanel topSettingsPanel = new JPanel(new GridBagLayout());
        topSettingsPanel.setOpaque(false);
        GridBagConstraints tGbc = new GridBagConstraints();
        tGbc.insets = new Insets(5, 5, 5, 5);
        tGbc.fill = GridBagConstraints.HORIZONTAL;
        tGbc.anchor = GridBagConstraints.WEST;

        // 1. Profile Preview Image
        selectedProfileImage = new JLabel();
        selectedProfileImage.setPreferredSize(new Dimension(120, 120)); // ภาพพรีวิวขนาด 120x120
        selectedProfileImage.setMinimumSize(new Dimension(120, 120));
        selectedProfileImage.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        tGbc.gridx = 0; tGbc.gridy = 0; tGbc.gridheight = 2;
        topSettingsPanel.add(selectedProfileImage, tGbc);

        // 3. Color Selection
        tGbc.gridx = 1; tGbc.gridy = 1;
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        colorPanel.setOpaque(false);
        JLabel colorLabel = new JLabel("Color :");
        colorLabel.setFont(new Font("Arial", Font.BOLD, 18));
        colorPanel.add(colorLabel);
        
        // สร้าง Grid ย่อยสำหรับวางสีเรียงกัน
        JPanel colorGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        colorGrid.setOpaque(false);


        ArrayList<JPanel> colorBoxes = new ArrayList<>();

        for (int i = 0; i < colors.length; i++) {
            final int index = i;
            JPanel colorBox = new JPanel();
            colorBox.setPreferredSize(new Dimension(30, 30));
            colorBox.setBackground(colors[i]);
            
            // ตั้งค่าสีเริ่มต้น (Pink มีขอบสีแดง, สีอื่นขอบเทา)
            if (i == 0) {
                colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
            } else {
                colorBox.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            }
            colorBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            colorBox.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectedColor = colorNames[index];
                    AudioManager.getInstance().playSound("click");
                    
                    // รีเซ็ตขอบทุกกล่อง
                    for (JPanel box : colorBoxes) {
                        box.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                    }
                    // ไฮไลต์กล่องที่ถูกเลือก
                    colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
                }
            });
            
            colorBoxes.add(colorBox);
            colorGrid.add(colorBox);
        }

        colorPanel.add(colorGrid);
        topSettingsPanel.add(colorPanel, tGbc);

        rGbc.gridy = 0;
        rightPanel.add(topSettingsPanel, rGbc);

        // เว้นระยะ
        rGbc.gridy = 1; rGbc.insets = new Insets(20, 0, 10, 0);
        JLabel arcanaTitle = new JLabel("Arcana Card");
        arcanaTitle.setFont(new Font("Arial", Font.BOLD, 18));
        rightPanel.add(arcanaTitle, rGbc);

        // --- Bottom Right Part: Arcana Cards Grid ---
        File arcanaFolder = new File("OOPgame/Assets/ImageForCards");
        File[] arcanaFiles = arcanaFolder.listFiles((dir, name) -> name.endsWith(".png") || name.endsWith(".jpg"));

        int arcanaCols = 3;
        int arcanaRows = 2;
        if (arcanaFiles != null && arcanaFiles.length > 0) {
            arcanaRows = (int) Math.ceil((double) arcanaFiles.length / arcanaCols);
        }

        JPanel arcanaGrid = new JPanel(new GridLayout(arcanaRows, arcanaCols, 15, 15));
        arcanaGrid.setOpaque(false);

        if (arcanaFiles != null) {
            for (File file : arcanaFiles) {
                JButton arcBtn = new JButton();
                arcBtn.setPreferredSize(new Dimension(70, 105)); // ขยายขึ้นนิดหน่อย
                arcBtn.setContentAreaFilled(false);
                arcBtn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

                ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                Image scaledImg = icon.getImage().getScaledInstance(70, 105, Image.SCALE_SMOOTH);
                arcBtn.setIcon(new ImageIcon(scaledImg));

                arcBtn.addActionListener(e -> {
                    selectedArcanaFileName = file.getAbsolutePath();
                    AudioManager.getInstance().playSound("click");
                    for (Component c : arcanaGrid.getComponents()) {
                        ((JButton) c).setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                    }
                    arcBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
                });
                arcanaGrid.add(arcBtn);
            }
        }

        JPanel arcanaWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        arcanaWrapper.setOpaque(false);
        arcanaWrapper.add(arcanaGrid);

        JScrollPane rightScrollPane = new JScrollPane(arcanaWrapper);
        rightScrollPane.setOpaque(false);
        rightScrollPane.getViewport().setOpaque(false);
        rightScrollPane.setBorder(null);

        rGbc.gridy = 2; rGbc.weighty = 1.0; rGbc.fill = GridBagConstraints.BOTH;
        rGbc.insets = new Insets(0, 0, 0, 0);
        rightPanel.add(rightScrollPane, rGbc);

        // นำฝั่งขวาใส่ mainPanel
        mainGbc.gridx = 1; mainGbc.gridy = 0;
        mainGbc.insets = new Insets(0, 20, 0, 0); // ระยะห่างจากซ้าย
        mainPanel.add(rightPanel, mainGbc);

        // ==========================================
        // BOTTOM PANEL: Buttons
        // ==========================================
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));

        confirmBtn = UIButtonFactory.createMenuButton("Confirm", btnNormalImg, btnHoverImg, this);
        backBtn = UIButtonFactory.createMenuButton("Back", btnNormalImg, btnHoverImg, this);

        MouseAdapter mouseHover = ZhuzheeGame.MOUSE_HOVER_SFX;
        confirmBtn.addMouseListener(mouseHover);
        backBtn.addMouseListener(mouseHover);

        btnPanel.add(confirmBtn);
        btnPanel.add(backBtn);

        bgCanvas.add(mainPanel, BorderLayout.CENTER);
        bgCanvas.add(btnPanel, BorderLayout.SOUTH);

        add(bgCanvas, BorderLayout.CENTER);

        // กำหนดภาพเริ่มต้นให้ selectedProfileImage ถ้ามีภาพอยู่ในโฟลเดอร์
        if (profileFiles != null && profileFiles.length > 0) {
            selectedProfileFileName = profileFiles[0].getAbsolutePath();
            ImageIcon defaultIcon = new ImageIcon(selectedProfileFileName);
            selectedProfileImage.setIcon(new ImageIcon(defaultIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH)));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == confirmBtn) {
            System.out.println("Selected Profile Image: " + selectedProfileFileName);
            System.out.println("Selected Arcana Image: " + selectedArcanaFileName);
            System.out.println("Color: " + selectedColor);

            // เซตเฉพาะสีให้กับผู้เล่น (ชื่อตั้งมาตั้งแต่ Create/Join Room แล้ว)
            if (ZhuzheeGame.CLIENT != null && ZhuzheeGame.CLIENT.getLocalPlayer() != null) {
                int colorIndex = java.util.Arrays.asList(colorNames).indexOf(selectedColor);
                if (colorIndex >= 0 && colorIndex < colors.length) {
                    Color color = colors[colorIndex];
                    ZhuzheeGame.CLIENT.getLocalPlayer().setColor(color);
                } else {
                    System.err.println("Warning: Selected color not found or out of bounds.");
                    // Optional: Set a default color here
                }
            }

            Screen.ChangeScreen(ZhuzheeGame.WAITING_ROOM_MENU);
            // To-Do: เก็บค่าลงใน Constructor หรือระบบที่คุณเตรียมไว้ก่อนเริ่มเกม
            // Player localPlayer = new Player(..., playerName, true, selectedColor, selectedProfileFileName, null);
        }
        if(e.getSource() == backBtn){
            Screen.ChangeScreen(ZhuzheeGame.LOBBY_MENU);
            ZhuzheeGame.CLIENT.disconnect();
        }
    }

    private void loadImages(){
        try {
            bgImage = javax.imageio.ImageIO.read(new File("OOPgame/Assets/UI/test.png"));
            btnNormalImg = javax.imageio.ImageIO.read(new File("OOPgame/Assets/UI/btn_normal.png"));
            btnHoverImg = javax.imageio.ImageIO.read(new File("OOPgame/Assets/UI/btn_hover.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}