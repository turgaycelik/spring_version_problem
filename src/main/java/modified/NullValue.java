package modified;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.support.AbstractValueAdaptingCache;

import java.io.PrintWriter;
import java.io.StringWriter;

import opsgenie.CacheElement;

/**
 * {@link NullValue} is used to store {@code null} values in a {@link org.springframework.cache.Cache}.
 *
 * @author Muhammed Demirbaş
 * @since 2018-01-23 13:42
 */
public class NullValue implements CacheElement {
    private static final Logger logger = LoggerFactory.getLogger(NullValue.class);

    public static Object  fromStoreValue(AbstractValueAdaptingCache cache, Object storeValue) {
        logIfNull(storeValue);
        return (cache.isAllowNullValues() && (storeValue instanceof NullValue)) ? null : storeValue;
    }

    public static Object toStoreValue(AbstractValueAdaptingCache cache, Object userValue) {
        logIfNull(userValue);
        return (cache.isAllowNullValues() && (userValue == null)) ? new NullValue() : userValue;
    }

    private static void logIfNull(Object obj) {
        if(obj instanceof org.springframework.cache.support.NullValue){
            final StringWriter writer = new StringWriter();
            new Throwable().printStackTrace(new PrintWriter(writer));
            logger.warn("Cache value is set to org.springframework.cache.support.NullValue -> " + writer.toString());
        } else if(obj instanceof NullValue){
            final StringWriter writer = new StringWriter();
            new Throwable().printStackTrace(new PrintWriter(writer));
            logger.warn("Cache value is set to com.opsgenie.management.cache.redis.NullValue -> " + writer.toString());
        }else if(obj == null){
            final StringWriter writer = new StringWriter();
            new Throwable().printStackTrace(new PrintWriter(writer));
            logger.warn("Cache value is set to null -> " + writer.toString());
        }
    }
}
