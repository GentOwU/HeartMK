package org.gentuwu.heartMK;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class PutOnHead implements CommandExecutor, TabCompleter {

    private static final TextColor ERROR_COLOR = TextColor.fromHexString("#FF0000");
    private static final TextColor SUCCESS_COLOR = TextColor.fromHexString("#00FF00");
    private static final TextColor WARNING_COLOR = TextColor.fromHexString("#FFFF00");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false; // Command can only be executed by a player
        }

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() == Material.AIR) {
            MessageUtils.sendColoredMessage(player, "You must be holding an item to put it on your head!", ERROR_COLOR);
            return true;
        }

        ItemStack currentHelmet = player.getInventory().getHelmet();
        ItemStack oneItemToMove = itemInHand.clone();
        oneItemToMove.setAmount(1);
        player.getInventory().setHelmet(oneItemToMove);
        updateItemInHand(player, itemInHand);
        handleOldHelmet(player, currentHelmet);
        sendSuccessMessage(player, oneItemToMove);

        return true;
    }

    private void updateItemInHand(Player player, ItemStack itemInHand) {
        int newAmount = itemInHand.getAmount() - 1;
        if (newAmount <= 0) {
            player.getInventory().setItemInMainHand(null); // Remove item if count reaches 0
        } else {
            itemInHand.setAmount(newAmount);
        }
    }

    private void handleOldHelmet(Player player, ItemStack currentHelmet) {
        if (currentHelmet != null && currentHelmet.getType() != Material.AIR) {
            if (player.getInventory().addItem(currentHelmet).isEmpty()) {
                MessageUtils.sendColoredMessage(player, "Old helmet was moved to your inventory.", SUCCESS_COLOR);
            } else {
                player.getWorld().dropItem(player.getLocation(), currentHelmet);
                MessageUtils.sendColoredMessage(player, "Old helmet was dropped on the ground because your inventory is full.", ERROR_COLOR);
            }
        }
    }

    private void sendSuccessMessage(Player player, ItemStack item) {
        player.sendMessage(Component.text("You have placed ")
                .append(Component.text("1 ").color(WARNING_COLOR))
                .append(Component.text(item.getType().toString().toLowerCase().replace('_', ' '))
                        .color(WARNING_COLOR))
                .append(Component.text(" on your head.").color(SUCCESS_COLOR)));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList(); // No tab completion for this command
    }
}
