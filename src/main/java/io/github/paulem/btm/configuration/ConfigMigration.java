package io.github.paulem.btm.configuration;

import com.google.common.base.Charsets;
import io.github.paulem.btm.BTM;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConfigMigration {
    private final BTM plugin;

    public ConfigMigration(BTM plugin){
        this.plugin = plugin;
    }

    public void migrate(){
        FileConfiguration config = plugin.getConfig();

        int detectedVersion = config.getInt("version", 0);

        plugin.getLogger().info("Detected version: " + (detectedVersion != 0 ? detectedVersion : "none"));

        if(BTM.CONFIG_VERSION == detectedVersion) return;

        plugin.getLogger().warning("Your configuration is outdated! Upgrading...");

        plugin.getConfig().options().copyDefaults(true);
        plugin.getConfig().setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("config.yml"), Charsets.UTF_8)));
        plugin.saveConfig();

        plugin.getLogger().info("Your configuration has been updated! You can find more informations about new option on the plugin resource page!");
    }
}
