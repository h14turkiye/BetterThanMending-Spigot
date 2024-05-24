package io.github.paulem.btm.listeners.extendables;

import io.github.paulem.btm.config.PlayerDataConfig;
import io.github.paulem.btm.damage.DamageManager;
import io.github.paulem.btm.managers.RepairManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class DataConfigManagersListener extends ManagersListener {
    protected final PlayerDataConfig playerDataConfig;

    public DataConfigManagersListener(@NotNull FileConfiguration config, DamageManager damageManager, RepairManager repairManager, PlayerDataConfig playerDataConfig) {
        super(config, damageManager, repairManager);

        this.playerDataConfig = playerDataConfig;
    }
}
