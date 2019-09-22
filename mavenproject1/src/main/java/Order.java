
import java.sql.Timestamp;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.Timestamp;

/**
 *
 * @author valov
 */
abstract interface Order extends Comparable<Order> {
    public int fill(int targetVolume, int targetPrice, boolean isAggresive);
    public boolean isFullyFilled();
    public boolean isBuy();
    public int remainingTotalVolume();
    public int price();
    public Timestamp timestamp();
    public void refresh();
    public void print();
}


