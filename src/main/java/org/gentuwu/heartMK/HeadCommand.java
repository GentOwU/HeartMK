package org.gentuwu.heartMK;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class HeadCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            // Check if player is holding an item
            if (itemInHand == null || itemInHand.getType() == Material.AIR) {
                player.sendMessage(ChatColor.RED + "You must be holding an item to put it on your head!");
                return true;
            }

            // Get the current helmet
            ItemStack currentHelmet = player.getInventory().getHelmet();

            // Move 1 item to the head
            ItemStack oneItemToMove = itemInHand.clone();
            oneItemToMove.setAmount(1);
            player.getInventory().setHelmet(oneItemToMove);

            // Decrease the amount in the player's hand by 1
            itemInHand.setAmount(itemInHand.getAmount() - 1);

            // If there was an old helmet, try to move it to the inventory
            if (currentHelmet != null && currentHelmet.getType() != Material.AIR) {
                if (player.getInventory().addItem(currentHelmet).isEmpty()) {
                    player.sendMessage(ChatColor.GREEN + "Old helmet was moved to your inventory.");
                } else {
                    // If inventory is full, drop the old helmet at the player's location
                    player.getWorld().dropItem(player.getLocation(), currentHelmet);
                    player.sendMessage(ChatColor.RED + "Old helmet was dropped on the ground because your inventory is full.");
                }
            }

            player.sendMessage(ChatColor.GREEN + "You have placed " + ChatColor.GOLD + "1 piece" + ChatColor.GREEN + " of the item on your head.");
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Since /head does not take any arguments, we just return an empty list
        return Collections.emptyList();
    }
}
