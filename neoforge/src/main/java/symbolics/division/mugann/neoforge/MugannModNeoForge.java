package symbolics.division.mugann.neoforge;

import net.neoforged.fml.common.Mod;
import symbolics.division.mugann.Mugann;

@Mod(Mugann.ID)
public final class MugannModNeoForge {
	public MugannModNeoForge() {
		// Run our common setup.
		Mugann.init(new MugannPlatformNeoForge());
	}
}
