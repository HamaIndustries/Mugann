package symbolics.division.mugann.xplat;

import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrarManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import symbolics.division.mugann.Mugann;
import symbolics.division.mugann.Platform;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

// wrapper for architectury API because I don't want to get pinned down by it
// i love abstraction i love abstraction

// tldr, put as much architectury in here as possible,
// and proxy to the actual platform if it doesnt exist
public class XPlatImpl implements Platform {

    // https://github.com/architectury/architectury-templates/blob/master/templates/api_1_20/common/src/main/java/net/examplemod/ExampleMod.java
    private static final Supplier<RegistrarManager> REGISTRIES = Suppliers.memoize(() -> RegistrarManager.get(Mugann.ID));

    // :D
    private static final Map<ResourceKey<Registry<?>>, DeferredRegister<?>> deferredRegisters = new HashMap<>();

    @SuppressWarnings("unchecked")
    private static <T> DeferredRegister<T> getRegister(ResourceKey<Registry<T>> registryKey) {
        // :D :D :D
        return (DeferredRegister<T>) deferredRegisters.computeIfAbsent((ResourceKey<Registry<?>>) (Object) registryKey, r -> DeferredRegister.create(Mugann.ID, registryKey));
    }

    private final Platform wrapped;

    public XPlatImpl(Platform wrappedPlatform) {
        this.wrapped = wrappedPlatform;
    }

    @Override
    public CreativeModeTab.Builder creativeTabBuilder() {
        return wrapped.creativeTabBuilder();
    }

    @Override
    public <T, E extends T> E register(ResourceKey<Registry<T>> registry, ResourceLocation key, E entry) {
        RegistrarManager.get(Mugann.ID).forRegistry(registry, reg -> reg.register(key, () -> entry));
        return entry;
    }
}
