package symbolics.division.mugann;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Supplier;

public interface Platform {
	CreativeModeTab.Builder creativeTabBuilder();

	<T, E extends T> Supplier<E> register(ResourceKey<Registry<T>> registry, ResourceLocation key, Supplier<E> entry);
}
