package symbolics.division.mugann.fabric;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import org.apache.commons.lang3.NotImplementedException;
import symbolics.division.mugann.Platform;

import java.util.function.Supplier;

public class MugannPlatformFabric implements Platform {
	private static <T> T proxy() {
		throw new NotImplementedException("This method should be handled by the xplat wrapper.");
	}

	@Override
	public CreativeModeTab.Builder creativeTabBuilder() {
		return FabricItemGroup.builder();
	}

	@Override
	public <T, E extends T> Supplier<E> register(ResourceKey<Registry<T>> registry, ResourceLocation key, Supplier<E> entry) {
		return proxy();
	}
}
