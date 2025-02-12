package me.cable.crossover.main.util;

import me.cable.crossover.main.object.Region;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class ConfigHelper {

    private @NotNull ConfigurationSection cs;

    public ConfigHelper(@NotNull ConfigurationSection cs) {
        this.cs = cs;
    }

    public ConfigHelper() {
        this(new YamlConfiguration());
    }

    public @NotNull ConfigurationSection getCs() {
        return cs;
    }

    public void setCs(@NotNull ConfigurationSection cs) {
        this.cs = cs;
    }

    public boolean isSet(@NotNull String path) {
        return cs.isSet(path);
    }

    public @NotNull Set<String> getKeys(boolean deep) {
        return cs.getKeys(deep);
    }

    public boolean bool(@NotNull String path) {
        return cs.getBoolean(path);
    }

    public boolean bool(@NotNull String path, boolean def) {
        return cs.getBoolean(path, def);
    }

    public int integer(@NotNull String path) {
        return cs.getInt(path);
    }

    public int integer(@NotNull String path, int def) {
        return cs.getInt(path, def);
    }

    public @Nullable Integer integerIfSet(@NotNull String path) {
        return cs.isInt(path) ? cs.getInt(path) : null;
    }

    public @NotNull List<Integer> intList(@NotNull String path) {
        return cs.getIntegerList(path);
    }

    public double doub(@NotNull String path) {
        return cs.getDouble(path);
    }

    public double doub(@NotNull String path, double def) {
        return cs.getDouble(path, def);
    }

    public @Nullable Double doubIfSet(@NotNull String path) {
        return cs.isSet(path) ? cs.getDouble(path) : null;
    }

    public @NotNull BigDecimal bd(@NotNull String path) {
        return bd(path, BigDecimal.ZERO);
    }

    @Contract("_, !null -> !null")
    public @Nullable BigDecimal bd(@NotNull String path, @Nullable BigDecimal def) {
        String val = str(path);
        return (val == null) ? def : new BigDecimal(val);
    }

    public @Nullable String str(@NotNull String path) {
        return cs.getString(path);
    }

    @Contract("_, !null -> !null")
    public @Nullable String str(@NotNull String path, @Nullable String def) {
        return cs.getString(path, def);
    }

    public @NotNull List<String> strList(@NotNull String path) {
        return cs.getStringList(path);
    }

    public @NotNull String snn(@NotNull String path) {
        return cs.getString(path, "");
    }

    public @NotNull Message message(@NotNull String path) {
        return new Message(strList(path));
    }

    @Contract("_, !null -> !null")
    public @Nullable Material mat(@NotNull String path, @Nullable Material def) {
        String str = str(path);
        if (str == null) return def;

        Material m = Material.getMaterial(str);
        return (m == null) ? def : m;
    }

    public @NotNull Material mat(@NotNull String path) {
        return mat(path, Material.AIR);
    }

    public @NotNull SoundEffect sound(@NotNull String path) {
        return SoundEffect.of(snn(path));
    }

    public boolean isCs(@NotNull String path) {
        return cs.isConfigurationSection(path);
    }

    public @Nullable ConfigurationSection cs(@NotNull String path) {
        return cs.getConfigurationSection(path);
    }

    public @NotNull ConfigurationSection csnn(@NotNull String path) {
        ConfigurationSection c = cs.getConfigurationSection(path);
        if (c == null) c = cs.createSection(path);
        return c;
    }

    public @NotNull ConfigHelper ch(@NotNull String path) {
        return new ConfigHelper(csnn(path));
    }

    public @NotNull Vector vec(@NotNull String path) {
        String[] parts = snn(path).split(",");
        return (parts.length >= 3) ? new Vector(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2])) : new Vector();
    }

    public @Nullable Location loc(@NotNull String path) {
        return Utils.locFromString(snn(path));
    }

    @Contract("_, !null -> !null")
    public @Nullable Location loc(@NotNull String path, @Nullable World world) {
        return Utils.locFromString(snn(path), world);
    }

    public @NotNull Region reg(@NotNull String path, @NotNull String worldName) {
        return Region.of(snn(path), worldName);
    }

    public @NotNull Region reg(@NotNull String path) {
        return Region.of(snn(path));
    }
}
