package symbolics.division.mugann.fabric;

import net.fabricmc.api.ModInitializer;
import symbolics.division.mugann.Mugann;

public final class MugannModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        Mugann.init(new MugannPlatformFabric());
    }
}
