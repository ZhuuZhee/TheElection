# Game Balancing Plan: City Stat Decay & Random Events

## The Problem
Currently, the `calculateLogScore` function in `City.java` uses logarithmic diminishing returns:
`K_LOG_MULTIPLIER * (Math.log(newVal + 1) - Math.log(currentVal + 1))`

This means as the city's stats (Facility, Environment, Economy) get higher over multiple turns, players receive **less and less score** for playing cards. In the late game, it becomes extremely difficult to gain points.

## The Solution
Introduce **Random Encounters / Events** that occasionally reduce the stats of cities. By lowering a city's stats, `currentVal` drops, and playing cards on that city once again yields a much higher score.

### Proposed System Mechanics
1. **Event Trigger Frequency:**
   - At the beginning of every turn (or every N turns), there is a chance (e.g., 20-30%) for a random event to occur.
   - The server (`GameState.java`) will calculate this chance and broadcast the event to all clients.

2. **Types of Random Events:**
   - **Economic Recession:** Reduces the `Economy` stat of a random city (e.g., -10 to -20).
   - **Infrastructure Decay / Disaster:** Reduces the `Facility` stat of a random city (e.g., -15).
   - **Pollution Crisis:** Reduces the `Environment` stat of a random city (e.g., -15).
   - **Nationwide Crisis (Late Game):** Reduces a specific stat across *all* cities by a small amount (e.g., -5).

3. **Implementation Details:**
   - Create a new class `RandomEventSystem` or utilize `EncounterEvent.java`.
   - Update `City.java` to handle stat reductions safely (ensuring stats don't drop below 0).
   - Update `GameState.java` in the server to manage the event logic and send packets so clients see a visual pop-up or notification: *"An Economic Recession hit [City Name]! Economy drops by 15."*

## Benefits
- Prevents the late-game from stalling due to score stagnation.
- Creates dynamic targets: A city hit by a disaster suddenly becomes a highly valuable target for players to invest cards into, naturally driving conflict.
- Adds unpredictability and thematic flavor to the election/political theme.