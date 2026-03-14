package Core.GameScreens;

import Core.ZhuzheeGame;
import ZhuzheeEngine.Screen;
import ZhuzheeEngine.Scene.NineSliceCanvas;
import ZhuzheeEngine.Scene.NineSliceButton;
import Core.UI.UIButtonFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class LobbyMenu extends Screen implements ActionListener {

    NineSliceButton createBtn;
    NineSliceButton joinBtn;
    NineSliceButton backBtn;

    BufferedImage bgImage;
    BufferedImage btnNormalImg;
    BufferedImage btnHoverImg;
    NineSliceCanvas bgCanvas;

    public LobbyMenu() {
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

        JLabel title = new JLabel("Game Lobby");
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        bgCanvas.add(title, BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setLayout(new GridLayout(3, 1, 10, 10));

        createBtn = UIButtonFactory.createMenuButton("Create Room", btnNormalImg, btnHoverImg, this);
        joinBtn = UIButtonFactory.createMenuButton("Join Game",   btnNormalImg, btnHoverImg, this);
        backBtn = UIButtonFactory.createMenuButton("Back to Menu",btnNormalImg, btnHoverImg, this);

        btnPanel.add(createBtn);
        btnPanel.add(joinBtn);
        btnPanel.add(backBtn);
        
        centerWrapper.add(btnPanel);

        bgCanvas.add(centerWrapper, BorderLayout.CENTER);
        add(bgCanvas, BorderLayout.CENTER);

    }



    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == createBtn) {
            ZhuzheeGame.CREATE_ROOM_MENU.executeServerStart();
            Screen.ChangeScreen(ZhuzheeGame.CREATE_ROOM_MENU);
        }
        else if (e.getSource() == joinBtn) {
            Screen.ChangeScreen(ZhuzheeGame.JOIN_ROOM_MENU);
        }
        else if (e.getSource() == backBtn) {
            Screen.ChangeScreen(ZhuzheeGame.MAIN_MENU);
        }
    }
}
