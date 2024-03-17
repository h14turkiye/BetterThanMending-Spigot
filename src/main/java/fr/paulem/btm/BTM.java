package fr.paulem.btm;

import fr.paulem.btm.interfaces.IDamageSystem;
import fr.paulem.btm.legacy.LegacyDamage;
import fr.paulem.btm.newer.NewerDamage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class BTM extends JavaPlugin implements Listener {
    public static IDamageSystem damageSystem = Versioning.isPost17() ? new NewerDamage() : new LegacyDamage();
    public static FileConfiguration config;

    @Override
    public void onEnable() {
        if(!Versioning.isPost9()) {
            getLogger().severe("You need to use a 1.9+ server! Mending isn't present in older versions!");
            setEnabled(false);
        }
        this.saveDefaultConfig();
        config = getConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled! See you later!");
    }

    @EventHandler
    public void onItemUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if(item.getType() == Material.AIR) return;

        if(!damageSystem.isDamageable(item)) return;


        // Continue if item has Mending, the player is sneaking, and he's right-clicking in air
        if(!player.isSneaking() ||
                !item.containsEnchantment(Enchantment.MENDING) ||
                e.getAction() != Action.RIGHT_CLICK_AIR) return;

        // If it doesn't have any damage, return
        if(!damageSystem.hasDamage(item)) return;

        double ratio = item.getEnchantmentLevel(Enchantment.MENDING) * 2.0;
        int playerXP = ExperienceSystem.getPlayerXP(player);

        int itemDamages = damageSystem.getDamage(item);

        if (playerXP >= 30 && itemDamages >= 20 * ratio) {
            damageSystem.setDamage(item, itemDamages - (int) (20 * ratio));
            ExperienceSystem.changePlayerExp(player, -20);
        } else if (playerXP >= 2) {
            damageSystem.setDamage(item, itemDamages - (int) (2 * ratio));
            ExperienceSystem.changePlayerExp(player, -2);
        } else return;

        // Should play sound?
        if(config.getBoolean("playSound", true))
            player.playSound(player.getLocation(),
                    Sound.BLOCK_ANVIL_PLACE,
                    (float) config.getDouble("soundVolume", 1),
                    (float) config.getDouble("soundPitch", 1));

        e.setCancelled(true);
    }
}
