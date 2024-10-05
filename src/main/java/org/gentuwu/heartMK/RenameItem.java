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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RenameItem implements CommandExecutor, TabCompleter {

    private static final List<String> STYLES = List.of(
            "AQUA", "BLACK", "BLUE", "BOLD", "DARK_AQUA", "DARK_BLUE",
            "DARK_GRAY", "DARK_GREEN", "DARK_PURPLE", "DARK_RED",
            "GOLD", "GRAY", "GREEN", "ITALIC", "LIGHT_PURPLE",
            "MAGIC", "RED", "RESET", "STRIKETHROUGH",
            "UNDERLINE", "WHITE", "YELLOW"
    );

    private final long cooldownTime; // Cooldown time in milliseconds
    private final Map<UUID, Long> cooldowns = new HashMap<>(); // Player cooldowns

    public RenameItem(int renameCooldown) {
        this.cooldownTime = renameCooldown * 1000L; // Convert to milliseconds
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.", NamedTextColor.RED));
            return true;
        }

        if (isOnCooldown(player.getUniqueId())) {
            long remaining = (cooldowns.get(player.getUniqueId()) + cooldownTime - System.currentTimeMillis()) / 1000;
            player.sendMessage(Component.text("You must wait " + remaining + " seconds before renaming again.", NamedTextColor.RED));
            return true;
        }

        return renameItem(player, args);
    }

    private boolean renameItem(Player player, String[] args) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage(Component.text("You must be holding an item to rename it!", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Component.text("Usage: /rename [format] <name>", NamedTextColor.RED));
            return true;
        }

        // Join the arguments to form the name
        String name = String.join(" ", args);

        // Check for empty or whitespace name
        if (name.trim().isEmpty()) {
            player.sendMessage(Component.text("You must provide a name for the item.", NamedTextColor.RED));
            return true;
        }

        // If the name does not contain formatting tags, set it directly
        if (!name.contains("[") && !name.contains("]")) {
            item.editMeta(meta -> meta.displayName(Component.text(name)));
            player.sendMessage(Component.text("Item renamed to: ", NamedTextColor.GREEN).append(Component.text(name)));
            return true;
        }

        // Otherwise, parse the format
        Component formattedName = parseFormat(name);
        item.editMeta(meta -> meta.displayName(formattedName));
        player.sendMessage(Component.text("Item renamed to: ", NamedTextColor.GREEN).append(formattedName));
        return true;
    }



    private Component parseFormat(String input) {
        Pattern pattern = Pattern.compile("\\[(.*?)\\]|([^\\[]+)");
        Matcher matcher = pattern.matcher(input);
        List<TextDecoration> activeDecorations = new ArrayList<>();
        TextColor currentColor = null;
        Component result = Component.empty();

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                String tag = matcher.group(1).toUpperCase();

                if (tag.startsWith("#")) {
                    currentColor = TextColor.fromHexString(tag);
                } else if (STYLES.contains(tag)) {
                    if (tag.equals("RESET")) {
                        activeDecorations.clear();
                        currentColor = null;
                    } else if (!tag.startsWith("/")) {
                        // Check for color mapping
                        TextColor color = mapColor(tag);
                        if (color != null) {
                            currentColor = color;
                        } else {
                            activeDecorations.add(mapDecoration(tag));
                        }
                    } else {
                        String closingTag = tag.substring(1);
                        activeDecorations.remove(mapDecoration(closingTag));
                    }
                }
            } else {
                String text = matcher.group(2);
                Component component = Component.text(text);

                if (currentColor != null) {
                    component = component.color(currentColor);
                }
                for (TextDecoration decoration : activeDecorations) {
                    component = component.decorate(decoration);
                }

                result = result.append(component);
            }
        }

        return result;
    }


    private TextDecoration mapDecoration(String style) {
        return switch (style) {
            case "BOLD" -> TextDecoration.BOLD;
            case "ITALIC" -> TextDecoration.ITALIC;
            case "UNDERLINE" -> TextDecoration.UNDERLINED;
            case "STRIKETHROUGH" -> TextDecoration.STRIKETHROUGH;
            case "MAGIC" -> TextDecoration.OBFUSCATED;
            default -> null; // Keep this default for text styles
        };
    }

    private TextColor mapColor(String colorName) {
        return switch (colorName) {
            case "AQUA" -> NamedTextColor.AQUA;
            case "BLACK" -> NamedTextColor.BLACK;
            case "BLUE" -> NamedTextColor.BLUE;
            case "DARK_AQUA" -> NamedTextColor.DARK_AQUA;
            case "DARK_BLUE" -> NamedTextColor.DARK_BLUE;
            case "DARK_GRAY" -> NamedTextColor.DARK_GRAY;
            case "DARK_GREEN" -> NamedTextColor.DARK_GREEN;
            case "DARK_PURPLE" -> NamedTextColor.DARK_PURPLE;
            case "DARK_RED" -> NamedTextColor.DARK_RED;
            case "GOLD" -> NamedTextColor.GOLD;
            case "GRAY" -> NamedTextColor.GRAY;
            case "GREEN" -> NamedTextColor.GREEN;
            case "LIGHT_PURPLE" -> NamedTextColor.LIGHT_PURPLE;
            case "RED" -> NamedTextColor.RED;
            case "WHITE" -> NamedTextColor.WHITE;
            case "YELLOW" -> NamedTextColor.YELLOW;
            case "RESET" -> null; // Resetting the color
            default -> null; // Unknown color
        };
    }


    private boolean isOnCooldown(UUID playerId) {
        return cooldowns.containsKey(playerId) && (System.currentTimeMillis() - cooldowns.get(playerId)) < cooldownTime;
    }

    // Implementing TabCompletion
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 0 || args[args.length - 1].isEmpty()) {
            suggestions.add("<name>");
            suggestions.add("[");
            return suggestions;
        }

        String lastArg = args[args.length - 1];
        int lastTagOpen = lastArg.lastIndexOf('['); // Index of last opening '['

        // Case: A new tag starts (e.g., "[")
        if (lastTagOpen != -1 && lastArg.endsWith("[")) {
            suggestions.add("<name>");
            for (String style : STYLES) {
                suggestions.add(lastArg + style + "]");
            }
        }
        // Case: Multiple tags (e.g., "[GREEN][D")
        else if (lastTagOpen != -1 && lastArg.contains("][")) {
            String previousTag = lastArg.substring(0, lastArg.lastIndexOf("][") + 2); // Extract before last "]["
            String partialTag = lastArg.substring(lastArg.lastIndexOf("][") + 2);     // After last "]["

            for (String style : STYLES) {
                if (style.startsWith(partialTag.toUpperCase())) {
                    suggestions.add(previousTag + style + "]");
                }
            }

            if (partialTag.isEmpty()) {
                suggestions.add(previousTag + "<name>");
            }
        }
        // Case: Incomplete single tag (e.g., "[D")
        else if (lastArg.startsWith("[") && !lastArg.startsWith("[/")) {
            String partialTag = lastArg.substring(1); // After opening '['
            for (String style : STYLES) {
                if (style.startsWith(partialTag.toUpperCase())) {
                    suggestions.add("[" + style + "]");
                }
            }
        }
        // Case: Detect incomplete closing tag "[/]" or "[/AQUA]" or "[/"
        else if (lastArg.startsWith("[/")) {
            String partialTag = lastArg.substring(2); // After "[/"

            // If only "[/" is typed without a tag, suggest closing tag based on last open tag
            if (partialTag.isEmpty() && args.length > 1) {
                // Check the previous argument for a valid open tag
                String lastOpenTag = args[args.length - 2];
                if (lastOpenTag.startsWith("[") && !lastOpenTag.startsWith("[/")) {
                    String openTag = lastOpenTag.substring(1); // Extract the tag inside "["
                    suggestions.add("[/" + openTag + "]");
                }
            } else {
                // Suggest closing tags based on the partial closing tag input
                for (String style : STYLES) {
                    if (style.startsWith(partialTag.toUpperCase())) {
                        suggestions.add("[/" + style + "]");
                    }
                }
            }
        }
        // Case: Hex color code (e.g., "[#")
        else if (lastArg.startsWith("[#")) {
            if (lastArg.length() <= 7) { // Valid hex length
                suggestions.add("[#FFFFFF]");
                suggestions.add("[#FF0000]");
                suggestions.add("[#00FF00]");
                suggestions.add("[#0000FF]");
            }
        }
        // Case: Closing bracket exists (e.g., "]")
        else if (lastArg.endsWith("]")) {
            String beforeLastBracket = lastArg.substring(0, lastArg.length() - 1);
            suggestions.add(beforeLastBracket + "]<name>");
            suggestions.add(beforeLastBracket + "][");
        }

        return suggestions;
    }
}
