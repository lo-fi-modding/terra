package lofimodding.terra;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;
import java.util.function.Function;

public class DeferredGenerator extends Feature<NoFeatureConfig> {
  public static final DeferredGenerator INSTANCE = new DeferredGenerator(NoFeatureConfig::deserialize);

  public DeferredGenerator(final Function<Dynamic<?>, ? extends NoFeatureConfig> configFactory) {
    super(configFactory);
  }

  @Override
  public boolean place(final IWorld world, final ChunkGenerator<? extends GenerationSettings> generator, final Random rand, final BlockPos start, final NoFeatureConfig config) {
    final DeferredGenerationStorage deferred = DeferredGenerationStorage.get((ServerWorld)world.getWorld());

    final ChunkPos chunkPos = new ChunkPos(start);

    if(deferred.has(chunkPos)) {
      deferred.getOres(chunkPos).forEach((pos, replacers) -> {
        final BlockState oldState = world.getBlockState(pos);

        for(final TerraOreVeinConfig.Replacer replacer : replacers) {
          if(oldState.getBlock().isReplaceableOreGen(oldState, world.getWorld(), pos, replacer)) {
            this.setBlockState(world, pos, replacer.blockToPlace);
            break;
          }
        }
      });

      deferred.getPebbles(chunkPos).forEach((pos, pebble) -> {
        final BlockPos.Mutable pebblePos = new BlockPos.Mutable(pos.getX(), 128, pos.getZ());

        for(BlockState iblockstate = world.getBlockState(pebblePos); pebblePos.getY() > 0 && (iblockstate.getMaterial().isReplaceable() || iblockstate.isIn(BlockTags.LOGS)); iblockstate = world.getBlockState(pebblePos)) {
          pebblePos.move(Direction.DOWN);
        }

        pebblePos.move(Direction.UP);

        if(pebble.isValidPosition(world, pebblePos)) {
          this.setBlockState(world, pebblePos, pebble);
        }
      });

      deferred.remove(chunkPos);
      deferred.markDirty();
    }

    return true;
  }
}
