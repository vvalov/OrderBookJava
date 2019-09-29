import java.io.BufferedReader; 
import java.io.InputStreamReader; 

/**
 * This application class is designed to be running application in user-specified mode
 * Supported modes are test and regular - test mode runs the test below
 * In industry environment we'd want the tests to do auto-validation, but for the
 * purposes of the exercise I've only done the validation manually by looking at the
 * output 
 * We'd also want each of the classes unit tested in isolation (which is possible
 * given the current design), but I haven't gone into all that, as this is not a 
 * persistent project and the benefits of unit testing will be limited
 * @author valov
 */
public class Application {
    
    public static void main(String[] args) {
        boolean isTest = true; // change this flag to run tests
        System.out.println("Application has started!"); 
        if (isTest)
            test();
        else
            run();
    }
    
    // run OrderBook in regular input mode
    public static void run() 
    {
        try
        {
            AppLogger log = new AppLogger();
            OrderBook book = new OrderBook(log);

            BufferedReader reader =  
                       new BufferedReader(new InputStreamReader(System.in)); 
            
            //Enter data using BufferReader 
            while (true)
            {
                // Reading data using readLine 
                String event = reader.readLine(); 

                if (!InputParser.skipInputLine(event))
                {
                    Order o = InputParser.parseInput(event);
                    book.addOrder(o, true);
                }
            }
        }
        catch(Exception e) 
        {
            // keeping error handling to minimum
            System.err.println("Exception happened while reading input: " + e.getMessage());
        }
    }
    
    // run test for OrderBook
    public static void test() {
        AppLogger log = new AppLogger();
        OrderBook book = new OrderBook(log);
        short price15 = 15;
        short price16 = 16;
        short price18 = 18;
        short price19 = 19;
        short price20 = 20;
        short price21 = 21023;
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
        book.addOrder(lb3, false);
        book.addOrder(ls1, false);
        book.addOrder(ls2, false);
        book.addOrder(ls3, true); // print OrderBook after entry
        book.addOrder(i2, true); // print OrderBook after entry
        book.addOrder(i1, true); // print OrderBook after entry
        LimitOrder lb4 = new LimitOrder('B', 92342, price21, 150000);
        book.addOrder(lb4, true); // print OrderBook after entry
    }
}
