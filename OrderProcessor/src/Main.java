import java.util.*;
import util.Order;
import util.OpeningVolume;
import handler.OrderPlacementSystemHandler;
import handler.MarketDataSystemHandler;
import handler.TradeProcessingSystemHandler;
import handler.FIXConnectivitySystemHandler;

/**
 *  Author: Edward Cai
 *  Date: Oct 20, 2023
 */

public class Main {
    public static void main(String[] args) {

        // generate dummy market data of open auction
        MarketDataSystemHandler marketDataSystemHandler = new MarketDataSystemHandler();
        HashMap<String, OpeningVolume> openingVolumes = marketDataSystemHandler.generateOpenMarketData();

        // generate dummy order placements from Portfolio Managers
        OrderPlacementSystemHandler orderPlacementSystemHandler = new OrderPlacementSystemHandler();
        ArrayList<Order> orders = orderPlacementSystemHandler.generateOrderPlacements();

        // core logic to internalize trades
        TradeProcessingSystemHandler tradeProcessingSystemHandler = new TradeProcessingSystemHandler();
        ArrayList<Order> ordersToSendToOpen = tradeProcessingSystemHandler.processTrades(orders, openingVolumes);

        // send to external broker algo interface
        FIXConnectivitySystemHandler fixConnectivitySystemHandler = new FIXConnectivitySystemHandler();
        fixConnectivitySystemHandler.ordersToSendToOpen(ordersToSendToOpen);
    }
}