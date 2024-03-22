package io.github.paulem.btm.newer;

import io.github.paulem.btm.interfaces.IDamageSystem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class NewerDamage implements IDamageSystem {
    @Override
    public boolean hasDamage(ItemStack item) {
        return ((Damageable) item.getItemMeta()).hasDamage();
    }

    @Override
    public int getDamage(ItemStack item) {
        return ((Damageable) item.getItemMeta()).getDamage();
    }

    @Override
    public void setDamage(ItemStack item, int damage) {
        Damageable damageable = (Damageable) item.getItemMeta();
        damageable.setDamage(damage);
        item.setItemMeta(damageable);
    }

    @Override
    public boolean isDamageable(ItemStack item) {
        return item.getItemMeta() instanceof Damageable;
    }
}
