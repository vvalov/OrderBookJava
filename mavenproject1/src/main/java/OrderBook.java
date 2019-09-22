/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author valov
 */
public class OrderBook {
    TreeSet<Order> d_buyOrders;
    TreeSet<Order> d_sellOrders;
    
    public OrderBook() {
        d_buyOrders = new TreeSet<Order>();
        d_sellOrders = new TreeSet<Order>();
    }
    
    public void print()
    {
        System.out.println("BUY ORDERS");
        for (Order o : d_buyOrders)
            o.print();
        System.out.println("SELL ORDERS");
        for (Order o : d_sellOrders)
            o.print();
    }
    
    public void addOrder(Order o, boolean printBook)
    {
        //System.out.println("adding order:"); 
        //o.print();
        boolean isFullyFilled = tryExecuteNewOrder(o);
        if (!isFullyFilled)
        {
            if (o.isBuy())
            {
                d_buyOrders.add(o);
            }
            else
            {
                d_sellOrders.add(o);
            }
        }
        if (printBook)
            print();
        
    }
    
    public boolean tryExecuteNewOrder(Order o)
    {
        if (o.isBuy())
        {
            if (!d_sellOrders.isEmpty())
            {
                //System.out.println("matching against sell orders. Target order:");
                // try to execute against sellOrders
                Order targetOrder = d_sellOrders.first();
                //targetOrder.print();
                boolean tryExecute = true;
                while (tryExecute && o.remainingTotalVolume() > 0 && !d_sellOrders.isEmpty())
                {
                    int fillAmt = targetOrder.fill(o.remainingTotalVolume(), o.price(), false); // passive
                    System.out.println("fillAmt="+fillAmt);
                    if (fillAmt > 0)
                    {
                        if (targetOrder.isFullyFilled())
                        {
                            d_sellOrders.pollFirst();
                            if (targetOrder.remainingTotalVolume() > 0)
                            {
                                // if order is fully filled, but we still have volume left
                                // it means we have an Iceberg order.
                                // We need to add a new order for the new amount
                                targetOrder.refresh();
                                addOrder(targetOrder, false); // this will not execute

                                // update the targetOrder with the new front of the set
                                targetOrder = d_sellOrders.first();
                            }
                        }
                        o.fill(fillAmt, o.price(), true); // aggresive
                        System.out.println("Filled amt=" + fillAmt + " at price=" + targetOrder.price());
                        // TODO: update data structure of fills
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
                //System.out.println("matching against buy orders. Target order:");
                Order targetOrder = d_buyOrders.first();
                //targetOrder.print();
                boolean tryExecute = true;
                while (tryExecute && o.remainingTotalVolume() > 0 && !d_buyOrders.isEmpty())
                {
                    int fillAmt = targetOrder.fill(o.remainingTotalVolume(), o.price(), false); // passive
                    System.out.println("fillAmt="+fillAmt);
                    if (fillAmt > 0)
                    {
                        if (targetOrder.isFullyFilled())
                        {
                            d_buyOrders.pollFirst();
                            if (targetOrder.remainingTotalVolume() > 0)
                            {
                                // if order is fully filled, but we still have volume left
                                // it means we have an Iceberg order.
                                // We need to add a new order for the new amount
                                targetOrder.refresh();
                                addOrder(targetOrder, false); // this will not execute

                                // update the targetOrder with the new front of the set
                                targetOrder = d_buyOrders.first();
                            }
                        }
                        o.fill(fillAmt, o.price(), true); // aggresive
                        System.out.println("Filled amt=" + fillAmt + " at price=" + targetOrder.price());
                        // TODO: update data structure of fills
                    }
                    else
                    {
                        // if we can't fill at the best sell, we are done
                        tryExecute = false;
                    }
                }
            }
        }
        
        return (o.remainingTotalVolume() == 0);
    }
    
}
