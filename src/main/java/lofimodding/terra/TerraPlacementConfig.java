package lofimodding.terra;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.placement.IPlacementConfig;

public class TerraPlacementConfig implements IPlacementConfig {
  public final int chance;
  public final int bottom;
  public final int top;

  public TerraPlacementConfig(final int chance, final int bottom, final int top) {
    this.chance = chance;
    this.bottom = bottom;
    this.top = top;
  }

  @Override
  public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
    return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(
      ops.createString("change"), ops.createInt(this.chance),
      ops.createString("bottom"), ops.createInt(this.bottom),
      ops.createString("top"), ops.createInt(this.top)
    )));
  }

  public static TerraPlacementConfig deserialize(final Dynamic<?> config) {
    return new TerraPlacementConfig(
      config.get("change").asInt(0),
      config.get("bottom").asInt(0),
      config.get("top").asInt(0)
    );
  }
}
