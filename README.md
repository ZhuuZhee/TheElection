## ZhuzheeEngine Structure and Workflow

### High-level architecture

- **`ZhuzheeEngine.Application`**: Abstract base for any game/app. Owns the main `JFrame`, manages the fixed-frame render loop (default 60 FPS), and exposes `getDeltaTime()` for time-based updates.
- **`ZhuzheeEngine.ApplicationAdapter`**: Interface that defines the core lifecycle methods (`create`, `resize`, `render`, `depose`) that screens and scenes implement.
- **`ZhuzheeEngine.Screen`**: Abstract `JPanel` that represents a logical screen (main menu, gameplay, etc.). Implements `ApplicationAdapter` and provides static `currentScreen` and `ChangeScreen(...)` for swapping screens.
- **`ZhuzheeEngine.Scene.Scene2D`**: A `JPanel` that implements `ApplicationAdapter`, owns and updates a list of `GameObject`s, and is responsible for rendering them each frame.
- **`ZhuzheeEngine.Scene.GameObject`**: Base class for anything that can be drawn in the scene2D (has position, size, and `zIndex`), with lifecycle hooks (`start()`, `render(Graphics)`, `onDestroy()`).

### Application and frame loop

1. **Launch**: The entry point is `Main` in `OOPgame/src/Main.java`, which calls:
   - `Application.LuchApp(new Core.ZhuzheeGame());`
2. **Create phase**:
   - `Application.LuchApp(...)` calls `Create()` on the `ZhuzheeGame` instance.
   - `Application.Create()` builds the main `JFrame`, sets size, and makes it visible.
3. **Render loop**:
   - A Swing `Timer` is created with a delay derived from the target FPS.
   - On every tick, `Application.Render()` is invoked and the frame is repainted.
   - `Core.ZhuzheeGame.Render()` extends this by calling `screenManager.currentScreen.render()`, where actual game logic per frame should live.
4. **Delta time**:
   - `Application.SetTargetFrameRate(int targetFPS)` controls how often `Render()` is called.
   - `Application.getDeltaTime()` returns the time between frames in seconds, so gameplay can be made frame-rate independent.

### Scene2D rendering and game objects

- **Scene singleton**:
  - A `Scene2D` is created with the main frame: `new Scene2D(Application.getMainFrame());`
  - The constructor sets `Scene2D.Instance = this`, allowing global registration via `Scene2D.register(GameObject)` and removal via `Scene2D.remove(GameObject)`.
- **Registration, lifecycle, and drawing**:
  - Every `GameObject` registers itself in its constructor via `Scene2D.register(this);` and immediately calls its `start()` method once.
  - `Scene2D.render()`:
    - Gets a `Graphics2D` from the component, translates the origin to the center (plus an optional offset origin).
    - Sorts `gameObjects` by `zIndex` and calls `obj.render(g2d)` for each object every frame.
  - `GameObject.Destroy(gameObject)` calls `onDestroy()` and removes the object from the current `Scene2D`.
- **Z ordering**:
  - Objects with higher `zIndex` are rendered later and appear on top of lower `zIndex` objects.
  - `GameObject` exposes `getzIndex()` / `setzIndex(...)` for controlling draw order.
- **Screen Point and World Point**:
  - Screen Point is point in `javax.SWING` coordination system that (0,0) is a `Top Left` of Screen.
  - World Point is point in `Scene2D` coordination system that (0,0) is a `Middle Center` of Screen.
  - Use `Scene2D.World2ScreenPoint(x,y)` or `Scene2D.Screen2WorldPoint(x,y)` to translate between these coordination system
### Input handling (cards example)

- **MouseHandler**:
  - `Core.Player.MouseHandler` works with the active `Scene2D` to:
    - Track hover state over `Core.Card.Card` instances.
    - Forward mouse pressed/dragged/released events to cards.
- **Cards as GameObjects**:
  - `Core.Card.Card` extends `GameObject` and implements:
    - **Dragging** (`onMousePressed`, `onMouseDragged`, `onMouseReleased`) with offset tracking and top `zIndex` while grabbed.
    - **Hover effect**: Scales the card slightly when hovered but not grabbed.
    - **Snapping**: On release, checks nearby `CardSlot`s and snaps into place if inside the slot’s magnetic field.

### Screens and game flow

- **Game-level application**: `Core.ZhuzheeGame` extends `Application` and is responsible for wiring the engine to the concrete game.
- **Screen management**:
  - An inner `ScreenManager` class in `ZhuzheeGame` holds `currentScreen` and `lastScreen`.
  - `ChangeScreen(Screen next)` calls `onScreenExit()` on the current screen and `onScreenEnter()` on the new one.
- **Scene and Screen**:
  - `ZhuzheeEngine.Scene.Scene2D` extends `ZhuzheeEngine.Screen`.
  - Its `create`, `render`, and `depose` methods are where the main game behavior should be implemented.


## Quick Start and Using ZhuzheeEngine

