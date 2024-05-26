package org.nicolas.crossplay;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Main extends JavaPlugin implements Listener {

    private String bedrockPrefix;
    private boolean allowBedrock;
    private boolean allowJava;
    private String disconnectMessage;

    private List<String> whitelist;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        loadConfigValues();

        getServer().getPluginManager().registerEvents(this, this);
    }

    private void loadConfigValues() {
        FileConfiguration config = getConfig();
        bedrockPrefix = config.getString("Bedrock-Prefix", ".");
        allowBedrock = config.getBoolean("Allow-Bedrock", true);
        allowJava = config.getBoolean("Allow-Java", true);
        String rawDisconnectMessage = config.getString("Disconnect-Message", "Pues el que ponga");
        disconnectMessage = ChatColor.translateAlternateColorCodes('&', rawDisconnectMessage);
        whitelist = config.getStringList("Whitelist");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();
        boolean isBedrockPlayer = playerName.startsWith(bedrockPrefix);

        if (whitelist.contains(playerName)) {
            return;
        }

        if ((isBedrockPlayer && !allowBedrock) || (!isBedrockPlayer && !allowJava)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getPlayer().kickPlayer(disconnectMessage);
                }
            }.runTaskLater(this, 1L);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("crossplay.admin")) {
            if (command.getName().equalsIgnoreCase("crossreload")) {
                this.reloadConfig();
                loadConfigValues();
                sender.sendMessage("Configuración del plugin Crossplay recargada.");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "No tienes permisos para hacer eso.");
        }
        return false;
    }
}
