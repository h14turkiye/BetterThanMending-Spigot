package fr.paulem.btm.interfaces;

import org.bukkit.inventory.ItemStack;

public interface IDamageSystem {
    boolean hasDamage(ItemStack item);

    int getDamage(ItemStack item);

    void setDamage(ItemStack item, int damage);

    /**
     * May return true if isn't damaged
     */
    boolean isDamageable(ItemStack item);
}
