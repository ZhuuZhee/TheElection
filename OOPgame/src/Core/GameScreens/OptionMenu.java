package Core.GameScreens;

import Core.ZhuzheeGame;
import ZhuzheeEngine.Application;
import ZhuzheeEngine.Audios.AudioManager;
import ZhuzheeEngine.Screen;
import ZhuzheeEngine.Scene.NineSliceButton;
import Core.UI.UIButtonFactory;
import ZhuzheeEngine.Scene.NineSliceCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;

public class OptionMenu extends Screen {
    JCheckBox fullscreen = null;
    JSlider soundSlider = null;
    JSlider musicSlider = null;
    
    BufferedImage bgImage;
    BufferedImage btnNormalImg;
    BufferedImage btnHoverImg;

    NineSliceCanvas bgCanvas;

    GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

    public OptionMenu() {
        setLayout(new BorderLayout());

        try {
            bgImage = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/test.png"));
            btnNormalImg = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/btn_normal.png"));
            btnHoverImg  = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/btn_hover.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // พื้นหลังเมนู
        bgCanvas = new NineSliceCanvas(bgImage, 25, 25, 25, 25) {};
        bgCanvas.setLayout(new GridBagLayout());

        // กรอบสีขาวสำหรับใส่ Settings
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBackground(Color.WHITE);
        settingsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 150, 255), 6), // ปรับขอบสีฟ้าให้หนาขึ้น
                BorderFactory.createEmptyBorder(60, 100, 60, 100) // เพิ่มพื้นที่ขอบด้านในให้กว้างขึ้น
        ));

        // หัวข้อ Settings
        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 55f)); // ขยายขนาดฟอนต์หัวข้อใหญ่ขึ้น
        titleLabel.setForeground(new Color(255, 100, 150));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsPanel.add(titleLabel);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // --- GRAPHICS SECTION ---
        JLabel graphicLabel = new JLabel("--- Graphic ---");
        graphicLabel.setFont(graphicLabel.getFont().deriveFont(Font.BOLD, 30f)); // ขยายฟอนต์
        graphicLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsPanel.add(graphicLabel);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel graphicRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0)); // เพิ่มระยะห่างแนวนอน
        graphicRow.setBackground(Color.WHITE);
        JLabel screenLabel = new JLabel("Screen Mode");
        screenLabel.setFont(screenLabel.getFont().deriveFont(26f)); // ขยายฟอนต์
        
        fullscreen = new JCheckBox("Fullscreen");
        fullscreen.setFont(fullscreen.getFont().deriveFont(24f)); // ขยายฟอนต์ Checkbox
        fullscreen.setBackground(Color.WHITE);
        fullscreen.setSelected(true);
        fullscreen.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                enableFullscreen();
            } else {
                disableFullscreen();
            }
        });
        graphicRow.add(screenLabel);
        graphicRow.add(fullscreen);
        settingsPanel.add(graphicRow);
        
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // --- AUDIO SECTION ---
        JLabel audioLabel = new JLabel("--- Audio ---");
        audioLabel.setFont(audioLabel.getFont().deriveFont(Font.BOLD, 30f)); // ขยายฟอนต์
        audioLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsPanel.add(audioLabel);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel sfxRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0)); // เพิ่มระยะห่าง
        sfxRow.setBackground(Color.WHITE);
        JLabel sfxLabel = new JLabel("SFX Volume");
        sfxLabel.setFont(sfxLabel.getFont().deriveFont(26f)); // ขยายฟอนต์
        soundSlider = new JSlider(0, 100, 50);
        soundSlider.setPreferredSize(new Dimension(250, 40)); // ขยายความยาวของตัวเลื่อนระดับเสียง
        soundSlider.setBackground(Color.WHITE);
        soundSlider.addChangeListener(e -> AudioManager.getInstance().setSFXVolume(soundSlider.getValue() / 100f));
        sfxRow.add(sfxLabel);
        sfxRow.add(soundSlider);
        settingsPanel.add(sfxRow);

        settingsPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel musicRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0)); // เพิ่มระยะห่าง
        musicRow.setBackground(Color.WHITE);
        JLabel musicLabel = new JLabel("Music Volume");
        musicLabel.setFont(musicLabel.getFont().deriveFont(26f)); // ขยายฟอนต์
        musicSlider = new JSlider(0, 100, 50);
        musicSlider.setPreferredSize(new Dimension(250, 40)); // ขยายความยาวของตัวเลื่อนระดับเสียง
        musicSlider.setBackground(Color.WHITE);
        musicSlider.addChangeListener(e -> AudioManager.getInstance().setBGMVolume(musicSlider.getValue() / 100f));
        musicRow.add(musicLabel);
        musicRow.add(musicSlider);
        settingsPanel.add(musicRow);

        settingsPanel.add(Box.createRigidArea(new Dimension(0, 60)));

        // ปุ่ม Close / Back
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(Color.WHITE);
        NineSliceButton backBtn = UIButtonFactory.createMenuButton("Close", btnNormalImg, btnHoverImg, e -> {
            AudioManager.getInstance().playSound("click");
            Screen.ChangeScreen(ZhuzheeGame.MAIN_MENU);
        });
        backBtn.addMouseListener(ZhuzheeGame.MOUSE_HOVER_SFX);
        btnPanel.add(backBtn);
        
        settingsPanel.add(btnPanel);

        // นำ Settings เข้าไปตรงกลางหน้าจอ
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        bgCanvas.add(settingsPanel, gbc);

        add(bgCanvas, BorderLayout.CENTER);
        setVisible(true);
    }

    private void enableFullscreen() {
        dispose();
        device.setFullScreenWindow(Application.getMainFrame());
        setVisible(true);
    }

    private void disableFullscreen() {
        device.setFullScreenWindow(null);
        dispose();
        setSize(800, 600); // Set default windowed size
        setVisible(true);
    }
}
