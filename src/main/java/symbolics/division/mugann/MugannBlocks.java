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

	public static final String[] ladderTypes = {
			"askew", "inlaid", "rigid"
	};
	public static final Map<String, Block> LADDERS = new HashMap<>();

	static {
		for (String id : ladderTypes) LADDERS.put(id, ladder(id));
	}

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

	// Thank you fabric wiki copyright fabric wiki Attribution-NonCommercial-ShareAlike 4.0 International
	private static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties settings, boolean shouldRegisterItem) {
		ResourceKey<Block> blockKey = blockKey(name);
		Block block = blockFactory.apply(settings.setId(blockKey));
		if (shouldRegisterItem) {
			ResourceKey<Item> itemKey = itemKey(name);
			BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
			Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
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

	private static Block grimoire(String id) {
		return register(
				"grimoire_" + id,
				GrimoireBlock::new,
				BlockBehaviour.Properties.of(),
				true
		);
	}

	private static Block grimoireVert(String id) {
		return register(
				"grimoire_" + id + "_vertical",
				VerticalGrimoireBlock::new,
				BlockBehaviour.Properties.of(),
				true
		);
	}

	private static Block ladder(String id) {
		return register(
				"ladder_" + id,
				LadderBlock::new,
				BlockBehaviour.Properties.of().forceSolidOff().strength(0.4f).sound(SoundType.LADDER).noOcclusion().pushReaction(PushReaction.DESTROY),
				true
		);
	}

	public static final ResourceKey<CreativeModeTab> CREATIVE_TAB_KEY = ResourceKey.create(
			BuiltInRegistries.CREATIVE_MODE_TAB.key(), Mugann.id("items")
	);

	public static final CreativeModeTab CREATIVE_TAB = FabricCreativeModeTab.builder()
			.icon(() -> new ItemStack(GRIMS.get("album")))
			.title(Component.translatable("creativeTab.mugann"))
			.displayItems((params, output) -> {
				for (Block b : ALL_BLOCKS) output.accept(b.asItem().getDefaultInstance());
			})
			.build();

	public static void init() {
		for (FluidState state : SOURCE_MOKSHA.getStateDefinition().getPossibleStates()) {
			Fluid.FLUID_STATE_REGISTRY.add(state);
		}
		for (FluidState state : FLOWING_MOKSHA.getStateDefinition().getPossibleStates()) {
			Fluid.FLUID_STATE_REGISTRY.add(state);
		}
//		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, CREATIVE_TAB_KEY, CREATIVE_TAB);
	}
}
