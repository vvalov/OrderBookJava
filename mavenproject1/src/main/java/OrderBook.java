import java.util.TreeSet;
import java.util.HashMap; 
import java.util.Iterator;

/**
 * This is the main class of the application
 * It keeps state locally of what buy and sell standing orders have been submitted 
 * and maintains order of priority, based on price and timestamp
 * It provides the user with ability to add new orders and generates execution
 * messages if any orders match.
 * @author valov
 */
public class OrderBook {
    TreeSet<Order> d_buyOrders;
    TreeSet<Order> d_sellOrders;
    AppLogger d_log;
    
    // to use when printing
    static String d_logTop = 
"+-----------------------------------------------------------------+\n" +
"| BUY      |                                             SELL     |\n" +
"| Id       | Volume      | Price | Price | Volume      | Id       |\n" +
"+----------+-------------+-------+-------+-------------+----------+\n";
    static String d_logBottom = 
"+-----------------------------------------------------------------+\n";
    static String d_emptyBuyOrderLine = 
"|          |             |       |";
    static String d_emptySellOrderLine = 
                                  "       |             |          |\n";
    
    /**
     * We can make this take an interface for passing different loggers
     * if needed, but for the purposes of this exercise I don't see much benefit
     * @param l - logger object to be used by the book
     * @author valov
     */
    public OrderBook(AppLogger l) {
        d_buyOrders = new TreeSet<>();
        d_sellOrders = new TreeSet<>();
        d_log = l;
    }
    
