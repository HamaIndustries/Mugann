package symbolics.division.mugann.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class VerticalGrimoireBlock extends GrimoireBlock {

	protected static final VoxelShape NORTH = Block.box(0, 0, 0, 16, 16, 8);
	protected static final VoxelShape SOUTH = Block.box(0, 0, 8, 16, 16, 16);
	protected static final VoxelShape EAST = Block.box(8, 0, 0, 16, 16, 16);
	protected static final VoxelShape WEST = Block.box(0, 0, 0, 8, 16, 16);

	public VerticalGrimoireBlock(Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockPos p = BlockPos.containing(ctx.getClickLocation().add(ctx.getClickedFace().getUnitVec3().scale(0.01)));
		BlockState oldState = ctx.getLevel().getBlockState(p);

		if (oldState.is(this)) {
			return oldState.setValue(TYPE, SlabType.DOUBLE);
		}

		BlockState state = super.getStateForPlacement(ctx);
		Vec3 center = ctx.getClickedPos().getCenter();
		state = state.setValue(FACING, ctx.getNearestLookingDirection().getClockWise());

		if (state.getValue(FACING).getAxis() == Direction.Axis.X) {
			return ctx.getClickLocation().x > center.x
					? state.setValue(TYPE, SlabType.TOP)
					: state.setValue(TYPE, SlabType.BOTTOM);
		} else {
			return ctx.getClickLocation().z < center.z
					? state.setValue(TYPE, SlabType.TOP)
					: state.setValue(TYPE, SlabType.BOTTOM);
		}
	}

	@Override
	protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
		ItemStack itemStack = context.getItemInHand();
		SlabType slabType = state.getValue(TYPE);
		if (slabType != SlabType.DOUBLE && itemStack.is(this.asItem())) {
			return new AABB(context.getClickedPos()).contains(context.getClickLocation().subtract(context.getClickedFace().getUnitVec3().scale(-0.01)));
		} else {
			return false;
		}
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(TYPE)) {
			case DOUBLE -> Shapes.block();
			case TOP -> switch (state.getValue(FACING)) {
				case Direction.EAST -> EAST;
				default -> NORTH;
			};
			case BOTTOM -> switch (state.getValue(FACING)) {
				case Direction.EAST -> WEST;
				default -> SOUTH;
			};
		};
	}
}
