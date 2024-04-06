package io.github.paulem.btm.listeners;

import io.github.paulem.btm.interfaces.DamageManager;
import io.github.paulem.btm.managers.CooldownManager;
import io.github.paulem.btm.managers.RepairManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class NeedManagersListenener implements Listener {
    protected final FileConfiguration config;
    protected final DamageManager damageManager;
    protected final RepairManager repairManager;
    protected final CooldownManager cooldownManager;

    public NeedManagersListenener(@NotNull FileConfiguration config, DamageManager damageManager, RepairManager repairManager){
        this.config = config;

        this.damageManager = damageManager;
        this.repairManager = repairManager;
        this.cooldownManager = new CooldownManager(config.getInt("cooldown.time", 0));
    }
}
