package me.cable.crossover.main.features.hiddenpath;

import me.cable.crossover.main.handler.ConfigHandler;
import me.cable.crossover.main.handler.LeaderboardsConfigHandler;
import me.cable.crossover.main.object.Region;
import me.cable.crossover.main.util.ConfigHelper;
import me.cable.crossover.main.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class HiddenPathVenue {

    private final String id;
    private final String worldName;
    private final BlockFace direction;
    private final Vector startBlock;
    private final int distance, spaceLeft, spaceRight;
    private final Vector finishButton;
    private final Region region;
    private final Region pathRegion;

    private @Nullable Player player;
    private @NotNull List<Block> path = Collections.emptyList();
    private long startTime;

    public HiddenPathVenue(@NotNull String id, @NotNull String worldName, @NotNull BlockFace direction,
                           @NotNull Vector startBlock, int distance, int spaceLeft, int spaceRight,
                           @NotNull Vector finishButton, @NotNull Region region) {
        this.id = id;
        this.worldName = worldName;
        this.direction = direction;
        this.startBlock = startBlock;
        this.distance = distance;
        this.spaceLeft = spaceLeft;
        this.spaceRight = spaceRight;
        this.finishButton = finishButton;
        this.region = region;
        pathRegion = createPathRegion();
    }

    private @NotNull Region createPathRegion() {
        Vector corner1 = Utils.getRelative(startBlock, Utils.getLeftBlockFace(direction), spaceLeft);
        Vector corner2 = Utils.getRelative(Utils.getRelative(startBlock, direction, distance - 1),
                Utils.getRightBlockFace(direction), spaceRight);
        return new Region(worldName, corner1.getBlockX(), corner1.getBlockY(), corner1.getBlockZ(),
                corner2.getBlockX(), corner2.getBlockY(), corner2.getBlockZ());
    }

    public void start(@NotNull Player player) {
        if (this.player != null) return;

        path = new PathGenerator(direction, startBlock.toLocation(player.getWorld()).getBlock(), distance, pathRegion).generate();
        this.player = player;
        startTime = System.currentTimeMillis();
        ConfigHandler.settings().message(ConfigHandler.PATH_MESSAGES + ".hidden-path-start").send(player);

        if (ConfigHandler.hiddenPathSettings().bool("show-paths.enabled")) {
            Material material = ConfigHandler.hiddenPathSettings().mat("show-paths.material");

            for (int i = 1; i < path.size(); i++) {
                path.get(i).setType(material);
            }
        }
    }

    public void finish() {
        if (player == null) return;

        long time = System.currentTimeMillis() - startTime;
        YamlConfiguration leaderboards = LeaderboardsConfigHandler.config();
        String path = HiddenPathHandler.LEADERBOARDS_PATH + "." + player.getUniqueId();
        long pb;

        if (leaderboards.isSet(path)) {
            pb = leaderboards.getLong(path);
        } else {
            pb = time;
            leaderboards.set(path, time);
        }
        if (time >= pb) {
            ConfigHandler.settings().message(ConfigHandler.PATH_MESSAGES + ".hidden-path-finish")
                    .placeholder("personal_best", Utils.formatDurationMillis(pb))
                    .placeholder("time", Utils.formatDurationMillis(time))
                    .send(player);
        } else {
            // new personal best
            ConfigHandler.settings().message(ConfigHandler.PATH_MESSAGES + ".hidden-path-finish-pb")
                    .placeholder("previous_personal_best", Utils.formatDurationMillis(pb))
                    .placeholder("time", Utils.formatDurationMillis(time))
                    .send(player);
            leaderboards.set(path, time);
        }

        cleanup();
    }

    public void end() {
        if (player == null) return;

        ConfigHandler.settings().message(ConfigHandler.PATH_MESSAGES + ".hidden-path-end").send(player);
        cleanup();
    }

    public void cleanup() {
        player = null;
        ConfigHelper placement = ConfigHandler.hiddenPathSettings()
                .ch(HiddenPathHandler.VENUES_PATH + "." + id + ".placements.fix");

        pathRegion.forEachBlock(block -> {
            if (block.getType() != Material.TARGET) {
                HiddenPathHandler.doPlacement(block, placement);
            }
        });
    }

    public @NotNull String getId() {
        return id;
    }

    public @Nullable World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    public @NotNull Vector getStartBlock() {
        return startBlock;
    }

    public @NotNull Vector getFinishButton() {
        return finishButton;
    }

    public @NotNull Region getRegion() {
        return region;
    }

    public @NotNull Region getPathRegion() {
        return pathRegion;
    }

    public boolean isPath(@NotNull Block block) {
        return path.contains(block);
    }

    public @Nullable Player getPlayer() {
        return player;
    }
}
