package Core.Cards.Stream;
import Core.Cards.ArcanaCard;
import Core.Cards.AllArcanaCards.*;

public class ArcanaCardRegistry {

    public static ArcanaCard createCard(String cardName) {
        if (cardName == null || cardName.isEmpty()) return null;

        String safeName = cardName.replace(" ", "");
        System.out.println("Try to create ArcanaCard{%s}".formatted(cardName));
        return switch (safeName) {
            case ArcanaCardName.THE_FOOL, ArcanaCardName.THE_FOOL_CARD -> new TheFoolCard(0, 0);
            case ArcanaCardName.WOF, ArcanaCardName.THE_WHEEL_OF_FORTUNE -> new TheWheelOfFortune(0, 0);
            case ArcanaCardName.DEATH -> new Death(0, 0);
            case ArcanaCardName.TOWER, ArcanaCardName.THE_TOWER -> new TheTower(0, 0);
            case ArcanaCardName.SUN, ArcanaCardName.THE_SUN -> new TheSun(0, 0);
            case ArcanaCardName.JUDGEMENT -> new Judgement(0, 0);

            default -> {
                System.err.println("[ArcanaCardRegistry] ไม่รู้จักการ์ด Arcana: " + cardName);
                yield null;
            }
        };
    }
}
