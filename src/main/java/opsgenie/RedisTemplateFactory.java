package opsgenie;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import opsgenie.CacheValueType;
import opsgenie.RedisEncryption;

/**
 * @author Kadir Turker Gulsoy
 * @version 11/03/17 18:07
 */
public interface RedisTemplateFactory {
    RedisTemplate<?, ?> create(CacheValueType cacheValueType, RedisConnectionFactory connectionFactory, RedisEncryption encryption);
}
