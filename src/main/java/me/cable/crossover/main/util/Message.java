package me.cable.crossover.main.util;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Message {

    private final List<String> lines;
    private final Map<String, String> placeholders = new HashMap<>();

    public Message(@NotNull List<String> lines) {
        this.lines = lines;
    }

    public Message() {
        this(new ArrayList<>());
    }

    public @NotNull Message placeholder(@NotNull String what, @Nullable String with) {
        if (with != null) placeholders.put(what, with);
        return this;
    }

    public void send(@NotNull CommandSender commandSender) {
        for (String line : lines) {
            for (Entry<String, String> entry : placeholders.entrySet()) {
                line = line.replace("%" + entry.getKey() + "%", entry.getValue());
            }

            commandSender.sendMessage(StringUtils.format(line));
        }
    }

    public void send(@NotNull List<? extends CommandSender> list) {
        list.forEach(this::send);
    }
}
