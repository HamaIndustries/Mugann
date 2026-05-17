package symbolics.division.mugann.client.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.GameRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import symbolics.division.mugann.Mugann;
import symbolics.division.mugann.MugannTags;
import symbolics.division.mugann.client.MugannClient;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	/*
	The MIT License (MIT)

	Copyright Tomate0613 (c) 2024

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in
	all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	THE SOFTWARE.
	 */

	@WrapOperation(method = "renderItemInHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;getPlayerMode()Lnet/minecraft/world/level/GameType;"))
	GameType shouldRenderHand(MultiPlayerGameMode instance, Operation<GameType> original) {
		if (MugannClient.displaced()) {
			return GameType.SPECTATOR;
		}

		return original.call(instance);
	}

	@Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
	void bobView(CameraRenderState cameraState, PoseStack poseStack, CallbackInfo ci) {
		if (MugannClient.displaced()) {
			ci.cancel();
		}
	}

	@Inject(method = "bobHurt", at = @At("HEAD"), cancellable = true)
	void bobHurt(CameraRenderState cameraState, PoseStack poseStack, CallbackInfo ci) {
		if (MugannClient.displaced()) {
			ci.cancel();
		}
	}

	@Inject(
			method = "extract",
			at = @At("HEAD"),
			cancellable = true
	)
	public void inject(final DeltaTracker deltaTracker, final boolean advanceGameTime, CallbackInfo ci) {
//		if (mugann$internalized(Minecraft.getInstance().player)) ci.cancel();
		if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.hasAttached(Mugann.FALSE_CURSE))
			ci.cancel();
	}

	@Unique
	private boolean mugann$internalized(Player player) {
		if (player != null) {
			Vec3 p = player.getEyePosition();
			BlockPos bp = new BlockPos(Mth.floor(p.x), Mth.floor(p.y), Mth.floor(p.z));
			var state = player.level().getBlockState(bp);
			if (state.is(MugannTags.Blocks.HYPOTHETICAL)) {
				return true;
			}
		}
		return false;
	}

	@Shadow
	@Final
	private FeatureRenderDispatcher featureRenderDispatcher;

//	@Inject(
//			method = "renderLevel",
//			at = @At("HEAD"),
//			cancellable = true
//	)
//	public void derender(final DeltaTracker deltaTracker, CallbackInfo ci) {
//		if (mugann$internalized(Minecraft.getInstance().player)) {
//			this.featureRenderDispatcher.clearSubmitNodes();
//			ci.cancel();
//		}
//	}

	@Shadow
	@Final
	private Minecraft minecraft;

	@Shadow
	@Final
	private GameRenderState gameRenderState;

	// thank uuuu iridith <33333
	@Definition(id = "shouldRenderLevel", local = @Local(type = boolean.class, name = "shouldRenderLevel"))
	@Expression("shouldRenderLevel")
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"),
			slice = @Slice(from = @At("MIXINEXTRAS:EXPRESSION"), to = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/fog/FogRenderer;endFrame()V")))
	private void fairyCollaboration(DeltaTracker deltaTracker, boolean advanceGameTime, CallbackInfo ci) {
		if (mugann$internalized(Minecraft.getInstance().player)) {
			RenderTarget mainRenderTarget = minecraft.getMainRenderTarget();
			// idk if this is necessary. GameRenderer doesn't bother checking
			if (mainRenderTarget.getColorTexture() != null && mainRenderTarget.getDepthTexture() != null) {
				// could probably just clear color..? but what does it really matter
				RenderSystem.getDevice()
						.createCommandEncoder()
						.clearColorAndDepthTextures(
								mainRenderTarget.getColorTexture(), gameRenderState.guiRenderState.clearColorOverride, mainRenderTarget.getDepthTexture(), 1.0
						);
			}
		}
	}

//	@Inject(
//			method = "tick",
//			at = @At("HEAD"),
//			cancellable = true
//	)
//	public void untick(CallbackInfo ci) {
//		if (mugann$internalized(Minecraft.getInstance().player)) ci.cancel();
//	}
//
//	@WrapOperation(
//			method = "render",
//			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isGameLoadFinished()Z")
//	)
//	public boolean unfinish(Minecraft instance, Operation<Boolean> original) {
//		return !mugann$internalized(instance.player) && original.call(instance);
//	}

}