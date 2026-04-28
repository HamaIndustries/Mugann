package symbolics.division.mugann;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class MugannTags {
	public static void init() {
	}

	public static class Blocks {
		public static final TagKey<Block> HYPOTHETICAL = TagKey.create(Registries.BLOCK, Mugann.id("hypothetical"));
	}
}
