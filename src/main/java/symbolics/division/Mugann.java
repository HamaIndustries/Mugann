package symbolics.division;

import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.item.component.PiercingWeapon;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
					entity, RANGE, e -> PiercingWeapon.canHitEntity(entity, e), ClipContext.Block.COLLIDER
			);

			level.playSound(null, entity.blockPosition(), SoundEvents.CREAKING_DEATH, SoundSource.PLAYERS, 4f, 1.8f);

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
					}
					Vec3 tp = entity.position().add(furthest.subtract(entity.position()).scale(1.5));
					entity.teleport(new TeleportTransition((ServerLevel) level, tp, Vec3.ZERO, entity.getYHeadRot(), entity.getXRot(), false, false, Set.of(), TeleportTransition.DO_NOTHING));
				});

				Vec3 newPos = entity.position().add(entity.getEyePosition().subtract(entity.position()).scale(0.5));
				Vec3 delta = newPos.subtract(prevPos);
				for (int i = 0; i < 100; i++) {
					Vec3 p = delta.scale(level.getRandom().nextFloat()).add(prevPos);
					double d = 0.5;
					((ServerLevel) level).sendParticles(ParticleTypes.CHERRY_LEAVES, p.x, p.y, p.z, 5, level.getRandom().nextFloat() * d, level.getRandom().nextFloat() * d, level.getRandom().nextFloat() * d, 0.1);
				}
			}
			return true;
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
	}

	public static final Item MUGANN = registerItem("blade", Blade::new, new Item.Properties().stacksTo(1));

	public static final SimpleParticleType SHATTER_PARTICLE = FabricParticleTypes.simple();
	public static final SimpleParticleType GOMMAGE_PARTICLE = FabricParticleTypes.simple();

	public static final EntityType<GommageEffect> SLICE = registerEntity(
			"slice",
			EntityType.Builder.<GommageEffect>of(GommageEffect::new, MobCategory.MISC)
					.sized(1, 1)
	);

	public static final AttachmentType<Float> CURSE = AttachmentRegistry.create(
			id("curse"),
			builder -> builder.persistent(Codec.FLOAT)
	);

	@Override
	public void onInitialize() {
		Registry.register(BuiltInRegistries.PARTICLE_TYPE, id("shatter"), SHATTER_PARTICLE);
		Registry.register(BuiltInRegistries.PARTICLE_TYPE, id("gommage"), GOMMAGE_PARTICLE);

		MugannTemp.init();
		ServerTickEvents.START_LEVEL_TICK.register(this::tickCurse);
	}

	public void tickCurse(ServerLevel level) {
		float cpt = 0.3f / 20.0f;
		for (var entity : level.getAllEntities()) {
			if (entity.hasAttached(CURSE)) {
				float v = entity.getAttached(CURSE);
				if (v > 1) {
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

//    public static void registerAttributes() {
//        FabricDefaultAttributeRegistry.register(SLICE, GommageEffect::createAttributes);
//    }

}