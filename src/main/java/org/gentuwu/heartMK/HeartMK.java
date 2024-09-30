package org.gentuwu.heartMK;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public final class HeartMK extends JavaPlugin {

    private static HeartMK instance;

    @Override
    public void onEnable() {
        instance = this;

        // Save default config
        saveDefaultConfig(); // This creates the config.yml if it doesn't exist

        // Register event listeners
        getServer().getPluginManager().registerEvents(new EntityListener(), this);

        // Register commands
        registerCommands(); // Move command registration to a separate method

        getLogger().info("HeartMK plugin has been enabled.");
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new EntityListener(), this);
    }

    private void registerCommands() {
        registerCommand("ayugram", new AyugramCommand());
        registerCommand("ren", new RenameCommand());
        registerCommand("head", new HeadCommand());
    }

    private void registerCommand(String commandName, CommandExecutor executor) {
        if (getCommand(commandName) != null) {
            getCommand(commandName).setExecutor(executor);
            if (executor instanceof TabExecutor) {
                getCommand(commandName).setTabCompleter((TabExecutor) executor);
            }
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("HeartMK plugin has been disabled.");
    }

    public static HeartMK getInstance() {
        return instance;
    }
}
