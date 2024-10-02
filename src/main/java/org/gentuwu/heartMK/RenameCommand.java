package org.gentuwu.heartMK;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RenameCommand implements CommandExecutor, TabCompleter {

    private final Map<UUID, Long> lastRename = new HashMap<>();
    private static final List<String> STYLES = List.of(
            "AQUA", "BLACK", "BLUE", "BOLD", "DARK_AQUA", "DARK_BLUE",
            "DARK_GRAY", "DARK_GREEN", "DARK_PURPLE", "DARK_RED",
            "GOLD", "GRAY", "GREEN", "ITALIC", "LIGHT_PURPLE",
            "MAGIC", "RED", "RESET", "STRIKETHROUGH",
            "UNDERLINE", "WHITE", "YELLOW"
    );

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.", NamedTextColor.RED));
            return true;
        }

        return renameItem(player, args);
    }

    private boolean renameItem(Player player, String[] args) {
        int renameTimeoutSeconds = HeartMK.getInstance().getConfig().getInt("renameTimeout", 60);
        long renameTimeoutMillis = renameTimeoutSeconds * 1000L;

        long currentTime = System.currentTimeMillis();
        UUID playerId = player.getUniqueId();

        if (lastRename.containsKey(playerId) && (currentTime - lastRename.get(playerId)) < renameTimeoutMillis) {
            long remainingTime = (renameTimeoutMillis - (currentTime - lastRename.get(playerId))) / 1000;
            player.sendMessage(Component.text("You can rename an item again in " + remainingTime + " seconds.", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage(Component.text("You must be holding an item to rename it!", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Component.text("Usage: /ren [format] <name>", NamedTextColor.RED));
            return true;
        }

        Component displayName = buildDisplayName(args);
        if (displayName.equals(Component.empty())) {
            player.sendMessage(Component.text("You must provide a name to rename the item!", NamedTextColor.RED));
            return false;
        }

        item.editMeta(meta -> meta.displayName(displayName));
        player.sendMessage(Component.text("Item renamed to: ", NamedTextColor.GREEN).append(displayName));

        lastRename.put(playerId, currentTime);
        return true;
    }

    private Component buildDisplayName(@NotNull String[] args) {
        Component nameComponent = Component.empty();
        List<String> currentFormats = new ArrayList<>();
        TextColor currentColor = null;

        for (String arg : args) {
            String upperArg = arg.toUpperCase();
            if (STYLES.contains(upperArg)) {
                currentFormats.add(upperArg);
                continue;
            }

            if (isHexColor(arg)) {
                currentColor = TextColor.fromHexString(arg);
                continue;
            }

            Component textComponent = Component.text(arg + " ");
            // Apply current formats
            for (String format : currentFormats) {
                textComponent = applyFormat(textComponent, format);
            }

            // Apply current color if set
            if (currentColor != null) {
                textComponent = textComponent.color(currentColor);
            }

            nameComponent = nameComponent.append(textComponent); // Append formatted text
        }

        return nameComponent.children().isEmpty() ? Component.empty() : nameComponent;
    }

    private boolean isHexColor(String arg) {
        return arg.matches("^#[0-9A-Fa-f]{6}$");
    }

    private Component applyFormat(Component component, String format) {
        return switch (format) {
            case "BOLD" -> component.decoration(TextDecoration.BOLD, true);
            case "ITALIC" -> component.decoration(TextDecoration.ITALIC, true);
            case "MAGIC" -> component.decoration(TextDecoration.OBFUSCATED, true);
            case "UNDERLINE" -> component.decoration(TextDecoration.UNDERLINED, true);
            case "STRIKETHROUGH" -> component.decoration(TextDecoration.STRIKETHROUGH, true);
            default -> applyColorStyle(component, format);
        };
    }

    private Component applyColorStyle(Component component, String color) {
        return switch (color) {
            case "AQUA" -> component.color(TextColor.fromHexString("#00FFFF"));
            case "BLACK" -> component.color(TextColor.fromHexString("#000000"));
            case "BLUE" -> component.color(TextColor.fromHexString("#0000FF"));
            case "DARK_AQUA" -> component.color(TextColor.fromHexString("#008B8B"));
            case "DARK_BLUE" -> component.color(TextColor.fromHexString("#00008B"));
            case "DARK_GRAY" -> component.color(TextColor.fromHexString("#A9A9A9"));
            case "DARK_GREEN" -> component.color(TextColor.fromHexString("#006400"));
            case "DARK_PURPLE" -> component.color(TextColor.fromHexString("#4B0082"));
            case "DARK_RED" -> component.color(TextColor.fromHexString("#8B0000"));
            case "GOLD" -> component.color(TextColor.fromHexString("#FFD700"));
            case "GRAY" -> component.color(TextColor.fromHexString("#808080"));
            case "GREEN" -> component.color(TextColor.fromHexString("#008000"));
            case "LIGHT_PURPLE" -> component.color(TextColor.fromHexString("#DDA0DD"));
            case "RED" -> component.color(TextColor.fromHexString("#FF0000"));
            case "RESET", "WHITE" -> component.color(TextColor.fromHexString("#FFFFFF"));
            case "YELLOW" -> component.color(TextColor.fromHexString("#FFFF00"));
            default -> component;
        };
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();

        // Add <name> if no color/format is entered
        if (args.length == 0) {
            suggestions.add("<name>");
        } else {
            boolean hasFormat = false;
            for (String arg : args) {
                if (STYLES.stream().anyMatch(style -> style.equalsIgnoreCase(arg))) {
                    hasFormat = true;
                    break;
                }
            }
            // If any valid format is entered, do not add <name>
            if (!hasFormat) {
                suggestions.add("<name>");
            }
        }

        // Add styles based on current input
        if (args.length > 0) {
            for (String style : STYLES) {
                if (style.startsWith(args[args.length - 1].toUpperCase())) {
                    suggestions.add(style);
                }
            }
        }

        // Add hex color hint if no valid formats were found
        if (args.length == 0 || args[args.length - 1].isEmpty()) {
            suggestions.add("#FFFFFF"); // Default hex color hint
            suggestions.add("#FF0000");
            suggestions.add("#00FF00");
            suggestions.add("#0000FF");
        }

        return suggestions;
    }
}
