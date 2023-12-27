package me.cable.crossover.main.command;

import me.cable.crossover.main.CrossoverMain;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public abstract class CustomCommand implements TabExecutor {

    protected final CrossoverMain crossoverMain;

    public CustomCommand(@NotNull CrossoverMain crossoverMain) {
        this.crossoverMain = crossoverMain;
    }

    public void register(@NotNull String label) {
        PluginCommand pluginCommand = crossoverMain.getCommand(label);

        if (pluginCommand == null) {
            throw new IllegalStateException("Invalid command: " + label);
        }

        pluginCommand.setExecutor(this);
        pluginCommand.setTabCompleter(this);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return Collections.emptyList();
    }
}
