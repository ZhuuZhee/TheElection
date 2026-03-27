package Core.Cards.Stream;
import Core.Cards.ArcanaCard;
import Core.Cards.AllArcanaCards.*;

public class ArcanaCardRegistry {
    public static ArcanaCard createCard(String cardName) {
        if (cardName == null || cardName.isEmpty()) return null;

        String safeName = cardName.replace(" ", "");

        return switch (safeName) {
            case "TheFool" -> new TheFoolCard(0, 0);

            default -> {
                System.err.println("[ArcanaCardRegistry] ไม่รู้จักการ์ด Arcana: " + cardName);
                yield null;
            }
        };
    }
}
