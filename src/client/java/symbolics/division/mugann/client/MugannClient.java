package symbolics.division.mugann.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import symbolics.division.mugann.GommageEffect;
import symbolics.division.mugann.Mugann;

public class MugannClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ParticleProviderRegistry.getInstance().register(Mugann.GOMMAGE_PARTICLE, GommageParticle.Provider::new);
		ParticleProviderRegistry.getInstance().register(Mugann.SHATTER_PARTICLE, GommageParticle.DashProvider::new);
		EntityRenderers.register(Mugann.SLICE, SliceRenderer::new);

//		HudElementRegistry.replaceElement(VanillaHudElements.EXPERIENCE_LEVEL, OmaeWaRenderer::replaceBar);
//		HudStatusBarHeightRegistry.addLeft(VanillaHudElements.HEALTH_BAR, (p) -> 10);
//		HudStatusBarHeightRegistry.addRight(VanillaHudElements.HEALTH_BAR, (p) -> 10);


//		HudStatusBarHeightRegistry.addLeft(Mugann.id("fate_bar"), HudStatusBarHeightRegistryImpl.);
	}

	public static final class ShatterParticle extends SimpleAnimatedParticle {
		private final float rotSpeed;

		ShatterParticle(ClientLevel level, double x, double y, double z, double xa, double ya, double za, SpriteSet sprites, float gravity, RandomSource random) {
			super(level, x, y, z, sprites, gravity);
			this.xd = xa;
			this.yd = ya;
			this.zd = za; // initial speed
			this.rotSpeed = (this.random.nextFloat() - 0.5F) * 0.1F;
			this.roll = this.random.nextFloat() * (float) (Math.PI * 2);

			this.lifetime = (int) (this.random.nextFloat() * 10.0F) + 30;
			this.sprite = sprites.get(random);
		}

		@Override
		public void tick() {
			super.tick();
			this.oRoll = this.roll;
			this.roll = (float) (this.roll + Math.PI * this.rotSpeed);
		}

		public static class Provider implements ParticleProvider<SimpleParticleType> {
			private final SpriteSet sprites;

			public Provider(final SpriteSet sprites) {
				this.sprites = sprites;
			}

			public Particle createParticle(
					final SimpleParticleType options,
					final ClientLevel level,
					final double x,
					final double y,
					final double z,
					final double xAux,
					final double yAux,
					final double zAux,
					final RandomSource random
			) {
				return new ShatterParticle(level, x, y, z, xAux, yAux, zAux, this.sprites, 0, random);
			}
		}
	}

	public static class GommageParticle extends FallingLeavesParticle {
		protected float xDir = 0;
		protected float yDir = 0;
		protected float zDir = 0;
		protected boolean shatter = false;

		protected GommageParticle(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite, float fallAcceleration, float sideAcceleration, boolean swirl, boolean flowAway, float scale, float startVelocity) {
			super(level, x, y, z, sprite, fallAcceleration, sideAcceleration, swirl, flowAway, scale, startVelocity);
			this.gravity = -0.0001f;
		}

		protected GommageParticle(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite, float fallAcceleration, float sideAcceleration, boolean swirl, boolean flowAway, float scale, float startVelocity, float xDir, float yDir, float zDir) {
			this(level, x, y, z, sprite, fallAcceleration, sideAcceleration, swirl, flowAway, scale, startVelocity);
			this.xDir = xDir + random.nextFloat() * 0.5f;
			this.yDir = yDir + random.nextFloat() * 0.5f;
			this.zDir = zDir + random.nextFloat() * 0.2f;
			this.xd = xDir * 0.1F;
			this.yd = yDir * 0.1F;
			this.zd = zDir * 0.1F;
			shatter = true;
		}

		@Override
		public void tick() {
			super.tick();
			if (shatter) {
				this.xd = this.xDir * 0.1F;
				this.yd = this.yDir * 0.1F;
				this.zd = this.zDir * 0.1F;
			}
		}


		public static class Provider implements ParticleProvider<SimpleParticleType> {
			private final SpriteSet sprites;

			public Provider(final SpriteSet sprites) {
				this.sprites = sprites;
			}

			public Particle createParticle(
					final SimpleParticleType options,
					final ClientLevel level,
					final double x,
					final double y,
					final double z,
					final double xAux,
					final double yAux,
					final double zAux,
					final RandomSource random
			) {
				return new GommageParticle(level, x, y, z, this.sprites.get(random), 0.001F, 0.5F, true, true, 2.0F, 0.0F);
			}
		}

		public static class DashProvider implements ParticleProvider<Mugann.ShatterParticleOptions> {
			private final SpriteSet sprites;

			public DashProvider(final SpriteSet sprites) {
				this.sprites = sprites;
			}

			public Particle createParticle(
					final Mugann.ShatterParticleOptions options,
					final ClientLevel level,
					final double x,
					final double y,
					final double z,
					final double xAux,
					final double yAux,
					final double zAux,
					final RandomSource random
			) {
				return new GommageParticle(level, x, y, z, this.sprites.get(random), 2F, 0.5F, false, false, 2.0F, 0.0F, (float) options.dir().x, (float) options.dir().y, (float) options.dir().z);
			}
		}

	}

	public static final class SliceRenderState extends EntityRenderState {
		public float bodyRot;
		public float yRot;
		public float xRot;
		public Vec3 target;
	}

	public static final class SliceRenderer extends EntityRenderer<GommageEffect, SliceRenderState> {

		Identifier TEXTURE = Identifier.withDefaultNamespace("textures/entity/end_portal/end_gateway_beam.png");

		SliceRenderer(EntityRendererProvider.Context context) {
			super(context);
		}

		@Override
		public boolean shouldRender(GommageEffect entity, Frustum culler, double camX, double camY, double camZ) {
			return true;
		}

		@Override
		public void submit(SliceRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
			poseStack.pushPose();
			poseStack.mulPose(
					Axis.XP.rotationDegrees(state.xRot).mul(Axis.YP.rotationDegrees(90 - state.bodyRot))
			);

			var length = state.target.length();

			double thin = 0.03f;
			poseStack.mulPose(Axis.YP.rotationDegrees(90));
			poseStack.mulPose(Axis.ZP.rotationDegrees(state.ageInTicks * 100));
			poseStack.translate(-thin / 2f, -thin / 2f, -length);

			poseStack.pushPose();

			for (int i = 0; i < 4; i++) {
				poseStack.pushPose();

				poseStack.scale((float) thin, (float) thin, (float) length);
				submitNodeCollector.submitCustomGeometry(
						poseStack,
						RenderTypes.beaconBeam(TEXTURE, false),
						this::draw
				);
				poseStack.popPose();

				poseStack.mulPose(Axis.ZP.rotationDegrees(90f));
				poseStack.translate(0, -thin, 0);
			}

			poseStack.popPose();


			poseStack.popPose();
			super.submit(state, poseStack, submitNodeCollector, camera);
		}

		@Override
		public SliceRenderState createRenderState() {
			return new SliceRenderState();
		}

		@Override
		public void extractRenderState(GommageEffect entity, SliceRenderState state, float partialTicks) {
			super.extractRenderState(entity, state, partialTicks);
			state.yRot = entity.getYHeadRot();
			state.xRot = entity.getXRot(partialTicks);
			state.bodyRot = entity.getYRot(partialTicks);
			state.target = entity.target;
		}

		private void draw(final PoseStack.Pose pose, VertexConsumer builder) {
			addVertex(pose, builder, 0xFFFFFF, 0, 0, 0, 0, 0);
			addVertex(pose, builder, 0xFFFFFF, 0, 0, 1, 0, 1);
			addVertex(pose, builder, 0xFFFFFF, 0, 1, 1, 1, 1);
			addVertex(pose, builder, 0xFFFFFF, 0, 1, 0, 1, 0);
		}


		private static void renderPart(
				final PoseStack.Pose pose,
				final VertexConsumer builder,
				final int color,
				final int beamStart,
				final int beamEnd,
				final float wnx,
				final float wnz,
				final float enx,
				final float enz,
				final float wsx,
				final float wsz,
				final float esx,
				final float esz,
				final float uu1,
				final float uu2,
				final float vv1,
				final float vv2
		) {
			renderQuad(pose, builder, color, beamStart, beamEnd, wnx, wnz, enx, enz, uu1, uu2, vv1, vv2);
			renderQuad(pose, builder, color, beamStart, beamEnd, esx, esz, wsx, wsz, uu1, uu2, vv1, vv2);
			renderQuad(pose, builder, color, beamStart, beamEnd, enx, enz, esx, esz, uu1, uu2, vv1, vv2);
			renderQuad(pose, builder, color, beamStart, beamEnd, wsx, wsz, wnx, wnz, uu1, uu2, vv1, vv2);
		}

		private static void renderQuad(
				final PoseStack.Pose pose,
				final VertexConsumer builder,
				final int color,
				final int beamStart,
				final int beamEnd,
				final float wnx,
				final float wnz,
				final float enx,
				final float enz,
				final float uu1,
				final float uu2,
				final float vv1,
				final float vv2
		) {
			addVertex(pose, builder, color, beamEnd, wnx, wnz, uu2, vv1);
			addVertex(pose, builder, color, beamStart, wnx, wnz, uu2, vv2);
			addVertex(pose, builder, color, beamStart, enx, enz, uu1, vv2);
			addVertex(pose, builder, color, beamEnd, enx, enz, uu1, vv1);
		}

		private static void addVertex(
				final PoseStack.Pose pose, final VertexConsumer builder, final int color, final float x, final float y, final float z, final float u, final float v
		) {
			builder.addVertex(pose, x, y, z)
					.setColor(color)
					.setUv(u, v)
					.setOverlay(OverlayTexture.NO_OVERLAY)
					.setLight(15728880)
					.setNormal(pose, 0.0F, 1.0F, 0.0F);
		}
	}

	public static boolean displaced() {
		return Minecraft.getInstance().player.getAttached(Mugann.DISPLACE) != null;
	}

}