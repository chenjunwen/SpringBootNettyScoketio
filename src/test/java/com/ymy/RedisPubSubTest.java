package com.ymy;

import com.ymy.common.JedisUtils;
import com.ymy.entity.Cmd;
import com.ymy.scoket.KeyExpiredListener;
import com.ymy.scoket.SubPubListener;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author chenjunwen
 * redis发布订阅测试
 */
public class RedisPubSubTest {
    public static void main(String[] args) throws InterruptedException {
        Jedis jedis = JedisUtils.getJedis();
        System.out.println(jedis+"----->>");
        SubPubListener subPubListener = new SubPubListener();
        KeyExpiredListener keyExpiredListener = new KeyExpiredListener();
        ExecutorService executorService = Executors.newCachedThreadPool();

        // 订阅订单
        /*executorService.submit(()->{
            System.out.println("1------>>");
            jedis.subscribe(subPubListener, Cmd.HouseDeal.value(), Cmd.OrderBook.value());
        });*/

        // 订阅过期key
        executorService.submit(()->{
            System.out.println("2------>>");
            //"__key*__:*"          "__keyevent@0__:expired
            jedis.psubscribe(keyExpiredListener, "__key*__:expired");
        });

    }

}