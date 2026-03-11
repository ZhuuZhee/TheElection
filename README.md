# ZhuzheeEngine

ZhuzheeEngine is a lightweight 2D game engine for Java Swing, designed for rapid prototyping and building simple games. It provides a basic structure for managing game loops, screens, scenes, and game objects.

## High-level architecture

- **`ZhuzheeEngine.Application`**: The core of the application. It creates and manages the main `JFrame`, runs the game loop, and delegates lifecycle events (`create`, `render`, `dispose`) to a root `ApplicationAdapter`.
- **`ZhuzheeEngine.ApplicationAdapter`**: An interface that defines the application's lifecycle methods. Your main game class will implement this.
- **`ZhuzheeEngine.ScreenManager`**: Manages the flow of screens in your application. It handles transitions between different game states (e.g., main menu, gameplay, game over).
- **`ZhuzheeEngine.Screen`**: A `JPanel` that represents a single screen in the game. It can be a menu, a level, or any other distinct part of the application.
- **`ZhuzheeEngine.Scene.Scene2D`**: A specialized `Screen` that manages and renders a list of `SceneObject`s. It provides a 2D world with a coordinate system centered on the screen.
- **`ZhuzheeEngine.Scene.SceneObject`**: An interface for any object that can be placed in a `Scene2D`.
- **`ZhuzheeEngine.Scene.GameObject`**: A concrete implementation of `SceneObject`. It's the base class for all entities in your game world.
- **`ZhuzheeEngine.Audios.AudioManager`**: : A Singleton service providing global audio control. It handles asset preloading and independent BGM/SFX volume management outside the scene hierarchy.

## Getting Started

### Prerequisites

- Java JDK 8 or newer.
- A Java IDE like IntelliJ IDEA, Eclipse, or VS Code with Java extensions.

### Running the Project

1.  Clone the repository.
2.  Open the project in your IDE.
3.  Set the main class to `Main.java`.
4.  Run the application.

## Creating Your Own Game

To create a game with ZhuzheeEngine, you'll follow these steps:

### 1. Create a Game Class

Create a class that implements `ApplicationAdapter`. This will be the main entry point for your game logic.

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
        // ... initialize your game objects here
    }

    @Override
    public void render() {
        // This is called every frame
    }

    @Override
    public void dispose() {
        // Clean up resources
    }

    @Override
    public void resize(int width, int height) {
        // Handle window resize
    }
}
```

### 2. Launch the Application

In your `main` method, instantiate your game class and pass it to `Application.LuchApp()`.

```java
import ZhuzheeEngine.Application;

public class Main {
    public static void main(String[] args) {
        Application.LaunchApp(new MyGame());
    }
}
```

### 3. Create GameObjects

Create classes that extend `GameObject` to represent the entities in your game.

```java
import ZhuzheeEngine.Scene.GameObject;
import java.awt.*;

public class Player extends GameObject {
    public Player(int x, int y) {
        super(x, y, 50, 50); // x, y, width, height
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(position.x, position.y, size.width, size.height);
    }
}
```

Then, in your `MyGame` class, you can add them to the scene:

```java
// in MyGame.create()
public void creat() {
    Player player = new Player(0, 0);
}
```

The `GameObject` will automatically be added to the `Scene2D` when it's created.

## Engine Features

### Game Loop

The `Application` class manages a fixed-step game loop. You can set the target frame rate with `Application.SetTargetFrameRate()`. The default is 60 FPS. You can get the time between frames using `Application.getDeltaTime()`.

### Scene and Coordinates

`Scene2D` provides a simple scene graph. Game objects are rendered based on their `zIndex`. You can convert between screen coordinates and world coordinates using `Scene2D.Screen2WorldPoint()` and `Scene2D.World2ScreenPoint()`. The world origin (0,0) is at the center of the screen.

### Input

The engine does not have a built-in input system. You can add your own mouse and key listeners to your `Screen`s. The `Core.Player.MouseHandler` in the project provides an example of how to handle mouse input for `GameObject`s.

### Screen Management

The `ScreenManager` handles transitions between screens. To change screens, simply call `screenManager.ChangeScreen(newNextScreen)`. This will call `onScreenExit()` on the current screen and `onScreenEnter()` on the new one.


## Project Testing Protocol

### Dummy Package
for testing any things you **MUST** create new method in `Dummy.Tester` Class.
```java

public class Tester {
    // example for testing new Core.UI
    public static void SampleCanvasTest() {
        new SampleCanvasUI();
    }
}
```
then in `Core.ZhuzheeGame` call your method for testing
```java
public class ZhuzheeGame implements ApplicationAdapter{

    public void create() {
        //create main screen
        MAIN_SCENE = new Scene2D();
        Screen.ChangeScreen(MAIN_SCENE);
        //your testing method
        Tester.SampleCanvasTest();
    }
}
```

## Core Project

### Structure
- `ZhuzheeGame`(Class) : the main `RootAdapter` for `Application`, contains main logic of game.
- `GameScreens`(Package) : The package that contains in-game screens.
- ### `Card`(Package) : contains card classes
  - `Card`(abstract class) base class for `ActiveCard`, `PassiveCard`. 
  - "มาเขียนด้วย"
