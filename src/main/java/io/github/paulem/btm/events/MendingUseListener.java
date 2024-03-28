package io.github.paulem.btm.events;

import io.github.paulem.btm.interfaces.DamageManager;
import io.github.paulem.btm.managers.CooldownManager;
import io.github.paulem.btm.managers.RepairManager;
import io.github.paulem.btm.versioning.Versioning;
import org.bukkit.ChatColor;
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

import java.time.Duration;
import java.util.UUID;

public class MendingUseListener implements Listener {
    private static final Sound ENDERMAN_TELEPORT_SOUND = Versioning.isPost13() ?
            Sound.valueOf(Sound.class, "ENTITY_ENDERMAN_TELEPORT") : Sound.valueOf(Sound.class, "ENTITY_ENDERMEN_TELEPORT");

    private final FileConfiguration config;
    private final DamageManager damageManager;
    private final RepairManager repairManager;
    private final CooldownManager cooldownManager;

    public MendingUseListener(FileConfiguration config, DamageManager damageManager, RepairManager repairManager){
        this.config = config;
        this.damageManager = damageManager;
        this.repairManager = repairManager;

        int cooldown = config.getInt("cooldown.time", 1);
        this.cooldownManager = new CooldownManager(cooldown > 1 ? cooldown-1 : cooldown);
    }

    @EventHandler
    public void onItemUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if(!player.hasPermission("btm.use")) return;

        ItemStack item = player.getInventory().getItemInMainHand();

        if(item.getType() == Material.AIR) return;

        if(!damageManager.isDamageable(item)) return;

        // Continue if item has Mending, the player is sneaking, and he's right-clicking in air
        if(!player.isSneaking() ||
                !item.containsEnchantment(Enchantment.MENDING) ||
                e.getAction() != Action.RIGHT_CLICK_AIR) return;

        // If it doesn't have any damage, return
        if(!damageManager.hasDamage(item)) return;

        UUID playerId = player.getUniqueId();
        Duration timeLeft = cooldownManager.getRemainingCooldown(playerId);
        if (timeLeft.isZero() || timeLeft.isNegative()) {
            repairManager.repairItem(player, item, config.getBoolean("playSound", true), config.getBoolean("playEffect", true));

            cooldownManager.setCooldown(playerId, Duration.ofSeconds(cooldownManager.getDefaultCooldown()));

            e.setCancelled(true);
        } else {
            if(config.getBoolean("cooldown.message", true)) {
                String text = config.getString(
                        "cooldown.text",
                        ChatColor.DARK_RED + "Please wait " + timeLeft.getSeconds() + " seconds before using this ability!"

                        ).replace("&", "§")
                        .replace("$s", ""+timeLeft.getSeconds());

                player.sendMessage(text);
            }
            if(config.getBoolean("cooldown.sound", true))
                player.playSound(
                        player.getLocation(),
                        ENDERMAN_TELEPORT_SOUND,
                        1, 1);
        }
    }
}
