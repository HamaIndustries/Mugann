package symbolics.division.mugann;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;
import pet.sylveon.petsful.event.PlayerPetCallback;

public class MugannCompat {
	public static void init() {
		if (FabricLoader.getInstance().isModLoaded("petsful")) {
			PlayerPetCallback.EVENT.register((player, entity) -> {
				if (entity instanceof ServerPlayer && player.getUUID().equals(Mugann.FIXED_POINT)) {
					Mugann.gommage(player, false);
				}
			});
		}
	}
}
