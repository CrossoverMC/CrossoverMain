package me.cable.crossover.main.features.playerspeed;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.util.NumberUtils;
import me.cable.crossover.main.util.Utils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WalkSpeedFlagHandler extends FlagValueChangeHandler<Double> {

    public static final Factory FACTORY = new Factory();

    public static class Factory extends Handler.Factory<WalkSpeedFlagHandler> {

        @Override
        public WalkSpeedFlagHandler create(Session session) {
            return new WalkSpeedFlagHandler(session);
        }
    }

    private @Nullable SpeedModifier speedModifier;

    public WalkSpeedFlagHandler(Session session) {
        super(session, CrossoverMain.WALK_SPEED_FLAG);
    }

    private void handle(@NotNull LocalPlayer localPlayer, @Nullable Double value) {
        Player player = BukkitAdapter.adapt(localPlayer);

        if (speedModifier != null) {
            speedModifier.detachWalk();
            speedModifier = null;
        }
        if (value != null && !Utils.hasBypass(player)) {
            float speed = (float) NumberUtils.minMax(value, 0.0, 1.0);
            speedModifier = new SpeedModifier(player, speed, SpeedPriority.HIGHEST);
            speedModifier.attachWalk();
        }
    }

    @Override
    protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, Double value) {
        handle(player, value);
    }

    @Override
    protected boolean onSetValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Double currentValue, Double lastValue, MoveType moveType) {
        handle(player, currentValue);
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Double lastValue, MoveType moveType) {
        handle(player, null);
        return true;
    }
}
