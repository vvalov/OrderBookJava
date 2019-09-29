import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.io.IOException;
import java.sql.Timestamp;

/**
 * We can make this class arbitrarily complex, and provide lots of 
 * useful functionality, but for the purposes of this exercise I 
 * have decided to keep functionality to minimum, and to only 
 * provide basic logging to a file.
 * @author valov
 */
public class AppLogger {
    private Logger d_logger;
    private FileHandler d_fh;  
    
    AppLogger() { 
        d_logger = Logger.getLogger("OrderBookLog"); 
        try {  
            Timestamp t = new Timestamp(System.currentTimeMillis());
            t.getTime();
            d_fh = new FileHandler("./" + t.getTime() + "_OrderBookLog.log");  
            // This block configure the logger with handler and formatter  
            d_logger.addHandler(d_fh);
            SimpleFormatter formatter = new SimpleFormatter();  
            d_fh.setFormatter(formatter);  
            
            // to not print to std out
            // set to true if you want logs in std out
            d_logger.setUseParentHandlers(false);

        } catch (SecurityException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }
    
    public void logError(String errStr)
    {
        // in real life we can add alerting here, or we could monitor logs 
        // for some unique string identifier and alert from there
        d_logger.severe(errStr);
    }
    
    public void logInfo(String info)
    {
        d_logger.info(info);
    }
}
