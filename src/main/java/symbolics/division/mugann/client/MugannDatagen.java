package symbolics.division.mugann.client;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
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
import java.util.concurrent.CompletableFuture;
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
				new MugannBlockStates(output, existingFileHelper)
		);
	}

	public static class MugannBlockStates extends BlockStateProvider {
		// https://docs.neoforged.net/docs/1.21.1/resources/client/models/datagen
		public MugannBlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
			super(output, Mugann.ID, exFileHelper);
		}

		@Override
		protected void registerStatesAndModels() {
			forType(MugannBlocks.CARPETS, this::basic);
			forType(MugannBlocks.WALLPAPERS, this::basic);
			forType(MugannBlocks.RUGS, this::horizontalWithItem);
		}

		protected <T extends Block> void forType(MugannBlocks.BlockType<T> type, Consumer<T> builder) {
			Arrays.stream(type.types).map(type::get).forEach(builder);
		}

		protected void basic(Block block) {
			simpleBlockWithItem(block, cubeAll(block));
		}

		protected void horizontalWithItem(Block block) {
			ModelFile model = cubeAll(block);
			horizontalBlock(block, model);
			simpleBlockItem(block, model);
		}
	}
}
