package lofimodding.terra;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeferredGenerationStorage extends WorldSavedData {
  private static final String DATA_NAME = TerraMod.MOD_ID + "_deferred_ore";

  public static DeferredGenerationStorage get(final ServerWorld world) {
    final DeferredGenerationStorage storage = world.getSavedData().get(DeferredGenerationStorage::new, DATA_NAME);

    if(storage == null) {
      final DeferredGenerationStorage newStorage = new DeferredGenerationStorage();
      world.getSavedData().set(newStorage);
      return newStorage;
    }

    return storage;
  }

  private final Map<ChunkPos, Deferred> deferred = new HashMap<>();

  public DeferredGenerationStorage() {
    super(DATA_NAME);
  }

  public boolean has(final ChunkPos pos) {
    return this.deferred.containsKey(pos);
  }

  public Map<BlockPos, List<TerraOreVeinConfig.Replacer>> getOres(final ChunkPos pos) {
    if(!this.deferred.containsKey(pos)) {
      final Deferred deferred = new Deferred();
      this.deferred.put(pos, deferred);
      return deferred.ores;
    }

    return this.deferred.get(pos).ores;
  }

  public Map<BlockPos, BlockState> getPebbles(final ChunkPos pos) {
    if(!this.deferred.containsKey(pos)) {
      final Deferred deferred = new Deferred();
      this.deferred.put(pos, deferred);
      return deferred.pebbles;
    }

    return this.deferred.get(pos).pebbles;
  }

  public void remove(final ChunkPos pos) {
    this.deferred.remove(pos);
  }

  @Override
  public void read(final CompoundNBT nbt) {
    this.deferred.clear();

    final ListNBT chunkList = nbt.getList("chunks", Constants.NBT.TAG_COMPOUND);

    for(final INBT chunkBase : chunkList) {
      final CompoundNBT chunkNbt = (CompoundNBT)chunkBase;

      final ChunkPos chunkPos = new ChunkPos(chunkNbt.getInt("x"), chunkNbt.getInt("z"));

      final Map<BlockPos, List<TerraOreVeinConfig.Replacer>> ores = this.getOres(chunkPos);
      final ListNBT oreList = chunkNbt.getList("ores", Constants.NBT.TAG_COMPOUND);

      for(final INBT oreBase : oreList) {
        final CompoundNBT oreNbt = (CompoundNBT)oreBase;

        final BlockPos blockPos = NBTUtil.readBlockPos(oreNbt.getCompound("pos"));

        final ListNBT replacerListNbt = oreNbt.getList("replacers", Constants.NBT.TAG_COMPOUND);
        final List<TerraOreVeinConfig.Replacer> replacers = new ArrayList<>();
        for(int i = 0; i < replacerListNbt.size(); i++) {
          final CompoundNBT replacerNbt = replacerListNbt.getCompound(i);
          replacers.add(TerraOreVeinConfig.Replacer.read(replacerNbt));
        }

        ores.put(blockPos, replacers);
      }

      final Map<BlockPos, BlockState> pebbles = this.getPebbles(chunkPos);
      final ListNBT pebbleList = chunkNbt.getList("pebbles", Constants.NBT.TAG_COMPOUND);

      for(final INBT pebbleBase : pebbleList) {
        final CompoundNBT pebbleNbt = (CompoundNBT)pebbleBase;

        final BlockPos blockPos = NBTUtil.readBlockPos(pebbleNbt.getCompound("pos"));
        final BlockState pebble = NBTUtil.readBlockState(pebbleNbt.getCompound("pebble"));

        pebbles.put(blockPos, pebble);
      }
    }
  }

  @Override
  public CompoundNBT write(final CompoundNBT compound) {
    final ListNBT chunkList = new ListNBT();
    compound.put("chunks", chunkList);

    this.deferred.forEach((chunkPos, deferred) -> {
      final CompoundNBT chunkNbt = new CompoundNBT();
      chunkList.add(chunkNbt);

      chunkNbt.putInt("x", chunkPos.x);
      chunkNbt.putInt("z", chunkPos.z);

      final ListNBT oreList = new ListNBT();
      chunkNbt.put("ores", oreList);

      deferred.ores.forEach((blockPos, replacers) -> {
        final CompoundNBT oreNbt = new CompoundNBT();
        oreList.add(oreNbt);

        oreNbt.put("pos", NBTUtil.writeBlockPos(blockPos));

        final ListNBT replacerList = new ListNBT();
        for(final TerraOreVeinConfig.Replacer replacer : replacers) {
          replacerList.add(replacer.write(new CompoundNBT()));
        }

        oreNbt.put("replacers", replacerList);
      });

      final ListNBT pebbleList = new ListNBT();
      chunkNbt.put("pebbles", pebbleList);

      deferred.pebbles.forEach((blockPos, pebble) -> {
        final CompoundNBT pebbleNbt = new CompoundNBT();
        pebbleList.add(pebbleNbt);

        pebbleNbt.put("pos", NBTUtil.writeBlockPos(blockPos));
        pebbleNbt.put("pebble", NBTUtil.writeBlockState(pebble));
      });
    });

    return compound;
  }

  private static class Deferred {
    private final Map<BlockPos, List<TerraOreVeinConfig.Replacer>> ores = new HashMap<>();
    private final Map<BlockPos, BlockState> pebbles = new HashMap<>();
  }
}
