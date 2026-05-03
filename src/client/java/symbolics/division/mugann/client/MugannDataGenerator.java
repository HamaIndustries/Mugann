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
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
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

		ModelTemplate dirSlabTop = createModelTemplate("block/directional_slab_top", "_top", TextureSlot.PARTICLE, TextureSlot.TEXTURE);
		ModelTemplate dirSlabBottom = createModelTemplate("block/directional_slab_bottom", "_bottom", TextureSlot.PARTICLE, TextureSlot.TEXTURE);
		ModelTemplate dirSlabFull = createModelTemplate("block/directional_slab_full", "_full", TextureSlot.PARTICLE, TextureSlot.TEXTURE);

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

			for (String id : MugannBlocks.grimTypes) {
				grimoire(MugannBlocks.GRIMS.get(id), MugannBlocks.VGRIMS.get(id), blockModelGenerators);
			}
		}


		private void curtain(Block block, BlockModelGenerators blockModelGenerators) {
			blockModelGenerators.createOrientableTrapdoor(block);
		}

		private void grimoire(Block block, Block vertical, BlockModelGenerators generators) {
			Material mat = new Material(BuiltInRegistries.BLOCK.getKey(block).withPrefix("block/"));

			TextureMapping fullBlockTextures = new TextureMapping()
					.put(TextureSlot.PARTICLE, mat)
					.put(TextureSlot.TEXTURE, mat);


			MultiVariant top = BlockModelGenerators.plainVariant(dirSlabTop.createWithSuffix(block, "_top", fullBlockTextures, generators.modelOutput));
			MultiVariant bottom = BlockModelGenerators.plainVariant(dirSlabBottom.createWithSuffix(block, "_bottom", fullBlockTextures, generators.modelOutput));
			MultiVariant full = BlockModelGenerators.plainVariant(dirSlabFull.createWithSuffix(block, "_full", fullBlockTextures, generators.modelOutput));

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
			MultiVariant vfull = BlockModelGenerators.plainVariant(dirSlabFull.createWithSuffix(vertical, "_full", fullBlockTextures, generators.modelOutput));

			var vertModel = MultiVariantGenerator.dispatch(vertical)
					.with(PropertyDispatch.initial(GrimoireBlock.TYPE).select(SlabType.BOTTOM, vbottom).select(SlabType.TOP, vtop).select(SlabType.DOUBLE, vfull))
					.with(
							PropertyDispatch.modify(GrimoireBlock.FACING)
									.select(Direction.EAST, BlockModelGenerators.X_ROT_270.then(BlockModelGenerators.NOP))
									.select(Direction.SOUTH, BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_90))
									.select(Direction.WEST, BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_180))
									.select(Direction.NORTH, BlockModelGenerators.X_ROT_270.then(BlockModelGenerators.Y_ROT_270))
					);

			generators.blockStateOutput.accept(vertModel);
			generators.registerSimpleItemModel(block, BuiltInRegistries.BLOCK.getKey(block));
			generators.registerSimpleItemModel(vertical, BuiltInRegistries.BLOCK.getKey(vertical));
		}

		private static ModelTemplate createModelTemplate(final String id, final String suffix, final TextureSlot... slots) {
			return new ModelTemplate(Optional.of(Mugann.id(id)), Optional.empty(), slots);
		}

		@Override
		public void generateItemModels(ItemModelGenerators itemModelGenerators) {

		}
	}
}
