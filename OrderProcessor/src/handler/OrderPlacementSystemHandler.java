package handler;
import util.Order;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class OrderPlacementSystemHandler {

    /**
     * Generate dummy OrderPlacements
     * @return orders
     */
    public ArrayList<Order> generateOrderPlacements(){
        Random random = new Random();
        ArrayList<Order> orders = new ArrayList<>();

        Order IBM_1 = new Order("IBM.N", random.nextInt(40), Order.Side.BUY);
        Order IBM_2 = new Order("IBM.N", random.nextInt(40), Order.Side.SELL);
        Order IBM_3 = new Order("IBM.N", random.nextInt(40), Order.Side.COVER);
        Order IBM_4 = new Order("IBM.N", random.nextInt(40), Order.Side.SHORT);
        orders.add(IBM_1); orders.add(IBM_2); orders.add(IBM_3); orders.add(IBM_4);

        Order AAPL_1 = new Order("AAPL.O", random.nextInt(40), Order.Side.BUY);
        Order AAPL_2 = new Order("AAPL.O", random.nextInt(40), Order.Side.SELL);
        Order AAPL_3 = new Order("AAPL.O", random.nextInt(40), Order.Side.COVER);
        Order AAPL_4 = new Order("AAPL.O", random.nextInt(40), Order.Side.SHORT);
        orders.add(AAPL_1); orders.add(AAPL_2); orders.add(AAPL_3); orders.add(AAPL_4);

        Order MSFT_1 = new Order("MSFT.O", random.nextInt(40), Order.Side.BUY);
        Order MSFT_2 = new Order("MSFT.O", random.nextInt(40), Order.Side.SELL);
        Order MSFT_3 = new Order("MSFT.O", random.nextInt(40), Order.Side.COVER);
        Order MSFT_4 = new Order("MSFT.O", random.nextInt(40), Order.Side.SHORT);
        orders.add(MSFT_1); orders.add(MSFT_2); orders.add(MSFT_3); orders.add(MSFT_4);

        Order AMZN_1 = new Order("AMZN.O", random.nextInt(40), Order.Side.BUY);
        Order AMZN_2 = new Order("AMZN.O", random.nextInt(40), Order.Side.SELL);
        Order AMZN_3 = new Order("AMZN.O", random.nextInt(40), Order.Side.COVER);
        Order AMZN_4 = new Order("AMZN.O", random.nextInt(40), Order.Side.SHORT);
        orders.add(AMZN_1); orders.add(AMZN_2); orders.add(AMZN_3); orders.add(AMZN_4);

        Order GOOGL_1 = new Order("GOOGL.N", random.nextInt(40), Order.Side.BUY);
        Order GOOGL_2 = new Order("GOOGL.N", random.nextInt(40), Order.Side.SELL);
        Order GOOGL_3 = new Order("GOOGL.N", random.nextInt(40), Order.Side.COVER);
        Order GOOGL_4 = new Order("GOOGL.N", random.nextInt(40), Order.Side.SHORT);
        orders.add(GOOGL_1); orders.add(GOOGL_2); orders.add(GOOGL_3); orders.add(GOOGL_4);

        Order TSLA_1 = new Order("TSLA.O", random.nextInt(40), Order.Side.BUY);
        Order TSLA_2 = new Order("TSLA.O", random.nextInt(40), Order.Side.SELL);
        Order TSLA_3 = new Order("TSLA.O", random.nextInt(40), Order.Side.COVER);
        Order TSLA_4 = new Order("TSLA.O", random.nextInt(40), Order.Side.SHORT);
        orders.add(TSLA_1); orders.add(TSLA_2); orders.add(TSLA_3); orders.add(TSLA_4);

        Order META_1 = new Order("META.O", random.nextInt(40), Order.Side.BUY);
        Order META_2 = new Order("META.O", random.nextInt(40), Order.Side.SELL);
        Order META_3 = new Order("META.O", random.nextInt(40), Order.Side.COVER);
        Order META_4 = new Order("META.O", random.nextInt(40), Order.Side.SHORT);
        orders.add(META_1); orders.add(META_2); orders.add(META_3); orders.add(META_4);

        Order C_1 = new Order("C.N", random.nextInt(40), Order.Side.BUY);
        Order C_2 = new Order("C.N", random.nextInt(40), Order.Side.SELL);
        Order C_3 = new Order("C.N", random.nextInt(40), Order.Side.COVER);
        Order C_4 = new Order("C.N", random.nextInt(40), Order.Side.SHORT);
        orders.add(C_1); orders.add(C_2); orders.add(C_3); orders.add(C_4);

        Order JPM_1 = new Order("JPM.N", random.nextInt(40), Order.Side.BUY);
        Order JPM_2 = new Order("JPM.N", random.nextInt(40), Order.Side.SELL);
        Order JPM_3 = new Order("JPM.N", random.nextInt(40), Order.Side.COVER);
        Order JPM_4 = new Order("JPM.N", random.nextInt(40), Order.Side.SHORT);
        orders.add(JPM_1); orders.add(JPM_2); orders.add(JPM_3); orders.add(JPM_4);

        Order MS_1 = new Order("MS.N", random.nextInt(40), Order.Side.BUY);
        Order MS_2 = new Order("MS.N", random.nextInt(40), Order.Side.SELL);
        Order MS_3 = new Order("MS.N", random.nextInt(40), Order.Side.COVER);
        Order MS_4 = new Order("MS.N", random.nextInt(40), Order.Side.SHORT);
        orders.add(MS_1); orders.add(MS_2); orders.add(MS_3); orders.add(MS_4);

        orders.forEach(order -> System.out.println("GenerateOrderPlacements INFO: Created order " + order.getSide() + " " + order.getStockSymbol() + " with " + order.getQuantity() + " shares"));

        orders = validateOrderPlacements(orders);
        return orders;
    }

    /**
     * Validates OrderPlacements for data integrity. Remove bogus orders from the ArrayList.
     * @param orders
     * @return orders
     */
    protected ArrayList<Order> validateOrderPlacements(ArrayList<Order> orders){
        int index = 0;
        Stack<Integer> stack = new Stack<>();

        for (Order order: orders) {
            if (order.getQuantity() <= 0) {
                System.out.println("ValidateOrderPlacements ERROR: Order quantity equal or less than ZERO... " + order.getSide() + " " + order.getStockSymbol() + " with " + order.getQuantity() + " shares");
                stack.push(index);
            }
            if (order.getStockSymbol() == null || order.getStockSymbol().trim().isEmpty()) {
                System.out.println("ValidateOrderPlacements ERROR: Order stockSymbol is null or empty... " + order.getSide() + " " + order.getStockSymbol() + " with " + order.getQuantity() + " shares");
                stack.push(index);
            }
            index++;
        }

        int stackSize = stack.size();
        for (int i = 0; i < stackSize; i++){
            Order removedOrder = orders.remove(stack.pop().intValue()); // deleting from the end of the array
            System.out.println("ValidateOrderPlacements WARNING: Removing order " + removedOrder.getSide() + " " + removedOrder.getStockSymbol() + " with " + removedOrder.getQuantity() + " shares");
        }
        return orders;
    }
}
