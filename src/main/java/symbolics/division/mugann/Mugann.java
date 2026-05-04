package symbolics.division.mugann;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.function.Function;

public class Mugann implements ModInitializer {
	public static final String MOD_ID = "mugann";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier id(String id) {
		return Identifier.fromNamespaceAndPath(MOD_ID, id);
	}

	public static final class Blade extends Item {
		private static final AttackRange RANGE = new AttackRange(1, 30, 1, 30, 1, 1);

		public Blade(Properties properties) {
			super(properties);
		}

		@Override
		public boolean releaseUsing(ItemStack itemStack, Level level, LivingEntity entity, int remainingTime) {
			var hit = ProjectileUtil.getHitEntitiesAlong(
					entity, RANGE, e -> true, ClipContext.Block.COLLIDER
			);

			level.playSound(null, entity.blockPosition(), SoundEvents.BELL_RESONATE, SoundSource.PLAYERS, 4f, 1.8f);

			if (!level.isClientSide()) {
				Vec3 prevPos = entity.position().add(entity.getEyePosition().subtract(entity.position()).scale(0.5));
				hit.ifLeft(blockHitResult -> {
					entity.teleport(new TeleportTransition((ServerLevel) level, blockHitResult.getLocation(), Vec3.ZERO, entity.getYHeadRot(), entity.getXRot(), false, false, Set.of(), TeleportTransition.DO_NOTHING));
				}).ifRight(entityHitResults -> {
					Vec3 furthest = entity.position();
					double furthestDist = 0;
					for (var target : entityHitResults) {
						double dist = target.getLocation().subtract(entity.position()).length();
						if (dist > furthestDist) {
							furthestDist = dist;
							furthest = target.getEntity().position();
						}
						target.getEntity().setAttached(CURSE, 0f);
						if (entity.isCrouching()) {
							target.getEntity().setAttached(HEALING, Unit.INSTANCE);
						}
						if (target.getEntity() instanceof ServerPlayer p) {
							setDisplacement(p);
						}
					}
					Vec3 tp = entity.position().add(furthest.subtract(entity.position()).scale(1.5));
					entity.teleport(new TeleportTransition((ServerLevel) level, tp, Vec3.ZERO, entity.getYHeadRot(), entity.getXRot(), false, false, Set.of(), TeleportTransition.DO_NOTHING));
				});

				Vec3 newPos = entity.position().add(entity.getEyePosition().subtract(entity.position()).scale(0.5));
				Vec3 delta = newPos.subtract(prevPos);
				double l = delta.length() * 2;
				for (int i = 0; i < l; i++) {
					Vec3 p = delta.scale(level.getRandom().nextFloat()).add(prevPos);
					double d = 0.5;

					((ServerLevel) level).sendParticles(new ShatterParticleOptions(delta.normalize().scale(-0.001)), p.x, p.y, p.z, 1, level.getRandom().nextFloat() * d, level.getRandom().nextFloat() * d, level.getRandom().nextFloat() * d, 0.0);
				}
			}
			return true;
		}

		public void setDisplacement(ServerPlayer player) {
			var result = ProjectileUtil.getHitResultOnViewVector(player, e -> false, 15);
			player.setAttached(DISPLACE, result.getLocation().add(0, 1, 0));
		}

		@Override
		public int getUseDuration(ItemStack itemStack, LivingEntity user) {
			return 72000;
		}

		public ItemUseAnimation getUseAnimation(final ItemStack itemStack) {
			return ItemUseAnimation.BOW;
		}

		@Override
		public InteractionResult use(final Level level, final Player player, final InteractionHand hand) {
			player.startUsingItem(hand);
			return InteractionResult.CONSUME;
		}

		@Override
		public void onUseTick(Level level, LivingEntity entity, ItemStack itemStack, int ticksRemaining) {
			super.onUseTick(level, entity, itemStack, ticksRemaining);
			var dir = entity.getLookAngle().scale(-1);
			if (ticksRemaining % 2 == 0) {
				if (level instanceof ServerLevel sv) {
					sv.sendParticles(new ShatterParticleOptions(dir), entity.getX(), entity.getY() + 0.3, entity.getZ(), 1, 1, 0.1, 1, 1);
				}
				if (ticksRemaining % 40 == 0) {
					entity.playSound(SoundEvents.CREAKING_HEART_HURT, 1, 0.2f);
				}
			}
		}

