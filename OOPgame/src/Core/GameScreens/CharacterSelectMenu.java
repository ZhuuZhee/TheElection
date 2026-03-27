package Core.GameScreens;

import Core.Network.Client.GameClientManager;
import Core.Network.PacketBuilder;
import Core.Player.Player;
import Core.UI.UITool;
import Core.ZhuzheeGame;
import ZhuzheeEngine.Audios.AudioManager;
import ZhuzheeEngine.Screen;
import ZhuzheeEngine.Scene.NineSliceButton;
import ZhuzheeEngine.Scene.NineSliceCanvas;
import Core.UI.UIButtonFactory;
import org.json.JSONObject;

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

    private String selectedColor = ""; // จะถูกกำหนดค่าเริ่มต้นตาม Color Map

    private JLabel selectedProfileImage;
    private String selectedProfileFileName = "";
    private String selectedArcanaFileName = "";

    private BufferedImage bgImage;
    private BufferedImage btnNormalImg;
    private BufferedImage btnHoverImg;
    private NineSliceCanvas bgCanvas;

    private Color[] getColors(){
        return Player.COLOR_MAP.values().toArray(new Color[0]);
    }
    private String[] getColorNames(){
        return Player.COLOR_MAP.keySet().toArray(new String[0]);
    }

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
        JPanel mainPanel =
                new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.fill = GridBagConstraints.BOTH; // ยืดขยายทั้งแนวตั้งแนวนอน
        mainGbc.weightx = 0.5; // แบ่งสัดส่วนความกว้างเป็น 50-50
        mainGbc.weighty = 1.0; // ใช้พื้นที่แนวตั้งให้เต็ม

        // ==========================================
        // LEFT PANEL: Profile Selection Grid
        // ==========================================

        File[] profileFiles = loadImageFiles(ZhuzheeGame.PROFILE_FILE_PATH);
        JPanel leftPanel = createLeftPanel(profileFiles);


        mainGbc.gridx = 0; mainGbc.gridy = 0;
        mainGbc.insets = new Insets(0, 0, 0, 20); // ระยะห่างจากตรงกลาง
        mainPanel.add(leftPanel, mainGbc);

        // ==========================================
        // RIGHT PANEL: Setting Details & Arcana Selection
        // ==========================================
        JPanel rightPanel = createRightPanel();
        // นำฝั่งขวาใส่ mainPanel
        mainGbc.gridx = 1; mainGbc.gridy = 0;
        mainGbc.insets = new Insets(0, 20, 0, 0); // ระยะห่างจากซ้าย
        mainPanel.add(rightPanel, mainGbc);

        // ==========================================
        // BOTTOM PANEL: Buttons
        // ==========================================
        JPanel btnPanel = buttonsPanel();

        //--------------------------------------------------
        bgCanvas.add(mainPanel, BorderLayout.CENTER);
        bgCanvas.add(btnPanel, BorderLayout.SOUTH);

        add(bgCanvas, BorderLayout.CENTER);

        // กำหนดภาพเริ่มต้นให้ selectedProfileImage ถ้ามีภาพอยู่ในโฟลเดอร์
        if (profileFiles != null && profileFiles.length > 0) {
            selectedProfileFileName = profileFiles[0].getName(); // เก็บแค่ชื่อไฟล์สำหรับ Network
            ImageIcon defaultIcon = new ImageIcon(profileFiles[0].getAbsolutePath()); // ใช้ Path เต็มสำหรับการโหลดรูปในเครื่องตัวเอง
            selectedProfileImage.setIcon(new ImageIcon(defaultIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH)));
        }

        // กำหนดสีเริ่มต้นจากสีแรกใน Color Map เพื่อให้ตรงกับ UI
        String[] names = getColorNames();
        if (names.length > 0) selectedColor = names[0];
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == confirmBtn) {
            System.out.println("Selected Profile Image: " + selectedProfileFileName);
            System.out.println("Selected Arcana Image: " + selectedArcanaFileName);
            System.out.println("Color: " + selectedColor);

            // เซตเฉพาะสีให้กับผู้เล่น (ชื่อตั้งมาตั้งแต่ Create/Join Room แล้ว)
            if (ZhuzheeGame.CLIENT != null && ZhuzheeGame.CLIENT.getLocalPlayer() != null) {
                sendPlayerData();
            }else {
                System.err.println("Warning: Selected color not found or out of bounds.");
                // Optional: Set a default color here
            }

            Screen.ChangeScreen(ZhuzheeGame.WAITING_ROOM_MENU);
        }
        if(e.getSource() == backBtn){
            Screen.ChangeScreen(ZhuzheeGame.LOBBY_MENU);
            if (ZhuzheeGame.CLIENT != null) ZhuzheeGame.CLIENT.disconnect();
        }
    }

    private void sendPlayerData(){
        GameClientManager client = ZhuzheeGame.CLIENT;
        if (client == null || client.getLocalPlayer() == null) {
            System.err.println("Cannot send Player data. No Local Player Detected");
            return;
        }

        Player localPlayer = client.getLocalPlayer();


        //localPlayer.setProfileImagePath(selectedProfileFileName);

        JSONObject playerPacket = PacketBuilder.createPlayerDataPacket(
                localPlayer.getPlayerId(),
                localPlayer.getPlayerName(),
                localPlayer.getCoin(),
                selectedArcanaFileName,
                selectedColor,
                selectedProfileFileName// ใช้ตัวแปรที่เก็บ Path ของไฟล์จริงๆ
        );

        //update self client
//        client.getLocalPlayer().updateFromJSON(playerPacket);

        //send to server for update all
        client.sendAction(playerPacket);
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
    private JPanel createLeftPanel(File[] profileFiles) {
        JPanel leftPanel = new JPanel(new GridBagLayout()); // เปลี่ยนมาใช้ GridBagLayout เพิ่อจัดให้อยู่ตรงกลาง
        leftPanel.setOpaque(false);

        // สร้าง Grid สำหรับ Profile และระบุว่าเป็น Profile Selection
        JPanel profileGrid = createGridPanel(profileFiles, 3, 0,
                new Dimension(100,100),
                new Dimension(15,15),
                "PROFILE");

        JScrollPane leftScrollPane = createScrollPane(profileGrid);

        //create layout
        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.gridx = 0; leftGbc.gridy = 0;
        leftGbc.weightx = 1.0; leftGbc.weighty = 1.0;

        leftPanel.add(leftScrollPane, leftGbc);

        return leftPanel;
    }
    private JPanel createRightPanel(){
        var rightPanel = new JPanel(new GridBagLayout()); // เปลี่ยนมาใช้ GridBagLayout
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
        selectedProfileImage = createProfileImagePreview();

        tGbc.gridx = 0; tGbc.gridy = 0; tGbc.gridheight = 2;
        topSettingsPanel.add(selectedProfileImage, tGbc);

        // 3. Color Selection
        tGbc.gridx = 1; tGbc.gridy = 1;


        // สร้าง Grid ย่อยสำหรับวางสีเรียงกัน
        var colorPanel = createColorSelectionPanel();
        topSettingsPanel.add(colorPanel, tGbc);

        rGbc.gridy = 0;
        rightPanel.add(topSettingsPanel, rGbc);

        // เว้นระยะ
        rGbc.gridy = 1; rGbc.insets = new Insets(20, 0, 10, 0);
        JLabel arcanaTitle = UITool.createLabel("Arcana Card",18f);
        rightPanel.add(arcanaTitle, rGbc);

        // --- Bottom Right Part: Arcana Cards Grid ---
        File[] arcanaFiles = loadImageFiles(ZhuzheeGame.CARD_IMAGES_FILE_PATH);
        // สร้าง Grid สำหรับ Arcana และระบุว่าเป็น Arcana Selection
        JPanel arcanaGrid = createGridPanel(arcanaFiles, 5, 0, 
                new Dimension(100, 150), 
                new Dimension(15, 15), 
                "ARCANA");

        //prevent ยืด
        JPanel arcanaWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        arcanaWrapper.setOpaque(false);
        arcanaWrapper.add(arcanaGrid);

        JScrollPane rightScrollPane = createScrollPane(arcanaWrapper);

        rGbc.gridy = 2; rGbc.weighty = 1.0; rGbc.fill = GridBagConstraints.BOTH;
        rGbc.insets = new Insets(0, 0, 0, 0);
        rightPanel.add(rightScrollPane, rGbc);


        return rightPanel;
    }
    private JPanel buttonsPanel(){
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
        return btnPanel;
    }
    File[] loadImageFiles(String path){
        File profileFolder = new File(path);
        return profileFolder.listFiles((dir, name) -> name.endsWith(".png") || name.endsWith(".jpg"));
    }
    private JPanel createGridPanel(File[] files, int cols, int rows, Dimension imageScale, Dimension gap, String type){
        // ใช้ 0 สำหรับ rows เพื่อให้ปรับตามจำนวนไฟล์อัตโนมัติ
        JPanel grid = new JPanel(new GridLayout(0, cols, gap.width, gap.height));
        grid.setOpaque(false);
        
        if (files != null) {
            for (File file : files) {
                JButton Btn = new JButton();
                Btn.setPreferredSize(new Dimension(imageScale.width, imageScale.height));
                Btn.setContentAreaFilled(false);
                Btn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                
                ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                Image scaledImg = icon.getImage().getScaledInstance(imageScale.width, imageScale.height, Image.SCALE_SMOOTH);
                Btn.setIcon(new ImageIcon(scaledImg));

                // เพิ่ม Action ตามประเภทของ Grid
                if (type.equals("PROFILE")) {
                    Btn.addActionListener(new ProfileSelectAction(file, icon, grid, Btn)); 
                } else if (type.equals("ARCANA")) {
                    Btn.addActionListener(new ArcanaCardSelectAction(file, grid, Btn)); 
                }

                grid.add(Btn);
            }
        }
        return grid;
    }
    private JScrollPane createScrollPane(JPanel panel){
        JScrollPane leftScrollPane = new JScrollPane(panel);
        leftScrollPane.setOpaque(false);
        leftScrollPane.getViewport().setOpaque(false);
        leftScrollPane.setBorder(null);
        leftScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return leftScrollPane;
    }
    private JLabel createProfileImagePreview(){
        var selectedProfileImage = new JLabel();
        selectedProfileImage.setPreferredSize(new Dimension(120, 120)); // ภาพพรีวิวขนาด 120x120
        selectedProfileImage.setMinimumSize(new Dimension(120, 120));
        selectedProfileImage.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        return selectedProfileImage;
    }
    private JPanel createColorSelectionPanel(){
        JPanel colorGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        colorGrid.setOpaque(false);
        JLabel colorLabel = new JLabel("Color :");
        colorLabel.setFont(new Font("Arial", Font.BOLD, 18));
        colorGrid.add(colorLabel);
        new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        ArrayList<JPanel> colorBoxes = new ArrayList<>();
        Color[] colors = getColors();
        String[] colorNames = getColorNames();

        for (int i = 0; i < colors.length; i++) {
            final int index = i;
            JPanel colorBox = new JPanel();
            colorBox.setPreferredSize(new Dimension(30, 30));
            colorBox.setBackground(colors[i]);

            // ไฮไลต์สีแรกเป็นค่าเริ่มต้น
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
        return colorGrid;
    }
    public class ProfileSelectAction implements ActionListener {
        public ProfileSelectAction(File file, ImageIcon image,JPanel profileGrid, JButton profBtn){
            this.file = file;
            this.icon = image;
            this.profileGrid = profileGrid;
            this.profBtn = profBtn;
        }
        private ImageIcon icon;
        private File file;
        private JPanel profileGrid;
        private JButton profBtn;
        @Override
        public void actionPerformed(ActionEvent e) {
            selectedProfileFileName = file.getName(); // ส่งแค่ชื่อไฟล์
            selectedProfileImage.setIcon(new ImageIcon(icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH)));
            AudioManager.getInstance().playSound("click");

            for (Component c : profileGrid.getComponents()) {
                if (c instanceof JButton) ((JButton) c).setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            }
            profBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
        }
    };
    public class ArcanaCardSelectAction implements ActionListener {
        private File file;
        private JPanel arcanaGrid;
        private JButton arcanaBtn;

        public ArcanaCardSelectAction(File file, JPanel arcanaGrid, JButton arcanaBtn) {
            this.file = file;
            this.arcanaGrid = arcanaGrid;
            this.arcanaBtn = arcanaBtn;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            selectedArcanaFileName = file.getName(); // ส่งแค่ชื่อไฟล์
            AudioManager.getInstance().playSound("click");

            for (Component c : arcanaGrid.getComponents()) {
                if (c instanceof JButton) ((JButton) c).setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            }
            arcanaBtn.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 4)); // ใช้สีส้มเพื่อให้ต่างจาก Profile
        }
    }
}