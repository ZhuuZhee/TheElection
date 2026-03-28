package Core.Cards.Stream;

import Core.Maps.PoliticsStats;

public class CardBufferObject {
    private String name;
    private int coin;
    private PoliticsStats stats;
    private final String imgPath;
    public CardBufferObject(String name, int coin, PoliticsStats stats, String imgPath){
        this.name = name;
        this.coin = coin;
        this.stats = stats;
        this.imgPath = imgPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public PoliticsStats getStats() {
        return stats;
    }

    public void setStats(PoliticsStats stats) {
        this.stats = stats;
    }

    public String getImgPath() {
        return imgPath;
    }
}
