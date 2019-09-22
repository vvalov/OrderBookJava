
import java.sql.Timestamp;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.Timestamp;
import java.lang.Integer;

/**
 *
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
        if (d_side == Side.BUY)
        {
            if (d_price != other.price())
                return Integer.compare(d_price, other.price());
            else
                return d_timestamp.compareTo(other.timestamp());
        }
        else // SELL Order
        {
            if (d_price != other.price())
                return Integer.compare(other.price(), d_price); // reverse order
            else
                return d_timestamp.compareTo(other.timestamp()); // same order
        }
    }
    
    @Override
    public void print()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(" orderId=" + d_orderId);
        sb.append(" d_price=" + d_price);
        sb.append(" d_side=" + (isBuy() ? "B" : "S"));
        sb.append(" d_visibleSize=" + d_visibleSize);
        sb.append(" d_timestamp=" + d_timestamp.toString());

        System.out.println(sb.toString());
    }
}