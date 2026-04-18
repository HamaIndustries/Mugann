package symbolics.division;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.debug.DebugSubscription;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class MugannTemp {
    public record DebugSliceInfo(Optional<Vec3> jumpTarget) {
        public static final StreamCodec<ByteBuf, DebugSliceInfo> STREAM_CODEC = StreamCodec.composite(
                Vec3.STREAM_CODEC.apply(ByteBufCodecs::optional),
                DebugSliceInfo::jumpTarget,
                DebugSliceInfo::new
        );
    }

    public static final DebugSubscription<DebugSliceInfo> SLICES = Registry.register(BuiltInRegistries.DEBUG_SUBSCRIPTION, Mugann.id("slices"), new DebugSubscription<>(DebugSliceInfo.STREAM_CODEC));

    public static void init() {

    }
}
