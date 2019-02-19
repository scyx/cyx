package com.nowcoder.async;

/**
 * @author cyx
 * @data 2019/2/16 14:44
 */
public enum EventType {
    Like(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3);
    private int value;
    EventType (int value){
        this.value =value;
    }
    public int getValue(){
        return value;
    }
}