		@Override
		public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
			if (owner.tickCount % 5 == 0 && owner instanceof ServerPlayer player && !player.hasAttached(NOTHING_BORNE)) {
				player.getInventory().removeItem(itemStack);
				player.setAttached(CURSE, 0f);
				player.setAttached(HEALING, Unit.INSTANCE);
			}
		}
	}

	public static final Item MUGANN = registerItem("oceans_of_grief", Blade::new, new Item.Properties().stacksTo(1));

	public static final ParticleType<ShatterParticleOptions> SHATTER_PARTICLE = FabricParticleTypes.complex(
			ShatterParticleOptions.MAP_CODEC, ShatterParticleOptions.STREAM_CODEC
	);
	public static final SimpleParticleType GOMMAGE_PARTICLE = FabricParticleTypes.simple();
	public static final SimpleParticleType HEALING_PARTICLE = FabricParticleTypes.simple();

	public static final EntityType<GommageEffect> SLICE = registerEntity(
			"slice",
			EntityType.Builder.<GommageEffect>of(GommageEffect::new, MobCategory.MISC)
					.sized(1, 1)
	);

	public static final AttachmentType<Float> CURSE = AttachmentRegistry.create(
			id("curse"),
			builder -> builder.persistent(Codec.FLOAT).syncWith(ByteBufCodecs.FLOAT, AttachmentSyncPredicate.targetOnly())
	);

	public static final AttachmentType<Unit> HEALING = AttachmentRegistry.create(
			id("i_will_heal_you"),
			builder -> builder.syncWith(StreamCodec.unit(Unit.INSTANCE), AttachmentSyncPredicate.targetOnly())
	);

	public static final AttachmentType<Vec3> DISPLACE = AttachmentRegistry.create(
			id("displacement"), builder -> builder.syncWith(Vec3.STREAM_CODEC, AttachmentSyncPredicate.targetOnly())
	);

	public static final AttachmentType<Unit> NOTHING_BORNE = AttachmentRegistry.create(
			id("nothing"), builder -> builder.persistent(Unit.CODEC)
	);

	private static String REMEMBER_WHAT_WE_TOLD_EACH_OTHER = "Ds�\u0015��2�P?c���'�\u0017�Z&_&UqRS\"�#��Z";

	@Override
	public void onInitialize() {
		MugannBlocks.init();
		MugannTags.init();
		MugannSounds.init();
		Registry.register(BuiltInRegistries.PARTICLE_TYPE, id("shatter"), SHATTER_PARTICLE);
		Registry.register(BuiltInRegistries.PARTICLE_TYPE, id("gommage"), GOMMAGE_PARTICLE);
		Registry.register(BuiltInRegistries.PARTICLE_TYPE, id("healing"), HEALING_PARTICLE);
		ServerTickEvents.START_LEVEL_TICK.register(this::tickCurse);

		ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((playerChatMessage, serverPlayer, bound) -> {
			try {
				MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
				messageDigest.update(playerChatMessage.signedContent().getBytes());
				String hash = new String(messageDigest.digest());
				if (hash.equals(REMEMBER_WHAT_WE_TOLD_EACH_OTHER)) {
					serverPlayer.setAttached(NOTHING_BORNE, Unit.INSTANCE);
					return false;
				}
			} catch (NoSuchAlgorithmException e) {

			}
			return true;
		});
	}

	public void tickCurse(ServerLevel level) {
		float cpt = 0.03f / 20.0f;
		for (var entity : level.getAllEntities()) {
			if (entity == null) continue;
			if (entity.hasAttached(CURSE)) {
				float v = entity.getAttached(CURSE);
				if (entity.tickCount % 10 == 0) {
					level.sendParticles(entity.hasAttached(HEALING) ? HEALING_PARTICLE : GOMMAGE_PARTICLE, entity.getX(), entity.getY() + 1, entity.getZ(), (int) (2 * (1 + v) * (1 + v)), 0.3, 0.3, 0.3, 0.1);
					if (entity instanceof LivingEntity living) {
						((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 3, false, false), living);
						if (living instanceof ServerPlayer player) {
							player.setGameMode(GameType.ADVENTURE);
						}
					}
				}
				if (v > 1) {
					entity.teleportRelative(0, -1000, 0);
					entity.kill(level);
				} else {
					entity.setAttached(CURSE, v + cpt);
				}
			}
		}
	}

	private static Item registerItem(String id, final Function<Item.Properties, Item> itemFactory, final Item.Properties properties) {
		final ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id(id));
		Item item = (Item) itemFactory.apply(properties.setId(key));
		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}

	private static <T extends Entity> EntityType<T> registerEntity(String name, EntityType.Builder<T> builder) {
		ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, id(name));
		return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
	}

	public record ShatterParticleOptions(Vec3 dir) implements ParticleOptions {
		public static final MapCodec<ShatterParticleOptions> MAP_CODEC = RecordCodecBuilder.mapCodec(
				i -> i.group(
						Vec3.CODEC.fieldOf("dir").forGetter(ShatterParticleOptions::dir)
				).apply(i, ShatterParticleOptions::new)
		);

		public static final StreamCodec<? super ByteBuf, ShatterParticleOptions> STREAM_CODEC = StreamCodec.composite(
				Vec3.STREAM_CODEC, ShatterParticleOptions::dir, ShatterParticleOptions::new
		);

		@Override
		public ParticleType<?> getType() {
			return SHATTER_PARTICLE;
		}
	}

}