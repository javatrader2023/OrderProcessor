package handler;

import org.junit.Assert;
import org.junit.Test;
import util.OpeningVolume;
import util.Order;

import java.util.ArrayList;
import java.util.HashMap;

public class TradeProcessingSystemHandlerTest {

    /**
     *              BUY       SELL      COVER   |   NET    |   SHORT    /       RESULT
     * Case 1:      100        20        10         90          50        (Buy 90, Short 50)
     * Case 2:      10         30        24         4           40        (Buy 4, Short 40)
     * Case 3:      10         30        18        -2           30        (Sell 2, Short 30)
     * Case 4:      0          20        2         -18          20        (Sell 18, Short 20)
     * Case 5:      0          10       15          5           10        (Buy 5, Short 10)
     * Case 6:      Process a 10 shares BUY and a 10 shares CROSS.        (Buy 10, skip Cross)
     */
    @Test
    public void internalizeTrades() {
        ArrayList<Order> orders = new ArrayList<>();
        TradeProcessingSystemHandler tradeProcessingSystemHandler = new TradeProcessingSystemHandler();

        // Case 1
        Order buyOrder1 = new Order("IBM.N", 100, Order.Side.BUY);
        Order sellOrder1 = new Order("IBM.N", 20, Order.Side.SELL);
        Order coverOrder1 = new Order("IBM.N", 10, Order.Side.COVER);
        Order shortOrder1 = new Order("IBM.N", 50, Order.Side.SHORT);
        orders.add(buyOrder1); orders.add(sellOrder1); orders.add(coverOrder1); orders.add(shortOrder1);
        orders = tradeProcessingSystemHandler.internalizeTrades(orders);
        Assert.assertEquals("IBM.N shall have 1 buy 1 short", 2, orders.size());
        Assert.assertEquals("IBM.N buy shall be 90 shares", 90, orders.get(0).getQuantity());
        Assert.assertEquals("IBM.N buy shall be 90 shares", Order.Side.BUY, orders.get(0).getSide());
        Assert.assertEquals("IBM.N short shall be 50 shares", 50, orders.get(1).getQuantity());
        Assert.assertEquals("IBM.N short shall be 50 shares", Order.Side.SHORT, orders.get(1).getSide());
        orders.clear();

        // Case 2
        Order buyOrder2 = new Order("FB.O", 10, Order.Side.BUY);
        Order sellOrder2 = new Order("FB.O", 30, Order.Side.SELL);
        Order coverOrder2 = new Order("FB.O", 24, Order.Side.COVER);
        Order shortOrder2 = new Order("FB.O", 40, Order.Side.SHORT);
        orders.add(buyOrder2); orders.add(sellOrder2); orders.add(coverOrder2); orders.add(shortOrder2);
        orders = tradeProcessingSystemHandler.internalizeTrades(orders);
        Assert.assertEquals("FB.O shall have 1 buy 1 short", 2, orders.size());
        Assert.assertEquals("FB.O buy shall be 4 shares", 4, orders.get(0).getQuantity());
        Assert.assertEquals("FB.O buy shall be 4 shares", Order.Side.BUY, orders.get(0).getSide());
        Assert.assertEquals("FB.O short shall be 40 shares", 40, orders.get(1).getQuantity());
        Assert.assertEquals("FB.O short shall be 40 shares", Order.Side.SHORT, orders.get(1).getSide());
        orders.clear();

        // Case 3
        Order buyOrder3 = new Order("JPM.N", 10, Order.Side.BUY);
        Order sellOrder3 = new Order("JPM.N", 30, Order.Side.SELL);
        Order coverOrder3 = new Order("JPM.N", 18, Order.Side.COVER);
        Order shortOrder3 = new Order("JPM.N", 30, Order.Side.SHORT);
        orders.add(buyOrder3); orders.add(sellOrder3); orders.add(coverOrder3); orders.add(shortOrder3);
        orders = tradeProcessingSystemHandler.internalizeTrades(orders);
        Assert.assertEquals("JPM.N shall have 1 sell 1 short", 2, orders.size());
        Assert.assertEquals("JPM.N sell shall be 2 shares", 2, orders.get(0).getQuantity());
        Assert.assertEquals("JPM.N sell shall be 2 shares", Order.Side.SELL, orders.get(0).getSide());
        Assert.assertEquals("JPM.N short shall be 30 shares", 30, orders.get(1).getQuantity());
        Assert.assertEquals("JPM.N short shall be 30 shares", Order.Side.SHORT, orders.get(1).getSide());
        orders.clear();

        // Case 4
        Order buyOrder4 = new Order("GS.N", 0, Order.Side.BUY);
        Order sellOrder4 = new Order("GS.N", 20, Order.Side.SELL);
        Order coverOrder4 = new Order("GS.N", 2, Order.Side.COVER);
        Order shortOrder4 = new Order("GS.N", 20, Order.Side.SHORT);
        orders.add(buyOrder4); orders.add(sellOrder4); orders.add(coverOrder4); orders.add(shortOrder4);
        orders = tradeProcessingSystemHandler.internalizeTrades(orders);
        Assert.assertEquals("GS.N shall have 1 sell 1 short", 2, orders.size());
        Assert.assertEquals("GS.N sell shall be 18 shares", 18, orders.get(0).getQuantity());
        Assert.assertEquals("GS.N sell shall be 18 shares", Order.Side.SELL, orders.get(0).getSide());
        Assert.assertEquals("GS.N short shall be 30 shares", 20, orders.get(1).getQuantity());
        Assert.assertEquals("GS.N short shall be 30 shares", Order.Side.SHORT, orders.get(1).getSide());
        orders.clear();

        // Case 5
        Order buyOrder5 = new Order("MS.N", 0, Order.Side.BUY);
        Order sellOrder5 = new Order("MS.N", 10, Order.Side.SELL);
        Order coverOrder5 = new Order("MS.N", 15, Order.Side.COVER);
        Order shortOrder5 = new Order("MS.N", 10, Order.Side.SHORT);
        orders.add(buyOrder5); orders.add(sellOrder5); orders.add(coverOrder5); orders.add(shortOrder5);
        orders = tradeProcessingSystemHandler.internalizeTrades(orders);
        Assert.assertEquals("GS.N shall have 1 buy 1 short", 2, orders.size());
        Assert.assertEquals("GS.N buy shall be 5 shares", 5, orders.get(0).getQuantity());
        Assert.assertEquals("GS.N buy shall be 5 shares", Order.Side.BUY, orders.get(0).getSide());
        Assert.assertEquals("GS.N short shall be 30 shares", 10, orders.get(1).getQuantity());
        Assert.assertEquals("GS.N short shall be 30 shares", Order.Side.SHORT, orders.get(1).getSide());
        orders.clear();

        // Case 6
        Order buyOrder6 = new Order("C.N", 10, Order.Side.BUY);
        Order crossOrder6 = new Order("C.N", 10, Order.Side.CROSS);
        orders.add(buyOrder6); orders.add(crossOrder6);
        orders = tradeProcessingSystemHandler.internalizeTrades(orders);
        Assert.assertEquals("C.N shall have 1 buy", 1, orders.size());
        Assert.assertEquals("C.N buy shall be 5 shares", 10, orders.get(0).getQuantity());
        Assert.assertEquals("C.N buy shall be 5 shares", Order.Side.BUY, orders.get(0).getSide());
        orders.clear();
    }

