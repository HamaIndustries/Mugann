package symbolics.division.mugann.client;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import symbolics.division.mugann.Mugann;
import symbolics.division.mugann.MugannBlocks;
import symbolics.division.mugann.block.GrimoireBlock;

import java.util.Optional;

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
			grimoire(MugannBlocks.GRIMOIRE, MugannBlocks.GRIMOIRE_VERTICAL, blockModelGenerators);
//			blockModelGenerators.createNonTemplateModelBlock(MugannBlocks.MOKSHA, Blocks.WATER);
		}

		private void curtain(Block block, BlockModelGenerators blockModelGenerators) {
			blockModelGenerators.createOrientableTrapdoor(block);
		}

		private void grimoire(Block block, Block vertical, BlockModelGenerators generators) {
			TextureMapping fullBlockTextures = new TextureMapping()
					.put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(block, "_north"))
					.put(TextureSlot.DOWN, TextureMapping.getBlockTexture(block, "_down"))
					.put(TextureSlot.UP, TextureMapping.getBlockTexture(block, "_up"))
					.put(TextureSlot.NORTH, TextureMapping.getBlockTexture(block, "_north"))
					.put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(block, "_south"))
					.put(TextureSlot.EAST, TextureMapping.getBlockTexture(block, "_east"))
					.put(TextureSlot.WEST, TextureMapping.getBlockTexture(block, "_west"));

			ModelTemplate dirSlabTop = createModelTemplate("block/directional_slab_top", "_top", TextureSlot.PARTICLE, TextureSlot.NORTH, TextureSlot.SOUTH, TextureSlot.EAST, TextureSlot.WEST, TextureSlot.UP, TextureSlot.DOWN);
			ModelTemplate dirSlabBottom = createModelTemplate("block/directional_slab_bottom", "_bottom", TextureSlot.PARTICLE, TextureSlot.NORTH, TextureSlot.SOUTH, TextureSlot.EAST, TextureSlot.WEST, TextureSlot.UP, TextureSlot.DOWN);

			MultiVariant top = BlockModelGenerators.plainVariant(dirSlabTop.createWithSuffix(block, "_top", fullBlockTextures, generators.modelOutput));
			MultiVariant bottom = BlockModelGenerators.plainVariant(dirSlabBottom.createWithSuffix(block, "_bottom", fullBlockTextures, generators.modelOutput));
			MultiVariant full = BlockModelGenerators.plainVariant(ModelTemplates.CUBE.create(block, fullBlockTextures, generators.modelOutput));

			var horizModel = MultiVariantGenerator.dispatch(block)
					.with(PropertyDispatch.initial(GrimoireBlock.TYPE).select(SlabType.BOTTOM, bottom).select(SlabType.TOP, top).select(SlabType.DOUBLE, full))
					.with(
							PropertyDispatch.modify(GrimoireBlock.FACING)
									.select(Direction.EAST, BlockModelGenerators.Y_ROT_90)
									.select(Direction.WEST, BlockModelGenerators.Y_ROT_270)
									.select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180)
									.select(Direction.NORTH, BlockModelGenerators.NOP)
					);

			generators.blockStateOutput.accept(horizModel);

			MultiVariant vtop = BlockModelGenerators.plainVariant(dirSlabTop.createWithSuffix(vertical, "_top", fullBlockTextures, generators.modelOutput));
			MultiVariant vbottom = BlockModelGenerators.plainVariant(dirSlabBottom.createWithSuffix(vertical, "_bottom", fullBlockTextures, generators.modelOutput));
			MultiVariant vfull = BlockModelGenerators.plainVariant(ModelTemplates.CUBE.create(vertical, fullBlockTextures, generators.modelOutput));

			var vertModel = MultiVariantGenerator.dispatch(vertical)
					.with(PropertyDispatch.initial(GrimoireBlock.TYPE).select(SlabType.BOTTOM, vbottom).select(SlabType.TOP, vtop).select(SlabType.DOUBLE, vfull))
					.with(
							PropertyDispatch.modify(GrimoireBlock.FACING)
									.select(Direction.EAST, BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_90))
									.select(Direction.WEST, BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_270))
									.select(Direction.SOUTH, BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_180))
									.select(Direction.NORTH, BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.NOP))
					);

			generators.blockStateOutput.accept(vertModel);
		}

		private static ModelTemplate createModelTemplate(final String id, final String suffix, final TextureSlot... slots) {
			return new ModelTemplate(Optional.of(Mugann.id(id)), Optional.empty(), slots);
		}

		@Override
		public void generateItemModels(ItemModelGenerators itemModelGenerators) {

		}
	}
}
