import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class CharacterSelectUI extends JFrame {

    //Dynamic character data (เพิ่ม/ลดได้)
    private static final String[] CHAR_NAMES = {"Human", "Mushroom", "Alien", "Cat"};

    //Dynamic card data (เพิ่ม/ลดได้)
    private static final String[] CARD_NAMES = {
            "Card 1", "Card 2", "Card 3", "Card 4", "Card 5", "Card 6"
    };

    //Team colors
    private static final Color[] TEAM_COLORS = {
            Color.RED, new Color(255,140,0), Color.YELLOW, Color.GREEN,
            Color.CYAN, Color.BLUE, new Color(180,0,255), Color.PINK
    };

    //State
    private int   selChar  = -1;
    private int   selCard  = -1;
    private Color selColor = null;

    //UI refs
    private List<JPanel> charCells  = new ArrayList<>();
    private List<JPanel> cardCells  = new ArrayList<>();
    private List<JPanel> colorCells = new ArrayList<>();
    private JPanel     previewBox;
    private JLabel     previewLbl;
    private JTextField nameField;

    public CharacterSelectUI() {
        super("Choose your Character");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setMinimumSize(new Dimension(900, 500));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        //Title
        JLabel title = new JLabel("Select your Character", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.PLAIN, 30));
        title.setBorder(BorderFactory.createEmptyBorder(18, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        //Body: left + right
        JPanel body = new JPanel(new GridLayout(1, 2, 16, 0));
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(8, 20, 20, 20));
        body.add(buildCharGrid());
        body.add(buildRightPanel());
        add(body, BorderLayout.CENTER);

        setVisible(true);
    }

    //Left: dynamic 2×2 character grid
    private JPanel buildCharGrid() {
        int cols = 2;
        int rows = (int) Math.ceil((double) CHAR_NAMES.length / cols);
        JPanel grid = new JPanel(new GridLayout(rows, cols, 8, 8));
        grid.setBackground(Color.WHITE);

        for (int i = 0; i < CHAR_NAMES.length; i++) {
            final int idx = i;
            JPanel cell = new JPanel(new BorderLayout());
            cell.setPreferredSize(new Dimension(110, 110));
            cell.setBackground(new Color(220, 220, 220)); 
            cell.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            cell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel lbl = new JLabel(CHAR_NAMES[i], SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.PLAIN, 13));
            cell.add(lbl, BorderLayout.CENTER);

            cell.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    selChar = idx;
                    refreshChars();
                    updatePreview();
                }
            });
            charCells.add(cell);
            grid.add(cell);
        }
        return grid;
    }

    //Right panel
    private JPanel buildRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(235, 235, 235));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        //Preview row
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        topRow.setBackground(new Color(235, 235, 235));
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        previewBox = new JPanel(new BorderLayout());
        previewBox.setPreferredSize(new Dimension(60, 60));
        previewBox.setBackground(Color.WHITE);
        previewBox.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        previewLbl = new JLabel("", SwingConstants.CENTER);
        previewLbl.setFont(new Font("Arial", Font.PLAIN, 10));
        previewBox.add(previewLbl, BorderLayout.CENTER);
        topRow.add(previewBox);

        //Name + Color stacked
        JPanel nameColorCol = new JPanel();
        nameColorCol.setLayout(new BoxLayout(nameColorCol, BoxLayout.Y_AXIS));
        nameColorCol.setBackground(new Color(235, 235, 235));

        //Name row
        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        nameRow.setBackground(new Color(235, 235, 235));
        nameRow.add(new JLabel("Your Name"));
        nameField = new JTextField(12);
        nameRow.add(nameField);
        nameColorCol.add(nameRow);

        //Color row
        JPanel colorRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        colorRow.setBackground(new Color(235, 235, 235));
        colorRow.add(new JLabel("Color"));
        for (int i = 0; i < TEAM_COLORS.length; i++) {
            final int idx = i;
            JPanel sw = new JPanel();
            sw.setPreferredSize(new Dimension(24, 24));
            sw.setBackground(TEAM_COLORS[i]);
            sw.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            sw.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            sw.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    selColor = TEAM_COLORS[idx];
                    refreshColors();
                }
            });
            colorCells.add(sw);
            colorRow.add(sw);
        }
        nameColorCol.add(colorRow);
        topRow.add(nameColorCol);
        panel.add(topRow);

        panel.add(Box.createVerticalStrut(8));
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(sep);
        panel.add(Box.createVerticalStrut(8));

        //Arcana Card label
        JLabel arcLbl = new JLabel("Arcana Card");
        arcLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        arcLbl.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(arcLbl);
        panel.add(Box.createVerticalStrut(6));

        //Dynamic card grid (3 per row)
        int cardCols = 3;
        int cardRows = (int) Math.ceil((double) CARD_NAMES.length / cardCols);
        JPanel cardGrid = new JPanel(new GridLayout(cardRows, cardCols, 6, 6));
        cardGrid.setBackground(new Color(235, 235, 235));
        cardGrid.setAlignmentX(CENTER_ALIGNMENT);
        cardGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, cardRows * 116));

        for (int i = 0; i < CARD_NAMES.length; i++) {
            final int idx = i;
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            card.setPreferredSize(new Dimension(80, 110));
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel clbl = new JLabel(CARD_NAMES[i], SwingConstants.CENTER);
            clbl.setFont(new Font("Arial", Font.PLAIN, 11));
            card.add(clbl, BorderLayout.CENTER);

            card.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    selCard = idx;
                    refreshCards();
                }
            });
            cardCells.add(card);
            cardGrid.add(card);
        }
        panel.add(cardGrid);
        panel.add(Box.createVerticalStrut(10));

        //Confirm
        JButton confirm = new JButton("Confirm");
        confirm.setAlignmentX(CENTER_ALIGNMENT);

        panel.add(confirm);

        return panel;
    }

    //Refresh helpers
    private void refreshChars() {
        for (int i = 0; i < charCells.size(); i++)
            charCells.get(i).setBorder(BorderFactory.createLineBorder(
                    i == selChar ? Color.BLACK : Color.GRAY, i == selChar ? 2 : 1));
    }

    private void refreshCards() {
        for (int i = 0; i < cardCells.size(); i++)
            cardCells.get(i).setBorder(BorderFactory.createLineBorder(
                    i == selCard ? Color.BLACK : Color.GRAY, i == selCard ? 2 : 1));
    }

    private void refreshColors() {
        for (int i = 0; i < colorCells.size(); i++)
            colorCells.get(i).setBorder(BorderFactory.createLineBorder(
                    TEAM_COLORS[i].equals(selColor) ? Color.BLACK : Color.GRAY,
                    TEAM_COLORS[i].equals(selColor) ? 2 : 1));
    }

    private void updatePreview() {
        if (selChar >= 0) {
            previewLbl.setText(CHAR_NAMES[selChar]);
            previewBox.repaint();
        }
    }

    //Entry point
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CharacterSelectUI::new);
    }
}