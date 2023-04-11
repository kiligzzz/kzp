package com.kiligz.kzp.redis.config;

import com.kiligz.kzp.common.constant.Consts;
import lombok.NonNull;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.time.Duration;

/**
 * 自定义RedisCacheManager
 *
 * @author Ivan
 * @since 2022/12/27
 */
public class KzpRedisCacheManager extends RedisCacheManager {

    public KzpRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
    }

    /**
     * 构造缓存的方法，cacheNames值：xxx-一天左右失效，xxx#10-10s失效，xxx#-永不失效
     */
    @NonNull
    @Override
    protected RedisCache createRedisCache(@NonNull String name, RedisCacheConfiguration cacheConfig) {
        if (name.contains(Consts.POUND)) {
            String[] arr = name.split(Consts.POUND);
            name = arr[0];
            if (arr.length == 2) {
                cacheConfig = cacheConfig.entryTtl(Duration.ofSeconds(Long.parseLong(arr[1])));
            }
        } else {
            cacheConfig = cacheConfig.entryTtl(Duration.ofSeconds(Consts.RANDOM_DAY));
        }
        return super.createRedisCache(name, cacheConfig);
    }
}
