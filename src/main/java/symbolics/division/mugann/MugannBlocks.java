package symbolics.division.mugann;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import symbolics.division.mugann.block.CurtainBlock;
import symbolics.division.mugann.block.DharmakirtianBlock;
import symbolics.division.mugann.block.GrimoireBlock;
import symbolics.division.mugann.block.VerticalGrimoireBlock;

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

	public static void init() {

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
