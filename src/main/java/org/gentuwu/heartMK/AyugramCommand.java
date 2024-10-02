package org.gentuwu.heartMK;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
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
    private static final Component BEE_CUSTOM_NAME = Component.text("AlexeyZavr").color(TextColor.fromHexString("#6A0DAD"));
    private static final String SUBCOMMAND_AUTGRAM = "autgram";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtils.sendColoredMessage(sender, "You must be a player to use this command.", TextColor.fromHexString("#FF0000"));
            return true;
        }

        Bee bee = player.getWorld().spawn(player.getLocation(), Bee.class);
        if (args.length == 1 && args[0].equalsIgnoreCase(SUBCOMMAND_AUTGRAM)) {
            bee.setBaby();
        }

        bee.setMetadata(BEE_METADATA_KEY, new FixedMetadataValue(HeartMK.getInstance(), true));
        bee.customName(BEE_CUSTOM_NAME);

        player.sendMessage(Component.text("You have summoned a bee named ")
                .append(BEE_CUSTOM_NAME)
                .append(Component.text(".").color(TextColor.fromHexString("#00FF00"))));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return args.length == 1 ? Collections.singletonList(SUBCOMMAND_AUTGRAM) : Collections.emptyList();
    }
}
