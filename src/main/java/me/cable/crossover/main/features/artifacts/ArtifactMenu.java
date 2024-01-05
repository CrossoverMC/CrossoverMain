package me.cable.crossover.main.features.artifacts;

import me.cable.crossover.main.handler.SettingsConfigHandler;
import me.cable.crossover.main.menu.Menu;
import me.cable.crossover.main.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ArtifactMenu extends Menu {

    private static final String CONFIG_PATH = "artifacts-menu";

    public ArtifactMenu(@NotNull Player player) {
        super(player);

        handleCustomItems(SettingsConfigHandler.getConfig().csnn(CONFIG_PATH + ".items.custom"));

        render(inv -> {
            List<Integer> slots = SettingsConfigHandler.getConfig().intList(CONFIG_PATH + ".slots");
            List<Material> artifacts = Arrays.stream(Material.values())
                    .filter(m -> m.toString().endsWith("_POTTERY_SHERD")).toList();

            for (int i = 0; i < Math.min(slots.size(), artifacts.size()); i++) {
                Material artifact = artifacts.get(i);
                String artifactName = ArtifactsHandler.getArtifactName(artifact);

                boolean hasArtifact = ArtifactsHandler.hasArtifact(player, artifact);
                ItemStack item = new ItemBuilder()
                        .material(artifact)
                        .config(SettingsConfigHandler.getConfig().csnn(CONFIG_PATH + ".items." + (hasArtifact ? "artifact" : "undiscovered")))
                        .placeholder("artifact", artifactName)
                        .create();

                inv.setItem(slots.get(i), item);
            }
        });
    }

    @Override
    protected @NotNull String title() {
        return SettingsConfigHandler.getConfig().snn(CONFIG_PATH + ".title");
    }

    @Override
    protected int rows() {
        return SettingsConfigHandler.getConfig().integer(CONFIG_PATH + ".rows");
    }
}
