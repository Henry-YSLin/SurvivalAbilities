package io.github.henryyslin.survivalabilities;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public final class SurvivalAbilities extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        registerGlow();

        ConfigManager configManager = new ConfigManager(this, getConfig());
        getServer().getPluginManager().registerEvents(new AbilitiesListener(this, configManager), this);
    }

    private void registerGlow() {
        if (Enchantment.getByKey(new NamespacedKey(this, "glow")) != null) return;
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Glow glow = new Glow(new NamespacedKey(this, "glow"));
            Enchantment.registerEnchantment(glow);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
