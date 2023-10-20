package handler;

import util.OpeningVolume;
import util.Order;

import java.util.ArrayList;
import java.util.HashMap;

public class TradeProcessingSystemHandler {
    private static final double MAX_PARTICIPATION = 0.2;

    public ArrayList<Order> processTrades(ArrayList<Order> orders, HashMap<String, OpeningVolume> openingVolumes) {

        ArrayList<Order> uncrossedOrders = internalizeTrades(orders);
        return calculateUncrossedOrdersToOpenVolume(uncrossedOrders, openingVolumes);
    }

    /**
     * Perform order internalization of Buy and Cover against Sell.
     * The method stores crossed trades in a hashmap and call to persist the executions in a database.
     * The method stores uncrossed trades in a hashmap and return the result set for further processing.
     * Note: Short Sell will not participate in crossing, and will be sent out as-is.
     *
     * @param orders
     * @return uncrossedOrders
     */
    protected ArrayList<Order> internalizeTrades (ArrayList<Order> orders){
        HashMap<String, Integer> uncrossedSymbolQtyMap = new HashMap<String, Integer>();
        HashMap<String, Integer> crossedSymbolQtyMap = new HashMap<String, Integer>();

        // skip SHORT SELL orders - they must secure locate, comply to uptick rule, and work at prime brokers
        ArrayList<Order> shortOrders = new ArrayList<>();
        ArrayList<Order> eligibleCrossingOrders = new ArrayList<>();
        for (Order order: orders){
            if (order.getSide() == Order.Side.SHORT){
                shortOrders.add(order);
                continue;
            }
            eligibleCrossingOrders.add(order);
        }

        for (Order order: eligibleCrossingOrders){
            String stockSymbol = order.getStockSymbol();
            Order.Side side = order.getSide();
            int quantity = 0;

            /* Quantity indicates execution side. From the exchange prospective, Long Buy and Buy-to-Cover execute in the same way.
               Long Sell and Short Sell may differ in the following ways:
                    1) Short Sell must secure SBL locate,
                    2) Short Sell must comply to the uptick rule, and
                    3) Short Sell is subject to the regulatory reporting requirements.
               On the contrary, Buy-to-Cover executes in the same way as Long Buy on exchanges/dark pools. Thus, BC shall offset Sell.
               During settlement, Buy-side Ops instruct brokers with allocation shape to flag out Buy-to-Cover account/instrument/shares.
             */
            switch(side){
                case BUY:
                case COVER: // similar to long buy, buy-to-cover shall internalize against long sell
                    quantity = order.getQuantity();;
                    break;
                case SELL:
                    quantity = -order.getQuantity();;
                    break;
                default:
                    System.out.println("InternalizeTrades ERROR: Unrecognized side " + side + " " + stockSymbol);
                    break;
            }

            // existing stockSymbol in uncrossedSymbolQty hashMap
            if (uncrossedSymbolQtyMap.containsKey(stockSymbol)){
                int uncrossedQty = uncrossedSymbolQtyMap.get(stockSymbol);

                // internalize the cross only when orders are in opposite execution sides
                if ((quantity > 0 && uncrossedQty < 0) || (quantity < 0 && uncrossedQty > 0)){
                    int crossedQty = Math.min(Math.abs(quantity), Math.abs(uncrossedQty));

                    // upsert into crossedSymbolQty hashMap
                    crossedSymbolQtyMap.put(stockSymbol, crossedSymbolQtyMap.containsKey(stockSymbol)?
                            crossedQty + crossedSymbolQtyMap.get(stockSymbol) : crossedQty);
                }
                uncrossedSymbolQtyMap.put(stockSymbol, uncrossedQty + quantity);
            }
            else{ // new stockSymbol in uncrossedSymbolQty hashMap
                uncrossedSymbolQtyMap.put(stockSymbol, quantity);
            }
        }
        persistCrossedTrades(crossedSymbolQtyMap);

        ArrayList<Order> uncrossedOrders = createUncrossedOrders(uncrossedSymbolQtyMap);
        uncrossedOrders.addAll(shortOrders);  // re-instate short sell orders
        return uncrossedOrders;
    }

