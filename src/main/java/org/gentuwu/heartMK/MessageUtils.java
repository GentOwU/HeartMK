package org.gentuwu.heartMK;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;

public class MessageUtils {
    public static void sendColoredMessage(CommandSender sender, String message, TextColor color) {
        sender.sendMessage(Component.text(message).color(color));
    }
}
