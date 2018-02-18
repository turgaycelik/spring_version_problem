package opsgenie;

import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author Kadir Turker Gulsoy
 * @version 11/03/17 18:05
 */
public enum RedisEncryption {

    NONE {
        @Override
        public RedisSerializer<?> create(RedisSerializer<?> defaultSerializer) {
            return defaultSerializer;
        }
    };


    private String password = "";

    public abstract RedisSerializer<?> create(RedisSerializer<?> defaultSerializer);

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static RedisEncryption fromString(String text) {
        if (text != null) {
            for (RedisEncryption e : RedisEncryption.values()) {
                if (text.equalsIgnoreCase(e.toString())) {
                    return e;
                }
            }
        }
        return null;
    }

}
