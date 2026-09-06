package symbolics.division.mugann.fabric.client;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.TexturedModel;
import symbolics.division.mugann.registry.MugannBlocks;

public class MugannDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(BlockModelProvider::new);
	}

	private static final class BlockModelProvider extends FabricModelProvider {
		public BlockModelProvider(FabricDataOutput output) {
			super(output);
		}

		@Override
		public void generateBlockStateModels(BlockModelGenerators generators) {
//			for (var block : MugannBlocks.BLOCKS) generators.createTrivialCube(block);
			for (var supplier : MugannBlocks.RUGS.blocks.values()) {
				generators.createColoredBlockWithStateRotations(TexturedModel.GLAZED_TERRACOTTA, supplier.get());
			}
		}

		@Override
		public void generateItemModels(ItemModelGenerators generators) {
		}
	}
}
