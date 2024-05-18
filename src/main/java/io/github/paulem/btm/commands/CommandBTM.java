package io.github.paulem.btm.commands;

import io.github.paulem.btm.BTM;
import io.github.paulem.btm.config.PlayerDataConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CommandBTM implements CommandExecutor, TabCompleter {
    public static PlayerDataConfig playerDataConfig;

    public final BTM plugin;

    public CommandBTM(BTM plugin) {
        this.plugin = plugin;

        playerDataConfig = new PlayerDataConfig(this.plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if((args.length == 0 || args[0].equalsIgnoreCase("toggle")) && sender instanceof Player) {
            Player player = (Player) sender;

            boolean enabled;
            try {
                enabled = playerDataConfig.getPlayerOrCreate(player, true);

                enabled = playerDataConfig.setPlayer(player, !enabled);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            player.sendMessage("Mending's ability has been successfully " + (enabled ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled") + " !");

            return true;
        }

        sender.sendMessage(ChatColor.BLUE + "Running BetterThanMending " + plugin.getDescription().getVersion() + " with config version " + plugin.getConfig().getInt("version", 0));
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Arrays.asList("toggle", "version");
    }
}
