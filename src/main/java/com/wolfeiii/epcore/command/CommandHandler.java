package com.wolfeiii.epcore.command;

import cloud.commandframework.CommandTree;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import cloud.commandframework.paper.PaperCommandManager;
import com.wolfeiii.epcore.EPCore;
import com.wolfeiii.epcore.command.implementations.SettingsCommand;
import com.wolfeiii.epcore.utilities.Utilities;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class CommandHandler {

    private final EPCore core;

    // Managers
    private PaperCommandManager<CommandSender> commandManager;
    private CommandConfirmationManager<CommandSender> confirmationManager;

    private static Map<Class<? extends BaseCommand>, BaseCommand> commandMap = new HashMap<>();

    public CommandHandler(@NotNull EPCore core) {
        this.core = core;
    }

    static {
        register(SettingsCommand.class);
    }

    public static void register(@NotNull Class<? extends BaseCommand> command) {
        try {
            commandMap.put(command, command.getDeclaredConstructor().newInstance());
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException exception) {
            exception.printStackTrace();
        }
    }

    public boolean setup() {
        Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction =
                AsynchronousCommandExecutionCoordinator.<CommandSender>newBuilder().build();

        Function<CommandSender, CommandSender> mapperFunction = Function.identity();
        try {
            this.commandManager = new PaperCommandManager<>(
                    core,
                    executionCoordinatorFunction,
                    mapperFunction,
                    mapperFunction
            );
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }

        if (this.commandManager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            this.commandManager.registerBrigadier();
        }

        this.confirmationManager = new CommandConfirmationManager<>(
                30L,
                TimeUnit.SECONDS,
                context -> context.getCommandContext().getSender().sendMessage(
                        Utilities.translate("&cConfirmation required. Confirm using /example confirm.")),
                sender -> sender.sendMessage(Utilities.translate("You don't have any pending confirmations"))
        );

        for (BaseCommand command : commandMap.values()) {
            command.register(commandManager);
        }

        return true;
    }
}
