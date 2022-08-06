package net.emeraldprison.epcore.configuration;

import lombok.Getter;
import lombok.Value;
import net.emeraldprison.epcore.EPCore;
import net.emeraldprison.epcore.configuration.serializer.Serializer;
import net.emeraldprison.epcore.configuration.serializer.Serializers;
import net.emeraldprison.epcore.configuration.serializer.styling.NameStyle;
import net.emeraldprison.epcore.utilities.Utilities;
import net.emeraldprison.epcore.utilities.reflection.ReflectionUtilities;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;

@Getter
public class Configuration extends YamlConfiguration {

    private final JavaPlugin owningPlugin;

    private final File file;
    private final NameStyle nameStyle;
    private final boolean automaticColorStrings;

    private final Map<String, Object> cache;
    private final Map<String, String> comments;

    public Configuration(@NotNull JavaPlugin owningPlugin, @NotNull File file, @NotNull NameStyle style, boolean automaticColorStrings) {
        this.owningPlugin = owningPlugin;

        this.file = file;
        this.nameStyle = style;
        this.automaticColorStrings = automaticColorStrings;

        this.cache = new HashMap<>();
        this.comments = new HashMap<>();
    }

    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        if (!(value instanceof Collection<?>) && !(value instanceof Map<?,?>) && (value == null || ReflectionUtilities.isSimpleType(value))) {
            super.set(path, value);

            if (value == null) {
                if (this.cache.containsKey(path)) {
                    this.cache.put(path, null);
                }

                return;
            }

            if (value.getClass().equals(String.class) && this.automaticColorStrings) {
                this.cache.put(path, Utilities.translate(value.toString()));
            }

            return;
        }

        Serializer<?> serializer = Serializers.of(value);
        if (serializer == null) {
            throw new IllegalStateException("Tried to serialize object but no serializer was found for this type: " + value.getClass().getName());
        }

        this.cache.put(path, value);
        serializer.serialize(path, value, this);
    }

    @Nullable
    @Override
    public Object get(@NotNull String path) {
        return this.cache.containsKey(path) ? this.cache.get(path) : super.get(path);
    }

    @NotNull
    @Override
    public String saveToString() {
        String yaml = super.saveToString();

        List<String> lines = new ArrayList<>();
        for (String line : yaml.split("\n")) {
            // Check if it's not a line with a new field:
            if (!line.contains(":") || line.startsWith(" ") || line.startsWith("\t")) {
                lines.add(line);
                continue;
            }

            String configFieldName = line.split(":")[0];
            String comment = this.comments.get(configFieldName);
            if (comment == null) {
                lines.add(line);
                continue;
            }

            lines.add("# " + comment);
            lines.add(line);
        }

        return String.join("\n", lines);
    }

    public void load() {
        this.load(file);
    }

    public void save() {
        try {
            this.save(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void load(@NotNull File file) {
        try {
            this.cache.clear();
            super.load(file);
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
    }

    public void copyDefaultConfig() {
        try {
            if (!owningPlugin.getDataFolder().exists()) {
                owningPlugin.getDataFolder().mkdir();
            }

            if (this.file.exists()) {
                return;
            }

            InputStream input = owningPlugin.getResource(file.getName());
            if (input == null) {
                this.file.createNewFile();
                return;
            }

            OutputStream output = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len;

            while ((len = input.read(buffer)) > 0) {
                output.write(buffer, 0, len);
            }

            output.close();
            input.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
