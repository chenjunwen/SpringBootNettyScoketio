package com.ymy.scoket;

import com.ymy.common.JedisUtils;
import com.ymy.entity.Cmd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * @author chenjunwen
 */
@Component
public class SubPubListener extends JedisPubSub {


    SubPubWebSocket subPubWebSocket = new SubPubWebSocket();

    @Autowired
    WebSocketTest webSocketTest;

    /**
     * 获取订阅消息内容后触发
     * @param channel
     * @param message
     */
    @Override
    public void onMessage(String channel, String message) {
        System.out.println("收到订阅消息：" + channel + "=" + message);
        if (subPubWebSocket==null){return;}
        if(Cmd.OrderBook.value().equals(channel)){
            subPubWebSocket.sendMessage(message, Cmd.OrderBook);
        }else if (Cmd.HouseDeal.value().equals(channel)){
            subPubWebSocket.sendMessage(message, Cmd.HouseDeal);
        }

    }




    /**
     * 初始化订阅时候的处理
     * @param channel
     * @param subscribedChannels
     */
    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        System.out.println("初始化订阅：" + channel + "=" + subscribedChannels);
    }

    /**
     * 取消订阅时候的处理
     * @param channel
     * @param subscribedChannels
     */
    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        System.out.println("取消订阅：" + channel + "=" + subscribedChannels);
    }

}

/**
 * 线程启动监听
 */
@Component
class SubscribeThread extends Thread {

    SubPubListener subPubListener = new SubPubListener();

    private Jedis jedis;

    SubscribeThread(){
        this.start();
    }

    @Override
    public void run() {
        try {
            jedis = JedisUtils.getJedis();
            jedis.subscribe(subPubListener, Cmd.getValues());
        }catch (Exception e){
            e.printStackTrace();
            if (jedis!=null){
                jedis.close();
            }

            this.start();
        }
    }
}