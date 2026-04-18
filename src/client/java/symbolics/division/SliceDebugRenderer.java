package symbolics.division;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugValueAccess;

import static symbolics.division.MugannTemp.SLICES;

public class SliceDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    private static final int JUMP_TARGET_LINE_COLOR = ARGB.color(255, 255, 100, 255);
    private static final int TARGET_LINE_COLOR = ARGB.color(255, 100, 255, 255);
    private static final int INNER_CIRCLE_COLOR = ARGB.color(255, 0, 255, 0);
    private static final int MIDDLE_CIRCLE_COLOR = ARGB.color(255, 255, 165, 0);
    private static final int OUTER_CIRCLE_COLOR = ARGB.color(255, 255, 0, 0);


    private final Minecraft minecraft;


    public SliceDebugRenderer(final Minecraft minecraft) {
        this.minecraft = minecraft;
    }


    @Override
    public void emitGizmos(
            final double camX, final double camY, final double camZ, final DebugValueAccess debugValues, final Frustum frustum, final float partialTicks
    ) {
        ClientLevel level = this.minecraft.level;
        debugValues.forEachEntity(
                SLICES,
                (entity, info) -> {
                    info.jumpTarget().ifPresent(blockPos -> {
                        Gizmos.arrow(entity.position(), blockPos, JUMP_TARGET_LINE_COLOR);
//                        Gizmos.cuboid(AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(blockPos)), GizmoStyle.fill(ARGB.colorFromFloat(1.0F, 1.0F, 0.0F, 0.0F)));
                    });
                }
        );
    }
}
