package io.github.henry_yslin.survivalabilities;

import io.github.henry_yslin.enderpearlabilities.EnderPearlAbilities;
import io.github.henry_yslin.enderpearlabilities.abilities.AbilityInfo;
import io.github.henry_yslin.enderpearlabilities.abilities.ActivationHand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class AbilitySelectionUI implements Listener {
    private final Plugin plugin;
    private final Inventory inventory;
    private final List<String> choices;
    private boolean completed = false;
    private final Consumer<String> onSelect;
    private final Runnable onCancel;

    public AbilitySelectionUI(Plugin plugin, List<String> choices, Consumer<String> onSelect, Runnable onCancel) {
        this.plugin = plugin;
        inventory = Bukkit.createInventory(null, InventoryType.HOPPER, "Choose an ability");
        this.choices = choices;
        this.onSelect = onSelect;
        this.onCancel = onCancel;

        initializeItems();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private void initializeItems() {
        EnderPearlAbilities abilities = EnderPearlAbilities.getInstance();
        for (int i = 0; i < choices.size(); i++) {
            final String codeName = choices.get(i);
            final Optional<AbilityInfo> info = abilities.getAbilityInfos().stream().filter(inf -> inf.getCodeName().equals(codeName)).findFirst();
            if (info.isEmpty()) continue;
            inventory.setItem(i, createChoice(info.get().getName(), codeName, info.get().getActivation(), info.get().getDescription()));
        }
    }

    private String truncate(String s, int maxLength) {
        if (s.length() > maxLength) {
            return s.substring(0, maxLength - 3) + "...";
        }
        return s;
    }

    private ItemStack createChoice(final String name, final String codeName, final ActivationHand activation, final String description) {
        final ItemStack item = new ItemStack(Material.ENDER_PEARL, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName("" + ChatColor.DARK_PURPLE + ChatColor.BOLD + name);
        Enchantment glow = Enchantment.getByKey(new NamespacedKey(plugin, "glow"));
        if (glow != null)
            meta.addEnchant(glow, 1, true);
        // Set the lore of the item
        meta.setLore(List.of(
                ChatColor.GRAY + codeName,
                ChatColor.GRAY + (activation == ActivationHand.OffHand ? "Off" : "Main") + " Hand",
                ChatColor.WHITE + truncate(description, 32)
        ));

        item.setItemMeta(meta);

        return item;
    }

    public void openInventory(final Player player) {
        player.openInventory(inventory);
    }

    public boolean isCompleted() {
        return completed;
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!event.getInventory().equals(inventory)) return;

        event.setCancelled(true);

        final ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() != Material.ENDER_PEARL) return;
        if (event.getSlot() < 0 || event.getSlot() >= choices.size()) return;
        if (completed) return;

        final Player player = (Player) event.getWhoClicked();
        completed = true;
        HandlerList.unregisterAll(this);
        onSelect.accept(choices.get(event.getSlot()));
        new BukkitRunnable() {
            @Override
            public void run() {
                player.closeInventory();
            }
        }.runTaskLater(plugin, 0);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getInventory().equals(inventory)) return;
        if (completed) return;
        completed = true;
        HandlerList.unregisterAll(this);
        onCancel.run();
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inventory)) {
            e.setCancelled(true);
        }
    }
}