package io.github.henry_yslin.survivalabilities;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private final Plugin plugin;
    private final FileConfiguration config;

    public ConfigManager(Plugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;

        initializeConfig();
    }

    private void initializeConfig() {
        if (config.getConfigurationSection("player-choices") == null) {
            config.createSection("player-choices");
        }
        config.addDefault("player-list", new ArrayList<String>());
        config.addDefault("prompt-on-join", new ArrayList<String>());
        config.options().copyDefaults(true);
        plugin.saveConfig();
    }

    public boolean isPlayerNew(String player) {
        return !config.getStringList("player-list").contains(player);
    }

    public List<String> getAbilityChoices(String player) {
        return config.getStringList("player-choices." + player);
    }

    public void setAbilityChoices(String player, List<String> choices) {
        config.set("player-choices." + player, choices);
        plugin.saveConfig();
    }

    public boolean promptOnJoin(String player) {
        return config.getStringList("prompt-on-join").contains(player);
    }

    public void addNewPlayer(String player) {
        List<String> playerList = config.getStringList("player-list");
        playerList.add(player);
        config.set("player-list", playerList);

        List<String> promptList = config.getStringList("prompt-on-join");
        promptList.add(player);
        config.set("prompt-on-join", promptList);

        plugin.saveConfig();
    }

    public void removeNewPlayer(String player) {
        List<String> promptList = config.getStringList("prompt-on-join");
        promptList.remove(player);
        config.set("prompt-on-join", promptList);

        plugin.saveConfig();
    }

    public void clearChoices(String player) {
        config.set("player-choices." + player, new ArrayList<String>());
        plugin.saveConfig();
    }
}
