package symbolics.division.mugann;

import net.minecraft.resources.ResourceLocation;
import symbolics.division.mugann.registry.MugannBlocks;
import symbolics.division.mugann.xplat.XPlatImpl;

public final class Mugann {
    public static final String ID = "mugann";
    private static Platform platform;

    public static ResourceLocation id(String value) {
        return ResourceLocation.fromNamespaceAndPath(ID, value);
    }

    public static void init(Platform wrappedPlatform) {
        Mugann.platform = new XPlatImpl(wrappedPlatform);

        MugannBlocks.init();
    }

    public static Platform platform() {
        if (platform == null) throw new RuntimeException("Cross-platform API was requested before it was initialized.");
        return platform;
    }
}
