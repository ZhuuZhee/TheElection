package Dummy;/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author WILLY
 */
import ZhuzheeEngine.Audios.AudioManager;
import javax.swing.*;

public class AudioManagerTester extends JFrame {

    public AudioManagerTester() {
        setTitle("Sound Settings Test");
        setSize(300, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // เริ่มเล่น BGM ทันที
        AudioManager.getInstance().playLoop("bgm_main");

        // --- ปุ่ม SFX ---
        JButton button = new JButton("PLAY SFX!");
        button.setBounds(80, 30, 120, 40);
        button.addActionListener(e -> AudioManager.getInstance().playSound("clap"));
        add(button);

        // --- Slider ปรับ BGM ---
        JSlider musicSlider = new JSlider(0, 100, 70);
        musicSlider.setBounds(20, 120, 240, 40);
        musicSlider.addChangeListener(e -> {
            AudioManager.getInstance().setBGMVolume(musicSlider.getValue() / 100f);
        });
        add(musicSlider);

        // --- Slider ปรับ SFX ---
        JSlider sfxSlider = new JSlider(0, 100, 70);
        sfxSlider.setBounds(20, 200, 240, 40);
        sfxSlider.addChangeListener(e -> {
            AudioManager.getInstance().setSFXVolume(sfxSlider.getValue() / 100f);
        });
        add(sfxSlider);

        setVisible(true);
    }
}