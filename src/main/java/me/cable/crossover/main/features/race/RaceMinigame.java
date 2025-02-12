package me.cable.crossover.main.features.race;

import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.features.playerspeed.SpeedModifier;
import me.cable.crossover.main.features.playerspeed.SpeedPriority;
import me.cable.crossover.main.handler.LeaderboardsConfigHandler;
import me.cable.crossover.main.object.Minigame;
import me.cable.crossover.main.object.Region;
import me.cable.crossover.main.util.ConfigHelper;
import me.cable.crossover.main.util.SoundEffect;
import me.cable.crossover.main.util.Utils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Lightable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;

public class RaceMinigame extends Minigame {

    public static final String LEADERBOARDS_PATH_FULL = "race";
    public static final String LEADERBOARDS_PATH_LAP = "race-lap";

    private Map<Player, PlayerInfo> players;
    private List<Entry<Player, PlayerInfo>> finishedPlayers;
    private List<Player> interactedFinishLine;
    private Region finishLineRegion;
    private long startTime;
    private int maxTimeTaskId;
    private BukkitTask startTask;

    public RaceMinigame(@NotNull ConfigurationSection instanceSettings) {
        super(instanceSettings);
    }

    @Override
    public @NotNull String id() {
        return "race";
    }

    private void setBarriers(boolean placed) {
        String worldName = getWorldName();
        Material material = placed ? Material.BARRIER : Material.AIR;

        for (String string : settings().strList("barriers")) {
            Region region = Region.of(string, worldName);
            region.fill(material);
        }
    }

    private void setLit(@NotNull List<String> startLights, boolean lit) {
        World world = getWorld();
        if (world == null) return;

        for (String string : startLights) {
            Block block = Utils.locFromString(string, world).getBlock();

            if (block.getBlockData() instanceof Lightable lightable) {
                lightable.setLit(lit);
                block.setBlockData(lightable, false);
            }
        }
    }

    private void startRace() {
        setBarriers(false);
        startTime = System.currentTimeMillis();

        maxTimeTaskId = Bukkit.getScheduler().scheduleSyncDelayedTask(CrossoverMain.getInstance(), () -> {
            getMessage("time-up").send(getNotFinished());
            endGame();
        }, settings().integer("max-time"));
    }

    private void handlePlayers(@NotNull List<Player> players) {
        this.players = new HashMap<>();
        Location startLoc = settings().loc("start-location", getWorld());

        for (Player player : players) {
            PlayerInfo playerInfo = new PlayerInfo();
            this.players.put(player, playerInfo);

            if (startLoc != null) {
                player.teleport(startLoc);
            }
        }
    }

    @Override
    protected void startGame(@NotNull List<Player> players) {
        handlePlayers(players);
        finishedPlayers = new ArrayList<>();
        interactedFinishLine = new ArrayList<>();
        finishLineRegion = settings().reg("finish-line", getWorldName());

        ConfigHelper startLightsSection = startLights();
        int totalStartLightFrames = totalStartLightFrames();

        startTask = new BukkitRunnable() {

            int lightIndex;

            @Override
            public void run() {
                ConfigHelper startLightSection = startLightsSection.ch("frames." + lightIndex);

                // lights
                setLit(startLightSection.strList("lights"), true);

                // sound
                SoundEffect soundEffect = startLightSection.sound("sound");

                for (Player player : players) {
                    soundEffect.play(player);
                }

                if (lightIndex >= totalStartLightFrames - 1) {
                    cancel();
                    startRace();
                } else {
                    lightIndex++;
                }
            }
        }.runTaskTimer(crossoverMain,
                startLightsSection.integer("delay"),
                startLightsSection.integer("interval"));
    }

    private void doFinishLineCheck() {
        Location finishLineCenter = finishLineRegion.getBottomCenter();
        if (finishLineCenter == null) return;

        double checkRadius = settings().doub("check-radius");

        for (Player player : List.copyOf(interactedFinishLine)) {
            if (player.getLocation().distanceSquared(finishLineCenter) > checkRadius * checkRadius) {
                interactedFinishLine.remove(player);
            }
        }
    }

    private void doTeleportRegionsCheck() {
        World world = getWorld();
        if (world == null) return;

        String worldName = getWorldName();
        ConfigHelper teleportRegionsSection = settings().ch("teleport-regions");
        List<Player> unhandledPlayers = getNotFinished();

        for (String key : teleportRegionsSection.getKeys(false)) {
            Region region = Region.of(key, worldName);
            Location loc = teleportRegionsSection.loc(key, world);

            for (Player player : List.copyOf(unhandledPlayers)) {
                if (region.contains(player)) {
                    unhandledPlayers.remove(player);
                    player.teleport(loc);
                }
            }
        }
    }

