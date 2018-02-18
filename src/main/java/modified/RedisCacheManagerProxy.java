package modified;


import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisOperations;

import java.util.Collections;

public class RedisCacheManagerProxy extends RedisCacheManager {
    private static final boolean ALLOW_NULL_VALUES = true;


    public RedisCacheManagerProxy(RedisOperations redisOperations) {
        // todo : open this call if switched to spring boot 1-3
//        super(redisOperations, Collections.emptyList());
        // todo : close this call if switched to spring boot 1-3
        super(redisOperations, Collections.emptyList(), ALLOW_NULL_VALUES);
    }

    @Override
    protected long computeExpiration(String name) {
        return super.computeExpiration("books");
    }


    // todo : close this method if switched to spring boot 1-3
    @Override
    protected RedisCache createCache(String cacheName) {
        long   expiration = computeExpiration(cacheName);
        byte[] prefix     = isUsePrefix() ? getCachePrefix().prefix(cacheName) : null;

        return new RedisCache(cacheName, prefix, getRedisOperations(), expiration, ALLOW_NULL_VALUES) {
            @Override
            protected Object fromStoreValue(Object storeValue) {
                return NullValue.fromStoreValue(this, storeValue);
            }

            @Override
            protected Object toStoreValue(Object userValue) {
                return NullValue.toStoreValue(this, userValue);
            }
        };
    }
}
