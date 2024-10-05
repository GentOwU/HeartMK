package org.gentuwu.heartMK;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public final class HeartMK extends JavaPlugin {

    private static HeartMK instance;
    public static final String REQUIRED_VERSION = "0.3.3-SNAPSHOT";
    private static final String CONFIG_FILE_NAME = "config.yml";
    private static final String BACKUP_FILE_NAME = "config_backup.yml";

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        checkConfig();

        getServer().getPluginManager().registerEvents(new BeeBoom(), this);
        registerCommands();
        getLogger().info("HeartMK plugin has been enabled.");
    }

    private void registerCommands() {
        registerCommand("ayugram", new AyuGram());
        int renameCooldown = getConfig().getInt("renameCooldown", 10);  // Fetch renameCooldown from config or use 10 seconds as default
        registerCommand("rename", new RenameItem(renameCooldown));  // Pass the timeout to RenameItem
        registerCommand("head", new PutOnHead());
        registerCommand("mkreload", new ConfigReload());
    }

    private void registerCommand(String commandName, CommandExecutor executor) {
        PluginCommand command = getCommand(commandName);
        if (command != null) {
            command.setExecutor(executor);
            if (executor instanceof TabExecutor tabExecutor) {
                command.setTabCompleter(tabExecutor);
            }
        } else {
            getLogger().warning("Command '" + commandName + "' is not defined in plugin.yml.");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("HeartMK plugin has been disabled.");
    }

    public static HeartMK getInstance() {
        return instance;
    }

    private void checkConfig() {
        File configFile = new File(getDataFolder(), CONFIG_FILE_NAME);
        File backupFile = new File(getDataFolder(), BACKUP_FILE_NAME);

        if (backupFile.exists()) {
            getLogger().warning("A backup of the config already exists!");
        } else {
            createBackup(configFile, backupFile);
        }

        FileConfiguration config = getConfig();
        String currentVersion = config.getString("version", "0.0");

        if (!currentVersion.equals(REQUIRED_VERSION)) {
            getLogger().warning("Configuration version mismatch! Backup created.");
            getLogger().warning(String.format("Current: %s, Required: %s", currentVersion, REQUIRED_VERSION));
            createBackup(configFile, backupFile);
        }
    }

    private void createBackup(File configFile, File backupFile) {
        try {
            Files.copy(configFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            getLogger().info("Backup created successfully.");
        } catch (IOException e) {
            getLogger().severe("Failed to create a backup of the config: " + e.getMessage());
        }
    }
}
