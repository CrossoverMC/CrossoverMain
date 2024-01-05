package me.cable.crossover.main.features.artifacts;

import me.cable.crossover.main.handler.PlayerData;
import me.cable.crossover.main.handler.SettingsConfigHandler;
import org.apache.commons.text.WordUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.DecoratedPot;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class ArtifactsHandler implements Listener {

    public static final String PLAYER_DATA_PATH_ARTIFACTS_COLLECTION = "egypt-artifacts.collection";
    public static final String PLAYER_DATA_PATH_ARTIFACTS_INITIATED = "egypt-artifacts.initiated"; // if players can collect shards

    private static boolean isValidArtifactType(@NotNull Material material) {
        return material.toString().endsWith("_POTTERY_SHERD");
    }

    private static void checkArtifactType(@NotNull Material material) {
        if (!isValidArtifactType(material)) {
            throw new IllegalArgumentException("Invalid artifact type " + material);
        }
    }

    public static boolean hasArtifact(@NotNull Player player, @NotNull Material material) {
        checkArtifactType(material);

        String artifact = material.toString();
        artifact = artifact.substring(0, artifact.length() - "_POTTERY_SHERD".length());

        YamlConfiguration playerData = PlayerData.get(player.getUniqueId());
        return playerData.getStringList(PLAYER_DATA_PATH_ARTIFACTS_COLLECTION).contains(artifact);
    }

    public static @NotNull String getArtifactName(@NotNull Material material) {
        checkArtifactType(material);
        String artifactName = material.toString();
        return WordUtils.capitalize(artifactName.substring(0, artifactName.length() - "_POTTERY_SHERD".length())
                .toLowerCase(Locale.ROOT).replaceAll("_", " "));
    }

    private static boolean addArtifact(@NotNull Player player, @NotNull Material material) {
        checkArtifactType(material);

        String artifact = material.toString();
        artifact = artifact.substring(0, artifact.length() - "_POTTERY_SHERD".length());

        YamlConfiguration playerData = PlayerData.get(player.getUniqueId());
        List<String> artifacts = playerData.getStringList(PLAYER_DATA_PATH_ARTIFACTS_COLLECTION);

        if (artifacts.contains(artifact)) {
            return false;
        }

        artifacts.add(artifact);
        playerData.set(PLAYER_DATA_PATH_ARTIFACTS_COLLECTION, artifacts);
        return true;
    }

    private static boolean isInitiated(@NotNull Player player) {
        return PlayerData.get(player.getUniqueId()).getBoolean(PLAYER_DATA_PATH_ARTIFACTS_INITIATED);
    }

    private static void setInitiated(@NotNull Player player, boolean initiated) {
        PlayerData.get(player.getUniqueId()).set(PLAYER_DATA_PATH_ARTIFACTS_INITIATED, initiated);
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getHand() != EquipmentSlot.HAND) return;

        Block block = e.getClickedBlock();
        if (block == null || !(block.getState() instanceof DecoratedPot decoratedPot)) return;

        Material type = decoratedPot.getSherd(DecoratedPot.Side.FRONT);
        if (!isValidArtifactType(type)) return;

        Player player = e.getPlayer();

        if (!isInitiated(player)) {
            SettingsConfigHandler.getConfig().message("messages.artifact-talk-to-archaeologist").send(player);
            return;
        }

        String artifactName = getArtifactName(type);
        boolean addSuccess = addArtifact(player, type);

        SettingsConfigHandler.getConfig().message("messages." + (addSuccess ? "artifact-find" : "artifact-duplicate"))
                .placeholder("artifact", artifactName)
                .send(player);
    }
}
