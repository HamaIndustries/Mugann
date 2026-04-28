package symbolics.division.mugann.block;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class CurtainBlock extends TrapDoorBlock {
	public CurtainBlock(BlockSetType type, Properties properties) {
		super(type, properties);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		// defaults up
		var state = super.getStateForPlacement(context);
		var player = context.getPlayer();
		if (player != null && player.isCrouching()) {
			return state;
		}
		return state.setValue(OPEN, true);
	}
}
