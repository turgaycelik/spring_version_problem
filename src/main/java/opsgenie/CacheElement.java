package opsgenie;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * {@code CacheElement} class is responsible to define a unified interface for cache elements
 * polymorphic serialization and deserialization.
 *
 * Please refer to Jackson JSON documents regarding polymorphic serialization and limitations
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface CacheElement {
}
