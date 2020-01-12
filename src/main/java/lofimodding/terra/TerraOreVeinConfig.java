package lofimodding.terra;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.IFeatureConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class TerraOreVeinConfig implements IFeatureConfig {
  public static TerraOreVeinConfig create(final Consumer<ConfigBuilder> callback) {
    final ConfigBuilder builder = new ConfigBuilder();
    callback.accept(builder);
    return builder.build();
  }

  public final Pebble[] pebbles;
  public final Stage[] stages;
  public final StateFunction<Integer> minLength;
  public final StateFunction<Integer> maxLength;

  private TerraOreVeinConfig(final Stage[] stages, final Pebble[] pebbles, final StateFunction<Integer> minLength, final StateFunction<Integer> maxLength) {
    this.stages = stages;
    this.pebbles = pebbles;
    this.minLength = minLength;
    this.maxLength = maxLength;
  }

  @Override
  public <T> Dynamic<T> serialize(final DynamicOps<T> dynamicOps) {
    return null; //TODO
  }

  public static TerraOreVeinConfig deserialize(final Dynamic<?> config) {
    //TODO
    return new TerraOreVeinConfig(new Stage[0], new Pebble[0], i -> 0, i -> 0);
  }

  public static final class Stage {
    public final BlockState ore;
    public final StateFunction<Integer> minRadius;
    public final StateFunction<Integer> maxRadius;
    public final StateFunction<Float> blockDensity;
    public final StateFunction<Float> stageSpawnChance;

    private Stage(final BlockState ore, final StateFunction<Integer> minRadius, final StateFunction<Integer> maxRadius, final StateFunction<Float> blockDensity, final StateFunction<Float> stageSpawnChance) {
      this.ore = ore;
      this.minRadius = minRadius;
      this.maxRadius = maxRadius;
      this.blockDensity = blockDensity;
      this.stageSpawnChance = stageSpawnChance;
    }
  }

  public static final class Pebble {
    public final BlockState pebble;
    public final float density;

    private Pebble(final BlockState pebble, final float density) {
      this.pebble = pebble;
      this.density = density;
    }
  }

  public static final class ConfigBuilder {
    private final List<Stage> stages = new ArrayList<>();
    private final List<Pebble> pebbles = new ArrayList<>();
    private StateFunction<Integer> minLength = state -> 3;
    private StateFunction<Integer> maxLength = state -> 5;

    private ConfigBuilder() { }

    public ConfigBuilder addStage(final Consumer<StageBuilder> callback) {
      final StageBuilder builder = new StageBuilder();
      callback.accept(builder);
      this.stages.add(builder.build());
      return this;
    }

    public ConfigBuilder addPebble(final Consumer<PebbleBuilder> callback) {
      final PebbleBuilder builder = new PebbleBuilder();
      callback.accept(builder);
      this.pebbles.add(builder.build());
      return this;
    }

    public ConfigBuilder minLength(final int length) {
      return this.minLength(depth -> length);
    }

    public ConfigBuilder minLength(final StateFunction<Integer> length) {
      this.minLength = length;
      return this;
    }

    public ConfigBuilder maxLength(final int length) {
      return this.maxLength(depth -> length);
    }

    public ConfigBuilder maxLength(final StateFunction<Integer> length) {
      this.maxLength = length;
      return this;
    }

    private static final Stage[] ZERO_LENGTH_STAGE = new Stage[0];
    private static final Pebble[] ZERO_LENGTH_PEBBLE = new Pebble[0];

    private TerraOreVeinConfig build() {
      return new TerraOreVeinConfig(this.stages.toArray(ZERO_LENGTH_STAGE), this.pebbles.toArray(ZERO_LENGTH_PEBBLE), this.minLength, this.maxLength);
    }
  }

  public static final class StageBuilder {
    private BlockState ore = Blocks.STONE.getDefaultState();
    private StateFunction<Integer> minRadius = state -> 0;
    private StateFunction<Integer> maxRadius = state -> 5;
    private StateFunction<Float> blockDensity = state -> 0.75f;
    private StateFunction<Float> stageSpawnChance = state -> 1.0f;

    private StageBuilder() { }

    public StageBuilder ore(final BlockState ore) {
      this.ore = ore;
      return this;
    }

    public StageBuilder minRadius(final int minRadius) {
      return this.minRadius(depth -> minRadius);
    }

    public StageBuilder minRadius(final StateFunction<Integer> minRadius) {
      this.minRadius = minRadius;
      return this;
    }

    public StageBuilder maxRadius(final int maxRadius) {
      return this.maxRadius(depth -> maxRadius);
    }

    public StageBuilder maxRadius(final StateFunction<Integer> maxRadius) {
      this.maxRadius = maxRadius;
      return this;
    }

    public StageBuilder blockDensity(final float density) {
      return this.blockDensity(depth -> density);
    }

    public StageBuilder blockDensity(final StateFunction<Float> density) {
      this.blockDensity = density;
      return this;
    }

    public StageBuilder stageSpawnChance(final float spawnChance) {
      return this.stageSpawnChance(depth -> spawnChance);
    }

    public StageBuilder stageSpawnChance(final StateFunction<Float> spawnChance) {
      this.stageSpawnChance = spawnChance;
      return this;
    }

    private Stage build() {
      return new Stage(this.ore, this.minRadius, this.maxRadius, this.blockDensity, this.stageSpawnChance);
    }
  }

  public static final class PebbleBuilder {
    private BlockState pebble = Blocks.AIR.getDefaultState();
    private float density = 0.5f;

    private PebbleBuilder() { }

    public PebbleBuilder pebble(final BlockState pebble) {
      this.pebble = pebble;
      return this;
    }

    public PebbleBuilder density(final float density) {
      this.density = density;
      return this;
    }

    private Pebble build() {
      return new Pebble(this.pebble, this.density);
    }
  }

  @FunctionalInterface
  public interface StateFunction<RETURN> extends Function<OreGenState, RETURN> {

  }
}
