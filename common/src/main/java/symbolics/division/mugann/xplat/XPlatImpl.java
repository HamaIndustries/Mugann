package symbolics.division.mugann.xplat;

import dev.architectury.registry.registries.RegistrarManager;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import symbolics.division.mugann.Mugann;
import symbolics.division.mugann.Platform;

import java.util.function.Function;
import java.util.function.Supplier;

// wrapper for architectury API because I don't want to get pinned down by it
// i love abstraction i love abstraction

// tldr, put as much architectury in here as possible,
// and proxy to the actual platform if it doesnt exist
public class XPlatImpl implements Platform {
	private final RegistrarManager REGISTRIES = RegistrarManager.get(Mugann.ID);

	private final Platform wrapped;

	public XPlatImpl(Platform wrappedPlatform) {
		this.wrapped = wrappedPlatform;
	}

	@Override
	public CreativeModeTab.Builder creativeTabBuilder() {
		return wrapped.creativeTabBuilder();
	}

	@Override
	public <T, E extends T> Supplier<E> register(ResourceKey<Registry<T>> registry, ResourceLocation key, Supplier<E> entry) {
		return REGISTRIES.get(registry).register(key, entry);
	}

	public <T, E extends T> void registerBlockItem(ResourceLocation key, Function<Block, Item> callback) {
		REGISTRIES.get(Registries.BLOCK).delegate(key).listen(block ->
				REGISTRIES.get(Registries.ITEM).register(key, () -> callback.apply(block))
		);
	}
}
