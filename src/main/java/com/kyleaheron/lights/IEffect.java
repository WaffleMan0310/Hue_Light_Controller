package main.java.com.kyleaheron.lights;

import com.kyleaheron.HueLight;
import main.java.com.kyleaheron.gui.components.ControllerSlider;
import main.java.com.kyleaheron.gui.components.ControllerColorPicker;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import main.java.com.kyleaheron.gui.components.ControllerToggle;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public interface IEffect {

    void setLight(HueLight light);

    HueLight getLight();

    void show();

    ConcurrentHashMap<PropertyKey<?>, Object> getPropertyMap();

    VBox getControlPane();

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
            ColorPicker source = (ColorPicker) e.getSource();
            getPropertyMap().computeIfPresent(propertyKey, (pKey, object) -> object = source.getValue());
        });
        getControlPane().getChildren().add(picker);
        return propertyKey;
    }

    default <T extends Boolean> PropertyKey<T> createPropertyWithToggle(String key, Class<T> type, T value) {
        ControllerToggle toggle = new ControllerToggle(key);
        PropertyKey<T> propertyKey = createProperty(key, type, value);
        toggle.getToggle().selectedProperty().addListener(((observable, oldValue, newValue) -> setProperty(propertyKey, propertyKey.type.cast(newValue))));
        getControlPane().getChildren().add(toggle);
        return propertyKey;
    }

    default <T extends Number> PropertyKey<T> createPropertyWithSlider(String key, Class<T> type, T min, T max, T value) {
        ControllerSlider slider = new ControllerSlider(key, min.doubleValue(), max.doubleValue(), value.doubleValue());
        PropertyKey<T> propertyKey = createProperty(key, type, value);
        slider.getSlider().valueProperty().addListener(((observable, oldValue, newValue) -> setProperty(propertyKey, propertyKey.type.equals(Integer.class) ? propertyKey.type.cast(newValue.intValue()) : propertyKey.type.cast(newValue))));
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
