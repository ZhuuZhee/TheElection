# ZhuzheeEngine

ZhuzheeEngine คือเกมเอนจิน 2D น้ำหนักเบา (Lightweight) สำหรับ Java Swing ออกแบบมาเพื่อการทำต้นแบบ (Rapid Prototyping) และการสร้างเกมที่ไม่ซับซ้อน โดยตัวเอนจินมีโครงสร้างพื้นฐานสำหรับจัดการ Game Loop, หน้าจอ (Screens), ฉาก (Scenes) และวัตถุในเกม (Game Objects)

- ### สถาปัตยกรรมระดับสูง (High-level architecture)

  - **`ZhuzheeEngine.Application`**: หัวใจหลักของแอปพลิเคชัน ทำหน้าที่สร้างและจัดการ `JFrame`, รัน Game Loop และส่งต่อเหตุการณ์วงจรชีวิต (`create`, `render`, `dispose`) ไปยัง `ApplicationAdapter` หลัก
  - **`ZhuzheeEngine.ApplicationAdapter`**: อินเทอร์เฟซที่กำหนด Method ของวงจรชีวิตแอปพลิเคชัน โดยคลาสหลักของเกมคุณจะต้อง Implement อินเทอร์เฟซนี้
  - **`ZhuzheeEngine.ScreenManager`**: จัดการลำดับการแสดงผลของหน้าจอในแอปพลิเคชัน รับผิดชอบการเปลี่ยนสถานะเกม (เช่น จากเมนูหลัก ไปยังหน้าเล่นเกม หรือหน้า Game Over)
  - **`ZhuzheeEngine.Screen`**: `JPanel` ที่แทนหน้าจอหนึ่งหน้าจอในเกม อาจเป็นเมนู ระดับเลเวล หรือส่วนอื่น ๆ ของแอปพลิเคชัน
  - **`ZhuzheeEngine.Scene.Scene2D`**: `Screen` รูปแบบพิเศษที่จัดการและเรนเดอร์รายการของ `SceneObject` โดยมีระบบพิกัด 2D ที่มีจุดศูนย์กลางอยู่ที่กลางหน้าจอ
  - **`ZhuzheeEngine.Scene.SceneObject`**: อินเทอร์เฟซสำหรับวัตถุใด ๆ ที่สามารถวางลงใน `Scene2D` ได้
  - **`ZhuzheeEngine.Scene.GameObject`**: คลาสที่ Implement มาจาก `SceneObject` เพื่อใช้งานจริง เป็นคลาสฐานสำหรับเอนทิตี (Entities) ทั้งหมดในโลกของเกมคุณ
  - **`ZhuzheeEngine.Audios.AudioManager`**: บริการแบบ Singleton สำหรับควบคุมเสียงในระดับ Global จัดการการโหลด Asset ล่วงหน้า และจัดการระดับเสียง BGM/SFX แยกกัน โดยอยู่นอกเหนือโครงสร้างลำดับชั้นของ Scene
- ### Zhuzhee.Utills
  - **`Vector2`,`Vector3`**: คลาสสำหรับช่วยคำนวนเกี่ยวกับ Vector ใช้ใน `SceneObject.getPosition`
## การเริ่มต้นใช้งาน

### สิ่งที่จำเป็นต้องมี (Prerequisites)

- Java JDK 8 หรือใหม่กว่า
- Java IDE เช่น IntelliJ IDEA, Eclipse หรือ VS Code พร้อมส่วนเสริม Java

### การรันโปรเจกต์

1. Clone repository นี้
2. เปิดโปรเจกต์ใน IDE ของคุณ
3. ตั้งค่า Main Class ไปที่ `Main.java`
4. รันแอปพลิเคชัน

## การสร้างเกมของคุณเอง

ในการสร้างเกมด้วย ZhuzheeEngine ให้ปฏิบัติตามขั้นตอนดังนี้:

### 1. สร้างคลาสเกม (Game Class)

สร้างคลาสที่ Implement `ApplicationAdapter` เพื่อเป็นจุดเริ่มต้นหลักสำหรับตรรกะของเกม

