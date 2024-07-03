package net.terra.listener;

import net.terra.api.event.machine.MachinePickupEvent;
import net.terra.api.event.machine.MachinePlaceEvent;
import net.terra.entity.machine.Machine;
import net.terra.util.ItemBuilder;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

import static net.terra.entity.machine.MachineController.*;

public final class MachineListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void handlePlayerInteractEvent(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if(block == null || block.getType() == Material.AIR) return;

        Player player = event.getPlayer();

        Machine machine = getMachinePlacedAt(block.getLocation());
        if(machine == null) return;
        if(!machine.getOwnerId().equals(player.getUniqueId())) return;

        event.setCancelled(true);

        machine.setLevel(machine.getLevel() + 1);
        if(updateMachine(machine)) player.sendMessage(String.valueOf(machine.getLevel()));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void handleBlockPlaceEvent(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        Player player = event.getPlayer();

        if(player == null) return;
        if(block.getType() != Material.IRON_BLOCK) return;

        ItemStack item = event.getItemInHand();
        if(item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if(!meta.hasDisplayName()) return;

            try {
                UUID id = UUID.fromString(meta.getDisplayName());
                Machine machine = getMachineById(id);

                if(machine == null) return;
                machine.setPlacedAt(block.getLocation());

                MachinePlaceEvent listener = new MachinePlaceEvent(player, machine);
                Bukkit.getServer().getPluginManager().callEvent(listener);

                if(listener.isCancelled()) {
                    event.setCancelled(true);
                } else {
                    if(!updateMachine(machine)) event.setCancelled(true);
                }
            } catch(IllegalArgumentException ignored) {}
        } else {
            Machine machine = new Machine(UUID.randomUUID(), player.getUniqueId(), block.getLocation(), 0);

            MachinePlaceEvent listener = new MachinePlaceEvent(player, machine);
            Bukkit.getServer().getPluginManager().callEvent(listener);

            if(listener.isCancelled()) {
                event.setCancelled(true);
            } else {
                if(!createMachine(machine)) event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void handleBlockBreakEvent(BlockBreakEvent event) {
        Block block = event.getBlock();
        Machine machine = getMachinePlacedAt(block.getLocation());
        if(machine == null) return;
        event.setCancelled(true);

        Player player = event.getPlayer();
        if(!player.isSneaking()) return;
        if(!player.getUniqueId().equals(machine.getOwnerId())) return;

        MachinePickupEvent listener = new MachinePickupEvent(player, machine);
        Bukkit.getServer().getPluginManager().callEvent(listener);

        if(listener.isCancelled()) return;

        machine.setPlacedAt(null);
        if(updateMachine(machine)) {
            block.setType(Material.AIR);

            player.getInventory().addItem(ItemBuilder.of(Material.IRON_BLOCK).name(machine.getId().toString()).build());
        }
    }

}
