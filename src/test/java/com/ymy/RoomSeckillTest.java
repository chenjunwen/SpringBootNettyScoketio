package com.ymy;

import com.ymy.common.FileUtils;
import com.ymy.common.JedisUtils;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 房间数量秒杀压力测试可直接运行
 */
public class RoomSeckillTest {

    /**
     * 添加lua脚本至redis缓存中
     */
    public String addRedisLoad(JedisUtils jedisUtils){
        String file = FileUtils.getFile("roomSeckill.lua");
        String has = jedisUtils.scriptLoad(file);
        return has;
    }

    /**
     * 执行秒杀
     */
    void execSeckill(JedisUtils redis) throws InterruptedException {
        // 清空缓存
        Jedis jedis = JedisUtils.getJedis();

        // 初始化房源数据
        String has = addRedisLoad(redis);
        redis.addValue("St.Num", 1513+"");

        // 用于统计耗时
        int threadNumber = 10;

        long start = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(threadNumber);
        for (int i = 0; i < 10000; i++) {
            executor.execute(new MySeckillRunnable(redis, has));
        }
        executor.shutdown();

        List<String> list = new ArrayList<String>();
        while(true){
            int count = Thread.activeCount();
            if(count == 3){
                break;
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("耗时时间：" + (end-start) +"毫秒");
    }


    public static void main(String[] args) throws InterruptedException {
        JedisUtils redis = new JedisUtils();
        RoomSeckillTest roomSeckillTest = new RoomSeckillTest();
        //Map<String, String> hgetall = redis.hgetall("Hs.RoomSeckill.BanId");
        roomSeckillTest.execSeckill(redis);

    }
}
