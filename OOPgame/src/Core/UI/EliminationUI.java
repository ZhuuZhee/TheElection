package Core.UI;

import Core.Player.Player;
import ZhuzheeEngine.Scene.Canvas;
import ZhuzheeEngine.Scene.Scene2D;
import ZhuzheeEngine.Scene.NineSliceButton;
import java.awt.image.BufferedImage;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EliminationUI extends Canvas {
    private List<Player> players;
    private final JPanel panel;

//    public EliminationUI(Scene2D scene, ArrayList<Player> players) {
    public EliminationUI(Scene2D scene) {
        super(scene);
        this.players = new ArrayList<>();
        this.players.add(new Player("1", "P'Few", true));
        this.players.add(new Player("2", "Xynezter", false));
        this.players.add(new Player("3", "Thana", false));
        this.players.add(new Player("4", "KUY", false));

        setPanelSize(600, 500);
        setLayout(new BorderLayout());

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(20, 20, 40));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.RED, 3),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        add(panel, BorderLayout.CENTER);
        buildUI();
    }

    private void buildUI() {
        BufferedImage btnNormalImg = null;
        BufferedImage btnHoverImg = null;
        try {
            btnNormalImg = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/btn_normal.png"));
            btnHoverImg = javax.imageio.ImageIO.read(new java.io.File("OOPgame/Assets/UI/btn_hover.png"));
        } catch (Exception e) {}

        JLabel titleLabel = new JLabel("ELIMINATION PHASE");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 40f));
        titleLabel.setForeground(Color.RED);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        JLabel subLabel = new JLabel("THE LOWEST RANKING PLAYER IS DISQUALIFIED");
        subLabel.setForeground(Color.LIGHT_GRAY);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subLabel);
        panel.add(Box.createVerticalStrut(25));

        for (int i = 0; i < players.size(); i++) {
            boolean isLast = (i == players.size() - 1);
            panel.add(createSquareRow(players.get(i), i + 1, isLast));
            panel.add(Box.createVerticalStrut(8));
        }

        panel.add(Box.createVerticalGlue());

        if (btnNormalImg != null && btnHoverImg != null) {
            NineSliceButton dismissBtn = UIButtonFactory.createMenuButton("DISMISS", btnNormalImg, btnHoverImg, e -> setVisible(false));
            dismissBtn.setPreferredSize(new Dimension(200, 45));
            dismissBtn.setMaximumSize(new Dimension(200, 45));
            dismissBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(dismissBtn);
        } else {
            JButton dismissBtn = new JButton("DISMISS");
            dismissBtn.setFocusPainted(false);
            dismissBtn.setBackground(Color.WHITE);
            dismissBtn.setForeground(Color.BLACK);
            dismissBtn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
            dismissBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            dismissBtn.addActionListener(e -> setVisible(false));
            panel.add(dismissBtn);
        }
    }

    private JPanel createSquareRow(Player p, int rank, boolean isEliminated) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setMaximumSize(new Dimension(500, 55));
        row.setBackground(isEliminated ? new Color(100, 0, 0) : new Color(40, 40, 60));
        row.setBorder(BorderFactory.createLineBorder(isEliminated ? Color.RED : Color.DARK_GRAY, 1));

        // Rank
        JLabel rankLbl = new JLabel(" " + rank + " ");
        rankLbl.setFont(rankLbl.getFont().deriveFont(Font.BOLD, 30f));
        rankLbl.setForeground(isEliminated ? Color.RED : Color.WHITE);
        row.add(rankLbl, BorderLayout.WEST);

        // Status
        String status = isEliminated ? "ELIMINATED" : "QUALIFIED";
        JLabel infoLbl = new JLabel(p.getPlayerName().toUpperCase() + " (" + status + ")");
        infoLbl.setForeground(Color.WHITE);
        row.add(infoLbl, BorderLayout.CENTER);

        // Stats
        JLabel statsLbl = new JLabel( "0 CITIES ");
        statsLbl.setForeground(Color.LIGHT_GRAY);
        row.add(statsLbl, BorderLayout.EAST);

        return row;
    }

    @Override
    protected void updateBounds(int w, int h) {
        setBounds((w - 600) / 2, (h - 500) / 2, 600, 500);
    }
}
