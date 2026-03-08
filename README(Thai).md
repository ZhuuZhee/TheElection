# ZhuzheeEngine (จู้จี้เอนจิ้น)

ZhuzheeEngine เป็นเอนจิ้นเกม 2 มิติขนาดเล็กสำหรับ Java Swing ถูกออกแบบมาเพื่อการสร้างต้นแบบอย่างรวดเร็วและสร้างเกมที่ไม่ซับซ้อน มีโครงสร้างพื้นฐานสำหรับจัดการ Game Loop, Screen, Scene และ Game Object

## สถาปัตยกรรมระดับสูง

- **`ZhuzheeEngine.Application`**: ส่วนหลักของแอปพลิเคชัน ทำหน้าที่สร้างและจัดการ `JFrame` หลัก, รัน Game Loop และส่งต่ออีเวนต์ของวงจรชีวิต (`create`, `render`, `dispose`) ไปยัง `ApplicationAdapter` หลัก
- **`ZhuzheeEngine.ApplicationAdapter`**: อินเทอร์เฟซที่กำหนดเมธอดวงจรชีวิตของแอปพลิเคชัน คลาสเกมหลักของคุณจะ implement อินเทอร์เฟซนี้
- **`ZhuzheeEngine.ScreenManager`**: จัดการการเปลี่ยนหน้าจอ (Screen) ในแอปพลิเคชันของคุณ จัดการการเปลี่ยนผ่านระหว่างสถานะต่างๆ ของเกม (เช่น เมนูหลัก, เกมเพลย์, เกมโอเวอร์)
- **`ZhuzheeEngine.Screen`**: `JPanel` ที่แสดงถึงหน้าจอเดียวในเกม อาจเป็นเมนู, ด่าน หรือส่วนอื่นๆ ที่แยกจากกันของแอปพลิเคชัน
- **`ZhuzheeEngine.Scene.Scene2D`**: `Screen` แบบพิเศษที่จัดการและเรนเดอร์รายการของ `SceneObject` จัดเตรียมโลก 2 มิติพร้อมระบบพิกัดที่มีศูนย์กลางอยู่ที่หน้าจอ
- **`ZhuzheeEngine.Scene.SceneObject`**: อินเทอร์เฟซสำหรับอ็อบเจกต์ใดๆ ที่สามารถวางใน `Scene2D` ได้
- **`ZhuzheeEngine.Scene.GameObject`**: การ υλοποίησηที่เป็นรูปธรรมของ `SceneObject` เป็นคลาสพื้นฐานสำหรับทุกเอนทิตีในโลกของเกมของคุณ

## การเริ่มต้นใช้งาน

### ข้อกำหนดเบื้องต้น

- Java JDK 8 หรือใหม่กว่า
- Java IDE เช่น IntelliJ IDEA, Eclipse หรือ VS Code พร้อมส่วนขยาย Java

### การรันโปรเจกต์

1.  Clone a repository.
2.  เปิดโปรเจกต์ใน IDE ของคุณ
3.  ตั้งค่าคลาสหลักเป็น `Main.java`
4.  รันแอปพลิเคชัน

## การสร้างเกมของคุณเอง

ในการสร้างเกมด้วย ZhuzheeEngine คุณจะทำตามขั้นตอนเหล่านี้:

### 1. สร้างคลาสเกม

สร้างคลาสที่ implement `ApplicationAdapter` ซึ่งจะเป็นจุดเริ่มต้นหลักสำหรับตรรกะของเกมของคุณ

```java
import ZhuzheeEngine.ApplicationAdapter;
import ZhuzheeEngine.ScreenManager;
import ZhuzheeEngine.Scene.Scene2D;

public class MyGame implements ApplicationAdapter {
    private ScreenManager screenManager;
    private Scene2D mainScene;

    @Override
    public void create() {
        mainScene = new Scene2D();
        screenManager = new ScreenManager();
        screenManager.ChangeScreen(mainScene);
        // ... เริ่มต้นอ็อบเจกต์เกมของคุณที่นี่
    }

    @Override
    public void render() {
        // เมธอดนี้ถูกเรียกทุกเฟรม
    }

    @Override
    public void dispose() {
        // ทำความสะอาดทรัพยากร
    }

    @Override
    public void resize(int width, int height) {
        // จัดการการปรับขนาดหน้าต่าง
    }
}
```

### 2. เปิดแอปพลิเคชัน

ในเมธอด `main` ของคุณ สร้างอินสแตนซ์ของคลาสเกมของคุณและส่งต่อไปยัง `Application.LuchApp()`

```java
import ZhuzheeEngine.Application;

public class Main {
    public static void main(String[] args) {
        Application.LaunchApp(new MyGame());
    }
}
```

### 3. สร้าง GameObjects

สร้างคลาสที่ขยาย `GameObject` เพื่อแสดงถึงเอนทิตีในเกมของคุณ

```java
import ZhuzheeEngine.Scene.GameObject;
import java.awt.*;

public class Player extends GameObject {
    public Player(int x, int y) {
        super(x, y, 50, 50); // x, y, ความกว้าง, ความสูง
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(position.x, position.y, size.width, size.height);
    }
}
```

จากนั้นในคลาส `MyGame` ของคุณ คุณสามารถเพิ่มพวกมันเข้าไปในซีนได้:

```java
// ใน MyGame.create()
Player player = new Player(0, 0);
```

`GameObject` จะถูกเพิ่มเข้าไปใน `Scene2D` โดยอัตโนมัติเมื่อถูกสร้างขึ้น

## ฟีเจอร์ของเอนจิ้น

### Game Loop

คลาส `Application` จัดการ Game Loop แบบ Fixed-step คุณสามารถตั้งค่าเฟรมเรตเป้าหมายได้ด้วย `Application.SetTargetFrameRate()` ค่าเริ่มต้นคือ 60 FPS คุณสามารถรับเวลาระหว่างเฟรมได้โดยใช้ `Application.getDeltaTime()`

### ซีนและพิกัด

`Scene2D` มี Scene Graph แบบง่าย อ็อบเจกต์เกมจะถูกเรนเดอร์ตาม `zIndex` ของมัน คุณสามารถแปลงระหว่างพิกัดหน้าจอและพิกัดโลกได้โดยใช้ `Scene2D.Screen2WorldPoint()` และ `Scene2D.World2ScreenPoint()` จุดกำเนิดของโลก (0,0) อยู่ที่กึ่งกลางของหน้าจอ

### การรับข้อมูล (Input)

เอนจิ้นไม่มีระบบการรับข้อมูลในตัว คุณสามารถเพิ่ม Mouse และ Key Listener ของคุณเองลงใน `Screen` ของคุณได้ `Core.Player.MouseHandler` ในโปรเจกต์เป็นตัวอย่างของวิธีการจัดการการรับข้อมูลจากเมาส์สำหรับ `GameObject`

### การจัดการหน้าจอ (Screen Management)

`ScreenManager` จัดการการเปลี่ยนผ่านระหว่างหน้าจอ หากต้องการเปลี่ยนหน้าจอ เพียงแค่เรียก `screenManager.ChangeScreen(newNextScreen)` เมธอดนี้จะเรียก `onScreenExit()` บนหน้าจอปัจจุบันและ `onScreenEnter()` บนหน้าจอใหม่
