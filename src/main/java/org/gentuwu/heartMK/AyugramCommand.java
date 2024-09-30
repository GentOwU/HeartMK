package org.gentuwu.heartMK;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class AyugramCommand implements CommandExecutor, TabExecutor {

    private static final String BEE_METADATA_KEY = HeartMK.getInstance().getConfig().getString("beeMetadataKey", "AlexeyZavr");
    private static final String BEE_CUSTOM_NAME = ChatColor.DARK_PURPLE + "AlexeyZavr";
    private static final String SUBCOMMAND_AUTGRAM = "autgram";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
            return true;
        }

        Bee bee = player.getWorld().spawn(player.getLocation(), Bee.class);
        if (args.length == 1 && args[0].equalsIgnoreCase(SUBCOMMAND_AUTGRAM)) {
            bee.setBaby();
        }

        bee.setMetadata(BEE_METADATA_KEY, new FixedMetadataValue(HeartMK.getInstance(), true));
        bee.setCustomName(BEE_CUSTOM_NAME);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return args.length == 1 ? Collections.singletonList(SUBCOMMAND_AUTGRAM) : Collections.emptyList();
    }
}
