package me.cable.crossover.main.util;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (with != null) placeholders.put('{' + what + '}', with);
        return this;
    }

    public void send(@NotNull CommandSender commandSender) {
        for (String line : lines) {
            commandSender.sendMessage(StringUtils.format(StringUtils.replace(line, placeholders)));
        }
    }

    public void send(@NotNull List<? extends CommandSender> list) {
        list.forEach(this::send);
    }
}
