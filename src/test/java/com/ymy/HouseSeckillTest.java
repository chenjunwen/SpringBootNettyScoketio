package com.ymy;

import com.ymy.common.FileUtils;
import com.ymy.common.JedisUtils;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 测试房间秒杀
 */
public class HouseSeckillTest {
    public static void main(String[] args) {
        Jedis jedis = JedisUtils.getJedis();
        String script = FileUtils.getFile("betaHouseSeckill.lua");
        //main(customerId, houseId, maxHouseNum, batchId, time)
        List<String> keys = new ArrayList<>();
        keys.add("eaa2c9c964974e259694a2c64a5c7f47");
        keys.add("0efa6bb2918741908df9ecfbdb05cb9");
        keys.add("3");
        keys.add("b16951a6b9aa4e94a5564d6810551747");
        long timeMillis = System.currentTimeMillis();
        keys.add("1515832689001");
        /*Object eval = jedis.eval(script, keys, Collections.emptyList());
        System.out.println(eval);*/

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        String shaKey = jedis.scriptLoad(script);
        //System.out.println("shaKey:"+shaKey);
        for (int i = 0; i < 1; i++) {
            unThreadSeckill(keys, shaKey, jedis, executorService);
        }

    }
    static void unThreadSeckill(List<String> keys, String shaKey, Jedis jedis, ExecutorService executorService){
        executorService.execute(()->{
            try {
                Object result = jedis.evalsha(shaKey, keys, Collections.emptyList());
                System.out.println(result);
                int state = Integer.parseInt(result.toString());
                switch (state){
                    case -4:
                        System.out.println("房间不存在");
                        break;
                    case -3:
                        System.out.println("还未到开盘时间");
                        break;
                    case -2:
                        System.out.println("你没有购房权限");
                        break;
                    case -1:
                        System.out.println("该房间已售完");
                        break;
                    case 0:
                        System.out.println("购房数量已上线");
                        break;
                    default:
                        System.out.println("购房成功");
                        break;

                }
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("抢房失败，请重试");
            }
        });
    }
}
