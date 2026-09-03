package symbolics.division.mugann.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LadderBlock;
import symbolics.division.mugann.Mugann;

public class RegistryUtil {

    // I could write an accesswidener. or I could... extend it...
    public static class MugannLadder extends LadderBlock {
        protected MugannLadder(Properties properties) {
            super(properties);
        }
    }

    public static ResourceKey<Block> blockKey(String name) {
        return ResourceKey.create(Registries.BLOCK, Mugann.id(name));
    }

    public static ResourceKey<Item> itemKey(String name) {
        return ResourceKey.create(Registries.ITEM, Mugann.id(name));
    }
}
