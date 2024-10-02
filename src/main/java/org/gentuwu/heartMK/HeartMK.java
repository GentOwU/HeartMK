package org.gentuwu.heartMK;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class HeartMK extends JavaPlugin {

    private static HeartMK instance;
    public static final String REQUIRED_VERSION = "0.2.0-SNAPSHOT"; // Updated to match the config version

    private final Map<String, CommandExecutor> commandMap = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new EntityListener(), this);
        registerCommands();
        getLogger().info("HeartMK plugin has been enabled.");
    }

    private void registerCommands() {
        commandMap.put("ayugram", new AyugramCommand());
        commandMap.put("ren", new RenameCommand());
        commandMap.put("head", new HeadCommand());
        commandMap.put("mkreload", new MkreloadCommand());

        for (Map.Entry<String, CommandExecutor> entry : commandMap.entrySet()) {
            registerCommand(entry.getKey(), entry.getValue());
        }
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
}
