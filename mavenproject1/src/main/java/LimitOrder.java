import java.sql.Timestamp;
import java.lang.Integer;

/**
 * This class represents Limit Order, see Order interface for more documentation
 * @author valov
 */

public class LimitOrder implements Order {
    enum Side {
        BUY, SELL 
    }
    
    final int d_orderId;
    final short d_price;
    final Side d_side;
    int d_visibleSize;
    Timestamp d_timestamp;
    
    public LimitOrder(char side, int id, short price, int size) 
    {
        d_orderId = id;
        d_price = price;
        d_visibleSize = size;
        d_side = (side == 'B') ? Side.BUY : Side.SELL;
        d_timestamp = new Timestamp(System.currentTimeMillis());
    }
    
    @Override
    public boolean isBuy()
    {
        return d_side == Side.BUY;
    }
    
    @Override
    public int remainingTotalVolume()
    {
        return d_visibleSize;
    }
    
    @Override
    public int price()
    {
        return d_price;
    }
    
    @Override
    public int id()
    {
        return d_orderId;
    }
    
    @Override
    public Timestamp timestamp()
    {
        return d_timestamp;
    }
    
    @Override
    public void refresh()
    {
        d_timestamp = new Timestamp(System.currentTimeMillis());
    }
    
    // tries to fill targetVol at targetPrice and return amount filled
    // updates remaining size of the Order
    @Override
    public int fill(int targetVol, int targetPrice, boolean isAggresive)
    {
        if (d_side == Side.BUY && targetPrice > d_price)
            return 0;
        if (d_side == Side.SELL && targetPrice < d_price)
            return 0;
        
        if (targetVol <= d_visibleSize)
        {
            d_visibleSize = d_visibleSize - targetVol;
            return targetVol;
        }
        else
        {
            int fillSize = d_visibleSize;
            d_visibleSize = 0;
            return fillSize;
        }
    }
    
    @Override
    public boolean isFullyFilled()
    {
        return d_visibleSize == 0;
    }
    
    @Override
    public int compareTo(Order other)
    {
        int comp = 0;
        if (d_side == Side.BUY)
        {
            if (d_price != other.price())
                comp = Integer.compare(other.price(), d_price);
            else
                comp = d_timestamp.compareTo(other.timestamp());
        }
        else // SELL Order
        {
            if (d_price != other.price())
                comp = Integer.compare(d_price, other.price()); // reverse order
            else
                comp = d_timestamp.compareTo(other.timestamp()); // same order
        }
        if (comp == 0) // in case of the same timestamp and price, look at id
            comp = Integer.compare(d_orderId, other.id());
        return comp;
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(" orderId=" + d_orderId);
        sb.append(" d_price=" + d_price);
        sb.append(" d_side=" + (isBuy() ? "B" : "S"));
        sb.append(" d_visibleSize=" + d_visibleSize);
        sb.append(" d_timestamp=" + d_timestamp.toString());

        return sb.toString();
    }
    
    @Override
    public String toOrderBookString()
    {
        if (isBuy())
        {
            return String.format("|%10d|%,13d|%,7d|",id(), d_visibleSize, d_price);
        }
        else
        {
            return String.format("%,7d|%,13d|%10d|\n",d_price, d_visibleSize, id());
        }
    }
}