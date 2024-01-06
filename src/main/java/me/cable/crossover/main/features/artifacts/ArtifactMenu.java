package me.cable.crossover.main.features.artifacts;

import me.cable.crossover.main.handler.ConfigHandler;
import me.cable.crossover.main.menu.MainMenu;
import me.cable.crossover.main.menu.Menu;
import me.cable.crossover.main.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ArtifactMenu extends Menu {

    private static final String CONFIG_PATH = "artifacts-menu";

    public ArtifactMenu(@NotNull Player player, boolean showBack) {
        super(player);

        handleCustomItems(ConfigHandler.settings().csnn(CONFIG_PATH + ".items.custom"));

        render(inv -> {
            List<Integer> slots = ConfigHandler.settings().intList(CONFIG_PATH + ".slots");
            List<Material> artifacts = ArtifactsHandler.getArtifactTypes();

            for (int i = 0; i < Math.min(slots.size(), artifacts.size()); i++) {
                Material artifact = artifacts.get(i);
                String artifactName = ArtifactsHandler.getArtifactName(artifact);

                boolean hasArtifact = ArtifactsHandler.hasArtifact(player, artifact);
                ItemStack item = new ItemBuilder()
                        .material(artifact)
                        .config(ConfigHandler.settings().csnn(CONFIG_PATH + ".items." + (hasArtifact ? "artifact" : "undiscovered")))
                        .placeholder("artifact", artifactName)
                        .create();

                inv.setItem(slots.get(i), item);
            }

            if (showBack) {
                new ItemBuilder().config(ConfigHandler.settings().csnn(CONFIG_PATH + ".items.back"))
                        .pd(itemKey, "BACK")
                        .place(inv);
            }
        });

        onClick((e, tag) -> {
            if (e.getClick() == ClickType.LEFT && "BACK".equals(tag)) {
                new MainMenu(player).open();
            }
        });
    }

    @Override
    protected @NotNull String title() {
        return ConfigHandler.settings().snn(CONFIG_PATH + ".title");
    }

    @Override
    protected int rows() {
        return ConfigHandler.settings().integer(CONFIG_PATH + ".rows");
    }
}
