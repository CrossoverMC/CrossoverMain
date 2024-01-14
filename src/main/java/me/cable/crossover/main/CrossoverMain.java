package me.cable.crossover.main;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.session.SessionManager;
import me.cable.crossover.main.command.ArtifactsCommand;
import me.cable.crossover.main.command.LinkCommand;
import me.cable.crossover.main.command.MainCommand;
import me.cable.crossover.main.command.UnlinkCommand;
import me.cable.crossover.main.features.antigravity.AntigravityFlagHandler;
import me.cable.crossover.main.features.antigravity.AntigravityListener;
import me.cable.crossover.main.features.artifacts.ArtifactsHandler;
import me.cable.crossover.main.features.blockreplace.BlockReplaceTask;
import me.cable.crossover.main.features.booth.BoothFlagHandler;
import me.cable.crossover.main.features.booth.BoothListener;
import me.cable.crossover.main.features.clutch.ClutchHandler;
import me.cable.crossover.main.features.clutch.ClutchListener;
import me.cable.crossover.main.features.dohandler.DoFlagHandler;
import me.cable.crossover.main.features.hiddenpath.HiddenPathHandler;
import me.cable.crossover.main.features.highblock.HighblockPE;
import me.cable.crossover.main.features.npcchat.NpcChatHandler;
import me.cable.crossover.main.features.playerspeed.WalkSpeedFlagHandler;
import me.cable.crossover.main.handler.*;
import me.cable.crossover.main.inventoryitem.SpeedBoostItem;
import me.cable.crossover.main.listeners.*;
import me.cable.crossover.main.object.Minigame;
import me.cable.crossover.main.object.RegistryItems;
import me.cable.crossover.main.task.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

public final class CrossoverMain extends JavaPlugin {

    public static StateFlag ANTIGRAVITY_FLAG;
    public static StringFlag BOOTH_FLAG;
    public static StringFlag DO_FLAG;
    public static DoubleFlag WALK_SPEED_FLAG;

    private ConfigHandler configHandler;
    private MinigameConfigHandler minigameConfigHandler;
    private LeaderboardsConfigHandler leaderboardsConfigHandler;
    private MailHandler mailHandler;
    private PlayerData playerData;

    public static @NotNull CrossoverMain getInstance() {
        return JavaPlugin.getPlugin(CrossoverMain.class);
    }

    @Override
    public void onLoad() {
        registerWorldGuardFlags();
    }

    private void registerWorldGuardFlags() {
        FlagRegistry flagRegistry = WorldGuard.getInstance().getFlagRegistry();

        try {
            ANTIGRAVITY_FLAG = new StateFlag("antigravity", false);
            flagRegistry.register(ANTIGRAVITY_FLAG);

            BOOTH_FLAG = new StringFlag("booth");
            flagRegistry.register(BOOTH_FLAG);

            DO_FLAG = new StringFlag("do");
            flagRegistry.register(DO_FLAG);

            WALK_SPEED_FLAG = new DoubleFlag("crossover-walk-speed");
            flagRegistry.register(WALK_SPEED_FLAG);
        } catch (FlagConflictException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        Minigame.initialize(this);
        initializeHandlers();
        registerListeners();
        registerCommands();
        startTasks();
        initializeFeatures();
        new RegistryItems().register();
    }

    @Override
    public void onDisable() {
        HiddenPathHandler.cleanup();
        Minigame.unregisterAll();
        SpeedBoostItem.saveSpeedBoosts();
        leaderboardsConfigHandler.save();
        playerData.saveAll();
    }

    private void initializeHandlers() {
        configHandler = new ConfigHandler(this);
        minigameConfigHandler = new MinigameConfigHandler(this);
        leaderboardsConfigHandler = new LeaderboardsConfigHandler(this);
        playerData = new PlayerData(this);
        mailHandler = new MailHandler();
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new InventoryListener(), this);
        pluginManager.registerEvents(new MenuListener(), this);
        pluginManager.registerEvents(new PlayerInteract(), this);
        pluginManager.registerEvents(new PlayerJoin(this), this);
        pluginManager.registerEvents(new PlayerQuit(), this);
        pluginManager.registerEvents(new PlayerChangedWorld(), this);
    }

    private void registerCommands() {
        new ArtifactsCommand(this).register("artifacts");
        new MainCommand(this).register("crossovermain");
        new LinkCommand(this).register("link");
        new UnlinkCommand(this).register("unlink");
    }

    private void startTasks() {
        BukkitScheduler bukkitScheduler = getServer().getScheduler();
        bukkitScheduler.scheduleSyncRepeatingTask(this, new FallTeleportTask(), 0, 2);
        bukkitScheduler.scheduleSyncRepeatingTask(this, new InventoryItemsTask(), 0, 60 * 20);
        bukkitScheduler.runTaskTimerAsynchronously(this, new PlaytimeTask(), 0, 20);
        bukkitScheduler.runTaskTimerAsynchronously(this, new Reader(this), 0, 20);
        bukkitScheduler.runTaskTimerAsynchronously(this, new UpdateLeaderboardTask(), 0, 60 * 20);
        bukkitScheduler.scheduleSyncRepeatingTask(this, new VelocityBlocksTask(), 0, 1);
    }

    private void initializeFeatures() {
        PluginManager pluginManager = getServer().getPluginManager();
        SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();

        // antigravity
        pluginManager.registerEvents(new AntigravityListener(), this);
        sessionManager.registerHandler(AntigravityFlagHandler.FACTORY, null);

        // artifacts
        pluginManager.registerEvents(new ArtifactsHandler(), this);

        // block replace
        new BlockReplaceTask(this).start();

        // booth
        pluginManager.registerEvents(new BoothListener(), this);
        sessionManager.registerHandler(BoothFlagHandler.FACTORY, null);

        // clutch
        pluginManager.registerEvents(new ClutchListener(), this);
        DoFlagHandler.registerModule(ClutchHandler.TYPE, new ClutchHandler());

        // do
        sessionManager.registerHandler(DoFlagHandler.FACTORY, null);

        // hidden path
        HiddenPathHandler.initialize(this);

        // highblock
        new HighblockPE().register();

        // NPC chat
        pluginManager.registerEvents(new NpcChatHandler(), this);

        // player speed
        sessionManager.registerHandler(WalkSpeedFlagHandler.FACTORY, null);
    }

    public MailHandler getMailHandler() {
        return mailHandler;
    }

    public MinigameConfigHandler getMinigameConfigHelper() {
        return minigameConfigHandler;
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }
}
