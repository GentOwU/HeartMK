package org.gentuwu.heartMK;

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

    private static final String BEE_METADATA_KEY = HeartMK.getInstance().getConfig().getString("beeMetadataKey", "AlexeyZavr");
    private static final String CHERRY_SAPLING_NAME = HeartMK.getInstance().getConfig().getString("cherrySaplingName", "Cherrygram");
    private static final float EXPLOSION_POWER = (float) HeartMK.getInstance().getConfig().getDouble("explosionPower", 4.0);

    @EventHandler
    public void onEntityRightClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity instanceof Bee bee && hasMetadata(bee, BEE_METADATA_KEY)) {
            handleBeeRightClick(player, bee);
        }
    }

    private void handleBeeRightClick(Player player, Bee bee) {
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand.getType() == Material.CHERRY_SAPLING && isCherrygram(itemInHand)) {
            bee.getWorld().createExplosion(bee.getLocation(), EXPLOSION_POWER);
        }
    }

    private boolean isCherrygram(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        return itemMeta != null && itemMeta.hasDisplayName() && CHERRY_SAPLING_NAME.equals(itemMeta.getDisplayName());
    }

    private boolean hasMetadata(Entity entity, String key) {
        List<MetadataValue> metadataList = entity.getMetadata(key);
        return !metadataList.isEmpty();
    }
}
