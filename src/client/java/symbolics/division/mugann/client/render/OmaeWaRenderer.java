package symbolics.division.mugann.client.render;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import symbolics.division.mugann.Mugann;

public class OmaeWaRenderer implements ContextualBarRenderer, HudElement {
	private static final Identifier EXPERIENCE_BAR_BACKGROUND_SPRITE = Mugann.id("hud/experience_bar_background");
	private static final Identifier EXPERIENCE_BAR_PROGRESS_SPRITE = Mugann.id("hud/fate_bar_progress");
	private final Minecraft minecraft;

	public OmaeWaRenderer(final Minecraft minecraft) {
		this.minecraft = minecraft;
	}
  
	@Override
	public void extractBackground(final GuiGraphicsExtractor graphics, final DeltaTracker deltaTracker) {
		LocalPlayer player = this.minecraft.player;
		int left = this.left(this.minecraft.getWindow());
		int top = this.top(this.minecraft.getWindow());
		float progress = player.getAttachedOrElse(Mugann.CURSE, 0f);
		if (progress > 0) {
			int bar = (int) (progress * 183.0F);
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, EXPERIENCE_BAR_BACKGROUND_SPRITE, left, top, 182, 5);
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, EXPERIENCE_BAR_PROGRESS_SPRITE, 182, 5, 0, 0, left, top, bar, 5);
		}
	}

	@Override
	public void extractRenderState(final GuiGraphicsExtractor graphics, final DeltaTracker deltaTracker) {
	}

	private static OmaeWaRenderer instance = null;

	public static HudElement replaceBar(HudElement element) {
		if (instance == null) {
			instance = new OmaeWaRenderer(Minecraft.getInstance());
		}
		LocalPlayer player = Minecraft.getInstance().player;
		if (player.getAttachedOrElse(Mugann.CURSE, 0f) > 0) {
			return instance;
		}
		return element;
	}
}
