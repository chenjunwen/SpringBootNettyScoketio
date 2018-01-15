package com.ymy;


import com.alibaba.fastjson.JSONObject;
import com.ymy.common.JedisUtils;
import com.ymy.entity.Cmd;
import redis.clients.jedis.Jedis;

public class RedisPublishTest {
    public static void main(String[] args) {

        Jedis jedis = JedisUtils.getJedis();
        for (int i = 0; i < 1000; i++) {
            JSONObject json = new JSONObject();
            json.put("batchId", "10001");
            json.put("order","这是个10001订单啊"+i);
            jedis.publish(Cmd.OrderBook.value(), json.toJSONString());
            JSONObject json2 = new JSONObject();
            json.put("batchId", "10002");
            json.put("order","这是个10002订单啊"+i);
            jedis.publish(Cmd.OrderBook.value(), json.toJSONString());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
