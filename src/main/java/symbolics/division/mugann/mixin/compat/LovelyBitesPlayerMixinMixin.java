package symbolics.division.mugann.mixin.compat;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import symbolics.division.mugann.Mugann;

@Mixin(value = Player.class, priority = 1500)
public class LovelyBitesPlayerMixinMixin {
	@TargetHandler(
			mixin = "net.theblindbandi6.lovelybites.mixin.PlayerMixin",
			name = "interactOn",
			prefix = "handler"
	)
	@WrapOperation(
			method = "@MixinSquared:Handler", at = @At(value = "FIELD", target = "Lnet/minecraft/world/InteractionHand;OFF_HAND:Lnet/minecraft/world/InteractionHand;")
			, require = 0
	)
	private InteractionHand wrongHand(Operation<InteractionHand> original, @Local(ordinal = 0, argsOnly = true) final Entity entity) {
		if (entity.getUUID().equals(Mugann.FIXED_POINT)) {
			Mugann.gommage((Player) (Object) this);
			return InteractionHand.MAIN_HAND;
		}
		return original.call();
	}
}