    @Override
    protected void tick() {
        doFinishLineCheck();
        doTeleportRegionsCheck();
    }

    @Override
    protected @Nullable SpeedModifier speedModifier(@NotNull Player player) {
        return new SpeedModifier(player, 1, SpeedPriority.HIGH);
    }

    private @NotNull List<Player> getNotFinished() {
        List<Player> list = new ArrayList<>();

        for (Entry<Player, PlayerInfo> entry : players.entrySet()) {
            if (!entry.getValue().finished) {
                list.add(entry.getKey());
            }
        }

        return list;
    }

    private void checkPlayerCount() {
        if (getNotFinished().size() == 0) {
            endGame();
        }
    }

    private void checkPersonalBests(@NotNull Player player, @NotNull PlayerInfo playerInfo) {
        YamlConfiguration lbc = LeaderboardsConfigHandler.config();
        String fullPath = LEADERBOARDS_PATH_FULL + "." + player.getUniqueId();

        if (lbc.isSet(fullPath)) {
            long pb = lbc.getLong(fullPath);

            if (playerInfo.raceTime < pb) {
                lbc.set(fullPath, playerInfo.raceTime);
                getMessage("pb-full")
                        .placeholder("time", Utils.formatDurationMillis(playerInfo.raceTime))
                        .send(player);
            }
        } else {
            lbc.set(fullPath, playerInfo.raceTime);
        }

        String lapPath = LEADERBOARDS_PATH_LAP + "." + player.getUniqueId();

        if (lbc.isSet(lapPath)) {
            long pb = lbc.getLong(lapPath);

            if (playerInfo.fastestLapTime < pb) {
                lbc.set(lapPath, playerInfo.fastestLapTime);
                getMessage("pb-lap")
                        .placeholder("time", Utils.formatDurationMillis(playerInfo.fastestLapTime))
                        .send(player);
            }
        } else {
            lbc.set(lapPath, playerInfo.fastestLapTime);
        }
    }

    private void showSplits(@NotNull Player player, @NotNull PlayerInfo playerInfo) {
        getMessage("splits.top").send(player);

        for (int i = 0; i < playerInfo.lapTimes.size(); i++) {
            long lapTime = playerInfo.lapTimes.get(i);
            getMessage("splits.split")
                    .placeholder("lap", Integer.toString(i + 1))
                    .placeholder("time", Utils.formatDurationMillis(lapTime))
                    .send(player);
        }

        getMessage("splits.bottom").send(player);
    }

    private void playerFinish(@NotNull Player player, @NotNull PlayerInfo playerInfo, long currentTime) {
        Location endLoc = endLocation();

        playerInfo.finished = true;
        playerInfo.raceTime = currentTime - startTime;
        detachSpeedModifier(player);
        finishedPlayers.add(new AbstractMap.SimpleEntry<>(player, playerInfo));

        if (endLoc != null) {
            player.teleport(endLoc);
        }

        getMessage("finish")
                .placeholder("player", player.getName())
                .placeholder("time", Utils.formatDurationMillis(playerInfo.raceTime))
                .send(players.keySet());
        checkPersonalBests(player, playerInfo);
        showSplits(player, playerInfo);
        checkPlayerCount();
    }

    private void onInteractFinishLine(@NotNull Player player) {
        PlayerInfo playerInfo = players.get(player);
        if (playerInfo == null || playerInfo.finished) return;

        int totalLaps = settings().integer("laps");
        long currentTime = System.currentTimeMillis();

        playerInfo.lap++;

        if (playerInfo.lap > 1) {
            long lapTime = currentTime - playerInfo.lapStartTime;
            playerInfo.lapTimes.add(lapTime);
            playerInfo.fastestLapTime = Math.min(playerInfo.fastestLapTime, lapTime);

            if (playerInfo.lap - 1 == totalLaps) {
                playerFinish(player, playerInfo, currentTime);
            } else {
                getMessage("lap")
                        .placeholder("lap", Integer.toString(playerInfo.lap))
                        .placeholder("previous_lap", Integer.toString(playerInfo.lap - 1))
                        .placeholder("split", Utils.formatDurationMillis(lapTime))
                        .send(player);
            }
        }

        playerInfo.lapStartTime = currentTime;
    }

