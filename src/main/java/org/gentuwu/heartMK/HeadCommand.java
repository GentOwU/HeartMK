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

public class HeadCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            // Check if player is holding an item
            if (itemInHand.getType() == Material.AIR) {
                player.sendMessage(Component.text("You must be holding an item to put it on your head!").color(TextColor.fromHexString("#FF0000")));
                return true;
            }

            // Get the current helmet
            ItemStack currentHelmet = player.getInventory().getHelmet();

            // Move 1 item to the head
            ItemStack oneItemToMove = itemInHand.clone();
            oneItemToMove.setAmount(1);
            player.getInventory().setHelmet(oneItemToMove);

            // Decrease the amount in the player's hand by 1
            int newAmount = itemInHand.getAmount() - 1;
            if (newAmount <= 0) {
                player.getInventory().setItemInMainHand(null); // Remove item if count reaches 0
            } else {
                itemInHand.setAmount(newAmount);
            }

            // If there was an old helmet, try to move it to the inventory
            if (currentHelmet != null && currentHelmet.getType() != Material.AIR) {
                if (player.getInventory().addItem(currentHelmet).isEmpty()) {
                    player.sendMessage(Component.text("Old helmet was moved to your inventory.").color(TextColor.fromHexString("#00FF00")));
                } else {
                    // If inventory is full, drop the old helmet at the player's location
                    player.getWorld().dropItem(player.getLocation(), currentHelmet);
                    player.sendMessage(Component.text("Old helmet was dropped on the ground because your inventory is full.").color(TextColor.fromHexString("#FF0000")));
                }
            }

            // Send a success message with the item name
            player.sendMessage(Component.text("You have placed ")
                    .append(Component.text("1 ").color(TextColor.fromHexString("#FFFF00")))
                    .append(Component.text(oneItemToMove.getType().toString().toLowerCase().replace('_', ' '))
                            .color(TextColor.fromHexString("#FFFF00")))
                    .append(Component.text(" on your head.").color(TextColor.fromHexString("#00FF00"))));

            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        // Since /head does not take any arguments, we just return an empty list
        return Collections.emptyList();
    }
}
