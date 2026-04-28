package symbolics.division.mugann;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class GommageEffect extends Entity {
	public Vec3 target;

	public GommageEffect(EntityType<?> type, Level level) {
		super(type, level);
		this.target = new Vec3(5, 3, 1);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return PathfinderMob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 5)
				.add(Attributes.TEMPT_RANGE, 10)
				.add(Attributes.MOVEMENT_SPEED, 0.3);
	}

	@Override
	public void tick() {
		super.tick();

		this.target = new Vec3(5 + 5 * Mth.sin(((float) level().getGameTime()) / 10f), 3, 1);

		this.lookAt(EntityAnchorArgument.Anchor.EYES, this.position().add(target));


		if (this.level().getGameTime() % 1 == 0) {
			double length = target.length();
			Vec3 dir = target.scale(1 / length);
			Vec3 pos = this.position().add(dir.scale(length * random.nextFloat()));
			this.level().addParticle(ParticleTypes.HEART, pos.x, pos.y, pos.z, 0, 0, 0);
		}
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder entityData) {

	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
		return false;
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {

	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {

	}
}
