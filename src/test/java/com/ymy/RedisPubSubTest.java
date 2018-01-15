package com.ymy;

import com.ymy.common.JedisUtils;
import com.ymy.entity.Cmd;
import com.ymy.scoket.SubPubListener;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

/**
 * @author chenjunwen
 * redis发布订阅测试
 */
public class RedisPubSubTest {
    public static void main(String[] args) throws InterruptedException {
        Jedis jedis = JedisUtils.getJedis();
        new SubscribeThread(jedis).start();
    }
}

/**
 * 线程启动监听
 */
@Component
class SubscribeThread extends Thread {

    Jedis jedis;

    SubPubListener subPubListener = new SubPubListener();
    SubscribeThread(Jedis jedis){
        this.jedis = jedis;
    }

    @Override
    public void run() {
        try {
            System.out.println("启动监听啦");
            jedis.subscribe(subPubListener, Cmd.HouseDeal.value(), Cmd.OrderBook.value());
        }catch (Exception e){
            this.start();
        }
    }
}