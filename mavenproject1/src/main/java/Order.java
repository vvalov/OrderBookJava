import java.sql.Timestamp;

/**
 * This is a generic order interface
 * For this exercise only Limit and Iceberg orders are supported, this may need
 * to be amended to support some other types of orders.
 * @author valov
 */
abstract interface Order extends Comparable<Order> {
    /**
     * Try to execute the current order at the specified price and volume
     * Order will execute if target price is not worse than the price of the 
     * current order
     * If targetVolume is larger than size of the order, the amount filled will
     * be returned
     * @param targetVolume target amount to fill
     * @param targetPrice target price to fill at
     * @param isAggresive the style of execution (matters for Iceberg orders)
     * @return amount filled in reality
     */
    public int fill(int targetVolume, int targetPrice, boolean isAggresive);
    
    // does the order have any visible volume left
    public boolean isFullyFilled();
    
    // get the side of the order
    public boolean isBuy();
    
    // total remaining volume, including hidden volume
    public int remainingTotalVolume();
    
    // limit price of the order
    public int price();
    
    // unique identifier of the order
    public int id();
    
    // timestamp of Order - used for setting time priority in Order Book
    public Timestamp timestamp();
    
    // updates the timestamp and any other fields that need updating
    // this is for convenience, to avoid creating new orders when 
    // Iceberg orders are fullyFilled
    public void refresh();
    
    // string representation of the order
    public String toString();
    
    // used when printing the Order Book
    public String toOrderBookString();
}
