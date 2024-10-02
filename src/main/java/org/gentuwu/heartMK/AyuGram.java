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

public class AyuGram implements CommandExecutor, TabExecutor {

    private final String beeMetadataKey;
    private final Component beeCustomName;
    private static final String SUBCOMMAND_AUTGRAM = "autgram";

    public AyuGram() {
        this.beeMetadataKey = HeartMK.getInstance().getConfig().getString("beeMetadataKey", "AlexeyZavr");
        this.beeCustomName = Component.text("AlexeyZavr").color(TextColor.fromHexString("#6A0DAD"));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtils.sendColoredMessage(sender, "You must be a player to use this command.", TextColor.fromHexString("#FF0000"));
            return true;
        }

        try {
            Bee bee = player.getWorld().spawn(player.getLocation(), Bee.class);

            if (args.length == 1 && args[0].equalsIgnoreCase(SUBCOMMAND_AUTGRAM)) {
                bee.setBaby();
            }

            bee.setMetadata(beeMetadataKey, new FixedMetadataValue(HeartMK.getInstance(), true));
            bee.customName(beeCustomName);

            player.sendMessage(Component.text("You have summoned a bee named ")
                    .append(beeCustomName)
                    .append(Component.text(".").color(TextColor.fromHexString("#00FF00"))));

        } catch (Exception e) {
            MessageUtils.sendColoredMessage(sender, "Failed to summon the bee: " + e.getMessage(), TextColor.fromHexString("#FF0000"));
        }

        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return args.length == 1 ? Collections.singletonList(SUBCOMMAND_AUTGRAM) : Collections.emptyList();
    }
}
