package symbolics.division.mugann.client;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.world.level.block.Block;
import symbolics.division.mugann.MugannBlocks;

public class MugannDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		var pack = fabricDataGenerator.createPack();
		pack.addProvider(ModelProvider::new);
	}

	public static final class ModelProvider extends FabricModelProvider {
		public ModelProvider(FabricPackOutput output) {
			super(output);
		}

		@Override
		public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
			curtain(MugannBlocks.RED_CURTAIN, blockModelGenerators);
			blockModelGenerators.createTrivialCube(MugannBlocks.HYPERTHETICAL_DHARMAKIRTIAN);
			blockModelGenerators.createTrivialCube(MugannBlocks.HYPOTHETICAL_DHARMAKIRTIAN);
		}

		private void curtain(Block block, BlockModelGenerators blockModelGenerators) {
			blockModelGenerators.createOrientableTrapdoor(block);
		}

		@Override
		public void generateItemModels(ItemModelGenerators itemModelGenerators) {

		}
	}
}
