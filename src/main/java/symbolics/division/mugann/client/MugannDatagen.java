package symbolics.division.mugann.client;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import symbolics.division.mugann.Mugann;
import symbolics.division.mugann.registry.MugannBlocks;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@EventBusSubscriber
public class MugannDatagen {
	@SubscribeEvent // on the mod event bus
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(
				event.includeClient(),
				new MugannBlockStates(output, existingFileHelper, lookupProvider)
		);
	}

	public static class MugannBlockStates extends BlockStateProvider {
		private static ModelTemplate createModelTemplate(final String id, final String suffix, final TextureSlot... slots) {
			return new ModelTemplate(Optional.of(Mugann.id(id)), Optional.empty(), slots);
		}

		private static ModelTemplate dirSlabTop = createModelTemplate("block/directional_slab_top", "_top", TextureSlot.PARTICLE, TextureSlot.TEXTURE);
		private static ModelTemplate dirSlabBottom = createModelTemplate("block/directional_slab_bottom", "_bottom", TextureSlot.PARTICLE, TextureSlot.TEXTURE);
		private static ModelTemplate dirSlabFull = createModelTemplate("block/directional_slab_full", "_full", TextureSlot.PARTICLE, TextureSlot.TEXTURE);
		private static ModelTemplate mouldingTemplateLower = createModelTemplate("block/moulding_template_lower", "", TextureSlot.TEXTURE);
//		ModelTemplate mouldingTemplateUpper = createModelTemplate("block/moulding_template_upper", "", TextureSlot.TEXTURE);

		private final CompletableFuture<HolderLookup.Provider> lookupProvider;

		// https://docs.neoforged.net/docs/1.21.1/resources/client/models/datagen
		public MugannBlockStates(PackOutput output, ExistingFileHelper exFileHelper, CompletableFuture<HolderLookup.Provider> lookupProvider) {
			super(output, Mugann.ID, exFileHelper);
			this.lookupProvider = lookupProvider;
		}

		ModelTemplate grimoireAltTemplate = createModelTemplate("item/grimoire_alt", "", TextureSlot.LAYER0, TextureSlot.LAYER1);

		@Override
		protected void registerStatesAndModels() {
			forType(MugannBlocks.CARPETS, this::basic);
			forType(MugannBlocks.WALLPAPERS, this::basic);
			forTypeNamed(MugannBlocks.RUGS, this::terracottaLike);
		}

		protected <T extends Block> void forTypeNamed(MugannBlocks.BlockType<T> type, BiConsumer<String, T> builder) {
			Arrays.stream(type.types).forEach(name -> builder.accept(name, type.get(name)));
		}

		protected <T extends Block> void forType(MugannBlocks.BlockType<T> type, Consumer<T> builder) {
			Arrays.stream(type.types).map(type::get).forEach(builder);
		}

		protected void basic(Block block) {
			simpleBlockWithItem(block, cubeAll(block));
		}

		protected void terracottaLike(String name, Block block) {
			ModelFile model = models().withExistingParent(name, mcLoc("template_glazed_terracotta"))
					.texture(TextureSlot.PATTERN.getId(), blockTexture(block));
			horizontalBlock(block, model);
			simpleBlockItem(block, model);
		}


//		private void moulding(Block block, BlockModelGenerators blockModelGenerators) {
//			TextureMapping mapping = TextureMapping.defaultTexture(block);
//			String id = "";
//			ModelFile.ExistingModelFile mouldingTemplateUpper = models().getExistingFile(Mugann.id("block/directional_slab_top"));
//
//
//			// default points north
//			// valence, horizontal, ...
//			getVariantBuilder(block)
//					.partialState().with(OrientableSlab.ORIENTATION, Orientation.HORIZONTAL)
//					.modelForState().
//
//
//			MultiVariant modelLower = BlockModelGenerators.plainVariant(mouldingTemplateLower.createWithSuffix(block, "_lower", mapping, m));
//			MultiVariant modelUpper = BlockModelGenerators.plainVariant(mouldingTemplateUpper.createWithSuffix(block, "_upper", mapping, blockModelGenerators.modelOutput));
//			blockModelGenerators.blockStateOutput.accept(
//					MultiVariantGenerator.dispatch(block)
//							.with(PropertyDispatch.initial(MouldingBlock.HALF).select(Half.BOTTOM, modelLower).select(Half.TOP, modelUpper))
//							.with(PropertyDispatch.modify(MouldingBlock.FACING)
//									.select(Direction.NORTH, BlockModelGenerators.NOP)
//									.select(Direction.EAST, BlockModelGenerators.Y_ROT_90)
//									.select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180)
//									.select(Direction.WEST, BlockModelGenerators.Y_ROT_270)
//							)
//			);
//			blockModelGenerators.registerSimpleItemModel(block, ModelLocationUtils.getModelLocation(block, "_lower"));
//		}
	}
}
