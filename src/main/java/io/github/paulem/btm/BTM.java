package io.github.paulem.btm;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import io.github.paulem.btm.commands.CommandBTM;
import io.github.paulem.btm.libs.bstats.Metrics;
import io.github.paulem.btm.listeners.MendingUseListener;
import io.github.paulem.btm.listeners.PreventDestroyListener;
import io.github.paulem.btm.config.ConfigManager;
import io.github.paulem.btm.interfaces.DamageManager;
import io.github.paulem.btm.legacy.LegacyDamage;
import io.github.paulem.btm.managers.RepairManager;
import io.github.paulem.btm.newer.NewerDamage;
import io.github.paulem.btm.versioning.Versioning;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class BTM extends JavaPlugin {
    private static final String SPIGOT_RESOURCE_ID = "112248";
    private static final int BSTAT_ID = 21472;
    public static FileConfiguration config;

    private static TaskScheduler scheduler;

    private final DamageManager DAMAGE_MANAGER = Versioning.isPost17() ? new NewerDamage() : new LegacyDamage();
    private RepairManager REPAIR_MANAGER;

    @Override
    public void onEnable() {
        if(!Versioning.isPost9()) {
            getLogger().severe("You need to use a 1.9+ server! Mending isn't present in older versions!");
            setEnabled(false);
        }

        saveDefaultConfig();
        config = getConfig();

        scheduler = UniversalScheduler.getScheduler(this);

        new ConfigManager(this).migrate();
        REPAIR_MANAGER = new RepairManager(this, DAMAGE_MANAGER);

        new UpdateChecker(this, UpdateCheckSource.SPIGET, SPIGOT_RESOURCE_ID) // You can also use Spiget instead of Spigot - Spiget's API is usually much faster up to date.
                .checkEveryXHours(24) // Check every 24 hours
                .setChangelogLink(SPIGOT_RESOURCE_ID)
                .setNotifyOpsOnJoin(true)
                .checkNow(); // And check right now

        getServer().getPluginManager().registerEvents(new MendingUseListener(config, DAMAGE_MANAGER, REPAIR_MANAGER), this);
        getServer().getPluginManager().registerEvents(new PreventDestroyListener(config, DAMAGE_MANAGER, REPAIR_MANAGER), this);

        CommandBTM commandBTM = new CommandBTM(this);
        getCommand("btm").setExecutor(commandBTM);
        getCommand("btm").setTabCompleter(commandBTM);

        getLogger().info("Enabled!");

        if(config.getBoolean("auto-repair", false))
            REPAIR_MANAGER.initAutoRepair();

        if(config.getBoolean("bstat", true)){
            new Metrics(this, BSTAT_ID);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled! See you later!");
    }

    public static TaskScheduler getScheduler() {
        return scheduler;
    }
}
