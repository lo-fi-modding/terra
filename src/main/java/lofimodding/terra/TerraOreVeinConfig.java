package lofimodding.terra;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

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

  public static abstract class Replacer implements Predicate<BlockState> {
    public static Replacer read(final CompoundNBT tag) {
      switch(tag.getString("type")) {
        case "tag":
          return TagReplacer.read(tag);

        case "state":
          return StateReplacer.read(tag);
      }

      throw new RuntimeException("Unknown replacer type: " + tag.getString("replacer"));
    }

    public final BlockState blockToPlace;

    protected Replacer(final BlockState blockToPlace) {
      this.blockToPlace = blockToPlace;
    }

    public abstract CompoundNBT write(final CompoundNBT tag);
  }

  public static class TagReplacer extends Replacer {
    public static TagReplacer read(final CompoundNBT tag) {
      return new TagReplacer(BlockTags.getCollection().get(new ResourceLocation(tag.getString("tagToReplace"))), NBTUtil.readBlockState(tag.getCompound("stateToPlace")));
    }

    private final Tag<Block> tag;

    public TagReplacer(final Tag<Block> tagToReplace, final BlockState blockToPlace) {
      super(blockToPlace);
      this.tag = tagToReplace;
    }

    @Override
    public boolean test(final BlockState state) {
      return state.isIn(this.tag);
    }

    @Override
    public CompoundNBT write(final CompoundNBT tag) {
      tag.putString("type", "tag");
      tag.putString("tagToReplace", this.tag.getId().toString());
      tag.put("stateToPlace", NBTUtil.writeBlockState(this.blockToPlace));
      return tag;
    }
  }

  public static class StateReplacer extends Replacer {
    public static StateReplacer read(final CompoundNBT tag) {
      return new StateReplacer(NBTUtil.readBlockState(tag.getCompound("stateToReplace")), NBTUtil.readBlockState(tag.getCompound("stateToPlace")));
    }

    private final BlockState state;

    public StateReplacer(final BlockState blockToReplace,  final BlockState blockToPlace) {
      super(blockToPlace);
      this.state = blockToReplace;
    }

    @Override
    public boolean test(final BlockState state) {
      return this.state.getBlock() == state.getBlock();
    }

    @Override
    public CompoundNBT write(final CompoundNBT tag) {
      tag.putString("type", "state");
      tag.put("stateToReplace", NBTUtil.writeBlockState(this.state));
      tag.put("stateToPlace", NBTUtil.writeBlockState(this.blockToPlace));
      return tag;
    }
  }

  public static final class Stage {
    public final List<Replacer> ores;
    public final StateFunction<Integer> minRadius;
    public final StateFunction<Integer> maxRadius;
    public final StateFunction<Float> blockDensity;
    public final StateFunction<Float> stageSpawnChance;

    private Stage(final List<Replacer> ores, final StateFunction<Integer> minRadius, final StateFunction<Integer> maxRadius, final StateFunction<Float> blockDensity, final StateFunction<Float> stageSpawnChance) {
      this.ores = ores;
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
    private final List<Replacer> ore = new ArrayList<>();
    private StateFunction<Integer> minRadius = state -> 0;
    private StateFunction<Integer> maxRadius = state -> 5;
    private StateFunction<Float> blockDensity = state -> 0.75f;
    private StateFunction<Float> stageSpawnChance = state -> 1.0f;

    private StageBuilder() { }

    public StageBuilder ore(final BlockState ore) {
      return this.ore(Tags.Blocks.STONE, ore);
    }

    public StageBuilder ore(final Tag<Block> tagToReplace, final BlockState ore) {
      return this.ore(new TagReplacer(tagToReplace, ore));
    }

    public StageBuilder ore(final BlockState stateToReplace, final BlockState ore) {
      return this.ore(new StateReplacer(stateToReplace, ore));
    }

    public StageBuilder ore(final Replacer replacer) {
      this.ore.add(replacer);
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
