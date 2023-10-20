package util;

public class Order {
    String stockSymbol;
    int quantity;
    Side side; // "buy", "sell", "cover", "short"

    public Order(String stockSymbol, int quantity, Side side) {
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.side = side;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public enum Side {
        BUY {
            @Override
            public String toString() {
                return "Buy";
            }
        },
        SELL {
            @Override
            public String toString() {
                return "Sell";
            }
        },
        COVER {
            @Override
            public String toString() {
                return "Cover";
            }
        },
        SHORT {
            @Override
            public String toString() {
                return "Short";
            }
        },
        CROSS {
            @Override
            public String toString() {
                return "Cross";
            }
        }
    }
}

