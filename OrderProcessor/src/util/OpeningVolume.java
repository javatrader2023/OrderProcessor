package util;

public class OpeningVolume {
    String stockSymbol;
    int volume;

    public OpeningVolume(String stockSymbol, int volume) {
        this.stockSymbol = stockSymbol;
        this.volume = volume;
    }

    public OpeningVolume(String stockSymbol) {
        this.stockSymbol = stockSymbol;
        this.volume = 0;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

}