package com.ymy.scoket;

import redis.clients.jedis.JedisPubSub;

/**
 * 过期key监听
 */
public class KeyExpiredListener extends JedisPubSub{

    /**
     * 匹配表达式
     * @param pattern
     * @param channel
     * @param message
     */
    @Override
    public void onPMessage(String pattern, String channel, String message) {
        System.out.println("匹配表达式订阅： "
                + pattern + " ----->" + channel + "----->> " + message);
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        System.out.println("onPSubscribe "
                + pattern + "----- " + subscribedChannels);
    }
}
