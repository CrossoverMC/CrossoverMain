package me.cable.crossover.main.features.hiddenpath;

import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.handler.ConfigHandler;
import me.cable.crossover.main.object.Region;
import me.cable.crossover.main.util.ConfigHelper;
import me.cable.crossover.main.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HiddenPathHandler implements Listener {

    public static final String LEADERBOARDS_PATH = "hidden-path";
    public static final String VENUES_PATH = "venues";

    private static HiddenPathHandler instance;

    private final List<HiddenPathVenue> venues;

    public static void load() {
        if (instance != null) {
            instance.cleanupInternal();
        }

        instance = new HiddenPathHandler();
        Bukkit.getPluginManager().registerEvents(instance, CrossoverMain.getInstance());
    }

    public static void cleanup() {
        instance.cleanupInternal();
    }

    public static void doPlacement(@NotNull Block block, @NotNull ConfigHelper ch) {
        for (int i = 0; true; i++) {
            String key = Integer.toString(i);
            if (!ch.isSet(key)) break;

            Material material = ch.mat(key);
            block.setType(material);
            block = block.getRelative(BlockFace.DOWN);
        }
    }

    private static @NotNull Map<Block, Player> getPlayerBlocks() {
        Map<Block, Player> map = new HashMap<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            map.put(player.getLocation().getBlock(), player);
        }

        return map;
    }

    public static void initialize(@NotNull CrossoverMain crossoverMain) {
        load();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(crossoverMain, () -> {
            List<Player> unhandledPlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
            Map<Block, Player> playerBlocks = getPlayerBlocks();

            for (HiddenPathVenue hiddenPathVenue : instance.getVenues()) {
                Player currentPlayer = hiddenPathVenue.getPlayer();
                World world = hiddenPathVenue.getWorld();

                if (world == null) {
                    if (currentPlayer != null) {
                        hiddenPathVenue.end();
                    }

                    continue;
                }

                Vector startBlock = hiddenPathVenue.getStartBlock();
                Block blockAboveStart = startBlock.toLocation(world).getBlock().getRelative(BlockFace.UP);

                if (currentPlayer == null) {
                    Player player = playerBlocks.get(blockAboveStart);

                    if (player != null) {
                        hiddenPathVenue.start(player);
                        unhandledPlayers.remove(player);
                    }
                } else {
                    Region region = hiddenPathVenue.getRegion();

                    if (!region.contains(currentPlayer) || !currentPlayer.isOnline()) {
                        hiddenPathVenue.end();
                        unhandledPlayers.remove(currentPlayer);
                    } else {
                        Block floorBlock = currentPlayer.getLocation().getBlock().getRelative(BlockFace.DOWN);

                        if (hiddenPathVenue.getPathRegion().contains(floorBlock)) {
                            unhandledPlayers.remove(currentPlayer);

                            if (!hiddenPathVenue.isPath(floorBlock) && currentPlayer.isOnGround()) {
                                doPlacement(floorBlock, ConfigHandler.hiddenPathSettings()
                                        .ch(VENUES_PATH + "." + hiddenPathVenue.getId() + ".placements.wrong"));

                                Location playerLoc = currentPlayer.getLocation();
                                Location loc = startBlock.toLocation(world).add(0.5, 1, 0.5);
                                loc.setYaw(playerLoc.getYaw());
                                loc.setPitch(playerLoc.getPitch());
                                currentPlayer.teleport(loc);
                            }
                        }
                    }
                }
            }

            handleUnhandledPlayers(unhandledPlayers);
        }, 0, 1);
    }

    private static void handleUnhandledPlayers(@NotNull List<Player> players) {
        ConfigHelper teleportSection = ConfigHandler.hiddenPathSettings().ch("teleport");
        Material teleportMaterial = teleportSection.mat("material");
        List<Region> teleportRegions = new ArrayList<>();
        List<Location> teleportLocations = new ArrayList<>();

        for (String str : teleportSection.strList("regions")) {
            Region region = Region.of(str);
            teleportRegions.add(region);
        }
        for (String str : teleportSection.strList("locations")) {
            Location loc = Utils.locFromString(str);
            teleportLocations.add(loc);
        }
        for (Player player : players) {
            if (!player.isOnGround()) continue;

            Location playerLoc = player.getLocation();
            if (playerLoc.getBlock().getRelative(BlockFace.DOWN).getType() != teleportMaterial) continue;

            boolean teleport = false;

            for (Region region : teleportRegions) {
                if (region.contains(player)) {
                    teleport = true;
                    break;
                }
            }
            if (teleport) {
                World world = player.getWorld();

                Location closest = null;
                double distanceSquared = 0;

                for (Location loc : teleportLocations) {
                    if (!world.equals(loc.getWorld())) continue;

                    double ds = playerLoc.distanceSquared(loc);

                    if (closest == null || ds < distanceSquared) {
                        closest = loc;
                        distanceSquared = ds;
                    }
                }

                if (closest != null) {
                    closest.setYaw(playerLoc.getYaw());
                    closest.setPitch(playerLoc.getPitch());
                    player.teleport(closest);
                }
            }
        }
    }

    private HiddenPathHandler() {
        List<HiddenPathVenue> venues = new ArrayList<>();
        ConfigHelper config = ConfigHandler.hiddenPathSettings();
        ConfigHelper venuesSection = config.ch(VENUES_PATH);

        for (String id : venuesSection.getKeys(false)) {
            ConfigHelper venueSection = venuesSection.ch(id);
            String worldName = venueSection.snn("world");
            String direction = venueSection.snn("direction");
            BlockFace blockFace = BlockFace.NORTH;

            for (BlockFace bf : List.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST)) {
                if (direction.equals(bf.toString())) {
                    blockFace = bf;
                    break;
                }
            }

            Vector startBlock = venueSection.vec("start-block");
            int distance = venueSection.integer("distance");
            int spaceLeft = venueSection.integer("space-left");
            int spaceRight = venueSection.integer("space-right");
            Vector finishButton = venueSection.vec("finish-button");
            Region region = venueSection.reg("region", worldName);

            HiddenPathVenue hiddenPathVenue = new HiddenPathVenue(id, worldName, blockFace, startBlock, distance, spaceLeft, spaceRight, finishButton, region);
            venues.add(hiddenPathVenue);
        }

        this.venues = List.copyOf(venues);
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();

        for (HiddenPathVenue hiddenPathVenue : venues) {
            if (player.equals(hiddenPathVenue.getPlayer())
                    && hiddenPathVenue.getFinishButton().toLocation(player.getWorld()).getBlock().equals(block)) {
                hiddenPathVenue.finish();
                break;
            }
        }
    }

    private void cleanupInternal() {
        for (HiddenPathVenue hiddenPathVenue : venues) {
            hiddenPathVenue.cleanup();
        }
    }

    public @NotNull List<HiddenPathVenue> getVenues() {
        return List.copyOf(venues);
    }
}
