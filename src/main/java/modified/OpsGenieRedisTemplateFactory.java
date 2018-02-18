package modified;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.springframework.cache.support.NullValue;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

import opsgenie.CacheElement;
import opsgenie.CacheValueType;
import opsgenie.DelegatingRedisSerializer;
import opsgenie.RedisEncryption;
import opsgenie.RedisTemplateFactory;

/**
 * @author Kadir Turker Gulsoy
 * @version 11/03/17 18:08
 */
@Component
public class OpsGenieRedisTemplateFactory implements RedisTemplateFactory {
    @Override
    public RedisTemplate<?, ?> create(CacheValueType cacheValueType,
                                      RedisConnectionFactory connectionFactory,
                                      RedisEncryption encryption        ) {
        RedisTemplate<String, ? extends CacheElement> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new DelegatingRedisSerializer<>(encryption.create(createSerializerForType(cacheValueType))));
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    private RedisSerializer<?> createSerializerForType(CacheValueType cacheValueType) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule().addSerializer(new NullValueSerializer(null)));

        switch (cacheValueType) {
            case SINGLE:
                final Jackson2JsonRedisSerializer<CacheElement> serializer = new Jackson2JsonRedisSerializer<>(CacheElement.class);
                serializer.setObjectMapper(mapper);
                return serializer;

            case LIST:

                JavaType     type   = mapper.getTypeFactory().constructCollectionType(List.class, CacheElement.class);
                mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
                Jackson2JsonRedisSerializer<List<? extends CacheElement>> defaultSerializer = new Jackson2JsonRedisSerializer<>(type);
                defaultSerializer.setObjectMapper(mapper);
                return defaultSerializer;

            default:
                throw new IllegalArgumentException("Unknown cache value type!");
        }
    }

    private class NullValueSerializer extends StdSerializer<NullValue> {

        private static final long serialVersionUID = 1999052150548658808L;
        private final String classIdentifier;

        /**
         * @param classIdentifier can be {@literal null} and will be defaulted to {@code @class}.
         */
        NullValueSerializer(String classIdentifier) {

            super(NullValue.class);
            this.classIdentifier = StringUtils.hasText(classIdentifier) ? classIdentifier : "@class";
        }

        /*
         * (non-Javadoc)
         * @see com.fasterxml.jackson.databind.ser.std.StdSerializer#serialize(java.lang.Object, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)
         */
        @Override
        public void serialize(NullValue value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException {

            jgen.writeStartObject();
            jgen.writeStringField(classIdentifier, NullValue.class.getName());
            jgen.writeEndObject();
        }
    }
}
