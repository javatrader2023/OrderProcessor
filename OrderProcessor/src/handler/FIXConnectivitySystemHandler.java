package handler;
import util.Order;

import java.util.ArrayList;

public class FIXConnectivitySystemHandler {
    public Boolean ordersToSendToOpen(ArrayList<Order> orders){
        ArrayList<Order> ioiOrders = sourceIOI();
        // TODO: tap into natural working liquidity at brokers based on IOI or AIOI

        return sendToBrokerDMA(orders);
    }

    protected ArrayList<Order> sourceIOI(){
        ArrayList<Order> ioiOrders = new ArrayList<>();

        // TODO: listen to broker IOI messages and identify natural working liquidity
        return ioiOrders;
    }

    protected Boolean sendToBrokerDMA(ArrayList<Order> orders){
        // TODO: send to broker DSA/DMA algo ATDL interface

        orders.forEach(order -> System.out.println("SendToBrokerDMA INFO: Routing order " + order.getSide() + " " + order.getStockSymbol() + " with " + order.getQuantity() + " shares"));
        return true;
    }
}
