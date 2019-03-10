package com.toutiaozixun.async;

/**
 * @author cyx
 * @data 2019/2/16 14:44
 */
public enum EventType {
    Like(0),   //喜欢
    COMMENT(1), //评论
    LOGIN(2),//登录
    MAIL(3),//邮件
    REGEIST(4); //注册
    private int value;
    EventType (int value){
        this.value =value;
    }
    public int getValue(){
        return value;
    }
}
