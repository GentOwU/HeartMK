package org.gentuwu.heartMK;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;

public class MessageUtils {

    // Send a colored message
    public static void sendColoredMessage(CommandSender sender, String message, TextColor color) {
        if (sender == null || message == null || color == null) return; // Parameter validation
        sender.sendMessage(Component.text(message).color(color));
    }

    // Overloaded method to send a message with default color
    public static void sendColoredMessage(CommandSender sender, String message) {
        sendColoredMessage(sender, message, TextColor.fromHexString("#FFFFFF")); // Default color
    }

    // Send a plain message
    public static void sendMessage(CommandSender sender, String message) {
        if (sender == null || message == null) return; // Parameter validation
        sender.sendMessage(Component.text(message));
    }

    // Send a formatted component message
    public static void sendComponentMessage(CommandSender sender, Component component) {
        if (sender == null || component == null) return; // Parameter validation
        sender.sendMessage(component);
    }
}
