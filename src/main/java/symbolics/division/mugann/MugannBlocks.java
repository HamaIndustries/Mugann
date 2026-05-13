package symbolics.division.mugann;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import symbolics.division.mugann.block.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MugannBlocks {

	public static final List<Block> ALL_BLOCKS = new ArrayList<>();
	public static final List<Item> ALL_ITEMS = new ArrayList<>();

	public static class BlockType<T extends Block> {
		public final String[] types;
		public final HashMap<String, T> blocks = new HashMap<>();

		public BlockType(
				String prefix,
				Function<BlockBehaviour.Properties, T> factory,
				BlockBehaviour.Properties properties,
				String... types) {
			this.types = types;
			for (String id : types) blocks.put(id, register(prefix + "_" + id, factory, properties, true));
		}

		public BlockType(String prefix,
						 Function<BlockBehaviour.Properties, T> factory,
						 String... types) {
			this(prefix, factory, BlockBehaviour.Properties.of(), types);
		}
	}

	public static final Block RED_CURTAIN = register("red_curtain",
			properties -> new CurtainBlock(BlockSetType.CRIMSON, properties),
			BlockBehaviour.Properties.ofFullCopy(Blocks.CRIMSON_TRAPDOOR),
			true
	);

	public static final Block HYPERTHETICAL_DHARMAKIRTIAN = register(
			"hyperthetical_dharmakirtian",
			DharmakirtianBlock::new,
			BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK),
			true
	);

	public static final Block HYPOTHETICAL_DHARMAKIRTIAN = register(
			"hypothetical_dharmakirtian",
			DharmakirtianBlock::new,
			BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK).noCollision(),
			true
	);

	public static final String[] grimTypes = {
			"album", "botany", "eye", "key", "library", "tattered", "tome", "written", "insightful"
	};

	public static final Map<String, Block> GRIMS = new HashMap<>();
	public static final Map<String, Block> VGRIMS = new HashMap<>();

	static {
		for (String id : grimTypes) {
			GRIMS.put(id, grimoire(id));
			VGRIMS.put(id, grimoireVert(id));
		}
	}

	public static final BlockType<LadderBlock> LADDERS = new BlockType<>(
			"ladder", LadderBlock::new, Blocks.LADDER.properties(),
			"askew", "inlaid", "rigid"
	);

	public static final BlockType<MouldingBlock> MOULDINGS = new BlockType<>(
			"moulding", MouldingBlock::new, Blocks.DARK_OAK_STAIRS.properties(),
			"antique", "classical", "esoteric"
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

	public static final FlowingFluid SOURCE_MOKSHA = Registry.register(
			BuiltInRegistries.FLUID, Mugann.id("moksha_residue"), new MokshaResidue.Source()
	);

	public static final FlowingFluid FLOWING_MOKSHA = Registry.register(
			BuiltInRegistries.FLUID, Mugann.id("flowing_moksha_residue"), new MokshaResidue.Flowing()
	);

	public static final Block MOKSHA = register(
			"moksha_residue",
			properties -> new LiquidBlock(SOURCE_MOKSHA, properties),
			BlockBehaviour.Properties.of()
					.mapColor(DyeColor.BLACK)
					.replaceable()
//					.noCollision()
					.strength(100)
					.pushReaction(PushReaction.DESTROY)
					.noLootTable()
					.liquid()
					.sound(SoundType.EMPTY),
			false
	);

	public static final Block DEEPEST_SECRET = register(
			"deepest_secret", Block::new,
			BlockBehaviour.Properties.of()
					.noOcclusion()
					.noCollision()
			, false
	);

	// Thank you fabric wiki copyright fabric wiki Attribution-NonCommercial-ShareAlike 4.0 International
	private static <T extends Block> T register(String name, Function<BlockBehaviour.Properties, T> blockFactory, BlockBehaviour.Properties settings, boolean shouldRegisterItem) {
		ResourceKey<Block> blockKey = blockKey(name);
		T block = blockFactory.apply(settings.setId(blockKey));
		if (shouldRegisterItem) {
			ResourceKey<Item> itemKey = itemKey(name);
			BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
			Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
			ALL_ITEMS.add(blockItem);
		}

		ALL_BLOCKS.add(block);
		return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
	}

	private static ResourceKey<Block> blockKey(String name) {
		return ResourceKey.create(Registries.BLOCK, Mugann.id(name));
	}

	private static ResourceKey<Item> itemKey(String name) {
		return ResourceKey.create(Registries.ITEM, Mugann.id(name));
	}

	private static GrimoireBlock grimoire(String id) {
		return register(
				"grimoire_" + id,
				GrimoireBlock::new,
				BlockBehaviour.Properties.of(),
				true
		);
	}

	private static VerticalGrimoireBlock grimoireVert(String id) {
		return register(
				"grimoire_" + id + "_vertical",
				VerticalGrimoireBlock::new,
				BlockBehaviour.Properties.of(),
				true
		);
	}

	public static final ResourceKey<CreativeModeTab> CREATIVE_TAB_KEY = ResourceKey.create(
			BuiltInRegistries.CREATIVE_MODE_TAB.key(), Mugann.id("items")
	);

	public static final CreativeModeTab CREATIVE_TAB = FabricCreativeModeTab.builder()
			.icon(() -> new ItemStack(GRIMS.get("album"), 1))
			.title(Component.translatable("creativeTab.mugann"))
			.displayItems((params, output) -> {
				output.acceptAll(ALL_ITEMS.stream().map(Item::getDefaultInstance).toList());
			})
			.build();

	public static void init() {
		for (FluidState state : SOURCE_MOKSHA.getStateDefinition().getPossibleStates()) {
			Fluid.FLUID_STATE_REGISTRY.add(state);
		}
		for (FluidState state : FLOWING_MOKSHA.getStateDefinition().getPossibleStates()) {
			Fluid.FLUID_STATE_REGISTRY.add(state);
		}
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, CREATIVE_TAB_KEY, CREATIVE_TAB);
	}
}
