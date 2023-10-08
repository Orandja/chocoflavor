package net.orandja.chocoflavor.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import net.orandja.chocoflavor.ChocoFlavor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class Settings {

    private static abstract class ObservableValue<T> {

        protected List<Consumer<T>> observers = Lists.newArrayList();
        protected T value;
        public void observe(Consumer<T> observer) {
            this.observers.add(observer);
        }

        protected void dispatch() {
            this.observers.forEach(observer -> observer.accept(this.value));
        }

        public T getValue() {
            return this.value;
        }

        public void setValue(T value) {
            this.value = value;
            this.dispatch();
        }

        public void setValueSilently(T value) {
            this.value = value;
        }

    }
    @SuppressWarnings("unchecked")
    private static abstract class Abstract<S extends Abstract<?, ?>, T> extends ObservableValue<T> {

        public static class SettingDisplay<S extends Abstract<?, ?>, T> {

            private final S setting;

            private SettingDisplay(S setting) {
                this.setting = setting;
            }

            public Abstract.SettingDisplay<S, T> setIcon(String icon) {
                setting.icon = icon;
                return this;
            }

            public Abstract.SettingDisplay<S, T> setUnit(String unit) {
                setting.unit = unit;
                return this;
            }

            public Abstract.SettingDisplay<S, T> setIncrement(int increment) {
                setting.increment = increment;
                return this;
            }

            public Abstract.SettingDisplay<S, T> setMinimum(int minimum) {
                setting.minimum = minimum;
                return this;
            }

            public Abstract.SettingDisplay<S, T> setMaximum(int maximum) {
                setting.maximum = maximum;
                return this;
            }

            public S done() {
                return setting;
            }

        }

        public final static Consumer<Abstract<?, ?>> emptyDeserializer = setting -> {};

        public String icon;
        public String unit;
        public int increment;
        public int minimum = 0;
        public int maximum = Integer.MAX_VALUE;

        private final String path;
        private boolean temporary = false;
        protected T defaultValue;

        public Abstract(String path, T defaultValue) {
            this(path, defaultValue, Settings::deserialize);
        }

        public Abstract(String path, T defaultValue, Consumer<Abstract<?, ?>> deserializer) {
            this.path = path;
            this.defaultValue = defaultValue;
            this.setValueSilently(defaultValue);
            deserializer.accept(this);
            Settings.settings.add(this);
        }

        public boolean isTemporary() {
            return this.temporary;
        }

        public S setTemporary(boolean temporary) {
            this.temporary = temporary;
            return (S) this;
        }

        public String getPath() {
            return this.path;
        }

        protected String serialize() {
            return this.getValue().toString();
        }

        public abstract void deserialize(String value);
        public void deserializeFromCache() {
            if(settingsCache.containsKey(this.getPath())) {
                this.deserialize(settingsCache.get(this.getPath()));
            }
        }

        public void toDefault() {
            this.value = this.defaultValue;
        }

        public T getDefaultValue() {
            return this.defaultValue;
        }

        public Abstract.SettingDisplay<S, T> getDisplay() {
            return new Abstract.SettingDisplay<>((S) this);
        }
    }

    public static class Boolean extends Abstract<Boolean, java.lang.Boolean> {

        public Boolean(String path, java.lang.Boolean defaultValue) {
            super(path, defaultValue);
        }

        @Override
        public void deserialize(String value) {
            this.value = value.equalsIgnoreCase("true");
        }

        public void invert(boolean silently) {
            if(silently) {
                this.setValueSilently(!this.getValue());
            } else {
                this.setValue(!this.getValue());
            }
        }

        public void invert() {
            this.invert(false);
        }
    }

    public static class Custom<T> extends Abstract<Custom<T>, T> {

        Serializer<T> customSerializer;
        Deserializer<T> customDeserializer;
        public Custom(String path, T defaultValue, Serializer<T> customSerializer, Deserializer<T> customDeserializer) {
            super(path, defaultValue, emptyDeserializer);
            this.customSerializer = customSerializer;
            this.customDeserializer = customDeserializer;
        }

        public interface Serializer<T> {
            String serialize(T customSetting);
        }

        public interface Deserializer<T> {
            T deserialize(String value);
        }

        public String serialize() {
            return this.customSerializer.serialize(this.getValue());
        }

        public void deserialize(String value) {
            this.value = this.customDeserializer.deserialize(value);
        }

    }

    public static class Delay extends Abstract<Delay, Long> {

        public long incrementation = -1;

        public Delay(String path, Long defaultValue, boolean temporary) {
            super(path, defaultValue);
        }

        public void increment(Long value) {
            this.incrementation += value;
        }

        public void increment() {
            this.increment(1L);
        }

        public boolean hasReached(boolean reset) {
            if(this.incrementation >= value) {
                if(reset) reset();

                return true;
            }

            return false;
        }

        public boolean hasReached() {
            return this.hasReached(false);
        }

        public void reset() {
            this.incrementation = 0;
        }
        @Override
        public void deserialize(String value) {
            try {
                this.value = Long.parseLong(value);
            } catch (Exception ignored) {
            }
        }
    }

    public static class Enum<E> extends Abstract<Enum<E>, E> {

        interface EnumKeyMap<T> {
            Map<String, T> map();
        }

        Map<String, E> values;
        String defaultKey;

        public Enum(String path, EnumKeyMap<E> keyMap, E defaultValue) {
            super(path, defaultValue, emptyDeserializer);

            this.values = keyMap.map();
            this.defaultKey = this.getKey(this.defaultValue);

            Settings.deserialize(this);
        }

        public E getValue(String key) {
            if (this.values.containsKey(key)) {
                return this.values.get(key);
            }
            return this.defaultValue;
        }

        public String getKey(E value) {
            Optional<Map.Entry<String, E>> optional = this.values.entrySet().stream().filter(it -> it.getValue().equals(value)).findFirst();
            if (optional.isPresent()) {
                return optional.get().getKey();
            }

            return this.defaultKey;
        }

        @Override
        protected String serialize() {
            return this.getKey(this.value);
        }

        @Override
        public void deserialize(String value) {
            this.value = this.getValue(value);
        }
    }

    public static class Number<N> extends Abstract<Number<N>, N> {

        public interface NumberParser<N> {
            N parse(String value);
        }

        private final NumberParser<N> parser;

        public Number(String path, N defaultValue, NumberParser<N> parser) {
            super(path, defaultValue);
            this.parser = parser;
        }

        @Override
        public void deserialize(String value) {
            try {
                this.value = this.parser.parse(value);
            } catch (Exception ignored) {}
        }
    }

    public static List<Abstract<?, ?>> settings = Lists.newArrayList();
    public static Map<String, String> settingsCache = Maps.newHashMap();
    public static String FILE_NAME = null;

    public static void load(String filename) {
        FILE_NAME = filename;
        try {
            String fileInput = FileUtils.readFileToString(new File("config/" + FILE_NAME + ".json"), StandardCharsets.UTF_8);
            if(fileInput.length() > 2) {
                JsonObject mainNode = JsonParser.parseString(fileInput).getAsJsonObject();

                mainNode.entrySet().forEach(settingEntry -> settingsCache.put(settingEntry.getKey(), settingEntry.getValue().getAsString()));
                settings.forEach(Abstract::deserializeFromCache);
            }
        } catch (NoSuchFileException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        if(FILE_NAME == null) {
            return;
        }

        JsonObject mainNode = new JsonObject();

        settingsCache.forEach(mainNode::addProperty);

        settings.stream().filter(Abstract::isTemporary).forEach(setting -> mainNode.addProperty(setting.getPath(), setting.serialize()));

        try {
            StringWriter writer = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(writer);
            jsonWriter.setIndent("  ");
            jsonWriter.setLenient(true);
            Streams.write(mainNode, jsonWriter);

            FileUtils.write(new File("config/" + FILE_NAME + ".json"), writer.toString(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deserialize(String key, Consumer<String> deserializer) {
        if (settingsCache.containsKey(key)) {
            deserializer.accept(settingsCache.get(key));
        }
    }

    static void deserialize(Abstract<?, ?> setting) {
        deserialize(setting.getPath(), setting::deserialize);
    }

}