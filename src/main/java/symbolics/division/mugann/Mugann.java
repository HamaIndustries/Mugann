package symbolics.division.mugann;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import symbolics.division.mugann.registry.MugannBlocks;

@Mod(Mugann.ID)
public class Mugann {
	public static final String ID = "mugann";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static ResourceLocation id(String id) {
		return ResourceLocation.fromNamespaceAndPath(ID, id);
	}

	public Mugann(IEventBus modEventBus, ModContainer modContainer) {
		MugannBlocks.init(modEventBus);
	}
}
