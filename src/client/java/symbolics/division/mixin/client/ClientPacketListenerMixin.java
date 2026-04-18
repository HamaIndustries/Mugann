package symbolics.division.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import symbolics.division.Mugann;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
	@WrapOperation(
			method = "handleEntityPositionSync",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D")
	)
	public double aw(Vec3 origin, Vec3 pos, Operation<Double> old, @Local(index = 0) Entity entity) {
		if (entity.getWeaponItem() != null && entity.getWeaponItem().is(Mugann.MUGANN) && pos.distanceToSqr(origin) > 5) {
			return 9001;
		}
		return old.call(origin, pos);
	}
}
