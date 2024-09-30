package org.gentuwu.heartMK;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class RenameCommand implements CommandExecutor {

    private final HashMap<UUID, Long> lastRename = new HashMap<>();

    @Override
    public synchronized boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length < 1) {
                player.sendMessage(ChatColor.RED + "Usage: /ren <new name>");
                return true;
            }

            long currentTime = System.currentTimeMillis();
            UUID playerId = player.getUniqueId();
            if (lastRename.containsKey(playerId) && (currentTime - lastRename.get(playerId)) < 60000) {
                player.sendMessage(ChatColor.RED + "You can only rename an item once every minute!");
                return true;
            }

            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() == Material.AIR) {
                player.sendMessage(ChatColor.RED + "You must be holding an item to rename it!");
                return true;
            }

            String newName = ChatColor.GOLD + String.join(" ", args);
            item.editMeta(meta -> meta.setDisplayName(newName));
            player.sendMessage(ChatColor.GREEN + "Item renamed to: " + newName);

            lastRename.put(playerId, currentTime);
            return true;
        }
        return false;
    }
}
