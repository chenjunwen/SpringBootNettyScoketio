package com.ymy.entity;

import org.apache.commons.lang3.EnumUtils;

public enum Cmd {
    /**
     * 订单命令
     */
    OrderBook("orderBook"),

    /**
     * 房间成交
     */
    HouseDeal("houseDeal"),

    /**
     * 在线人数
     */
    OnlineNumber("onlineNumber");

    private String cmdStr;



    Cmd(String val){
        this.cmdStr = val;
    }

    public String value(){
        return this.cmdStr;
    }

    /**
     * 通过值获取枚举
     * @param name
     * @return
     */
    public static Cmd getEnum(String name) {
        for (Cmd cmd : Cmd.values()){
            if(name.equals(cmd.value())){
                return cmd;
            }
        }
        return null;
    }



    /**
     * 获取到所有的value
     * @return
     */
    public static String[] getValues(){
        Cmd[] enums = Cmd.values();
        String [] values = new String[enums.length];
        for (int i = 0; i < enums.length; i++) {
            values[i] = enums[i].value();
        }
        return values;
    }

    /**
     * 判断某个订阅是否存在
     * @param cmdString
     * @return
     */
    public static boolean hasCmd(String cmdString){
        for(Cmd cmd :Cmd.values()){
            if(cmd.value().equals(cmdString)){
                return true;
            }
        }
        return false;
    }
}
