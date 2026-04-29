package symbolics.division.mugann.client.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import symbolics.division.mugann.Mugann;
import symbolics.division.mugann.client.MugannClient;

@Mixin(Camera.class)
public abstract class CameraMixin {
	//
	@Shadow
	@Nullable
	private Entity entity;

	@Shadow
	private boolean detached;

	@Shadow
	protected abstract void setPosition(Vec3 vec3);

	@Shadow
	protected abstract void setRotation(float f, float g);

	@Shadow
	private Vec3 position;

	@Shadow
	public abstract float getCameraEntityPartialTicks(DeltaTracker deltaTracker);

	@Shadow
	private float yRot;
	@Shadow
	private float xRot;

	@Unique
	Vec3 oldCameraPosition;
	@Unique
	Vec2 oldCameraRotation;
	@Unique
	float lastTimeSinceLastTick;


	@Unique
	private static Vec3 overridePosition;
	@Unique
	private static Vec2 overrideRotation;

	@Inject(method = "update", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Camera;hudFov:F", shift = At.Shift.AFTER, opcode = Opcodes.PUTFIELD))
	void setup(DeltaTracker deltaTracker, CallbackInfo ci) {
		float partialTicks = getCameraEntityPartialTicks(deltaTracker);
		lastTimeSinceLastTick = partialTicks;
		mugann$tickPosition(partialTicks);
		mugann$tickRotation(partialTicks);
	}

	@Unique
	void mugann$tickRotation(float timeSinceLastTick) {
		if (!MugannClient.displaced() || oldCameraRotation == null) return;
		setRotation(overrideRotation.y, overrideRotation.x);
		var newRotation = mugann$lerpRotation(oldCameraRotation, overrideRotation, timeSinceLastTick);
		setRotation(newRotation.y, newRotation.x);
	}


	@Unique
	void mugann$tickPosition(float timeSinceLastTick) {
		if (!MugannClient.displaced() || oldCameraPosition == null) return;
		detached = true;
		var newPosition = oldCameraPosition.lerp(overridePosition, timeSinceLastTick);
		setPosition(newPosition);

	}

	// source: Tomate, Map Utils, whatever the license is there
	@Inject(method = "tick", at = @At("HEAD"))
	void tick(CallbackInfo ci) {
		lastTimeSinceLastTick = 0;

		oldCameraPosition = overridePosition;
		oldCameraRotation = overrideRotation;

		Vec3 disp = entity.getAttached(Mugann.DISPLACE);
		if (disp != null) {
			this.detached = true;
			overridePosition = disp;
			overrideRotation = entity.getEyePosition().subtract(disp).rotation();
		} else {
			return;
		}

		if (oldCameraPosition == null) {
			oldCameraPosition = position;
		}

		if (oldCameraRotation == null) {
			oldCameraRotation = new Vec2(yRot, xRot);
		}
	}

	@Unique
	private static Vec2 mugann$lerpRotation(Vec2 start, Vec2 end, float t) {
		return new Vec2(Mth.rotLerp(t, start.x, end.x), Mth.rotLerp(t, start.y, end.y));
	}
}
