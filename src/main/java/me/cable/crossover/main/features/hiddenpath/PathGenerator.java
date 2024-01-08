package me.cable.crossover.main.features.hiddenpath;

import me.cable.crossover.main.object.Region;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PathGenerator {

    private final BlockFace direction;
    private final Block startBlock;
    private final int distance;
    private final Region region;

    private final List<BlockFace> availableDirections;
    private List<Block> path;

    public PathGenerator(@NotNull BlockFace direction, @NotNull Block startBlock, int distance, @NotNull Region region) {
        this.direction = direction;
        this.startBlock = startBlock;
        this.distance = distance;
        this.region = region;
        availableDirections = getAvailableDirections(direction);
    }

    private @NotNull List<BlockFace> getAvailableDirections(@NotNull BlockFace direction) {
        List<BlockFace> directions = new ArrayList<>(List.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST));
        directions.remove(direction.getOppositeFace());
        return directions;
    }

    private @NotNull List<BlockFace> getOptions(@NotNull Block block) {
        List<BlockFace> list = new ArrayList<>();

        for (BlockFace blockFace : availableDirections) {
            Block option = block.getRelative(blockFace);

            if (blockFace != direction) {
                Block back = option.getRelative(direction.getOppositeFace());
                if (path.contains(back)) continue;
            }
            if (!path.contains(option) && region.contains(option)) {
                list.add(blockFace);
            }
        }

        return list;
    }

    public @NotNull List<Block> generate() {
        int distanceGenerated = 1; // start block counts
        path = new ArrayList<>();
        path.add(startBlock);

        Block current = startBlock;

        while (distanceGenerated < distance) {
            List<BlockFace> options = getOptions(current);
            BlockFace selected = options.get((int) (Math.random() * options.size()));
            current = current.getRelative(selected);
            path.add(current);

            if (selected == direction) {
                distanceGenerated++;
            }
        }

        return path;
    }
}
