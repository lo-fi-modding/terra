package lofimodding.terra;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Supplier;

@Mod(TerraMod.MOD_ID)
public class TerraMod {
  public static final String MOD_ID = "terra";
  public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

  private final Set<Block> oresToRemove = new HashSet<>();

  public TerraMod() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
  }

  private void loadComplete(final FMLLoadCompleteEvent event) {
    if(!this.oresToRemove.isEmpty()) {
      LOGGER.info("Removing vanilla ore generation for the following ores: {}", this.oresToRemove);
    }

    for(final Biome biome : Biome.BIOMES) {
      if(!this.oresToRemove.isEmpty()) {
        final Iterator<ConfiguredFeature<?, ?>> it = biome.getFeatures(GenerationStage.Decoration.UNDERGROUND_ORES).iterator();
        while(it.hasNext()) {
          ConfiguredFeature<?, ?> configuredFeature = it.next();

          if(configuredFeature.config instanceof DecoratedFeatureConfig) {
            configuredFeature = ((DecoratedFeatureConfig)configuredFeature.config).feature;
          }

          final Feature<?> feature = configuredFeature.feature;

          if(feature instanceof OreFeature) {
            final OreFeatureConfig config = (OreFeatureConfig)configuredFeature.config;
            final Block ore = config.state.getBlock();

            if(this.oresToRemove.contains(ore)) {
              it.remove();
            }
          }
        }
      }

      biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, DeferredGenerator.INSTANCE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
    }
  }

  private void processIMC(final InterModProcessEvent event) {
    event.getIMCStream("remove_ore"::equals).forEach(message -> {
      final Supplier<ResourceLocation> id = message.getMessageSupplier();
      final Block ore = ForgeRegistries.BLOCKS.getValue(id.get());

      if(ore != null) {
        LOGGER.info("{} has requested removal of {} generation", message.getSenderModId(), ore);
        this.oresToRemove.add(ore);
      } else {
        LOGGER.warn("{} has requested the removal of an invalid ore {}", message.getSenderModId(), id.get());
      }
    });
  }
}
