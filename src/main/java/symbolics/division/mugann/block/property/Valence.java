package symbolics.division.mugann.block.property;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public enum Valence implements StringRepresentable {
	// sinister = bottom if horizontal, left if vertical
	SINISTER("sinister"),
	DEXTRAL("dextral"),
	AMBIVALENT("ambivalent");

	public static final EnumProperty<Valence> PROPERTY = EnumProperty.create("valence", Valence.class);

	private final String name;

	Valence(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}

	public Direction relative(Direction reference) {
		return switch (this) {
			case SINISTER -> reference.getCounterClockWise();
			case DEXTRAL -> reference.getClockWise();
			default -> reference;
		};
	}
}