```java
import ZhuzheeEngine.ApplicationAdapter;
import ZhuzheeEngine.ScreenManager;
import ZhuzheeEngine.Scene.Scene2D;

public class MyGame implements ApplicationAdapter {
    private Scene2D mainScene;

    @Override
    public void create() {
        mainScene = new Scene2D();
        Screen.ChangeScreen(mainScene);
        // ... เริ่มต้นสร้างวัตถุในเกมของคุณที่นี่
    }

    @Override
    public void render() {
        // ส่วนนี้จะถูกเรียกใช้งานในทุกเฟรม
    }

    @Override
    public void dispose() {
        // คืนค่าทรัพยากรต่างๆ
    }

    @Override
    public void resize(int width, int height) {
        // จัดการเมื่อมีการปรับขนาดหน้าต่าง
    }
}

```

### 2. เรียกใช้แอปพลิเคชัน (Launch Application)

ใน Method `main` ให้สร้าง Instance ของคลาสเกมที่คุณสร้างขึ้นและส่งไปยัง `Application.LaunchApp()`

```java
import ZhuzheeEngine.Application;

public class Main {
    public static void main(String[] args) {
        Application.LaunchApp(new MyGame());
    }
}

```

### 3. สร้าง GameObject

สร้างคลาสที่ Extend `GameObject` เพื่อแทนเอนทิตีต่าง ๆ ในเกม

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

จากนั้นในคลาส `MyGame` คุณสามารถเพิ่มพวกมันเข้าไปใน Scene ได้:

```java
// ใน MyGame.create()
public void create() {
    Player player = new Player(0, 0);
}

```

`GameObject` จะถูกเพิ่มเข้าไปใน `Scene2D` โดยอัตโนมัติเมื่อมีการสร้างขึ้น

## ฟีเจอร์ของเอนจิน (Engine Features)

### Game Loop

คลาส `Application` จะจัดการ Game Loop แบบ Fixed-step คุณสามารถตั้งค่า Frame Rate ที่ต้องการได้ด้วย `Application.SetTargetFrameRate()` (ค่าเริ่มต้นคือ 60 FPS) และสามารถเรียกดูเวลาที่ใช้ระหว่างเฟรมได้ผ่าน `Application.getDeltaTime()`

### Scene และระบบพิกัด (Coordinates)

`Scene2D` มีระบบ Scene Graph อย่างง่าย วัตถุในเกมจะถูกเรนเดอร์ตามค่า `zIndex` คุณสามารถแปลงพิกัดระหว่างหน้าจอ (Screen) และโลกในเกม (World) ได้โดยใช้ `Scene2D.Screen2WorldPoint()` และ `Scene2D.World2ScreenPoint()` โดยจุดกำเนิดของโลก (0,0) จะอยู่ที่จุดศูนย์กลางของหน้าจอ
`GameObject` จะถูก `Scene2D` เรียกใช้ `update()` เมื่อ `getEnable() == true` จะถูกเรียกใช้ `render(Graphic g)` เมื่อ `getVisible() == true`;
### การรับข้อมูล (Input)

เอนจินนี้ไม่มีระบบ Input สำเร็จรูปในตัว คุณสามารถเพิ่ม Mouse Listener หรือ Key Listener ของคุณเองลงใน `Screen` ได้ ทั้งนี้ในโปรเจกต์มี `Core.Player.MouseHandler` เป็นตัวอย่างการจัดการ Input เมาส์สำหรับ `GameObject`

### การจัดการหน้าจอ (Screen Management)

`ScreenManager` ทำหน้าที่จัดการการเปลี่ยนหน้าจอ หากต้องการเปลี่ยนหน้าจอ ให้เรียกใช้ `screenManager.ChangeScreen(newNextScreen)` ซึ่งจะเรียก `onScreenExit()` ของหน้าจอปัจจุบัน และ `onScreenEnter()` ของหน้าจอใหม่โดยอัตโนมัติ

---

## โปรโตคอลการทดสอบโปรเจกต์ (Project Testing Protocol)

### Dummy Package

สำหรับการทดสอบสิ่งต่างๆ คุณ **ต้อง** สร้าง Method ใหม่ในคลาส `Dummy.Tester` เท่านั้น

```java

public class Tester {
    // ตัวอย่างสำหรับการทดสอบ Core.UI ใหม่
    public static void SampleCanvasTest() {
        new SampleCanvasUI();
    }
}

```

จากนั้นใน `Core.ZhuzheeGame` ให้เรียก Method ทดสอบของคุณ

```java
public class ZhuzheeGame implements ApplicationAdapter{

    public void create() {
        // สร้างหน้าจอหลัก
        MAIN_SCENE = new Scene2D();
        Screen.ChangeScreen(MAIN_SCENE);

        // เรียก Method สำหรับทดสอบของคุณ
        Tester.SampleCanvasTest();
    }
}

```

