package com.ymy;

import com.ymy.common.JedisUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class MySeckillRunnable implements Runnable{

    private JedisUtils redis;
    private String sha;

    MySeckillRunnable(JedisUtils redis, String sha){
        this.redis = redis;
        this.sha = sha;
    }

    @Override
    public void run() {
        String user = UUID.randomUUID().toString();
        List<String> keys = new ArrayList<>();
        keys.add(user);
        keys.add(user);
        Object val = redis.evalSha(sha, keys, keys);
        System.out.println(val);
    }
}
