package org.gentuwu.heartMK;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class EntityListener implements Listener {

    private final String beeMetadataKey;
    private final String cherrySaplingName;
    private final float explosionPower;

    public EntityListener() {
        // Load configuration values once in the constructor
        beeMetadataKey = HeartMK.getInstance().getConfig().getString("beeMetadataKey", "AlexeyZavr");
        cherrySaplingName = HeartMK.getInstance().getConfig().getString("cherrySaplingName", "Cherrygram");
        explosionPower = (float) HeartMK.getInstance().getConfig().getDouble("explosionPower", 1.0);
    }

    @EventHandler
    public void onEntityRightClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity instanceof Bee bee && hasMetadata(bee, beeMetadataKey)) {
            handleBeeRightClick(player, bee);
        }
    }

    private void handleBeeRightClick(Player player, Bee bee) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand.getType() == Material.CHERRY_SAPLING && isCherrygram(itemInHand)) {
            bee.getWorld().createExplosion(bee.getLocation(), explosionPower);
            player.sendMessage("Boom! The bee has exploded.");
        } else {
            player.sendMessage("You need to hold a Cherrygram sapling to interact with this bee.");
        }
    }

    private boolean isCherrygram(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        return itemMeta != null && itemMeta.hasDisplayName() &&
                itemMeta.displayName().equals(Component.text(cherrySaplingName));
    }

    private boolean hasMetadata(Entity entity, String key) {
        List<MetadataValue> metadataList = entity.getMetadata(key);
        return !metadataList.isEmpty();
    }
}
