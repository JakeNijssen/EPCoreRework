package com.wolfeiii.epcore.ui.actionbar;

import com.wolfeiii.epcore.EPCore;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;

public class ActionBarHandler {

    private final EPCore core;

    public ActionBarHandler(@NotNull EPCore core) throws IllegalAccessException {
        this.core = core;

        String packageName = Bukkit.getServer().getClass().getPackageName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);

        if (!version.equalsIgnoreCase("v1_19_R1")) {
            throw new IllegalAccessException("Tried to initialize ActionBarHandler on a version that is not supported.");
        }
    }

    public void sendActionBar(@NotNull Player player, String message) {
        IChatBaseComponent iChatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        ClientboundSystemChatPacket clientboundSystemChatPacket;

        try {
            Class<?> clazz = Class.forName(ClientboundSystemChatPacket.class.getName());
            Constructor<?> constructor = clazz.getConstructor(IChatBaseComponent.class, int.class);

            clientboundSystemChatPacket = (ClientboundSystemChatPacket) constructor.newInstance(iChatBaseComponent, 2);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException exception) {
            clientboundSystemChatPacket = new ClientboundSystemChatPacket(iChatBaseComponent, true);
        }
        ((CraftPlayer) player).getHandle().b.a(clientboundSystemChatPacket);
    }

    public void sendActionBar(@NotNull Player player, @NotNull String message, @NotNull Duration duration) {
        new BukkitRunnable() {
            final long targetMilliseconds = System.currentTimeMillis() + duration.toMillis();

            @Override
            public void run() {
                if (System.currentTimeMillis() >= targetMilliseconds) {
                    cancel();
                    return;
                }

                sendActionBar(player, message);
            }
        }.runTaskTimer(core, 0L, 20L);
    }
}
