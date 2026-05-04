package symbolics.division.mugann.block;

import com.mojang.math.OctahedralGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class MouldingBlock extends Block {
	public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
	public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;

	public static final VoxelShape NORTH = Block.box(0, 0, 8, 16, 8, 16);
	public static final VoxelShape EAST = Shapes.rotate(NORTH, OctahedralGroup.BLOCK_ROT_Y_90);
	public static final VoxelShape SOUTH = Shapes.rotate(NORTH, OctahedralGroup.BLOCK_ROT_Y_180);
	public static final VoxelShape WEST = Shapes.rotate(NORTH, OctahedralGroup.BLOCK_ROT_Y_270);
	public static final VoxelShape NORTH_UP = Block.box(0, 8, 8, 16, 16, 16);
	public static final VoxelShape EAST_UP = Shapes.rotate(NORTH_UP, OctahedralGroup.BLOCK_ROT_Y_90);
	public static final VoxelShape SOUTH_UP = Shapes.rotate(NORTH_UP, OctahedralGroup.BLOCK_ROT_Y_180);
	public static final VoxelShape WEST_UP = Shapes.rotate(NORTH_UP, OctahedralGroup.BLOCK_ROT_Y_270);

	public MouldingBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(HALF, Half.BOTTOM));
	}

	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction clickedFace = context.getClickedFace();
		BlockPos pos = context.getClickedPos();
		return this.defaultBlockState()
				.setValue(FACING, context.getHorizontalDirection().getOpposite())
				.setValue(
						HALF, clickedFace != Direction.DOWN && (clickedFace == Direction.UP || !(context.getClickLocation().y - pos.getY() > 0.5)) ? Half.BOTTOM : Half.TOP
				);
	}

	@Override
	protected boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(HALF)) {
			case Half.TOP -> switch (state.getValue(FACING)) {
				case Direction.NORTH -> NORTH_UP;
				case Direction.EAST -> EAST_UP;
				case Direction.SOUTH -> SOUTH_UP;
				default -> WEST_UP;
			};
			case Half.BOTTOM -> switch (state.getValue(FACING)) {
				case Direction.NORTH -> NORTH;
				case Direction.EAST -> EAST;
				case Direction.SOUTH -> SOUTH;
				default -> WEST;
			};
		};
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, HALF);
	}
}
