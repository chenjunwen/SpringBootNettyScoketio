package com.ymy;


import com.alibaba.fastjson.JSONObject;
import com.ymy.common.JedisUtils;
import com.ymy.entity.Cmd;
import redis.clients.jedis.Jedis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisPublishTest {
    public static void main(String[] args) {

        Jedis jedis = JedisUtils.getJedis();
        ExecutorService executorService = Executors.newCachedThreadPool();
        // 发布订单测试
        /*executorService.execute(()->{
            for (int i = 0; i < 1000; i++) {
                JSONObject json = new JSONObject();
                json.put("batchId", "10001");
                json.put("order","这是个10001订单啊"+i);
                jedis.publish(Cmd.OrderBook.value(), json.toJSONString());
                JSONObject json2 = new JSONObject();
                json2.put("batchId", "10002");
                json2.put("order","这是个10002订单啊"+i);
                System.out.println("订单"+i);
                jedis.publish(Cmd.OrderBook.value(), json.toJSONString());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });*/

        // 过期key测试
        /*executorService.execute(()->{
            for (int i = 0; i < 1000 ; i++) {
                String key = "test" + i;
                jedis.setnx(key, i+"");
                jedis.expire(key, 5);
                System.out.println("发布: ----------------" + key);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });*/

        String str = "St.LockRoom.f713f05cd6c949b1a438bae4173a286a";
        System.out.println(str);
        String key = str.substring(12);
        System.out.println(key+"------");

        //executorService.shutdown();

    }
}