    private @NotNull List<Entry<Player, Long>> getTopSplits() {
        List<Entry<Player, Long>> list = new ArrayList<>();

        for (Entry<Player, PlayerInfo> entry : players.entrySet()) {
            PlayerInfo playerInfo = entry.getValue();
            if (playerInfo.finished) list.add(new AbstractMap.SimpleEntry<>(entry.getKey(), playerInfo.fastestLapTime));
        }

        list.sort((a, b) -> (int) (a.getValue() - b.getValue()));
        return list;
    }

    private void announceWinners() {
        Set<Player> sendTo = players.keySet();

        if (finishedPlayers.isEmpty()) {
            getMessage("nobody-finished").send(sendTo);
            return;
        }

        getMessage("winners.full.top").send(sendTo);

        for (int i = 0; i < Math.min(finishedPlayers.size(), 3); i++) {
            Entry<Player, PlayerInfo> entry = finishedPlayers.get(i);

            getMessage("winners.full.winner")
                    .placeholder("player", entry.getKey().getName())
                    .placeholder("pos", Integer.toString(i + 1))
                    .placeholder("time", Utils.formatDurationMillis(entry.getValue().raceTime))
                    .send(sendTo);
        }

        getMessage("winners.full.bottom").send(sendTo);
        getMessage("winners.lap.top").send(sendTo);

        List<Entry<Player, Long>> topSplits = getTopSplits();

        for (int i = 0; i < Math.min(topSplits.size(), 3); i++) {
            Entry<Player, Long> entry = topSplits.get(i);

            getMessage("winners.lap.winner")
                    .placeholder("player", entry.getKey().getName())
                    .placeholder("pos", Integer.toString(i + 1))
                    .placeholder("time", Utils.formatDurationMillis(entry.getValue()))
                    .send(sendTo);
        }

        getMessage("winners.lap.bottom").send(sendTo);
    }

    private void displayWinnerNpcs() {
        if (finishedPlayers.isEmpty()) return;

        ConfigurationSection podiumNpcsSection = settings().csnn("podium-npcs");
        List<Integer> podiumNpcIds = new ArrayList<>();

        for (int i = 0; true; i++) {
            String key = Integer.toString(i);
            if (!podiumNpcsSection.isSet(key)) break;
            podiumNpcIds.add(podiumNpcsSection.getInt(key));
        }
        for (int i = 0; i < podiumNpcIds.size(); i++) {
            int npcId = podiumNpcIds.get(i);
            NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);

            if (npc != null) {
                boolean hasPlayer = i < finishedPlayers.size();
                npc.data().setPersistent(NPC.Metadata.NAMEPLATE_VISIBLE, hasPlayer);
                npc.setName(hasPlayer ? finishedPlayers.get(i).getKey().getName() : "N/A");
            }
        }
    }

    @Override
    protected void cleanup() {
        Bukkit.getScheduler().cancelTask(maxTimeTaskId);
        startTask.cancel();

        Location endLoc = endLocation();

        for (Entry<Player, PlayerInfo> entry : players.entrySet()) {
            Player player = entry.getKey();
            PlayerInfo playerInfo = entry.getValue();

            if (!playerInfo.finished && endLoc != null) {
                player.teleport(endLoc);
            }
        }

        announceWinners();
        displayWinnerNpcs();
        setBarriers(true);

        players = null;
        finishedPlayers = null;
        interactedFinishLine = null;

        // lights

        ConfigHelper startLightsSection = startLights();

        for (int i = 0; i < totalStartLightFrames(); i++) {
            ConfigHelper startLightSection = startLightsSection.ch("frames." + i);
            setLit(startLightSection.strList("lights"), false);
        }
    }

    @EventHandler
    public void onPlayerMove(@NotNull PlayerMoveEvent e) {
        Player player = e.getPlayer();

        if (!interactedFinishLine.contains(player) && finishLineRegion.contains(player)) {
            interactedFinishLine.add(player);
            onInteractFinishLine(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if (players.containsKey(player)) {
            players.remove(player);
            checkPlayerCount();
        }
    }

    private @Nullable Location endLocation() {
        return settings().loc("end-location", getWorld());
    }

    private @NotNull ConfigHelper startLights() {
        return settings().ch("start-lights");
    }

    private int totalStartLightFrames() {
        ConfigHelper startLightsSection = startLights();

        for (int i = 0; true; i++) {
            if (!startLightsSection.isSet("frames." + i)) {
                return i;
            }
        }
    }

    private static class PlayerInfo {
        List<Long> lapTimes = new ArrayList<>();
        int lap; // player's current lap
        long lapStartTime; // time of lap start
        long fastestLapTime = Long.MAX_VALUE;
        long raceTime; // time taken to finish race
        boolean finished;
    }
}
