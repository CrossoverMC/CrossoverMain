package me.cable.crossover.main.util;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
        if (with != null) placeholders.put(Utils.placeholder(what), with);
        return this;
    }

    public @NotNull Message add(@NotNull Message message) {
        lines.addAll(message.lines);
        return this;
    }

    public void send(@NotNull CommandSender commandSender) {
        for (String line : lines) {
            commandSender.sendMessage(StringUtils.format(StringUtils.replace(line, placeholders)));
        }
    }

    public void send(@NotNull Collection<? extends CommandSender> list) {
        list.forEach(this::send);
    }
}
