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

public class MkreloadCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("heartmk.command.mkreload")) {
            MessageUtils.sendColoredMessage(sender, "You do not have permission to use this command.", TextColor.fromHexString("#FF0000"));
            return true;
        }

        HeartMK.getInstance().reloadConfig();
        sender.sendMessage("Configuration reloaded successfully!");

        checkVersion();

        return true;
    }

    private void checkVersion() {
        FileConfiguration config = HeartMK.getInstance().getConfig();
        String currentVersion = config.getString("version", "0.0");
        if (!currentVersion.equals(HeartMK.REQUIRED_VERSION)) {
            HeartMK.getInstance().getLogger().warning("Configuration version mismatch! Current: " + currentVersion + ", Required: " + HeartMK.REQUIRED_VERSION);
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList(); // No arguments for /mkreload
    }
}
