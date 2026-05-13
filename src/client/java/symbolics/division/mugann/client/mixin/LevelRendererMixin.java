package symbolics.division.mugann.client.mixin;

import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

//	@Unique
//	private boolean mugann$internalized(Player player) {
//		if (player != null) {
//			Vec3 p = player.getEyePosition();
//			BlockPos bp = new BlockPos(Mth.floor(p.x), Mth.floor(p.y), Mth.floor(p.z));
//			var state = player.level().getBlockState(bp);
//			if (state.is(MugannTags.Blocks.HYPOTHETICAL)) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	@Inject(
//			method = "extractLevel",
//			at = @At("HEAD"),
//			cancellable = true
//	)
//	public void extractttt(final DeltaTracker deltaTracker, final Camera camera, final float deltaPartialTick, CallbackInfo ci) {
////		if (mugann$internalized(Minecraft.getInstance().player)) {
////			ci.cancel();
////		}
//	}
}
