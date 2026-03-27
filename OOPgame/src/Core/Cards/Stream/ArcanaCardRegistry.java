package Core.Cards.Stream;
import Core.Cards.ArcanaCard;
import Core.Cards.AllArcanaCards.*;

public class ArcanaCardRegistry {

    public static ArcanaCard createCard(String cardName) {
        if (cardName == null || cardName.isEmpty()) return null;

        String safeName = cardName.replace(" ", "");
        System.out.println("Try to create ArcanaCard{%s}".formatted(cardName));
        return switch (safeName) {
            case ArcanaCardName.THE_FOOL -> new TheFoolCard(0, 0);
            case ArcanaCardName.WOF -> new TheWheelOfFortune(0, 0);
            case "Death" -> new Death(0, 0);
            case "Tower" -> new TheTower(0, 0);
            case "Sun" -> new TheSun(0, 0);
            case "Judgement" -> new Judgement(0, 0);

            default -> {
                System.err.println("[ArcanaCardRegistry] ไม่รู้จักการ์ด Arcana: " + cardName);
                yield null;
            }
        };
    }
}
