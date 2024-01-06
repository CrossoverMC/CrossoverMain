package me.cable.crossover.main.menu;

import me.cable.crossover.main.features.artifacts.ArtifactMenu;
import me.cable.crossover.main.handler.ConfigHandler;
import me.cable.crossover.main.util.ConfigHelper;
import me.cable.crossover.main.util.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class MainMenu extends Menu {

    public static @Nullable Function<Player, Menu> petsMenuFactory;

    private static final String CONFIG_PATH = "main-menu";

    public MainMenu(@NotNull Player player) {
        super(player);

        handleCustomItems(ConfigHandler.settings().csnn(CONFIG_PATH + ".items.custom"));

        render(inv -> {
            ConfigHelper items = ConfigHandler.settings().ch(CONFIG_PATH + ".items");
            new ItemBuilder().config(items.csnn("artifacts"))
                    .pd(itemKey, "ARTIFACTS")
                    .place(inv);
            new ItemBuilder().config(items.csnn("pets"))
                    .pd(itemKey, "PETS")
                    .place(inv);
        });

        onClick((e, tag) -> {
            if (tag == null || e.getClick() != ClickType.LEFT) return;

            switch (tag) {
                case "ARTIFACTS" -> {
                    new ArtifactMenu(player, true).open();
                }
                case "PETS" -> {
                    if (petsMenuFactory != null) {
                        petsMenuFactory.apply(player).open();
                    }
                }
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
