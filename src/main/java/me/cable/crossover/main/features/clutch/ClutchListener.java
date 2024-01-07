package me.cable.crossover.main.features.clutch;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.handler.ConfigHandler;
import me.cable.crossover.main.util.Color;
import me.cable.crossover.main.util.Constants;
import me.cable.crossover.main.util.SoundEffect;
import me.cable.crossover.main.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ClutchListener implements Listener {

    private void teleport(@NotNull Player player, int level) {
        Location levelLoc = ConfigHandler.settings().loc("clutch-levels." + level, player.getWorld());
        player.teleport(levelLoc);
        ClutchHandler.clutchFailed.remove(player);
    }

    private boolean isInClutchRegion(@NotNull Location loc) {
        RegionQuery regionQuery = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        ApplicableRegionSet set = regionQuery.getApplicableRegions(BukkitAdapter.adapt(loc));

        for (String value : set.queryAllValues(null, CrossoverMain.DO_FLAG)) {
            if (value.equals(ClutchHandler.TYPE)) {
                return true;
            }
        }

        return false;
    }

    private void handleLevel(@NotNull PlayerInteractEvent e, int level) {
        e.setCancelled(true);

        Player player = e.getPlayer();
        ClutchHandler.savedPlayerLevels.put(player, level);
        teleport(player, level);
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent e) {
        if (!e.getAction().toString().startsWith("RIGHT_")) return;

        ItemStack item = e.getItem();
        if (item == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        String toolId = meta.getPersistentDataContainer().get(Constants.KEY_TOOL, PersistentDataType.STRING);
        if (toolId == null) return;

        switch (toolId) {
            case Constants.TOOL_LEVEL_1 -> handleLevel(e, 1);
            case Constants.TOOL_LEVEL_2 -> handleLevel(e, 2);
            case Constants.TOOL_LEVEL_3 -> handleLevel(e, 3);
        }
    }

    @EventHandler
    public void onEntityDamage(@NotNull EntityDamageEvent e) {
        if (e.getEntity() instanceof Player player
                && isInClutchRegion(player.getLocation())
                && e.getCause() == EntityDamageEvent.DamageCause.FALL
                && ClutchHandler.savedPlayerLevels.containsKey(player)
                && !Utils.hasBypass(player)) {
            player.sendMessage(Color.ERROR + "Clutch failed!");
            ClutchHandler.clutchFailed.add(player);
        }
    }

    @EventHandler
    public void onBlockPlace(@NotNull BlockPlaceEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();

        if (isInClutchRegion(block.getLocation()) && !Utils.hasBypass(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBucketEmpty(@NotNull PlayerBucketEmptyEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();

        if (!isInClutchRegion(block.getLocation()) || Utils.hasBypass(player)) {
            return;
        }

        Integer savedLevel = ClutchHandler.savedPlayerLevels.get(player);

        if (savedLevel == null) {
            player.sendMessage(Color.ERROR + "Select a level!");
            e.setCancelled(true);
            return;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(CrossoverMain.getInstance(), () -> {
            if (block.getBlockData() instanceof Waterlogged waterlogged) {
                waterlogged.setWaterlogged(false);
                block.setBlockData(waterlogged);
            } else {
                block.setType(Material.AIR);
            }
            if (!ClutchHandler.clutchFailed.contains(player)) {
                // clutched successfully
                SoundEffect.SUCCESS.play(player);
            }

            teleport(player, savedLevel);
            player.getInventory().setItem(ClutchHandler.WATER_BUCKET_SLOT, new ItemStack(Material.WATER_BUCKET));
        }, 20);
    }
}