    /**
     *            NET BUY   NET SELL    SHORT    /       OPEN VOLUME    /   RESULT
     * Case 1:      90       n/a         50      /          100         /   Buy 20, Short 20
     * Case 2:      4        n/a         40      /          1000        /   Buy 4, Short 40
     * Case 3:      n/a       2          30      /          -1          /   Abort Transaction
     * Case 4:      n/a       18         20      /          0           /   Abort Transaction
     * Case 5:      5        n/a         10      /          n/a         /   Abort Transaction
     */
    @Test
    public void calculateUncrossedOrdersToOpenVolume() {
        ArrayList<Order> uncrossedOrders = new ArrayList<>();
        HashMap<String, OpeningVolume > openingVolumes = new HashMap<>();
        TradeProcessingSystemHandler tradeProcessingSystemHandler = new TradeProcessingSystemHandler();

        // Case 1
        Order buyOrder1 = new Order("IBM.N", 90, Order.Side.BUY);
        Order shortOrder1 = new Order("IBM.N", 50, Order.Side.SHORT);
        uncrossedOrders.add(buyOrder1); uncrossedOrders.add(shortOrder1);
        OpeningVolume openingVolume1 = new OpeningVolume("IBM.N", 100);
        openingVolumes.put(openingVolume1.getStockSymbol(),openingVolume1);
        ArrayList<Order> ordersToSendToOpen = tradeProcessingSystemHandler.calculateUncrossedOrdersToOpenVolume(uncrossedOrders, openingVolumes);
        Assert.assertEquals("IBM.N shall have 1 buy 1 short", 2, ordersToSendToOpen.size());
        Assert.assertEquals("IBM.N buy shall be 20 shares", 20, ordersToSendToOpen.get(0).getQuantity());
        Assert.assertEquals("IBM.N buy shall be 20 shares", Order.Side.BUY, ordersToSendToOpen.get(0).getSide());
        Assert.assertEquals("IBM.N short shall be 20 shares", 20, ordersToSendToOpen.get(1).getQuantity());
        Assert.assertEquals("IBM.N short shall be 20 shares", Order.Side.SHORT, ordersToSendToOpen.get(1).getSide());
        uncrossedOrders.clear(); openingVolumes.clear();

        //TODO: Case 1 has 2 residual orders to stage into continuous trading (e.g. Buy IBM.N 70 shares, Short IBM.N 30 shares)
        //TODO: That requires to retrieve the residual orders from the order entity manager. Skipping here...

        // Case 2
        Order buyOrder2 = new Order("FB.O", 4, Order.Side.BUY);
        Order shortOrder2 = new Order("FB.O", 40, Order.Side.SHORT);
        uncrossedOrders.add(buyOrder2); uncrossedOrders.add(shortOrder2);
        OpeningVolume openingVolume2 = new OpeningVolume("FB.O", 1000);
        openingVolumes.put(openingVolume2.getStockSymbol(),openingVolume2);
        ordersToSendToOpen = tradeProcessingSystemHandler.calculateUncrossedOrdersToOpenVolume(uncrossedOrders, openingVolumes);
        Assert.assertEquals("FB.O shall have 1 buy 1 short", 2, ordersToSendToOpen.size());
        Assert.assertEquals("FB.O buy shall be 4 shares", 4, ordersToSendToOpen.get(0).getQuantity());
        Assert.assertEquals("FB.O buy shall be 4 shares", Order.Side.BUY, ordersToSendToOpen.get(0).getSide());
        Assert.assertEquals("FB.O short shall be 40 shares", 40, ordersToSendToOpen.get(1).getQuantity());
        Assert.assertEquals("FB.O short shall be 40 shares", Order.Side.SHORT, ordersToSendToOpen.get(1).getSide());
        uncrossedOrders.clear(); openingVolumes.clear();

        // Case 3
        Order buyOrder3 = new Order("JPM.N", 2, Order.Side.SELL);
        Order shortOrder3 = new Order("JPM.N", 30, Order.Side.SHORT);
        uncrossedOrders.add(buyOrder3); uncrossedOrders.add(shortOrder3);
        OpeningVolume openingVolume3 = new OpeningVolume("JPM.N", -1);
        openingVolumes.put(openingVolume3.getStockSymbol(),openingVolume3);
        ordersToSendToOpen = tradeProcessingSystemHandler.calculateUncrossedOrdersToOpenVolume(uncrossedOrders, openingVolumes);
        Assert.assertEquals("JPM.N shall have no trade.", 0, ordersToSendToOpen.size());
        uncrossedOrders.clear(); openingVolumes.clear();

        //TODO: Case 3 has 2 residual orders to stage into continuous trading (e.g. Sell JPM.N 2 shares, Short JPM.N 30 shares)
        //TODO: That requires to retrieve the residual orders from the order entity manager. Skipping here...

        // Case 4
        Order buyOrder4 = new Order("GS.N", 18, Order.Side.SELL);
        Order shortOrder4 = new Order("GS.N", 20, Order.Side.SHORT);
        uncrossedOrders.add(buyOrder4); uncrossedOrders.add(shortOrder4);
        OpeningVolume openingVolume4 = new OpeningVolume("GS.N", 0);
        openingVolumes.put(openingVolume4.getStockSymbol(),openingVolume4);
        ordersToSendToOpen = tradeProcessingSystemHandler.calculateUncrossedOrdersToOpenVolume(uncrossedOrders, openingVolumes);
        Assert.assertEquals("GS.N shall have no trade.", 0, ordersToSendToOpen.size());
        uncrossedOrders.clear(); openingVolumes.clear();

        //TODO: Case 4 has 2 residual orders to stage into continuous trading (e.g. Sell GS.N 18 shares, Short GS.N 20 shares)
        //TODO: That requires to retrieve the residual orders from the order entity manager. Skipping here...

        // Case 5
        Order buyOrder5 = new Order("MS.N", 5, Order.Side.BUY);
        Order shortOrder5 = new Order("MS.N", 30, Order.Side.SHORT);
        uncrossedOrders.add(buyOrder5); uncrossedOrders.add(shortOrder5);
        ordersToSendToOpen = tradeProcessingSystemHandler.calculateUncrossedOrdersToOpenVolume(uncrossedOrders, openingVolumes);
        Assert.assertEquals("MS.N shall have no trade.", 0, ordersToSendToOpen.size());
        uncrossedOrders.clear(); openingVolumes.clear();
    }

    /**
     * Case 1: Create a ZERO quantity order. Result false.
     * Case 2: Create a POSITIVE quantity order. Result true.
     * Case 3: Create a NEGATIVE quantity order. Result true.
     */
    @Test
    public void createUncrossedOrders() {
        HashMap<String, Integer> uncrossedSymbolQtyMap = new HashMap<>();
        uncrossedSymbolQtyMap.put("IBM.N", 0);
        uncrossedSymbolQtyMap.put("FB.O", 1);       // net buy
        uncrossedSymbolQtyMap.put("JPM.N", -1);     // net sell

        TradeProcessingSystemHandler tradeProcessingSystemHandler = new TradeProcessingSystemHandler();
        ArrayList<Order> uncrossedOrders = tradeProcessingSystemHandler.createUncrossedOrders(uncrossedSymbolQtyMap);

        Assert.assertEquals("Orders size shall be 2", 2, uncrossedOrders.size());
        Assert.assertEquals("C.N shall be the net buy order", "FB.O", uncrossedOrders.get(0).getStockSymbol());
        Assert.assertEquals("JPM.N shall be the net sell order", "JPM.N", uncrossedOrders.get(1).getStockSymbol());
    }
}