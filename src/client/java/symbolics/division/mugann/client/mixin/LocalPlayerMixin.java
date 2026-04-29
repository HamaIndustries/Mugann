package symbolics.division.mugann.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import symbolics.division.mugann.Mugann;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
	public LocalPlayerMixin(ClientLevel level, GameProfile gameProfile) {
		super(level, gameProfile);
	}

	@WrapOperation(
			method = "applyInput",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Input;jump()Z")
	)
	public boolean ground(Input instance, Operation<Boolean> original) {
		return original.call(instance) && !this.hasAttached(Mugann.CURSE);
	}
}
