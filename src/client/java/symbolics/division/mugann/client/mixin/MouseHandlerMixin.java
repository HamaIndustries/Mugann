package symbolics.division.mugann.client.mixin;

import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
//	@Inject(
//			method = "turnPlayer",
//			at = @At("HEAD"),
//			cancellable = true
//	)
//	public void unturn(final double mousea, CallbackInfo ci) {
//		if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.hasAttached(Mugann.FALSE_CURSE)) {
//			ci.cancel();
//		}
//	}
}
