package me.cable.crossover.main.features.dohandler;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.util.Utils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class DoFlagHandler extends FlagValueChangeHandler<String> {

    private static final Map<String, DoModule> modules = new HashMap<>();

    public static final Factory FACTORY = new Factory();

    public static class Factory extends Handler.Factory<DoFlagHandler> {

        @Override
        public DoFlagHandler create(Session session) {
            return new DoFlagHandler(session);
        }
    }

    public static void registerModule(@NotNull String type, @NotNull DoModule doModule) {
        modules.put(type, doModule);
    }

    public DoFlagHandler(Session session) {
        super(session, CrossoverMain.DO_FLAG);
    }

    private void onEnter(@NotNull Player player, @NotNull String type) {
        if (Utils.hasBypass(player)) return;
        DoModule doModule = modules.get(type);
        if (doModule != null) doModule.onEnter(player);
    }

    private void onLeave(@NotNull Player player, @NotNull String type) {
        if (Utils.hasBypass(player)) return;
        DoModule doModule = modules.get(type);
        if (doModule != null) doModule.onLeave(player);
    }

    @Override
    protected void onInitialValue(LocalPlayer localPlayer, ApplicableRegionSet applicableRegionSet, String current) {
        if (current != null) {
            onEnter(BukkitAdapter.adapt(localPlayer), current);
        }
    }

    @Override
    protected boolean onSetValue(LocalPlayer localPlayer, Location from, Location to, ApplicableRegionSet applicableRegionSet, String current, String last, MoveType moveType) {
        if (!current.equals(last)) {
            Player player = BukkitAdapter.adapt(localPlayer);

            if (last != null) {
                onLeave(player, last);
            }

            onEnter(player, current);
        }

        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer localPlayer, Location from, Location to, ApplicableRegionSet applicableRegionSet, String last, MoveType moveType) {
        onLeave(BukkitAdapter.adapt(localPlayer), last);
        return true;
    }
}
