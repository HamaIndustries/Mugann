package symbolics.division.mugann.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import symbolics.division.mugann.block.property.Valence;

import static net.minecraft.world.phys.shapes.Shapes.or;

public class MouldingBlock extends OrientableSlab {
	protected static final VoxelShape NNN = Block.box(0.0, 0.0, 0.0, 8.0, 8.0, 8.0);
	protected static final VoxelShape NNP = Block.box(0.0, 0.0, 8.0, 8.0, 8.0, 16.0);
	protected static final VoxelShape NPN = Block.box(0.0, 8.0, 0.0, 8.0, 16.0, 8.0);
	protected static final VoxelShape NPP = Block.box(0.0, 8.0, 8.0, 8.0, 16.0, 16.0);
	protected static final VoxelShape PNN = Block.box(8.0, 0.0, 0.0, 16.0, 8.0, 8.0);
	protected static final VoxelShape PNP = Block.box(8.0, 0.0, 8.0, 16.0, 8.0, 16.0);
	protected static final VoxelShape PPN = Block.box(8.0, 8.0, 0.0, 16.0, 16.0, 8.0);
	protected static final VoxelShape PPP = Block.box(8.0, 8.0, 8.0, 16.0, 16.0, 16.0);

	protected static final VoxelShape BOTTOM_NORTH = or(NNN, PNN);
	protected static final VoxelShape BOTTOM_EAST = or(NNN, NNP);
	protected static final VoxelShape BOTTOM_SOUTH = or(NNP, PNP);
	protected static final VoxelShape BOTTOM_WEST = or(PNP, PNN);
	protected static final VoxelShape TOP_NORTH = or(NPN, PPN);
	protected static final VoxelShape TOP_EAST = or(NPN, NPP);
	protected static final VoxelShape TOP_SOUTH = or(NPP, PPP);
	protected static final VoxelShape TOP_WEST = or(PPP, PPN);
	protected static final VoxelShape NORTH_WEST = or(NNN, NPN);
	protected static final VoxelShape NORTH_EAST = or(PNN, PPN);
	protected static final VoxelShape SOUTH_WEST = or(NNP, NPP);
	protected static final VoxelShape SOUTH_EAST = or(PNP, PPP);

	// vert horz
	// sin dex amb
	// nesw (by plane)

	// down, then facing

	// swne by data2d
	protected static final VoxelShape[] HORIZONTAL_SHAPES = {
			BOTTOM_SOUTH, BOTTOM_EAST, BOTTOM_NORTH, BOTTOM_WEST,
			TOP_SOUTH, TOP_EAST, TOP_NORTH, TOP_WEST
	};

	// sinister-dextral, then swne
	// dextral is just sinister + 1
	protected static final VoxelShape[] VERTICAL_SHAPES = {
			SOUTH_EAST, SOUTH_WEST, NORTH_WEST, NORTH_EAST, SOUTH_EAST
	};

	public MouldingBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected @NotNull VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
		Valence val = blockState.getValue(VALENCE);
		Direction dir = blockState.getValue(FACING);
		if (val == Valence.AMBIVALENT) return Shapes.block();
		return switch (blockState.getValue(ORIENTATION)) {
			case HORIZONTAL -> HORIZONTAL_SHAPES[val.ordinal() * 4 + dir.get2DDataValue()];
			case VERTICAL -> VERTICAL_SHAPES[val.ordinal() + dir.get2DDataValue()];
		};
	}

	@Override
	protected boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
		return false;
	}
}
