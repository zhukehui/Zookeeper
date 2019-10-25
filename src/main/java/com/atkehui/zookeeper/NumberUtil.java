package com.atkehui.zookeeper;

/**
 * @author eternity
 * @create 2019-10-21 22:22
 */
public class NumberUtil {
    private static int number =  0 ;
    public String getOrderNumber(){
        return "" + (++number);
    }
}
