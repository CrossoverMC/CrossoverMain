package me.cable.crossover.main.task;

import me.cable.crossover.main.handler.ConfigHandler;
import me.cable.crossover.main.util.ConfigHelper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FallTeleportTask implements Runnable {

    public static final Map<Player, Location> lastGroundLocations = new HashMap<>();

    private boolean isSafeLoc(@NotNull Location loc) {
        Block block = loc.getBlock().getRelative(BlockFace.DOWN);
        if (block.getType().isAir()) return false;

        for (BlockFace blockFace : List.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST)) {
            Block a = block.getRelative(blockFace);
            if (a.getType().isAir()) return false;
        }

        return true;
    }

    @Override
    public void run() {
        ConfigHelper fallTeleportConfig = ConfigHandler.settings().ch("fall-teleport");

        for (World world : Bukkit.getWorlds()) {
            String worldName = world.getName();
            if (!fallTeleportConfig.isSet(worldName)) continue;

            double tpy = fallTeleportConfig.doub(worldName);

            for (Player player : world.getPlayers()) {
                Location loc = player.getLocation();

                if (isSafeLoc(loc)) {
                    lastGroundLocations.put(player, loc);
                }
                if (loc.getY() > tpy) {
                    continue;
                }

                Location tpLoc = lastGroundLocations.get(player);

                if (tpLoc == null) {
                    player.kickPlayer("You fell too far.");
                } else {
                    tpLoc.setYaw(loc.getYaw());
                    tpLoc.setPitch(loc.getPitch());
                    player.teleport(tpLoc);
                }
            }
        }
    }
}
