package symbolics.division.mugann.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import symbolics.division.mugann.Mugann;

public class RegistryUtil {
	public static ResourceKey<Block> blockKey(String name) {
		return ResourceKey.create(Registries.BLOCK, Mugann.id(name));
	}

	public static ResourceKey<Item> itemKey(String name) {
		return ResourceKey.create(Registries.ITEM, Mugann.id(name));
	}
}