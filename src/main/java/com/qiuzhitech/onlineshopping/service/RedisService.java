package com.qiuzhitech.onlineshopping.service;

import org.springframework.stereotype.Service;

import java.util.Collections;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
@Slf4j
public class RedisService {

    @Resource
    JedisPool jedisPool;

    public void setValue(String key, String value) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.set(key, value);
        jedisClient.close();
    }

    public String getValue(String key) {
        Jedis jedisClient = jedisPool.getResource();
        String value = jedisClient.get(key);
        jedisClient.close();
        return value;
    }

    public long stockDeduct(String redisKey) {
        try (Jedis jedisClient = jedisPool.getResource()) {
            String script =
                    "if redis.call('exists', KEYS[1]) == 1 then " +
                            "    local stock = tonumber(redis.call('get', " +
                            "KEYS[1])) " +
                            "    if (stock<=0) then " +
                            "        return -1" +
                            "    end " +
                            "    redis.call('decr', KEYS[1]); " +
                            "    return stock - 1; " +
                            "end " +
                            "return -1; ";
            Long stock = (long) jedisClient.eval(script,
                    Collections.singletonList(redisKey),
                    Collections.emptyList());
            if (stock < 0) {
                log.info("There is no stock available");
                return -1;
            } else {
                log.info("Validate and decreased redis stock, current " +
                        "available stock:{}", stock);
                return stock;
            }
        } catch (Throwable e) {
            log.error("Redis failed on stockDeduct");
            return -1;
        }
    }

    // 为什么要加requestId，比方说当A和B对commodity进行上锁时，然后A去数据库操作然后释放锁
    // 如果没有这个requestId作为value，就会导致A错误释放B的锁
    public boolean tryGetDistributedLock(String lockKey, String requestId,
                                         int expireTime) {
        Jedis jedisClient = jedisPool.getResource();
        String result = jedisClient.set(lockKey, requestId, "NX", "PX",
                expireTime);
        jedisClient.close();
        return "OK".equals(result);
    }

    public boolean releaseDistributedLock(String lockKey, String requestId) {
        Jedis jedisClient = jedisPool.getResource();
        // get redis value (requestId)
        String script = "if redis.call('get', KEYS[1]) == ARGV[1]" +
                " then return redis.call('del', KEYS[1])" +
                " else return 0 end";
        Long result = (Long) jedisClient.eval(script,
                Collections.singletonList(lockKey),
                Collections.singletonList(requestId));
        return result == 1L;
    }

    public void revertStock(String redisKey) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.incr(redisKey);
        jedisClient.close();
    }

    // 通过 redis 维护一个 denyList 的 hashSet，通过判定当前商品是否已经被某用户购买过来判定
    public boolean isInDenyList(Long userId, Long commodityId) {
        Jedis jedisClient = jedisPool.getResource();
        Boolean isInDenyList = jedisClient.sismember("denyList:" + userId,
                String.valueOf(commodityId));
        jedisClient.close();
        log.info("userId:{}, commodityId:{} is InDenyList result:{}", userId,
                commodityId, isInDenyList);
        return isInDenyList;
    }

    public void addToDenyList(Long userId, Long commodityId) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.sadd("denyList:" + userId, String.valueOf(commodityId));
        jedisClient.close();
        log.info("Add userId: {} into denyList for commodityId: {}",
                userId, commodityId);
    }

    public void removeFromDenyList(Long userId, Long commodityId) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.srem("denyList:" + userId, String.valueOf(commodityId));
        jedisClient.close();
        log.info("Remove userId: {} into denyList for commodityId: {}",
                userId, commodityId);
    }
}
