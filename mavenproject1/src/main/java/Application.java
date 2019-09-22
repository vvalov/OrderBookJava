/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author valov
 */
public class Application {
    
    public static void main(String[] args) {
        System.out.println("Hello World!"); 
        
        OrderBook book = new OrderBook();
        short price1 = 15;
        short price2 = 14;
        LimitOrder l1 = new LimitOrder('B', 1, price1, 10000);
        LimitOrder l2 = new LimitOrder('S', 2, price2, 15000);
        IcebergOrder i1 = new IcebergOrder('S', 3, price2, 15000, 2000);
        IcebergOrder i2 = new IcebergOrder('B', 4, price2, 25000, 2000);
        //book.addOrder(l1);
        //book.addOrder(l2);
        book.addOrder(i1, true);
        book.addOrder(i2, true);
    }
}
