/**
 * @Xynezter 14/3/2026 15:03
 */
package Core.Cards;

public abstract class ArcanaCard extends Card {
    private final int maxCooldown;
    private int currentCooldown;
    public ArcanaCard(String name,CardSlot targetSlot, int maxCooldown) {
        super(name, targetSlot.getPosition().x, targetSlot.getPosition().y, 100, 150);
        this.maxCooldown = maxCooldown;
        this.currentCooldown = 0;
    }

    public ArcanaCard(String name, CardSlot targetSlot, int maxCooldown, String imagePath) {
        super(name, targetSlot.getPosition().x, targetSlot.getPosition().y, 100, 150, imagePath);
        this.maxCooldown = maxCooldown;
        this.currentCooldown = 0;
    }

    protected abstract void activateSkill();

    public final void onMouseClick() {
//         รอ นับเทริน
//        if (this.currentCooldown == 0) {
//            System.out.println("Used skill : " + this.name);
//            activateSkill();
//
//            // Reset cooldown
//            this.currentCooldown = this.maxCooldown;
//        } else {
//            System.out.println(this.name + " Cant use! wait for " + this.currentCooldown + " tern");
//        }
        activateSkill();
        this.currentCooldown = this.maxCooldown;
    }

    // รอ นับเทริน เรียกใช้
    public void decreaseCooldown() {
        if (this.currentCooldown > 0) {
            this.currentCooldown--;
        }
    }

}