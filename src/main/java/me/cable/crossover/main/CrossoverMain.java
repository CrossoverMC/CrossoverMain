package me.cable.crossover.main;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.session.SessionManager;
import me.cable.crossover.main.command.LinkCommand;
import me.cable.crossover.main.command.MainCommand;
import me.cable.crossover.main.command.ShopCommand;
import me.cable.crossover.main.command.UnlinkCommand;
import me.cable.crossover.main.currency.CoinsCurrency;
import me.cable.crossover.main.currency.MoneyCurrency;
import me.cable.crossover.main.features.antigravity.AntigravityHandler;
import me.cable.crossover.main.features.antigravity.AntigravityListener;
import me.cable.crossover.main.features.booth.BoothHandler;
import me.cable.crossover.main.features.booth.BoothListener;
import me.cable.crossover.main.features.clutch.ClutchHandler;
import me.cable.crossover.main.features.clutch.ClutchListener;
import me.cable.crossover.main.features.dohandler.DoHandler;
import me.cable.crossover.main.features.highblock.HighblockPE;
import me.cable.crossover.main.handler.*;
import me.cable.crossover.main.listeners.InventoryListener;
import me.cable.crossover.main.listeners.MenuListener;
import me.cable.crossover.main.listeners.PlayerJoin;
import me.cable.crossover.main.object.Minigame;
import me.cable.crossover.main.papi.CrossoverPE;
import me.cable.crossover.main.shop.CustomShopItem;
import me.cable.crossover.main.shop.ShopItem;
import me.cable.crossover.main.task.FallTeleportTask;
import me.cable.crossover.main.task.VelocityBlocksTask;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

public final class CrossoverMain extends JavaPlugin {

    public static StateFlag ANTIGRAVITY_FLAG;
    public static StringFlag BOOTH_FLAG;
    public static StringFlag DO_FLAG;

    private SettingsConfigHandler settingsConfigHandler;
    private MinigameConfigHandler minigameConfigHandler;
    private ShopConfigHandler shopConfigHandler;
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
        registerCurrencies();
        ShopItem.register("custom", CustomShopItem::new);
        startTasks();
        initializeFeatures();
        new CrossoverPE().register();
    }

    @Override
    public void onDisable() {
        playerData.saveAll();
    }

    private void initializeHandlers() {
        settingsConfigHandler = new SettingsConfigHandler(this);
        minigameConfigHandler = new MinigameConfigHandler(this);
        shopConfigHandler = new ShopConfigHandler(this);
        playerData = new PlayerData(this);
        mailHandler = new MailHandler();
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new InventoryListener(), this);
        pluginManager.registerEvents(new MenuListener(), this);
        pluginManager.registerEvents(new PlayerJoin(this), this);
    }

    private void registerCommands() {
        new MainCommand(this).register("crossovermain");
        new LinkCommand(this).register("link");
        new ShopCommand(this).register("shop");
        new UnlinkCommand(this).register("unlink");
    }

    private void registerCurrencies() {
        new CoinsCurrency().register();
        new MoneyCurrency().register();
    }

    private void startTasks() {
        BukkitScheduler bukkitScheduler = getServer().getScheduler();
        bukkitScheduler.scheduleSyncRepeatingTask(this, new FallTeleportTask(), 0, 10);
//        bukkitScheduler.runTaskTimerAsynchronously(this, new Reader(this), 0, 20);
        bukkitScheduler.scheduleSyncRepeatingTask(this, new VelocityBlocksTask(), 0, 1);
    }

    private void initializeFeatures() {
        PluginManager pluginManager = getServer().getPluginManager();
        SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();

        // antigravity
        pluginManager.registerEvents(new AntigravityListener(), this);
        sessionManager.registerHandler(AntigravityHandler.FACTORY, null);

        // booth
        pluginManager.registerEvents(new BoothListener(), this);
        sessionManager.registerHandler(BoothHandler.FACTORY, null);

        // clutch
        pluginManager.registerEvents(new ClutchListener(), this);
        DoHandler.registerModule(ClutchHandler.TYPE, new ClutchHandler());

        // do
        sessionManager.registerHandler(DoHandler.FACTORY, null);

        // highblock
        new HighblockPE().register();
    }

    public MailHandler getMailHandler() {
        return mailHandler;
    }

    public MinigameConfigHandler getMinigameConfigHelper() {
        return minigameConfigHandler;
    }

    public SettingsConfigHandler getSettingsConfigHandler() {
        return settingsConfigHandler;
    }

    public ShopConfigHandler getShopConfigHandler() {
        return shopConfigHandler;
    }
}