    void logBook()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("BUY ORDERS\n");
        for (Order o : d_buyOrders) {
            sb.append(o.toString());
            sb.append("\n");
        }
        sb.append("\nSELL ORDERS\n");
        for (Order o : d_sellOrders) {
            sb.append(o.toString());
            sb.append("\n");
        }
        d_log.logInfo(sb.toString());
    }
    
    void prettyPrint()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(d_logTop);
        Iterator<Order> it_buy = d_buyOrders.iterator();
        Iterator<Order> it_sell = d_sellOrders.iterator();
        
        for (int i = 0; i < Math.max(d_buyOrders.size(), d_sellOrders.size()); ++i)
        {
            if (i >= d_buyOrders.size())
                sb.append(d_emptyBuyOrderLine);
            else 
                sb.append(it_buy.next().toOrderBookString());
            
            
            if (i >= d_sellOrders.size())
                sb.append(d_emptySellOrderLine);
            else
                sb.append(it_sell.next().toOrderBookString());
        }
        
        sb.append(d_logBottom);
        System.out.println(sb.toString());
    }
    
    // we assume order details are valid, no duplicate orderIds
    void addOrder(Order o, boolean printBook)
    {
        d_log.logInfo("New order received:\n" + o.toString());
        boolean isFullyFilled = tryExecuteNewOrder(o);
        if (!isFullyFilled)
        {
            d_log.logInfo("Order not fully filled, adding to order book.");
            if (o.isBuy())
            {
                d_buyOrders.add(o);
            }
            else
            {
                d_sellOrders.add(o);
            }
        }
        if (printBook) {
            logBook(); // to log file
            prettyPrint(); // this logs to std out as per requirement
        }
    }
    
    /**
     * This will match an incoming order against the current standing orders in
     * the book in priority order (based on limit price and timestamp)
     * This will also generate matching messages to std out, if applicable
     * @param o incoming order
     * @return true if incoming order has been fully matched, false otherwise
     */
    boolean tryExecuteNewOrder(Order o)
    {
        ExecutionDetailsMap em = new ExecutionDetailsMap(o.isBuy());
        if (o.isBuy())
        {
            if (!d_sellOrders.isEmpty())
            {
                // try to execute against sellOrders
                Order targetOrder = d_sellOrders.first();
                boolean tryExecute = true;
                while (tryExecute && o.remainingTotalVolume() > 0 && !d_sellOrders.isEmpty())
                {
                    int fillAmt = targetOrder.fill(o.remainingTotalVolume(), o.price(), false); // passive
                    d_log.logInfo("fillAmt=" + fillAmt + " buyId=" + o.id() + " sellId=" + targetOrder.id());
                    if (fillAmt > 0)
                    {
                        o.fill(fillAmt, o.price(), true); // aggresive
                        em.addExec(o, targetOrder, fillAmt);
                        
                        if (targetOrder.isFullyFilled())
                        {
                            d_sellOrders.pollFirst();
                            if (targetOrder.remainingTotalVolume() > 0)
                            {
                                // if target order is fully filled, but we still have volume left
                                // it means we have an Iceberg order.
                                // We need to add a new order for the new amount
                                targetOrder.refresh();
                                addOrder(targetOrder, false); // this should not match any orders
                            }

                            // update the targetOrder with the new front of the set
                            if (!d_sellOrders.isEmpty())
                                targetOrder = d_sellOrders.first();
                        }
                    }
                    else
                    {
                        // if we can't fill at the best sell, we are done
                        tryExecute = false;
                    }
                }
            }
        }
        else
        {
            if (!d_buyOrders.isEmpty())
            {
                // try to execute against buyOrders
                Order targetOrder = d_buyOrders.first();
                boolean tryExecute = true;
                while (tryExecute && o.remainingTotalVolume() > 0 && !d_buyOrders.isEmpty())
                {
                    int fillAmt = targetOrder.fill(o.remainingTotalVolume(), o.price(), false); // passive
                    d_log.logInfo("fillAmt=" + fillAmt + " buyId=" + targetOrder.id() + " sellId=" + o.id());
                    if (fillAmt > 0)
                    {
                        o.fill(fillAmt, o.price(), true); // aggresive
                        em.addExec(o, targetOrder, fillAmt);
                        
                        if (targetOrder.isFullyFilled())
                        {
                            d_buyOrders.pollFirst();
                            if (targetOrder.remainingTotalVolume() > 0)
                            {
                                // if target order is fully filled, but we still have volume left
                                // it means we have an Iceberg order.
                                // We need to add a new order for the new amount
                                targetOrder.refresh();
                                addOrder(targetOrder, false); // this should not match any orders
                            }

                            // update the targetOrder with the new front of the set
                            if (!d_buyOrders.isEmpty())
                                targetOrder = d_buyOrders.first();
                        }
                    }
                    else
                    {
                        // if we can't fill at the best sell, we are done
                        tryExecute = false;
                    }
                }
            }
        }
        em.generateFills();
        return (o.remainingTotalVolume() == 0);
    }
    
    // helper class for generating fill messages
    private class ExecutionDetails {
        public int buyOrderId;
        public int volume;
        public int price;
        public int sellOrderId;
        
        public void generateOrderMessage()
        {
            StringBuilder sb = new StringBuilder();
            sb.append(buyOrderId);
            sb.append(",");
            sb.append(sellOrderId);
            sb.append(",");
            sb.append(volume);
            sb.append(",");
            sb.append(price);
            System.out.println(sb.toString());
        }
    }
    
    // wrapper around orderIdToExecMap to avoid duplicate code
    private class ExecutionDetailsMap {
        private HashMap<Integer, ExecutionDetails> d_orderIdToExecMap;
        private final boolean d_isAggresiveBuy;
        
        ExecutionDetailsMap(boolean isBuy) {
            d_orderIdToExecMap = new HashMap<>();
            d_isAggresiveBuy = isBuy;
        }
        
        void addExec(Order o, Order targetOrder, int fillAmt) {
            int orderId = targetOrder.id();
            if (d_orderIdToExecMap.containsKey(orderId))
            {
                ExecutionDetails ed = d_orderIdToExecMap.get(orderId);
                ed.volume = ed.volume + fillAmt;
                d_orderIdToExecMap.put(orderId, ed);
            }
            else
            {
                ExecutionDetails ed = new ExecutionDetails();
                ed.buyOrderId = d_isAggresiveBuy ? o.id() : orderId;
                ed.sellOrderId = d_isAggresiveBuy ? orderId : o.id();
                ed.volume = fillAmt;
                ed.price = targetOrder.price();
                d_orderIdToExecMap.put(orderId, ed);
            }
        }
        
        void generateFills() {
            d_orderIdToExecMap.forEach((k,v)->v.generateOrderMessage()); 
        }
    }
}
