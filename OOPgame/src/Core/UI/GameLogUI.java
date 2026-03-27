package Core.UI;

import ZhuzheeEngine.Scene.Canvas;
import ZhuzheeEngine.Scene.Scene2D;
import ZhuzheeEngine.Debug.GameLogger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

public class GameLogUI extends Canvas implements GameLogger.LogListener {

    private final JTextArea textArea;
    private final JScrollPane scrollPane;

    public GameLogUI(Scene2D scene) {
        super(scene);
        
        // กว้าง 300, สูง 200, อยู่มุมขวาล่าง
        setPanelSize(350, 200);
        setAnchors(1, -1); // ยึดมุมขวาล่าง
        setMargins(20, 20, 20, 20); // เว้นขอบจอ

        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(new Color(30, 30, 30, 220));
        setBorder(new LineBorder(new Color(100, 100, 100), 2));

        // หัวข้อ
        JLabel titleLabel = UITool.createLabel("Game Event Logs", 14f);
        titleLabel.setForeground(new Color(200, 200, 200));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
        add(titleLabel, BorderLayout.NORTH);

        // กล่องข้อความ
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(false);
        textArea.setForeground(Color.WHITE);
        textArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        textArea.setBorder(new EmptyBorder(5, 5, 5, 5));

        // เอา Text Area ไปใส่ใน Scroll Pane
        scrollPane = new JScrollPane(textArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        
        // ซ่อน Scrollbar แนวตั้งถ้าไม่ใช้ (หรือให้แสดงอัตโนมัติ)
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // ปรับแต่ง Scrollbar สไตล์โมเดิร์น
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scrollPane.getVerticalScrollBar().setBackground(new Color(50, 50, 50, 150));
        
        add(scrollPane, BorderLayout.CENTER);

        // ดึง Log เก่ามาแสดง (เผื่อมีคน Log ไว้ก่อนสร้าง UI)
        loadExistingLogs();

        // เริ่มรับฟัง Log ใหม่
        GameLogger.addListener(this);

        onResize(scene.getWidth(), scene.getHeight());
        setVisible(true);
    }

    private void loadExistingLogs() {
        List<String> logs = GameLogger.getLogs();
        for (String log : logs) {
            textArea.append(log + "\n");
        }
        scrollToBottom();
    }

    @Override
    public void onLogAdded(String message) {
        SwingUtilities.invokeLater(() -> {
            textArea.append(message + "\n");
            
            // จำกัดจำนวนบรรทัดไม่ให้เกิน 100 บรรทัด (ลดการกินแรม/แลค)
            if (textArea.getLineCount() > 100) {
                try {
                    int end = textArea.getLineEndOffset(0); // เอาตำแหน่งจบบรรทัดแรก
                    textArea.replaceRange("", 0, end); // ลบบรรทัดแรกทิ้ง
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            scrollToBottom();
        });
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        // วาดสีพื้นหลังเองแบบโปร่งแสง เพื่อไม่ให้ Swing วาดซ้อนทับจนกระพริบ
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }
}
