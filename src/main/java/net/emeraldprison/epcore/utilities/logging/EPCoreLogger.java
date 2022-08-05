package net.emeraldprison.epcore.utilities.logging;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

public class EPCoreLogger {
    
    private final ChatColor messageColor;
    private final LogLevel max;

    public EPCoreLogger(ChatColor messageColor, LogLevel max) {
        this.messageColor = messageColor;
        this.max = max;
    }

    public void log(LogLevel level, String message) {
        if (level.ordinal() < max.ordinal()) {
            return;
        }

        String text = "";
        switch (level) {
            case DEBUG -> text += ChatColor.YELLOW + "[DEBUG] ➔";
            case ERROR -> text += ChatColor.RED + "[ERROR] ➔";
            case FATAL -> text += ChatColor.RED + "[FATAL] ➔";
            case INFO -> text += ChatColor.WHITE + "[INFO] ➔";
            case WARN -> text += ChatColor.GOLD + "[WARN] ➔";
            case SUCCESS -> text += ChatColor.GREEN + "[SUCCESS] ➔";
            default -> text += "";
        }

        Bukkit.getConsoleSender().sendMessage(text + messageColor + message);
    }
}
