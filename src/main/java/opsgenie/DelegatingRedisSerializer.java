package opsgenie;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * @author Muhammed Demirbaş
 * @since 2018-01-22 16:26
 */
public class DelegatingRedisSerializer<T> implements RedisSerializer<T> {
    private RedisSerializer<T> delegate;

    public DelegatingRedisSerializer(RedisSerializer<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        return delegate.serialize(t);
    }


    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        return delegate.deserialize(bytes);
    }
}
