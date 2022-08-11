package com.wolfeiii.epcore.settings;

import com.wolfeiii.epcore.utilities.Utilities;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Setting {

    private final String name, displayName, toggleMessage;
    private final List<String> description, commands;
    private final Material type;
    private final ChatColor color;

    private final boolean confirmation, defaultEnabled, message;

    public Setting(@NotNull String name, @NotNull Material type, @NotNull ChatColor color, @NotNull String toggleMessage, @NotNull String displayName,
                   @Nullable List<String> description, List<String> commands, boolean message, boolean confirmation, boolean defaultValue) {

        this.name = name;
        this.type = type;
        this.color = color;
        this.toggleMessage = toggleMessage;
        this.displayName = Utilities.translate(displayName);
        this.description = Utilities.translate(description == null
                ? new ArrayList<>()
                : description);
        this.commands = commands;
        this.message = message;
        this.confirmation = confirmation;
        this.defaultEnabled = defaultValue;
    }

}
