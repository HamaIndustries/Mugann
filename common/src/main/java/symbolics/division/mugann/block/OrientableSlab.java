package symbolics.division.mugann.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;


// horizontal or vertical slab, not necessarily with a direction
public abstract class OrientableSlab extends Block implements SimpleWaterloggedBlock {
	public static final EnumProperty<Valence> VALENCE = Valence.PROPERTY;
	public static final EnumProperty<Orientation> ORIENTATION = Orientation.PROPERTY;
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	// north: -z
	// west: -x
	// south points at you
	protected static final VoxelShape BOTTOM_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
	protected static final VoxelShape TOP_AABB = Block.box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape NORTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 8.0);
	protected static final VoxelShape SOUTH_AABB = Block.box(0.0, 0.0, 8.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape EAST_AABB = Block.box(8.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape WEST_AABB = Block.box(0.0, 0.0, 0.0, 8.0, 16.0, 16.0);

	protected OrientableSlab(BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState()
				.setValue(VALENCE, Valence.SINISTER)
				.setValue(ORIENTATION, Orientation.HORIZONTAL)
				.setValue(FACING, Direction.NORTH)
				.setValue(WATERLOGGED, false)
		);
	}

	@Override
	protected boolean useShapeForLightOcclusion(BlockState blockState) {
		return blockState.getValue(VALENCE) != Valence.AMBIVALENT;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(VALENCE, ORIENTATION, FACING, WATERLOGGED);
	}

	@Override
	protected VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
		return getShape(blockState);
	}

	protected VoxelShape getShape(BlockState blockState) {
		Valence val = blockState.getValue(VALENCE);
		Direction dir = blockState.getValue(FACING);
		if (val == Valence.AMBIVALENT) return Shapes.block();
		return switch (blockState.getValue(ORIENTATION)) {
			case HORIZONTAL -> val == Valence.SINISTER ? BOTTOM_AABB : TOP_AABB;
			case VERTICAL -> switch (val.relative(dir)) {
				case NORTH -> NORTH_AABB;
				case EAST -> EAST_AABB;
				case SOUTH -> SOUTH_AABB;
				default -> WEST_AABB;
			};
		};
	}

	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {

		BlockPos blockPos = blockPlaceContext.getClickedPos();
		BlockState existingBlockState = blockPlaceContext.getLevel().getBlockState(blockPos);
		if (existingBlockState.is(this)) {
			return existingBlockState.setValue(VALENCE, Valence.AMBIVALENT).setValue(WATERLOGGED, false);
		}

		FluidState fluidState = blockPlaceContext.getLevel().getFluidState(blockPos);

		BlockState blockState = this.defaultBlockState()
				.setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER)
				.setValue(FACING, blockPlaceContext.getHorizontalDirection());

		if (blockPlaceContext.isSecondaryUseActive()) { // vertical
			Vec3 loc = blockPlaceContext.getClickLocation();
			boolean xPositive = loc.x - blockPos.getX() > 0.5;
			boolean zPositive = loc.z - blockPos.getZ() > 0.5;
			return blockState.setValue(ORIENTATION, Orientation.VERTICAL).setValue(VALENCE,
					switch (blockPlaceContext.getHorizontalDirection()) {
						case NORTH -> xPositive ? Valence.DEXTRAL : Valence.SINISTER;
						case SOUTH -> xPositive ? Valence.SINISTER : Valence.DEXTRAL;
						case EAST -> zPositive ? Valence.DEXTRAL : Valence.SINISTER;
						default -> zPositive ? Valence.SINISTER : Valence.DEXTRAL;
					}
			);
		} else { // horizontal
			Direction direction = blockPlaceContext.getClickedFace();
			blockState = blockState.setValue(ORIENTATION, Orientation.HORIZONTAL);
			return direction != Direction.DOWN && (direction == Direction.UP || !(blockPlaceContext.getClickLocation().y - blockPos.getY() > 0.5))
					? blockState.setValue(VALENCE, Valence.SINISTER)
					: blockState.setValue(VALENCE, Valence.DEXTRAL);
		}
	}

	@Override
	protected boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
		ItemStack itemStack = blockPlaceContext.getItemInHand();
		Valence val = blockState.getValue(VALENCE);
		if (val == Valence.AMBIVALENT || !itemStack.is(this.asItem())) {
			return false;
		} else if (blockPlaceContext.replacingClickedOnBlock()) {
			Vec3 loc = blockPlaceContext.getClickLocation();
			BlockPos blockPos = blockPlaceContext.getClickedPos();
			var relative = loc.subtract(Vec3.atLowerCornerOf(blockPos)).scale(16);
			return !getShape(blockState).bounds().contains(relative);
//
//			Direction direction = blockPlaceContext.getClickedFace();
//			if (blockState.getValue(ORIENTATION) == Orientation.HORIZONTAL) {
//				boolean up = loc.y - blockPos.getY() > 0.5;
//				return val == Valence.SINISTER
//						? direction == Direction.UP || up && direction.getAxis().isHorizontal()
//						: direction == Direction.DOWN || !up && direction.getAxis().isHorizontal();
//			} else {
//				boolean xPositive = loc.x - blockPos.getX() > 0.5;
//				boolean zPositive = loc.z - blockPos.getZ() > 0.5;
//			}
		} else {
			return true;
		}
	}

	@Override
	protected FluidState getFluidState(BlockState blockState) {
		return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
	}

	@Override
	public boolean placeLiquid(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
		return blockState.getValue(VALENCE) != Valence.AMBIVALENT && SimpleWaterloggedBlock.super.placeLiquid(levelAccessor, blockPos, blockState, fluidState);
	}

	@Override
	public boolean canPlaceLiquid(@Nullable Player player, BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Fluid fluid) {
		return blockState.getValue(VALENCE) != Valence.AMBIVALENT && SimpleWaterloggedBlock.super.canPlaceLiquid(player, blockGetter, blockPos, blockState, fluid);
	}

	@Override
	protected BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
		if (blockState.getValue(WATERLOGGED)) {
			levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
		}

		return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
	}

	@Override
	protected boolean isPathfindable(BlockState blockState, PathComputationType pathComputationType) {
		return pathComputationType == PathComputationType.WATER && blockState.getFluidState().is(FluidTags.WATER);
	}
}
