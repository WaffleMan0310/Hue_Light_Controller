package com.kyleaheron.lights;

import com.kyleaheron.HueLight;
import com.kyleaheron.gui.components.ControllerColorPicker;
import com.kyleaheron.gui.components.ControllerSlider;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public interface IEffect {

    void setLight(HueLight light);

    HueLight getLight();

    void show();

    ConcurrentHashMap<PropertyKey<?>, Object> getPropertyMap();

    HBox getControlPane();

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

    default <T extends Color> PropertyKey<T> createPropertyWithColorChooser(String key, Class<T> type, T value) {
        ControllerColorPicker picker = new ControllerColorPicker(key, value);
        PropertyKey<T> propertyKey = createProperty(key, type, value);
        picker.getColorPicker().setOnAction(e -> {
            ControllerColorPicker source = (ControllerColorPicker) e.getSource();
            getPropertyMap().computeIfPresent(propertyKey, (pKey, object) -> object = source.getColorPicker().getValue());
        });
        getControlPane().getChildren().add(picker);
        return propertyKey;
    }

    default <T extends Boolean> PropertyKey<T> createPropertyWithToggle(String key, Class<T> type, T value) {
        return null;
    }

    default <T extends Number> PropertyKey<T> createPropertyWithSlider(String key, Class<T> type, T min, T max, T value) {
        ControllerSlider slider = new ControllerSlider(key, min.doubleValue(), max.doubleValue(), value.doubleValue());
        PropertyKey<T> propertyKey = createProperty(key, type, value);
        slider.getSlider().valueProperty().addListener(((observable, oldValue, newValue) -> getPropertyMap().computeIfPresent(propertyKey, (pKey, object) -> object = newValue)));
        getControlPane().getChildren().add(slider);
        return propertyKey;
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
