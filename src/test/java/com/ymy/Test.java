package com.ymy;


import com.ymy.common.JedisUtils;
import com.ymy.common.RedisKeys;
import redis.clients.jedis.Jedis;

import java.util.*;

public class Test {
    public static void main(String[] args) {
        Jedis jedis = JedisUtils.getJedis();
        Test test = new Test(jedis);
        long begin = System.currentTimeMillis();
        test.withdrawBatchRedisData("b16951a6b9aa4e94a5564d6810551747");
        long end = System.currentTimeMillis();
        System.out.println("耗时："+ (end-begin));



    }

    void showRedisData(){
        Jedis jedis = JedisUtils.getJedis();
        Set<String> batchIds = jedis.zrange("Ss.Batch", 0, -1);
        for (String batchId : batchIds){
            System.out.println("批次id："+batchId);
            Map<String, String> stringStringMap = jedis.hgetAll("Hs.Batch." + batchId);
            System.out.println("批次数据："+stringStringMap);
            Set<String> buildingIds = jedis.zrange("Ss.Building." + batchId, 0, -1);
            for (String buldingId : buildingIds){
                System.out.println("--楼栋id："+ buldingId);
                Map<String, String> building = jedis.hgetAll("Hs.Building." + buldingId);
                System.out.println("--楼栋数据："+ building);
                Set<String> unitIds = jedis.zrange("Ss.Unit."+buldingId, 0, -1);
                for (String unitId : unitIds){
                    System.out.println("------单元id："+ unitId);
                    Map<String, String> unit = jedis.hgetAll("Hs.Unit." + unitId);
                    System.out.println("------单元数据："+ unit);
                    Set<String> floorIds = jedis.zrange("Ss.Floor." + unitId, 0, -1);
                    for (String floorId : floorIds){
                        System.out.println("-----------楼层id："+ floorId);
                        Map<String, String> floor = jedis.hgetAll("Hs.Floor." + floorId);
                        System.out.println("-----------楼层数据："+ floor);
                        Set<String> houseIds = jedis.zrange("Ss.House." + floorId, 0, -1);
                        for (String houseId : houseIds){
                            System.out.println("--------------房间id："+ houseId);
                            Map<String, String> house = jedis.hgetAll("Hs.House." + houseId);
                            System.out.println("--------------房间数据："+house);

                        }
                    }

                }

            }

        }
    }


    private Jedis jedis;

    public Test(Jedis jedis) {
        this.jedis = JedisUtils.getJedis();
    }

    public Test(){}
    /**
     * 撤回发布至redis的批次数据
     * @param batchId
     */
    void withdrawBatchRedisData(String batchId){
        Set<String> delKeys = new LinkedHashSet<>();
        // 删除批次相关key
        jedis.zrem(RedisKeys.Ss_Batch, batchId);
        delKeys.add(RedisKeys.Hs_Batch+batchId);
        delKeys.add(RedisKeys.Ss_Building+ batchId);
        delKeys.add(RedisKeys.Se_Customer_Batch+batchId);



        // 删除楼栋相关key
        Set<String> buildingIds = getBatchBuildingIds(batchId);
        for (String buildingId : buildingIds){
            delKeys.add(RedisKeys.Hs_Building+buildingId);
            delKeys.add(RedisKeys.Ss_Unit+ buildingId);
            delKeys.add(RedisKeys.Ss_House_Building + buildingId);
        }

        // 删除单元相关key
        Set<String> unitIds = getBuildingUnitIds(buildingIds);
        for (String unitId : unitIds){
            delKeys.add(RedisKeys.Hs_Unit+unitId);
            delKeys.add(RedisKeys.Ss_Floor+ unitId);
        }

        // 删除楼层相关key
        Set<String> floorIds = getUnitFloorIds(unitIds);
        for (String floorId : floorIds){
            delKeys.add(RedisKeys.Hs_Floor+floorId);
            delKeys.add(RedisKeys.Ss_House_Floor+ floorId);
        }

        // 删除房间相关key
        Set<String> houseIds = getFloorIdHouseIds(floorIds);
        for (String houseId : houseIds){
            delKeys.add(RedisKeys.Hs_House+houseId);
        }

        // 一次性删除所有的key
        String[] delKeysStr = delKeys.toArray(new String[delKeys.size()]);
        jedis.del(delKeysStr);
    }

    /**
     * 获取批次楼栋ids
     */
    Set<String> getBatchBuildingIds(String batchId){
        Set<String> buildingIds = jedis.zrange("Ss.Building." + batchId, 0, -1);
        return buildingIds;
    }

    /**
     * 获取楼栋的单元ids
     * @param buildingIds
     * @return
     */
    Set<String> getBuildingUnitIds(Set<String> buildingIds){
        Set<String> allUnitIds = new HashSet<>();
        for (String buildingId : buildingIds){
            Set<String> unitIds = jedis.zrange("Ss.Unit."+ buildingId, 0, -1);
            allUnitIds.addAll(unitIds);
        }
        return allUnitIds;
    }

    /**
     * 获取单元的楼层ids
     * @param unitIds
     * @return
     */
    Set<String> getUnitFloorIds(Set<String> unitIds){
        Set<String> allFloorIds = new HashSet<>();
        for (String unitId : unitIds){
            Set<String> floorIds = jedis.zrange("Ss.Floor."+ unitId, 0, -1);
            allFloorIds.addAll(floorIds);
        }
        return allFloorIds;
    }


    /**
     * 获取楼层的房间ids
     * @param floorIds
     * @return
     */
    Set<String> getFloorIdHouseIds(Set<String> floorIds){
        Set<String> allHouseIds = new HashSet<>();
        for (String floorId : floorIds){
            Set<String> houseIds = jedis.zrange("Ss.House.Floor."+ floorId, 0, -1);
            allHouseIds.addAll(houseIds);
        }
        return allHouseIds;
    }


}
