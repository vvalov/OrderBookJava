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
        test();
    }
    
    public static void test() {
        OrderBook book = new OrderBook();
        short price15 = 15;
        short price14 = 14;
        short price16 = 16;
        short price17 = 17;
        short price18 = 18;
        short price19 = 19;
        short price20 = 20;
        LimitOrder lb1 = new LimitOrder('B', 1, price15, 10000);
        LimitOrder lb2 = new LimitOrder('B', 2, price15, 8000);
        LimitOrder lb3 = new LimitOrder('B', 3, price16, 15000);
        LimitOrder ls1 = new LimitOrder('S', 4, price18, 15000);
        LimitOrder ls2 = new LimitOrder('S', 5, price19, 10000);
        LimitOrder ls3 = new LimitOrder('S', 6, price20, 13000);
        IcebergOrder i1 = new IcebergOrder('S', 7, price15, 20000, 2000);
        IcebergOrder i2 = new IcebergOrder('B', 8, price19, 28000, 1500);
        book.addOrder(lb1, false);
        book.addOrder(lb2, false);
        book.addOrder(lb3, true);
        book.addOrder(ls1, false);
        book.addOrder(ls2, false);
        book.addOrder(ls3, true);
        //book.addOrder(l2);
        book.addOrder(i2, true);
        book.addOrder(i1, true);
    }
}
