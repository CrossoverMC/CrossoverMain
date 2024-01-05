package me.cable.crossover.main.task;

import me.cable.crossover.main.CrossoverMain;
import me.cable.crossover.main.currency.Currency;
import me.cable.crossover.main.handler.MailHandler;
import me.cable.crossover.main.handler.SettingsConfigHandler;
import me.cable.crossover.main.util.Color;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class Reader implements Runnable {

    private final MailHandler mailHandler;

    public Reader(@NotNull CrossoverMain crossoverMain) {
        mailHandler = crossoverMain.getMailHandler();
    }

    @Override
    public void run() {
        if (!SettingsConfigHandler.getConfig().bool("reader.enabled")) return;

        List<String> lines = readLines();

        for (String line : lines) {
            handleLine(line);
        }
    }

    private void handleLine(@NotNull String line) {
        String[] parts = line.split(" ");

        if (parts[0].equals("add_coins")) {
            UUID playerUuid = UUID.fromString(parts[1]);
            BigDecimal amount = new BigDecimal(parts[2]);

            Currency.getCoins().deposit(playerUuid, amount);
            mailHandler.sendMail(playerUuid, List.of(Color.SUCCESS + "You have received "
                    + Color.SPECIAL + amount.toPlainString() + Color.SUCCESS + " coins"));
        }
    }

    private @NotNull List<String> readLines() {
        String path = SettingsConfigHandler.getConfig().snn("reader.file-path");

        try (FileChannel fileChannel = FileChannel.open(Path.of(path),
                StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
             FileLock ignored = fileChannel.lock()) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            StringBuilder data = new StringBuilder();
            int bytesRead = fileChannel.read(byteBuffer);

            while (bytesRead != -1) {
                byteBuffer.flip();

                while (byteBuffer.hasRemaining()) {
                    char c = (char) byteBuffer.get();
                    data.append(c);
                }

                byteBuffer.clear();
                bytesRead = fileChannel.read(byteBuffer);
            }

            fileChannel.truncate(0);
            return Stream.of(data.toString().split("\n")).filter(v -> !v.isBlank()).toList();
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
