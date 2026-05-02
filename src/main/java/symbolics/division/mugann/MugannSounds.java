package symbolics.division.mugann;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public class MugannSounds {
	public static void init() {

	}

	public static final SoundEvent SUN = Registry.register(BuiltInRegistries.SOUND_EVENT, Mugann.id("sun"),
			SoundEvent.createVariableRangeEvent(Mugann.id("sun")));
}
