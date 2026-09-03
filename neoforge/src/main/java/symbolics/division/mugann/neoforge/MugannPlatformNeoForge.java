package symbolics.division.mugann.neoforge;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import org.apache.commons.lang3.NotImplementedException;
import symbolics.division.mugann.Platform;

public class MugannPlatformNeoForge implements Platform {
    private static <T> T proxy() {
        throw new NotImplementedException("This method should be handled by the xplat wrapper");
    }

    @Override
    public CreativeModeTab.Builder creativeTabBuilder() {
        return CreativeModeTab.builder();
    }

    @Override
    public <T, E extends T> E register(ResourceKey<Registry<T>> registry, ResourceLocation key, E entry) {
        return proxy();
    }
}
