package com.wolfeiii.epcore.command.implementations;

import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import com.wolfeiii.epcore.command.BaseCommand;
import com.wolfeiii.epcore.settings.inventory.SettingsGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SettingsCommand extends BaseCommand {

    @Override
    public void register(@NotNull CommandManager<CommandSender> commandHandler) {
        commandHandler.command(commandHandler.commandBuilder("settings")
                .senderType(Player.class)
                .handler(this::execute));
    }

    private void execute(@NotNull CommandContext<CommandSender> context) {
        SettingsGUI settingsGUI = new SettingsGUI((Player) context.getSender());
        settingsGUI.open();
    }
}
