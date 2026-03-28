/**
 * @WILLY 8/3/2026 14:10
 */

package ZhuzheeEngine.Audios;

import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * AudioManager: ระบบจัดการเสียงแบบ Singleton
 * 1. โหลดไฟล์เสียงไว้ในหน่วยความจำตอนเริ่มต้น (Preloading)
 * 2. ควบคุมระดับเสียงแยกกลุ่ม BGM และ SFX ได้
 * 3. เรียกใช้ได้จากทุกคลาสในโปรเจกต์
 */
public class AudioManager {
    // Instance เดียวของคลาส (Singleton Pattern)
    private static final AudioManager instance = new AudioManager();

    private final Map<String, Clip> soundMap = new HashMap<>();
    private final Map<Clip, LineListener> listenerMap = new HashMap<>();
    private Clip currentBgmClip;

    private float bgmVolume = 0.5f;
    private float sfxVolume = 0.5f;

    // Private Constructor: ทำการโหลดเสียงทั้งหมดในจุดนี้
    private AudioManager() {
        // เพิ่มไฟล์เสียงที่ต้องการใช้ในเกมที่นี่
        loadSound("clap", "Clap_Your_Hand.WAV");
        loadSound("bgm_main", "guntrum.wav");
    }

    /**
     * ดึง Instance ของ AudioManager ไปใช้งาน
     * Ex: AudioManager.getInstance().playSound("clap");
     */
    public static AudioManager getInstance() {
        return instance;
    }

    /**
     * โหลดไฟล์เสียงเข้าสู่ RAM และเก็บไว้ใน Map. Default Directory คือ `OOPgame\Assets\Sounds`
     * @param name     ชื่อที่ตั้งให้คลิปเสียง (สำหรับเรียกใช้ภายหลัง)
     * @param fileName ชื่อไฟล์เสียงพร้อมนามสกุล (เช่น "jump.wav")
     */
    public void loadSound(String name, String fileName) {
        try {
            // ปรับ Path ให้ตรงกับโครงสร้างโฟลเดอร์ในเครื่อง
            String path = System.getProperty("user.dir") + File.separator + "OOPgame" + File.separator + "Assets" + File.separator + "Sounds" + File.separator + fileName;
            File soundFile = new File(path);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            soundMap.put(name, clip);
        } catch (Exception e) {
            System.err.println("Error loading: " + fileName);
            e.printStackTrace();
        }
    }

    /**
     * ลบเสียงที่โหลดไว้ออกจากหน่วยความจำ (Unload specific sound)
     * @param name ชื่อ Key ของเสียงที่ต้องการลบ
     */
    public void unloadSound(String name) {
        Clip clip = soundMap.remove(name);
        if (clip != null) {
            if (listenerMap.containsKey(clip)) {
                clip.removeLineListener(listenerMap.remove(clip));
            }
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.close(); // ปิด Clip เพื่อคืนทรัพยากรให้กับระบบ
        }
    }

    /**
     * เล่นเสียงแบบครั้งเดียวจบ Ex(SFX)
     */
    public void playSound(String name) {
        Clip clip = soundMap.get(name);
        if (clip != null) {
            updateVolume(clip, sfxVolume);
            clip.stop();
            clip.setFramePosition(0); // รีเซ็ตตำแหน่งเสียงให้เริ่มเล่นจากจุดเริ่มต้น
            clip.start();
        }
    }

    /**
     * เล่นเสียงแบบวนซ้ำต่อเนื่อง Ex(BGM)
     * ถ้ามีการเล่นเพลงเดิมอยู่ จะทำการหยุดเพลงเก่าก่อนเริ่มเพลงใหม่
     */
    public void playLoop(String name) {
        if (currentBgmClip != null && currentBgmClip.isRunning()) currentBgmClip.stop();
        currentBgmClip = soundMap.get(name);
        if (currentBgmClip != null) {
            updateVolume(currentBgmClip, bgmVolume);
            currentBgmClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /**
     * เล่นเพลงแรก 1 รอบจนจบ แล้วเล่นเพลงที่สองวนลูปไปเรื่อยๆ
     */
    public void playOnceThenLoop(String onceName, String loopName) {
        if (currentBgmClip != null && currentBgmClip.isRunning()) currentBgmClip.stop();

        Clip firstClip = soundMap.get(onceName);
        if (firstClip != null) {
            updateVolume(firstClip, bgmVolume);
            firstClip.stop();
            firstClip.setFramePosition(0);

            // ลบ Listener เก่าออกเพื่อกันซ้ำซ้อน
            if (listenerMap.containsKey(firstClip)) {
                firstClip.removeLineListener(listenerMap.remove(firstClip));
            }

            LineListener listener = new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP) {
                        firstClip.removeLineListener(this);
                        listenerMap.remove(firstClip);
                        // เล่นจบเพลงจริงๆ (ไม่ใช่ถูกกดข้าม) ค่อยเล่น loop song
                        if (firstClip.getFramePosition() >= firstClip.getFrameLength()) {
                            playLoop(loopName);
                        }
                    }
                }
            };
            firstClip.addLineListener(listener);
            listenerMap.put(firstClip, listener);

            currentBgmClip = firstClip;
            firstClip.start();
        } else {
            // ถ้าเพลงแรกไม่มี ให้เล่นเพลง loop เลย
            playLoop(loopName);
        }
    }

    /**
     * ปรับระดับเสียงเพลงประกอบ (BGM)
     */
    public void setBGMVolume(float volume) {
        this.bgmVolume = Math.max(0.0f, Math.min(1.0f, volume));
        if (currentBgmClip != null) updateVolume(currentBgmClip, this.bgmVolume);
    }

    /**
     * ปรับระดับเสียงเอฟเฟกต์ (SFX)
     */
    public void setSFXVolume(float volume) {
        this.sfxVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }

    // คำนวณค่า Decibels สำหรับ FloatControl (แปลงค่า 0-1 เป็น dB)
    private void updateVolume(Clip clip, float volume) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume <= 0.0 ? 0.0001 : volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
    }
}