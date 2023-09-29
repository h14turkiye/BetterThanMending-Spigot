package fr.paulem.btm;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.plugin.java.JavaPlugin;

// upload github et spigot

public class BTM extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getLogger().info("Enabled !");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled !");
    }

    @EventHandler
    public void onItemUse(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        Player player = e.getPlayer();
        if(item == null || !player.isSneaking() ||
                !item.containsEnchantment(Enchantment.MENDING) ||
                !(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
        if(isPost17() && !(item.getItemMeta() instanceof Damageable)) return;
        if(!hasDamage(item)) return;

        double ratio = item.getEnchantmentLevel(Enchantment.MENDING) * 2.0;
        int playerXP = getPlayerXP(player);

        if (playerXP >= 30 && getDamage(item) >= 20 * ratio) {
            setDamage(item, getDamage(item) - (int) (20 * ratio));
            changePlayerExp(player, -20);
        } else if (playerXP >= 2) {
            setDamage(item, getDamage(item) - (int) (2 * ratio));
            changePlayerExp(player, -2);
        } else return;
        e.setCancelled(true);
    }

    public static boolean hasDamage(ItemStack item){
        System.out.println((item.getType().getMaxDurability() - item.getDurability()) < item.getType().getMaxDurability());
        if(isPost17()) return ((Damageable) item.getItemMeta()).hasDamage();
        else return (item.getType().getMaxDurability() - item.getDurability()) < item.getType().getMaxDurability();
    }

    public static int getDamage(ItemStack item){
        if(isPost17()) return ((Damageable) item.getItemMeta()).getDamage();
        else return item.getDurability();
    }

    public static void setDamage(ItemStack item, int damage){
        if(isPost17()){
            Damageable damageable = (Damageable) item.getItemMeta();
            damageable.setDamage(damage);
            item.setItemMeta(damageable);
        } else item.setDurability((short) damage);
    }

    public static void changePlayerExp(Player player, int exp){
        int currentExp = getPlayerXP(player);

        player.setExp(0);
        player.setLevel(0);

        player.giveExp(currentExp + exp);
    }

    public static int getPlayerXP(Player player) {
        return (int) (getExperienceForLevel(player.getLevel()) + (player.getExp() * player.getExpToLevel()));
    }

    public static int getExperienceForLevel(int level) {
        if (level == 0) return 0;
        if (level > 0 && level < 16) return (int) (Math.pow(level, 2) + 6 * level);
        else if (level > 15 && level < 32) return (int) (2.5 * Math.pow(level, 2) - 40.5 * level + 360);
        else return (int) (4.5 * Math.pow(level, 2) - 162.5 * level + 2220);
    }

    public static boolean isPost17() {
        String version = Bukkit.getVersion();
        String[] mcParts = version.substring(version.indexOf("MC: ") + 4, version.length() - 1).split("\\.");
        return Integer.parseInt(mcParts[1]) > 17 || (Integer.parseInt(mcParts[1]) == 17 && Integer.parseInt(mcParts[2]) >= 1);
    }
}
