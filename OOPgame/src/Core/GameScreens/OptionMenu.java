package Core.GameScreens;

import ZhuzheeEngine.Audios.AudioManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

public class OptionMenu extends JFrame {
    JCheckBox fullscreen = null;
    JSlider soundSlider = null;
    JSlider musicSlider = null;

    GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

    public OptionMenu(){
        setTitle("Settings Menu");
        setSize(450,300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        //fullscreen checkbox
        gbc.gridx = 0;
        gbc.gridy = 0;
        fullscreen = new JCheckBox("Fullscreen");
        fullscreen.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    enableFullscreen();
                } else {
                    disableFullscreen();
                }
            }
        });

        panel.add(fullscreen);
        add(panel);

        //Game Sound
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Game sound"), gbc);

        //game Sound Slider
        gbc.gridx = 1;
        soundSlider = new JSlider(0,100,50);
        soundSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                AudioManager.getInstance().setSFXVolume(soundSlider.getValue() / 100f);
            }
        });
        panel.add(soundSlider, gbc);

        //Music Label
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Music"), gbc);

        //game Sound Slider
        gbc.gridx = 1;
        musicSlider = new JSlider(0,100,50);
            musicSlider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    AudioManager.getInstance().setBGMVolume(musicSlider.getValue() / 100f);
                }
            });
        panel.add(musicSlider, gbc);

        //Back
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        JButton back = new JButton("Back");
        back.addActionListener(e -> dispose());
        panel.add(back, gbc);

        add(panel);
        setVisible(true);
    }
    private void enableFullscreen() {
        dispose();
        setUndecorated(true);
        device.setFullScreenWindow(this);
        setVisible(true);
    }

    private void disableFullscreen() {
        device.setFullScreenWindow(null);
        dispose();
        setUndecorated(false);
        setSize(450, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args){
        new OptionMenu();
    }
}
