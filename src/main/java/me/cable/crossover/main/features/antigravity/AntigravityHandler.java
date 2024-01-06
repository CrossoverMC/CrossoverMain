package me.cable.crossover.main.features.antigravity;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.util.ItemBuilder;
import me.cable.crossover.main.util.Constants;
import me.cable.crossover.main.util.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class AntigravityHandler extends FlagValueChangeHandler<StateFlag.State> {

    public static final Factory FACTORY = new Factory();

    public static class Factory extends Handler.Factory<AntigravityHandler> {

        @Override
        public AntigravityHandler create(Session session) {
            return new AntigravityHandler(session);
        }
    }

    public AntigravityHandler(Session session) {
        super(session, CrossoverMain.ANTIGRAVITY_FLAG);
    }

    private void onEnter(Player player) {
        if (Utils.hasBypass(player)) return;

        player.setGravity(false);
        player.getInventory().setItem(Constants.PRIMARY_SLOT, new ItemBuilder().material(Material.BRUSH)
                .name("&6&lSmall Jetpack")
                .lore("&a&lRight-Click &7to propel yourself.")
                .pd(Constants.KEY_TOOL, Constants.TOOL_SMALL_JETPACK)
                .create());
        player.getInventory().setItem(Constants.SECONDARY_SLOT, new ItemBuilder().material(Material.TORCHFLOWER)
                .name("&9&lLarge Jetpack")
                .lore("&a&lRight-Click &7to propel yourself.")
                .pd(Constants.KEY_TOOL, Constants.TOOL_LARGE_JETPACK)
                .create());
    }

    private void onLeave(Player player) {
        player.setGravity(true);

        if (!Utils.hasBypass(player)) {
            player.getInventory().setItem(Constants.PRIMARY_SLOT, null);
            player.getInventory().setItem(Constants.SECONDARY_SLOT, null);
        }
    }

    private void handle(LocalPlayer localPlayer, StateFlag.State current, StateFlag.State last) {
        if (current == null) current = StateFlag.State.DENY;
        if (last == null) last = StateFlag.State.DENY;
        if (current == last) return;

        Player player = BukkitAdapter.adapt(localPlayer);

        if (current == StateFlag.State.ALLOW) {
            onEnter(player);
        } else {
            onLeave(player);
        }
    }

    @Override
    protected void onInitialValue(LocalPlayer localPlayer, ApplicableRegionSet applicableRegionSet, StateFlag.State current) {
        handle(localPlayer, current, null);
    }

    @Override
    protected boolean onSetValue(LocalPlayer localPlayer, Location from, Location to, ApplicableRegionSet applicableRegionSet, StateFlag.State current, StateFlag.State last, MoveType moveType) {
        handle(localPlayer, current, last);
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer localPlayer, Location from, Location to, ApplicableRegionSet applicableRegionSet, StateFlag.State last, MoveType moveType) {
        handle(localPlayer, null, last);
        return true;
    }
}
