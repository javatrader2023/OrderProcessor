package handler;

import org.junit.Assert;
import org.junit.Test;
import util.Order;

import java.util.ArrayList;

public class OrderPlacementSystemHandlerTest {

    /**
     * Case 1: Instrument is "". Result false.
     * Case 2: Instrument is null. Result false.
     * Case 3: Instrument is "   ". Result false.
     * Case 4: Instrument FB.O has volume less than ZERO. Result false.
     * Case 5: Instrument IBM.N has volume ZERO. Result false.
     * Case 6: Instrument C.N has volume 1. Result true.
     */
    @Test
    public void validateOrderPlacements() {
        ArrayList<Order> orders = new ArrayList<>();

        Order order1 = new Order("", 1, Order.Side.BUY);
        Order order2 = new Order(null, 1, Order.Side.SELL);
        Order order3 = new Order("   ", 1, Order.Side.COVER);
        Order order4 = new Order("FB.O", -1, Order.Side.SHORT);
        Order order5 = new Order("IBM.N", 0, Order.Side.BUY);
        Order order6 = new Order("C.N", 1, Order.Side.SELL);

        orders.add(order1); orders.add(order2); orders.add(order3);
        orders.add(order4); orders.add(order5); orders.add(order6);

        OrderPlacementSystemHandler orderPlacementSystemHandler = new OrderPlacementSystemHandler();
        orders = orderPlacementSystemHandler.validateOrderPlacements(orders);

        Assert.assertEquals("Orders size shall be 1", 1, orders.size());
        Assert.assertEquals("C.N shall be the only element", "C.N", orders.get(0).getStockSymbol());
    }
}