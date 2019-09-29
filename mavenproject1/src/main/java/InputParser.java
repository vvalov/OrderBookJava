/**
 * Simple class for parsing lines from std in, as per spec
 * Assumes valid input, as per spec
 * @author valov
 */
public class InputParser {
    static boolean skipInputLine(String s)
    {
        if (s.length() == 0 || s.charAt(0) == ' ' || s.charAt(0) == '#')
            return true;
        return false;
    }
    
    static Order parseInput(String s)
    {
        // (assume input is valid)
        String[] items = s.split(",");
        char side = items[0].charAt(0);
        int id = Integer.parseInt(items[1]);
        short price = Short.parseShort(items[2]);
        int amount = Integer.parseInt(items[3]);
        if (items.length == 4)
        {
            LimitOrder lo = new LimitOrder(side, id, price, amount);
            return lo;
        }
        else // length == 5 
        {
            int peak = Integer.parseInt(items[4]);
            IcebergOrder io = new IcebergOrder(side, id, price, amount, peak);
            return io;
        }
    }
}
