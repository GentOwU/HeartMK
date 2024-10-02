package org.gentuwu.heartMK;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ConfigReload implements CommandExecutor, TabExecutor {

    private static final String NO_PERMISSION = "You do not have permission to use this command.";
    private static final String RELOAD_SUCCESS_KEY = "messages.reload_success";
    private static final String NO_PERMISSION_KEY = "messages.no_permission";
    private static final TextColor WARNING_COLOR = TextColor.fromHexString("#FFA500");
    private static final TextColor SUCCESS_COLOR = TextColor.fromHexString("#00FF00"); // Custom green color

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        HeartMK.getInstance().getLogger().info(sender.getName() + " attempted to reload configuration.");

        if (!sender.hasPermission("heartmk.command.mkreload")) {
            sendMessage(sender, NO_PERMISSION_KEY, NO_PERMISSION, TextColor.fromHexString("#FF0000"));
            return true;
        }

        HeartMK.getInstance().reloadConfig();
        sendMessage(sender, RELOAD_SUCCESS_KEY, "Configuration reloaded successfully!", SUCCESS_COLOR);
        HeartMK.getInstance().getLogger().info(sender.getName() + " reloaded configuration successfully.");

        checkVersion(sender);
        return true;
    }

    private void checkVersion(CommandSender sender) {
        FileConfiguration config = HeartMK.getInstance().getConfig();
        String currentVersion = config.getString("version", "0.3.0-SNAPSHOT");

        if (!currentVersion.equals(HeartMK.REQUIRED_VERSION)) {
            String warningMessage = String.format("Configuration version mismatch! Current: %s, Required: %s", currentVersion, HeartMK.REQUIRED_VERSION);
            HeartMK.getInstance().getLogger().warning(warningMessage);
            sender.sendMessage(WARNING_COLOR + "Warning: " + warningMessage);
        }
    }

    private void sendMessage(CommandSender sender, String configKey, String defaultMessage, TextColor color) {
        String message = HeartMK.getInstance().getConfig().getString(configKey, defaultMessage);
        MessageUtils.sendColoredMessage(sender, message, color);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList(); // No arguments for /mkreload
    }
}
