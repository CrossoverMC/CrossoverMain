package me.cable.crossover.main.features.booth;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.util.ItemBuilder;
import me.cable.crossover.main.util.Constants;
import me.cable.crossover.main.util.Rest;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BoothHandler extends FlagValueChangeHandler<String> {

    public static final Factory FACTORY = new Factory();

    public static class Factory extends Handler.Factory<BoothHandler> {

        @Override
        public BoothHandler create(Session session) {
            return new BoothHandler(session);
        }
    }

    public static final Map<Player, String> playerBooths = new HashMap<>();
    public static final List<Player> potentiallyConnected = new ArrayList<>();

    public BoothHandler(Session session) {
        super(session, CrossoverMain.BOOTH_FLAG);
    }

    private void leaveVoice(@NotNull Player player) {
        Rest.putRequest(Rest.HOST + "/voice-booth",
                Map.of("booth", "none", "user", player.getUniqueId().toString()), null);
    }

    private void onEnter(@NotNull Player player, @NotNull String booth) {
        playerBooths.put(player, booth);
        player.getInventory().setItem(Constants.PRIMARY_SLOT, new ItemBuilder().material(Material.GRAY_DYE)
                .name("&6&lJoin Booth Voice")
                .lore("&a&lRight-Click &7to join this", "&7booth's &9Discord &7voice channel.")
                .pd(Constants.TOOL_KEY, Constants.TOOL_BOOTH)
                .create());
    }

    private void onLeave(@NotNull Player player) {
        playerBooths.remove(player);
        player.getInventory().setItem(Constants.PRIMARY_SLOT, null);
    }

    private void handle(LocalPlayer localPlayer, String current, String last) {
        if (Objects.equals(current, last)) return;
        Player player = BukkitAdapter.adapt(localPlayer);

        if (potentiallyConnected.contains(player)) { // changed -> leave voice if necessary
            potentiallyConnected.remove(player);
            leaveVoice(player);
        }
        if (current == null) {
            onLeave(player);
        } else {
            onEnter(player, current);
        }
    }

    @Override
    protected void onInitialValue(LocalPlayer localPlayer, ApplicableRegionSet applicableRegionSet, String current) {
        handle(localPlayer, current, null);
    }

    @Override
    protected boolean onSetValue(LocalPlayer localPlayer, Location from, Location to, ApplicableRegionSet applicableRegionSet, String current, String last, MoveType moveType) {
        handle(localPlayer, current, last);
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer localPlayer, Location from, Location to, ApplicableRegionSet applicableRegionSet, String last, MoveType moveType) {
        handle(localPlayer, null, last);
        return true;
    }
}
