package me.cable.crossover.main.features.npcchat;

import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.features.artifacts.ArtifactsHandler;
import me.cable.crossover.main.handler.ConfigHandler;
import me.cable.crossover.main.handler.InventoryItems;
import me.cable.crossover.main.inventoryitem.SapphireItem;
import me.cable.crossover.main.util.SoundEffect;
import me.cable.crossover.main.util.StringUtils;
import me.cable.crossover.main.util.Utils;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NpcChatHandler implements Listener {

    private static final List<Player> inConversation = new ArrayList<>();

    public static void startChat(@NotNull Player player, @NotNull NPC npc, @NotNull ConfigurationSection lines) {
        if (inConversation.contains(player)) {
            return;
        }

        String lineFormat = ConfigHandler.npcChatSettings().snn("line");
        inConversation.add(player);

        new BukkitRunnable() {

            int lineI = 0;

            @Override
            public void run() {
                ConfigurationSection lineSection = lines.getConfigurationSection(Integer.toString(lineI++));

                if (lineSection == null || !player.isOnline()) {
                    inConversation.remove(player);
                    cancel();
                    return;
                }

                List<String> commands = lineSection.getStringList("commands");
                ConsoleCommandSender consoleCommandSender = Bukkit.getConsoleSender();

                for (String command : commands) {
                    command = command.replace(Utils.placeholder("player"), player.getName());
                    Bukkit.dispatchCommand(consoleCommandSender, command);
                }

                String message = lineSection.getString("message");

                if (message != null && !message.isEmpty()) {
                    String line = lineFormat.replace(Utils.placeholder("message"), message)
                            .replace(Utils.placeholder("npc_name"), npc.getName());
                    player.sendMessage(StringUtils.format(line));
                }

                List<String> sounds = lineSection.getStringList("sounds");

                for (String soundString : sounds) {
                    SoundEffect.of(soundString).play(player);
                }
            }
        }.runTaskTimer(CrossoverMain.getInstance(), 0, ConfigHandler.npcChatSettings().integer("period"));
    }

    @EventHandler
    public void onNPCRightClick(@NotNull NPCRightClickEvent e) {
        Player player = e.getClicker();
        NPC npc = e.getNPC();
        int npcId = npc.getId();

        ConfigurationSection linesSection;

        if (ConfigHandler.npcChatSettings().integer("npcs.archaeologist.id") == npcId) {
            String state = ArtifactsHandler.getState(player);
            String path;

            if (state == null) {
                path = "collect";
                ArtifactsHandler.setState(player, "COLLECTING");
            } else if (state.equals("COLLECTING")) {
                if (ArtifactsHandler.hasAllArtifacts(player)) {
                    path = "collected";
                    ArtifactsHandler.setState(player, "COLLECTED");
                } else {
                    path = "collect";
                }
            } else {
                path = "after";
            }

            linesSection = ConfigHandler.npcChatSettings().csnn("npcs.archaeologist." + path);
        } else if (ConfigHandler.npcChatSettings().integer("npcs.traveler.id") == npcId) {
            String state = ArtifactsHandler.getState(player);
            String path;

            if ("COLLECTED".equals(state)) {
                InventoryItems inventoryItems = InventoryItems.get(player);
                int sapphireAmount = inventoryItems.get(SapphireItem.ID);

                if (sapphireAmount > 0) {
                    path = "give";
                    inventoryItems.set(SapphireItem.ID, 0);
                } else {
                    path = "given";
                }
            } else {
                path = "searching";
            }

            linesSection = ConfigHandler.npcChatSettings().csnn("npcs.traveler." + path);
        } else {
            linesSection = ConfigHandler.npcChatSettings().cs("npcs.custom." + npcId);
        }
        if (linesSection != null) {
            startChat(player, npc, linesSection);
        }
    }
}
