package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.DefaultRedisCachePrefix;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import modified.OpsGenieRedisTemplateFactory;
import modified.RedisCacheManagerProxy;
import opsgenie.CacheValueType;
import opsgenie.RedisEncryption;
import opsgenie.RedisTemplateFactory;

@SpringBootApplication
@EnableCaching
public class Application {


    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    RedisTemplateFactory redisTemplateFactory(){
        return new OpsGenieRedisTemplateFactory();
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate() {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }



    @Autowired
    private RedisTemplateFactory redisTemplateFactory;


    @Bean(name = "new_raw")
    public CacheManager rawCacheManager(RedisConnectionFactory connectionFactory) {
        RedisTemplate<?, ?> operations =
                redisTemplateFactory.create(CacheValueType.SINGLE, connectionFactory, RedisEncryption.NONE);
        RedisCacheManagerProxy proxy = new RedisCacheManagerProxy(operations);
        proxy.setCachePrefix(new DefaultRedisCachePrefix("-"));
        proxy.setUsePrefix(true);
        proxy.setExpires(Collections.singletonMap("scheduleService.onCallSchedules", TimeUnit.DAYS.toSeconds(2)));
        proxy.setDefaultExpiration(TimeUnit.MINUTES.toSeconds(60));
        return proxy;
    }

}