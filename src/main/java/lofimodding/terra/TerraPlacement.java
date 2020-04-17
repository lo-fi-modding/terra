package lofimodding.terra;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.placement.Placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class TerraPlacement extends Placement<TerraPlacementConfig> {
  public static final TerraPlacement INSTANCE = new TerraPlacement(TerraPlacementConfig::deserialize);

  public TerraPlacement(final Function<Dynamic<?>, ? extends TerraPlacementConfig> config) {
    super(config);
  }

  @Override
  public Stream<BlockPos> getPositions(final IWorld world, final ChunkGenerator<? extends GenerationSettings> generator, final Random random, final TerraPlacementConfig config, final BlockPos pos) {
    if(random.nextInt(config.chance) == 0) {
      final int x = random.nextInt(16);
      final int y = random.nextInt(config.top) + config.bottom;
      final int z = random.nextInt(16);

      return Stream.of(pos.add(x, y, z));
    }

    return Stream.empty();
  }
}
