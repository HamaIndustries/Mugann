package symbolics.division.mugann.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.WaterFluid;
import org.jspecify.annotations.Nullable;
import symbolics.division.mugann.MugannBlocks;

import static symbolics.division.mugann.MugannBlocks.FLOWING_MOKSHA;
import static symbolics.division.mugann.MugannBlocks.SOURCE_MOKSHA;

public abstract class MokshaResidue extends WaterFluid {
	@Override
	public Fluid getFlowing() {
		return FLOWING_MOKSHA;
	}

	@Override
	public Fluid getSource() {
		return SOURCE_MOKSHA;
	}

	@Override
	public Item getBucket() {
		return Items.AIR;
	}

	@Override
	public @Nullable ParticleOptions getDripParticle() {
		return ParticleTypes.SQUID_INK;
	}

	@Override
	public BlockState createLegacyBlock(FluidState fluidState) {
		return MugannBlocks.MOKSHA.defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(fluidState));
	}

	@Override
	public boolean isSame(final Fluid other) {
		return other == SOURCE_MOKSHA || other == FLOWING_MOKSHA;
	}

	@Override
	public boolean canBeReplacedWith(final FluidState state, final BlockGetter level, final BlockPos pos, final Fluid other, final Direction direction) {
		return direction == Direction.DOWN && !isSame(other);
	}

	@Override
	public void animateTick(Level level, BlockPos pos, FluidState fluidState, RandomSource random) {
		if (random.nextInt(10) == 0 && level.getBlockState(pos.above()).isAir()) {
			level.addParticle(
					ParticleTypes.SQUID_INK, pos.getX() + random.nextDouble(), pos.getY() + random.nextDouble(), pos.getZ() + random.nextDouble(), 0.0, random.nextFloat() * 0.3 + 0.1, 0.0
			);
		}
		var p = level.getBlockState(pos);
	}

	public static class Flowing extends MokshaResidue {
		@Override
		protected void createFluidStateDefinition(final StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		@Override
		public int getAmount(final FluidState fluidState) {
			return (Integer) fluidState.getValue(LEVEL);
		}

		@Override
		public boolean isSource(final FluidState fluidState) {
			return false;
		}
	}

	public static class Source extends MokshaResidue {
		@Override
		public int getAmount(final FluidState fluidState) {
			return 8;
		}

		@Override
		public boolean isSource(final FluidState fluidState) {
			return true;
		}
	}
}
