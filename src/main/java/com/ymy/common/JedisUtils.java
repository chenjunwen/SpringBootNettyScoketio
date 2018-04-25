package com.ymy.common;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.Map;

/**
 * @author chenjunwen
 */
public class JedisUtils {

    private static final String IP = "127.0.0.1";
    //"52.80.63.100","127.0.0.1",193.112.46.224;
    private static final int PORT = 6379;
    private static final String AUTH = "";
    //"d5e848bf16e74388aec812e5f3d7b82e","youming888";
    private static int MAX_ACTIVE = 1024;
    private static int MAX_IDLE = 200;
    private static int MAX_WAIT = -1;
    private static int TIMEOUT = 10000;
    private static boolean BORROW = true;
    private static JedisPool pool;

    /**
     * 初始化线程池
     */
    static {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(MAX_ACTIVE);
        config.setMaxIdle(MAX_IDLE);
        config.setMaxWaitMillis(MAX_WAIT);
        config.setTestOnBorrow(BORROW);
        //pool = new JedisPool(config, IP, PORT, TIMEOUT, AUTH);
        System.out.println(IP);
        pool = new JedisPool(config, IP, PORT);
    }


    /**
     * 获取连接
     */
    public static synchronized Jedis getJedis() {
        try {
            if (pool != null) {
                return pool.getResource();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * @param @param  key
     * @param @param  seconds
     * @param @return
     * @return boolean 返回类型
     * @Description:设置失效时间
     */
    public static void disableTime(String key, int seconds) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.expire(key, seconds);

        } catch (Exception e) {
            System.out.println("设置失效失败");
        } finally {
            getColse(jedis);
        }
    }




    /**
     * <p>通过key同时设置 hash的多个field</p>
     *
     * @param key
     * @param hash
     * @return 返回OK 异常返回null
     */
    public String hmset(String key, Map<String, String> hash) {
        Jedis jedis = null;
        String res = null;
        try {
            jedis = getJedis();
            res = jedis.hmset(key, hash);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getColse(jedis);
        }
        return res;
    }


    /**
     * @param @param key
     * @param @param value
     * @return void 返回类型
     * @Description:存储key~value
     */

    public static boolean addValue(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            String code = jedis.set(key, value);
            if (code.equals("ok")) {
                return true;
            }
        } catch (Exception e) {
            return false;
        } finally {
            getColse(jedis);
        }
        return false;
    }

    /**
     * @param @param  key
     * @param @return
     * @return boolean 返回类型
     * @Description:删除key
     */
    public static boolean delKey(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            Long code = jedis.del(key);
            if (code > 1) {
                return true;
            }
        } catch (Exception e) {
            return false;
        } finally {
            getColse(jedis);
        }
        return false;
    }

    /**
     * @param @param jedis
     * @return void 返回类型
     * @Description: 关闭连接
     */

    public static void getColse(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    /**
     * <p>通过key 和 fields 获取指定的value 如果没有对应的value则返回null</p>
     *
     * @param key
     * @param fields 可以使 一个String 也可以是 String数组
     * @return
     */
    public List<String> hmget(String key, String... fields) {
        Jedis jedis = null;
        List<String> res = null;
        try {
            jedis = getJedis();
            res = jedis.hmget(key, fields);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getColse(jedis);
        }
        return res;
    }

    /**
     * <p>通过key获取所有的field和value</p>
     *
     * @param key
     * @return
     */
    public Map<String, String> hgetall(String key) {
        Jedis jedis = null;
        Map<String, String> res = null;
        try {
            jedis = getJedis();
            res = jedis.hgetAll(key);
        } catch (Exception e) {
            // TODO
        } finally {
            getColse(jedis);
        }
        return res;
    }

    /**
     * <p>上传脚本</p>
     *
     * @param script 需要移除的
     * @return
     */
    public String scriptLoad(String script) {
        Jedis jedis = null;
        String res = null;
        try {
            jedis = getJedis();
            res = jedis.scriptLoad(script);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getColse(jedis);
        }
        return res;
    }

    /**
     * 执行lua脚本
     *
     * @param sha  生成的校验码
     * @param keys
     * @param args
     * @return
     */
    public Object evalSha(String sha, List<String> keys, List<String> args) {
        Jedis jedis = null;
        Object res = null;
        try {
            jedis = getJedis();
            res = jedis.evalsha(sha, keys, args);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getColse(jedis);
        }
        return res;
    }

}
