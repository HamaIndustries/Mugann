package symbolics.division.mugann.client;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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

		private static final PropertyDispatch<VariantMutator> ROTATION_HORIZONTAL_FACING = PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING)
				.select(Direction.EAST, BlockModelGenerators.Y_ROT_90)
				.select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180)
				.select(Direction.WEST, BlockModelGenerators.Y_ROT_270)
				.select(Direction.NORTH, BlockModelGenerators.NOP);

		@Override
		public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
			curtain(MugannBlocks.RED_CURTAIN, blockModelGenerators);
			blockModelGenerators.createTrivialCube(MugannBlocks.HYPERTHETICAL_DHARMAKIRTIAN);
			blockModelGenerators.createTrivialCube(MugannBlocks.HYPOTHETICAL_DHARMAKIRTIAN);
			grimoire(MugannBlocks.GRIMOIRE, blockModelGenerators);
		}

		private void curtain(Block block, BlockModelGenerators blockModelGenerators) {
			blockModelGenerators.createOrientableTrapdoor(block);
		}

		private void grimoire(Block block, BlockModelGenerators generators) {
			//TextureMapping textures =
//			TextureMapping sideTexture = TextureMapping.columnWithWall()


//			TextureMapping fullBlockTextures = TextureMapping.cube(fullBlock);
//			MultiVariant bottom = plainVariant(ModelTemplates.SLAB_BOTTOM.create(block, fullBlockTextures, this.modelOutput));
//			MultiVariant top = plainVariant(ModelTemplates.SLAB_TOP.create(block, fullBlockTextures, this.modelOutput));
//			top.with(variant -> v.)
//			this.blockStateOutput.accept(createSlab(petrifiedSlab, petrifiedSlabBottom, petrifiedSlabTop, fullBlockModel));


//			MultiVariant topModel = plainVariant(ModelLocationUtils.getModelLocation(block, "_top"));
//			MultiVariant bottomModel = plainVariant(ModelLocationUtils.getModelLocation(block, "_bottom"));
//			generators.blockStateOutput.accept(
//					MultiVariantGenerator.dispatch(block)
//							.with(PropertyDispatch.initial(GrimoireBlock.TYPE).select(SlabType.BOTTOM, bottomModel).select(SlabType.TOP, topModel))
//							.with(ROTATION_HORIZONTAL_FACING)
//			);
		}

		@Override
		public void generateItemModels(ItemModelGenerators itemModelGenerators) {

		}
	}
}
