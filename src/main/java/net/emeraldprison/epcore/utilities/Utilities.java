package net.emeraldprison.epcore.utilities;

import net.md_5.bungee.api.ChatColor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Utilities {

    public static final Pattern HEX_PATTERN = Pattern.compile("&#(\\w{5}[0-9a-f])");

    public static String translate(String textToTranslate) {
        Matcher matcher = HEX_PATTERN.matcher(textToTranslate);
        StringBuilder builder = new StringBuilder();

        while(matcher.find()) {
            matcher.appendReplacement(builder, ChatColor.of("#" + matcher.group(1)).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(builder).toString());
    }

    public static List<String> translate(List<String> textsToTranslate) {
        return textsToTranslate.stream()
                .map(Utilities::translate)
                .collect(Collectors.toList());
    }


}