---

คุณต้องการให้ฉันช่วยอธิบายส่วนไหนของโค้ดเพิ่มเติม หรือสร้างตัวอย่างคลาส GameObject แบบที่ซับซ้อนขึ้นไหมครับ?

## การสร้าง Policy Card

### Create Class and extands PassiveCard implements PolicyCard

````java
// สืบทอด PassiveCard และใช้ Interface PolicyCard
``` java
public abstract class PassiveCard  {
//...
@Override
public void onActionCardPlayed(ActionCard playedCard, Citybanna city) {
// put business logic in here
}

    @Override
    protected boolean isDroppable(Object bottom) {
        // ให้วางทับ CardSlot ได้
        return bottom instanceof CardSlot;
    }
}
````

---
## Error Reports

### **`ConcurrentModificationException`** (@Munin)
คือ Error ที่เกิดขึ้นเมื่อคุณพยายาม **"แก้ไข"** ข้อมูลใน List (เช่น เพิ่มหรือลบข้อมูล) ในขณะที่กำลัง **"วน Loop" (Iterate)** อ่านข้อมูลใน List นั้นอยู่ครับ
เปรียบเทียบง่าย ๆ เหมือนคุณกำลัง **นับจำนวนคนในแถว** แต่ในขณะที่คุณกำลังนับ มีคนกระโดดออกจากแถว หรือมีคนใหม่วิ่งเข้ามาแทรก ทำให้ตัวเลขที่คุณกำลังนับ "รวน" และระบบ Java จะเลือกหยุดการทำงานทันทีเพื่อป้องกันข้อมูลพัง
### ทำไมในโค้ด Game ของคุณถึงมีโอกาสเกิด?
ในระบบ Game Loop มักจะมีเหตุการณ์แบบนี้เกิดขึ้น:
1. **Thread การวาด (Render):** กำลังวน Loop `sceneObjects` เพื่อวาดรูปลงหน้าจอ
2. **Thread การคำนวณ (Update/Logic):** ในวินาทีเดียวกันนั้น กระสุนวิ่งไปชนศัตรู ทำให้ศัตรูตัวนั้นต้องถูกลบออกจาก `sceneObjects`
เมื่อมีการ **ลบ (Remove)** ขณะที่ฝั่ง Render ยังวาดไม่เสร็จ Java จะพ่น `ConcurrentModificationException` ออกมาทันที
### วิธีแก้ที่คุณใช้ (Copying the List)
โค้ดที่คุณคอมเมนต์ไว้ว่า `-AI` ใช้วิธี **"ถ่ายเอกสาร"** ครับ:
```java
ArrayList<SceneObject> objectsCopy = new ArrayList<>(sceneObjects);
```
* **หลักการ:** คุณสร้าง List ใหม่ที่เป็นสำเนาของ `sceneObjects` ณ วินาทีนั้นขึ้นมา
* **ผลลัพธ์:** แม้ว่าในขณะที่วาด จะมีการเพิ่มหรือลบ Object ใน `sceneObjects` ตัวจริง (ต้นฉบับ) แต่มันจะไม่ส่งผลกระทบต่อ `objectsCopy` (ใบปลิวที่ก๊อปมาแล้ว) ทำให้ Loop วาดจนจบได้อย่างปลอดภัย
---
### วิธีแก้ทางเลือกอื่น ๆ
นอกจากวิธี Copy แล้ว ยังมีวิธีที่นิยมในงาน Game Development อีก 2 วิธีครับ:
1. **ใช้ `CopyOnWriteArrayList**`:
   เป็น List ประเภทพิเศษที่ออกแบบมาเพื่อปัญหานี้โดยเฉพาะ ทุกครั้งที่มีการแก้ไข มันจะก๊อปปี้ตัวเองให้อัตโนมัติ (แต่จะกิน RAM และ CPU หน่อยถ้ามีการแก้ไขบ่อยมาก)
```java
List<SceneObject> sceneObjects = new CopyOnWriteArrayList<>();
```

**สรุป:** โค้ดที่คุณส่งมานั้นแก้ปัญหาได้ถูกต้องแล้วครับ เป็นวิธีมาตรฐานสำหรับการแยกส่วนแสดงผลออกจากส่วนข้อมูลเพื่อความปลอดภัย (Thread-safety)