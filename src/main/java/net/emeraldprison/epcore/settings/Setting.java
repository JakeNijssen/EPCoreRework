package net.emeraldprison.epcore.settings;

import lombok.Getter;
import net.emeraldprison.epcore.utilities.Utilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Setting {

    private final String name;
    private final String displayName;
    private final List<String> description;
    private final boolean confirmation;
    private final boolean defaultEnabled;

    public Setting(@NotNull String name, @NotNull String displayName, @Nullable List<String> description,
                   boolean confirmation, boolean defaultValue) {

        this.name = name;
        this.displayName = Utilities.translate(displayName);
        this.description = Utilities.translate(description == null
                ? new ArrayList<>()
                : description);
        this.confirmation = confirmation;
        this.defaultEnabled = defaultValue;
    }

}
