package net.terra;

import lombok.Getter;
import lombok.Setter;

import net.terra.listener.*;
import net.terra.provider.Provider;
import net.terra.provider.database.Database;
import net.terra.provider.database.DatabaseProvider;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Setter @Getter
public final class Plugin extends JavaPlugin {

    private Database pool;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        initProviders(new DatabaseProvider());

        registerListeners(new MachineListener());
    }

    public void initProviders(Provider... providers) {
        for(Provider provider : providers) {
            provider.initProvider(this);
        }
    }

    public void registerListeners(Listener... listeners) {
        for(Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }

    public static Plugin getInstance() {
        return getPlugin(Plugin.class);
    }

}