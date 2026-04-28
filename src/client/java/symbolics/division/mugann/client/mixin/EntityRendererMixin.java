package symbolics.division.mugann.client.mixin;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import symbolics.division.mugann.Mugann;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
	@Inject(
			method = "shouldRender",
			at = @At("HEAD"),
			cancellable = true
	)
	public <E extends Entity> void shouldRender(final E entity, final Frustum culler, final double camX, final double camY, final double camZ, CallbackInfoReturnable<Boolean> ci) {
		if (entity.getAttached(Mugann.DISPLACE) != null) {
			ci.setReturnValue(true);
			ci.cancel();
		}
	}
}
