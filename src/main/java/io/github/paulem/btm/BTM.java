package io.github.paulem.btm;

import com.github.Anon8281.universalScheduler.UniversalRunnable;
import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import io.github.paulem.btm.configuration.ConfigMigration;
import io.github.paulem.btm.experience.ExperienceSystem;
import io.github.paulem.btm.interfaces.DamageSystem;
import io.github.paulem.btm.legacy.LegacyDamage;
import io.github.paulem.btm.newer.NewerDamage;
import io.github.paulem.btm.versioning.Versioning;
import org.bukkit.Bukkit;
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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class BTM extends JavaPlugin implements Listener {
    private static final String SPIGOT_RESOURCE_ID = "112248";
    private static TaskScheduler scheduler;
    public static final int CONFIG_VERSION = 2;

    public static final DamageSystem damageSystem = Versioning.isPost17() ? new NewerDamage() : new LegacyDamage();
    public static FileConfiguration config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();

        new ConfigMigration(this).migrate();

        scheduler = UniversalScheduler.getScheduler(this);

        new UpdateChecker(this, UpdateCheckSource.SPIGET, SPIGOT_RESOURCE_ID) // You can also use Spiget instead of Spigot - Spiget's API is usually much faster up to date.
                .checkEveryXHours(24) // Check every 24 hours
                .setChangelogLink(SPIGOT_RESOURCE_ID)
                .setNotifyOpsOnJoin(true)
                .checkNow(); // And check right now

        if(!Versioning.isPost9()) {
            getLogger().severe("You need to use a 1.9+ server! Mending isn't present in older versions!");
            setEnabled(false);
        }

        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Enabled!");

        long delay = config.getLong("delay", 40L);
        boolean repairFullInventory = config.getBoolean("repairFullInventory", true);

        if(config.getBoolean("auto-repair", false)) getScheduler().runTaskTimer(new UniversalRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()){
                    if(!player.hasPermission("btm.use")) continue;

                    List<ItemStack> damageables = Arrays.stream(player.getInventory().getContents())
                            .filter(i -> i != null &&
                                    i.getItemMeta() != null &&
                                    i.getType() != Material.AIR &&
                                    i.containsEnchantment(Enchantment.MENDING) &&
                                    damageSystem.isDamageable(i) &&
                                    damageSystem.hasDamage(i)).collect(Collectors.toList());
                    if(!damageables.isEmpty()) {
                        if (repairFullInventory) {
                            for (ItemStack item : damageables) {
                                if (item != null)
                                    repairItem(player, item, false);
                            }
                        } else {
                            ItemStack item = damageables.get(ThreadLocalRandom.current().nextInt(damageables.size()));
                            if (item != null)
                                repairItem(player, item, false);
                        }
                    }
                }
            }
        }, delay, delay);
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled! See you later!");
    }

    @EventHandler
    public void onItemUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if(!player.hasPermission("btm.use")) return;

        ItemStack item = player.getInventory().getItemInMainHand();

        if(item.getType() == Material.AIR) return;

        if(!damageSystem.isDamageable(item)) return;


        // Continue if item has Mending, the player is sneaking, and he's right-clicking in air
        if(!player.isSneaking() ||
                !item.containsEnchantment(Enchantment.MENDING) ||
                e.getAction() != Action.RIGHT_CLICK_AIR) return;

        // If it doesn't have any damage, return
        if(!damageSystem.hasDamage(item)) return;

        repairItem(player, item, config.getBoolean("playSound", true));

        e.setCancelled(true);
    }

    public static void repairItem(Player player, ItemStack item, boolean playSound){

        double ratio = item.getEnchantmentLevel(Enchantment.MENDING) * config.getDouble("ratio", 2.0);
        int playerXP = ExperienceSystem.getPlayerXP(player);

        int itemDamages = damageSystem.getDamage(item);

        int expValue = config.getInt("expValue", 20);

        if (playerXP >= 30 && itemDamages >= expValue * ratio) {
            damageSystem.setDamage(item, itemDamages - (int) (expValue * ratio));
            ExperienceSystem.changePlayerExp(player, -expValue);
        } else if (playerXP >= expValue/10) {
            damageSystem.setDamage(item, itemDamages - (int) (expValue/10 * ratio));
            ExperienceSystem.changePlayerExp(player, -expValue/10);
        } else return;

        // Should play sound?
        if(playSound)
            player.playSound(player.getLocation(),
                    Sound.BLOCK_ANVIL_PLACE,
                    (float) config.getDouble("soundVolume", 1),
                    (float) config.getDouble("soundPitch", 1));

    }

    public static TaskScheduler getScheduler() {
        return scheduler;
    }
}
