/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author valov
 */
public class IcebergOrder extends LimitOrder {
    final int d_peak;
    int d_totalSize;
    
    public IcebergOrder(char side, int id, short price, int size, int peak) 
    {
        super(side, id, price, Math.min(peak, size));
        d_peak = peak;
        d_totalSize = size;
    }
    
    @Override
    public int remainingTotalVolume()
    {
        return d_totalSize;
    }
    
    @Override
    public int fill(int targetVol, int targetPrice, boolean isAggresive)
    {
        if (isAggresive)
        {
            // this is called when iceberg order is aggresively entering the market
            // we may execute bigger than visible size at once
            d_totalSize = d_totalSize - targetVol;
            d_visibleSize = Math.min(d_peak, d_totalSize);
            return targetVol;
        }
        else
        {
            // when passive, execution works as for Limit Order
            int filledAmt = super.fill(targetVol, targetPrice, isAggresive);
            d_totalSize = d_totalSize - filledAmt;
            return filledAmt;
        }
    }
     
    @Override
    public void refresh()
    {
        super.refresh();
        d_visibleSize = Math.min(d_peak, d_totalSize);
    }
    
    @Override
    public void print()
    {
        super.print();
        StringBuilder sb = new StringBuilder();
        sb.append(" d_peak=" + d_peak);
        sb.append(" d_totalSize=" + d_totalSize);
        System.out.println(sb.toString());
    }
}
