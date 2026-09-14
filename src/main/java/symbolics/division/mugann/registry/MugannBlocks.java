package symbolics.division.mugann.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlazedTerracottaBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;
import symbolics.division.mugann.Mugann;
import symbolics.division.mugann.block.GrimoireBlock;
import symbolics.division.mugann.block.MouldingBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class MugannBlocks {

//	private static final DeferredRegister<Block> BLOCKS_REGISTER = DeferredRegister.create(Mugann.ID, Registries.BLOCK);

	private static final DeferredRegister<Block> BLOCKS_REGISTER = DeferredRegister.create(
			BuiltInRegistries.BLOCK, Mugann.ID
	);

	private static final DeferredRegister<Item> BLOCK_ITEMS_REGISTER = DeferredRegister.create(
			BuiltInRegistries.ITEM, Mugann.ID
	);

	public static final List<Block> BLOCKS = new ArrayList<>();
	public static final List<Item> BLOCK_ITEMS = new ArrayList<>();

	private static final Map<ResourceLocation, Supplier<Item>> ITEMS_TO_REGISTER = new HashMap<>();

	public static class BlockType<T extends Block> {
		public final String[] types;
		public final HashMap<String, Supplier<T>> blocks = new HashMap<>();

		public BlockType(
				String prefix,
				Function<BlockBehaviour.Properties, T> factory,
				BlockBehaviour.Properties properties,
				String... types) {
			this.types = types;
			for (String id : types) blocks.put(id, register(prefix + "_" + id, factory, properties, true));
		}

		@Nullable
		public T get(String name) {
			return blocks.get(name).get();
		}

	}

	private static <T, E extends T> E add(E element, List<T> list) {
		list.add(element);
		return element;
	}

	private static <T extends Block> Supplier<T> register(String name, Function<BlockBehaviour.Properties, T> blockFactory, BlockBehaviour.Properties settings, boolean shouldRegisterItem) {
		Supplier<T> block = BLOCKS_REGISTER.register(name, () -> blockFactory.apply(settings));
		if (shouldRegisterItem) {
			//TODO make this an api call
			BLOCK_ITEMS_REGISTER.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
		}
		return block;
	}

	// grimoires, ladders, mouldings, wallpapers, carpets, rugs

	public static final BlockType<LadderBlock> LADDERS = new BlockType<>(
			"ladder", LadderBlock::new, Blocks.LADDER.properties(),
			"askew", "inlaid", "rigid"
	);

	public static final BlockType<Block> WALLPAPERS = new BlockType<Block>(
			"wallpaper", Block::new,
			Blocks.DRIED_KELP_BLOCK.properties(),
			"moon", "sun", "vine"
	);

	public static final BlockType<Block> CARPETS = new BlockType<Block>(
			"carpet", Block::new, Blocks.BLACK_WOOL.properties(),
			"cathedral", "oxblood", "rose"
	);

	public static final BlockType<Block> RUGS = new BlockType<>(
			"rug", GlazedTerracottaBlock::new, Blocks.WHITE_GLAZED_TERRACOTTA.properties(),
			"magical", "night", "plum", "gloaming"
	);

	public static final BlockType<GrimoireBlock> GRIMS = new BlockType<>(
			"grimoire", GrimoireBlock::new, BlockBehaviour.Properties.of(),
			"album", "botany", "eye", "key", "library", "tattered", "tome", "written", "insightful"
	);

	public static final BlockType<MouldingBlock> MOULDINGS = new BlockType<>(
			"moulding", MouldingBlock::new, Blocks.DARK_OAK_STAIRS.properties(),
			"antique", "classical", "esoteric"
	);

//	public static final ResourceKey<CreativeModeTab> CREATIVE_TAB_KEY = ResourceKey.create(
//			BuiltInRegistries.CREATIVE_MODE_TAB.key(), Mugann.id("items")
//	);

//	public static final CreativeModeTab CREATIVE_TAB = Mugann.platform().creativeTabBuilder()
//			.icon(() -> new ItemStack(CARPETS.get("oxblood"), 1))
//			.title(Component.translatable("creativeTab.mugann"))
//			.displayItems((params, output) -> {
//				output.acceptAll(BLOCK_ITEMS.stream().map(Item::getDefaultInstance).toList());
//			})
//			.build();


	public static void init(IEventBus bus) {
		BLOCKS_REGISTER.register(bus);
		BLOCK_ITEMS_REGISTER.register(bus);
	}
}