### Running this project

- **Requirements**:
  - **Java**: JDK 8+ (any modern JDK should work).
  - **Build tool/IDE**: Any Java IDE (IntelliJ IDEA, Eclipse, VS Code with Java) that can compile and run a standard Swing project.
- **Steps**:
  - **1. Clone the repository** into your workspace.
  - **2. Open the project** in your Java IDE.
  - **3. Set the run configuration** to use the `Main` class in `OOPgame/src/Main.java` as the entry point.
  - **4. Run the application**. A Swing window should appear with the prototype card scene2D.

### Creating your own game application

To build your own game on top of ZhuzheeEngine, you typically:

1. **Create an `Application` subclass**:
   - Extend `ZhuzheeEngine.Application`.
   - Override `Create()` to:
     - Call `super.Create()` (to build the main frame).
     - Create a `Scene` with `new Scene(Application.getMainFrame());`.
     - Instantiate your `ScreenManager` (or reuse the pattern in `ZhuzheeGame`).
     - Create and set your initial `Screen` or `Scene2D`.
     - Set your `currentScreen` to initial `Screen` by `ScreenManager`.ChangeScreen(`your initial Screen`)
   - Optionally override `Render()` to call `currentScreen.render()` and any global per-frame logic.
2. **Create one or more `Screen` subclasses**:
   - Extend `ZhuzheeEngine.Screen`.
   - Implement `create`, `render`, and `depose`:
     - **`create`**: Allocate resources, initialize game objects, register listeners.
     - **`render`**: Per-frame logic, state updates (drawing is handled by `Scene`).
     - **`depose`**: Cleanup when leaving the screen.
3. **Use `Scene` and `GameObject`**:
   - For visual entities, extend `GameObject` and override `draw(Graphics g)` to render your content.
   - Construct your objects (they auto-register with the current `Scene`).
4. **Wire the entry point**:
   - In your `Main` class, call:

```java
public class Main {
    public static void main(String[] args) {
        Application.LuchApp(new YourGameApplication());
    }
}
```

5. **Tune performance and behavior**:
   - Adjust frame rate using `Application.SetTargetFrameRate(...)`.
   - Use `Application.getDeltaTime()` in your update logic to achieve smooth animations.

### Using the card system example

- **Core.Card.Card** is an example of a more complex `GameObject` with:
  - Hover animations.
  - Drag-and-drop behavior.
  - Slot snapping (`CardSlot`).
- You can:
  - Create new card types (e.g., `ActionCard`, `PassiveCard`) by extending `Card`.
  - Configure their behavior via:
    - `setDraggable(boolean)`.
    - Enabling/disabling.
    - Customizing `isDroppable(Object bottom)` and extending the snapping logic if needed.


## About This Project (`Core` and `Dummy`)

### Core package

- **`Core.ZhuzheeGame`**:
  - The main game controller that extends `Application`.
  - Sets up the `ScreenManager`, the primary `Scene`, and the initial `MainGameScreen`.
  - Currently calls `Dummy.Tester.CardsTestingOnScene(scene2D)` in `Create()` to populate the scene2D with sample cards and slots.
- **`Core.GameScreens.MainGameScreen`**:
  - Represents the main gameplay screen.
  - Holds a reference to the shared `Scene`.
  - Intended to contain the primary game logic in its `create`, `render`, and `depose` methods.
- **`Core.Card` package**:
  - **`Card`**: Abstract base for all cards (rendering, dragging, snapping behavior).
  - **`ActionCard` / `PassiveCard`**: Concrete card types used in the current prototype.
  - **`CardSlot`**: Represents a location a card can snap into (used by `Card.snapToSlot()`).
- **`Core.Player.Player`**:
  - Placeholder for player-related data and logic.
  - Can be extended to track hand of cards, resources, health, or other gameplay stats.

### Dummy package

- **Purpose**:
  - Contains **prototype, test, and demo code** that showcases how to use the engine and card system without being part of the final game logic.
- **Key classes**:
  - **`Dummy.Tester`**:
    - Method `CardsTestingOnScene(Scene scene2D)` creates a `CardSlot` and a couple of `ActionCard` instances.
    - Demonstrates:
      - How to place card slots and cards on a `Scene`.
      - How to use `setDraggable(false)` to lock a card in place.
  - **`Dummy.Citybanna`**:
    - Simple example data class storing a `cityName`.
    - Serves as a placeholder for future domain objects in the project.

### Intended usage

- **Engine vs. game code**:
  - The **ZhuzheeEngine** package is the reusable mini-engine: windowing, loop, screens, scene2D graph, and input helper.
  - The **Core** package is the actual game built on the engine (for “The Election”), including cards, players, and screens.
  - The **Dummy** package is a scratchpad for experiments and examples that help you understand and test the engine.

As you develop the project, you can move stable patterns out of `Dummy` into `Core` (or new feature packages) and keep `ZhuzheeEngine` focused on engine-level, reusable functionality.
