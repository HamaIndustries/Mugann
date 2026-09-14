package symbolics.division.mugann.block.property;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public enum Orientation implements StringRepresentable {
	VERTICAL("vertical"),
	HORIZONTAL("horizontal");

	public static final EnumProperty<Orientation> PROPERTY = EnumProperty.create("orientation", Orientation.class);

	private final String name;

	Orientation(final String name) {
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
}