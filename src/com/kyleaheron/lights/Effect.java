package com.kyleaheron.lights;

import com.kyleaheron.HueLight;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public interface Effect {

    void setLight(HueLight light);

    HueLight getLight();

    void show();

    ConcurrentHashMap<PropertyKey<?>, Object> getPropertyMap();

    void setEffect(EffectEnum effect);

    EffectEnum getEffect();

    default <T> void addProperty(PropertyKey<T> key, T defaultValue) {
        getPropertyMap().put(key, defaultValue);
    }

    default <T> T getProperty(PropertyKey<T> key) {
        return key.type.cast(getPropertyMap().get(key));
    }

    default <T> void setProperty(PropertyKey<T> key, T newValue) {
        getPropertyMap().computeIfPresent(key, (keyObj, object) -> object = newValue);
    }

    default <T> PropertyKey<T> createProperty(String key, Class<T> type, T defaultValue) {
        PropertyKey<T> propertyKey = new PropertyKey<>(key, type);
        getPropertyMap().put(propertyKey, defaultValue);
        return propertyKey;
    }

    class PropertyKey<T> {

        Class<T> type;
        String key;

        PropertyKey(String key, Class<T> type) {
            this.key = key;
            this.type = type;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(key) + Objects.hashCode(type);
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof PropertyKey)) return false;
            PropertyKey key = (PropertyKey) o;
            return key.key.equals(this.key) && key.type.equals(this.type);
        }
    }
}
