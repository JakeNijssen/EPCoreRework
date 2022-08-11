package com.wolfeiii.epcore.command;

import cloud.commandframework.CommandManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class BaseCommand {

    public abstract void register(@NotNull CommandManager<CommandSender> commandHandler);
}