    /**
     * Calculates to participate in open auction at or below PARTICIPATION_RATE percentage.
     * The method takes openingVolumes from market data feed, and performs the calculation accordingly.
     * Any residual order quantities will be staged and processed during continuous trading.
     *
     * @param uncrossedOrders
     * @param openingVolumes
     * @return ordersToSendToOpen
     */
    protected ArrayList<Order> calculateUncrossedOrdersToOpenVolume(ArrayList<Order> uncrossedOrders, HashMap<String, OpeningVolume> openingVolumes){
        ArrayList<Order> ordersToSendToOpen = new ArrayList<>();
        ArrayList<Order> residualOrders = new ArrayList<>();  // residual qty after checking 20% open volume

        for (Order order: uncrossedOrders){
            int threshold;
            if (!openingVolumes.containsKey(order.getStockSymbol())){
                System.out.println("CalculateUncrossedOrdersToOpenVolume ERROR: No volume found! Instrument code " + order.getStockSymbol());
                continue;
            }
            else {
                OpeningVolume openingVolume = openingVolumes.get(order.getStockSymbol());
                threshold = (int) Math.floor(openingVolume.getVolume() * MAX_PARTICIPATION);
            }

            // cap participation at 20%, create residual order to trade in continuous session
            if (order.getQuantity() > threshold) {
                Order residue = new Order(order.getStockSymbol(),
                        threshold > 0? order.getQuantity() - threshold : order.getQuantity(), order.getSide());
                residualOrders.add(residue);

                order.setQuantity(threshold);
            }

            // skip ZERO or lesser threshold orders in open auction
            if (threshold <= 0){
                System.out.println("CalculateUncrossedOrdersToOpenVolume WARNING: Open volume threshold is equal or less than ZERO! Skipping " + order.getSide() + " " + order.getStockSymbol());
                continue;
            }
            ordersToSendToOpen.add(order);
        }

        // spawn a new thread to stage and process residual qty to trade into the continuous session
        Runnable runnable = () -> {
            stageOrdersToContinuousTrading(residualOrders);
        };
        Thread thread = new Thread(runnable);
        thread.start();
        return ordersToSendToOpen;
    }

    /**
     * Stages residual quantities for intraday processing in continuous trading session.
     * @param residualOrders
     * @return boolean
     */
    protected Boolean stageOrdersToContinuousTrading(ArrayList<Order> residualOrders){
        // TODO: after checking 20% open volume, stage the order and continue to process residue qty during continuous session

        residualOrders.forEach(order -> System.out.println("StageOrdersToContinuousTrading INFO: Staged order " + order.getSide() + " " + order.getStockSymbol() + " with " + order.getQuantity() + " shares"));
        return true;
    }

    /**
     * Persists crossed symbol/qty pairs into database for trade operations.
     * @param crossedSymbolQty
     * @return boolean
     */
    protected Boolean persistCrossedTrades(HashMap<String, Integer> crossedSymbolQty){
        for (HashMap.Entry<String, Integer> entry : crossedSymbolQty.entrySet()) {
            String stockSymbol = entry.getKey();
            Integer quantity = entry.getValue();

            System.out.println("PersistCrossedTrades INFO: Successfully crossed " + stockSymbol +  " with " + quantity + " shares");

            // TODO: persist execution details into database
        }
        return true;
    }

    /**
     * Creates orders in an ArrayList from uncrossedSymbolQty hashmap.
     * The method skips any zero quantity order from further processing.
     *
     * @param uncrossedSymbolQtyMap
     * @return uncrossedOrders
     */
    protected ArrayList<Order> createUncrossedOrders(HashMap<String, Integer> uncrossedSymbolQtyMap){
        ArrayList<Order> uncrossedOrders = new ArrayList<>();
        for (HashMap.Entry<String, Integer> entry : uncrossedSymbolQtyMap.entrySet()) {
            String stockSymbol = entry.getKey();
            Integer quantity = entry.getValue();
            if (quantity == 0){ // skip 0 share order
                continue;
            }

            /* For execution purposes, brokers shall only know Long Buy or Long Sell.
               To avoid a squeeze, they shouldn't know if a Buy trade was meant for covering existing short exposure. */
            Order newOrder = new Order(stockSymbol, Math.abs(quantity), quantity > 0? Order.Side.BUY : Order.Side.SELL);

            uncrossedOrders.add(newOrder);
        }
        return uncrossedOrders;
    }
}