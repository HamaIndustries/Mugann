package symbolics.division.mugann;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import symbolics.division.mugann.block.*;

import java.util.function.Function;

public class MugannBlocks {

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

	public static final Block GRIMOIRE = register(
			"grimoire",
			GrimoireBlock::new,
			BlockBehaviour.Properties.of(),
			true
	);
	public static final Block GRIMOIRE_VERTICAL = register(
			"grimoire_vertical",
			VerticalGrimoireBlock::new,
			BlockBehaviour.Properties.of(),
			true
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

	public static void init() {
		for (FluidState state : SOURCE_MOKSHA.getStateDefinition().getPossibleStates()) {
			Fluid.FLUID_STATE_REGISTRY.add(state);
		}
		for (FluidState state : FLOWING_MOKSHA.getStateDefinition().getPossibleStates()) {
			Fluid.FLUID_STATE_REGISTRY.add(state);
		}
	}

	// Thank you fabric wiki copyright fabric wiki Attribution-NonCommercial-ShareAlike 4.0 International
	private static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties settings, boolean shouldRegisterItem) {
		ResourceKey<Block> blockKey = blockKey(name);
		Block block = blockFactory.apply(settings.setId(blockKey));
		if (shouldRegisterItem) {
			ResourceKey<Item> itemKey = itemKey(name);
			BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
			Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
		}

		return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
	}

	private static ResourceKey<Block> blockKey(String name) {
		return ResourceKey.create(Registries.BLOCK, Mugann.id(name));
	}

	private static ResourceKey<Item> itemKey(String name) {
		return ResourceKey.create(Registries.ITEM, Mugann.id(name));
	}
}
