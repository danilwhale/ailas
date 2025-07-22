package mcp.mobius.waila.gui.config;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tessellator;
import mcp.mobius.waila.gui.GuiOptions;
import mcp.mobius.waila.gui.config.value.OptionsEntryValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.gui.widget.ObjectListWidget;
import net.minecraft.util.math.MathHelper;

import java.util.Collections;
import java.util.List;

public class OptionsListWidget extends ObjectListWidget<OptionsListWidget.Entry> {

    private final GuiOptions owner;
    private final Runnable diskWriter;

    public OptionsListWidget(GuiOptions owner, Minecraft client, int x, int height, int width, int y, int entryHeight, Runnable diskWriter) {
        super(client, x, height, width, y, entryHeight);

        this.owner = owner;
        this.diskWriter = diskWriter;
    }

    public OptionsListWidget(GuiOptions owner, Minecraft client, int x, int height, int width, int y, int entryHeight) {
        this(owner, client, x, height, width, y, entryHeight, null);
    }

    @Override
    public int getRowWidth() {
        return 250;
    }

    public void render(int mouseX, int mouseY, float delta) {
        int scrollPosX = this.getScrollbarPosition();
        int j = scrollPosX + 6;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        this.minecraft.getTextureManager().bind(GuiElement.BACKGROUND_LOCATION);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        int rowLeft = this.getRowLeft();
        int scrollJump = this.minY + 4 - (int)this.getScrollAmount();

        this.renderList(rowLeft, scrollJump, mouseX, mouseY, delta);
		this.minecraft.getTextureManager().bind(GuiElement.BACKGROUND_LOCATION);
		GlStateManager.enableDepthTest();
		GlStateManager.depthFunc(519);
		bufferBuilder.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
		bufferBuilder.vertex(this.minX, this.minY, -100.0D).texture(0.0F, (float)this.minY / 32.0F).color(64, 64, 64, 255).nextVertex();
		bufferBuilder.vertex((this.minX + this.width), this.minY, -100.0D).texture((float)this.width / 32.0F, (float)this.minY / 32.0F).color(64, 64, 64, 255).nextVertex();
		bufferBuilder.vertex((this.minX + this.width), 0.0D, -100.0D).texture((float)this.width / 32.0F, 0.0F).color(64, 64, 64, 255).nextVertex();
		bufferBuilder.vertex(this.minX, 0.0D, -100.0D).texture(0.0F, 0.0F).color(64, 64, 64, 255).nextVertex();
		bufferBuilder.vertex(this.minX, this.height, -100.0D).texture(0.0F, (float)this.height / 32.0F).color(64, 64, 64, 255).nextVertex();
		bufferBuilder.vertex((this.minX + this.width), this.height, -100.0D).texture((float)this.width / 32.0F, (float)this.height / 32.0F).color(64, 64, 64, 255).nextVertex();
		bufferBuilder.vertex((this.minX + this.width), this.maxY, -100.0D).texture((float)this.width / 32.0F, (float)this.maxY / 32.0F).color(64, 64, 64, 255).nextVertex();
		bufferBuilder.vertex(this.minX, this.maxY, -100.0D).texture(0.0F, (float)this.maxY / 32.0F).color(64, 64, 64, 255).nextVertex();
		tessellator.end();
		GlStateManager.depthFunc(515);
		GlStateManager.disableDepthTest();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        GlStateManager.disableAlphaTest();
        GlStateManager.shadeModel(7425);
        GlStateManager.disableTexture();
		bufferBuilder.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
		bufferBuilder.vertex(this.minX, (this.minY + 4), 0.0D).texture(0.0F, 1.0F).color(0, 0, 0, 0).nextVertex();
		bufferBuilder.vertex(this.maxX, (this.minY + 4), 0.0D).texture(1.0F, 1.0F).color(0, 0, 0, 0).nextVertex();
		bufferBuilder.vertex(this.maxX, this.minY, 0.0D).texture(1.0F, 0.0F).color(0, 0, 0, 255).nextVertex();
		bufferBuilder.vertex(this.minX, this.minY, 0.0D).texture(0.0F, 0.0F).color(0, 0, 0, 255).nextVertex();
		bufferBuilder.vertex(this.minX, this.maxY, 0.0D).texture(0.0F, 1.0F).color(0, 0, 0, 255).nextVertex();
		bufferBuilder.vertex(this.maxX, this.maxY, 0.0D).texture(1.0F, 1.0F).color(0, 0, 0, 255).nextVertex();
		bufferBuilder.vertex(this.maxX, (this.maxY - 4), 0.0D).texture(1.0F, 0.0F).color(0, 0, 0, 0).nextVertex();
		bufferBuilder.vertex(this.minX, (this.maxY - 4), 0.0D).texture(0.0F, 0.0F).color(0, 0, 0, 0).nextVertex();
		tessellator.end();
		int o = Math.max(0, this.getMaxPosition() - (this.maxY - this.minY - 4));
		if (o > 0) {
			int p = (int)((float)((this.maxY - this.minY) * (this.maxY - this.minY)) / (float)this.getMaxPosition());
			p = MathHelper.clamp(p, 32, this.maxY - this.minY - 8);
			int q = (int)this.getScrollAmount() * (this.maxY - this.minY - p) / o + this.minY;
			if (q < this.minY) {
				q = this.minY;
            }

			bufferBuilder.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
			bufferBuilder.vertex(scrollPosX, this.maxY, 0.0D).texture(0.0F, 1.0F).color(0, 0, 0, 255).nextVertex();
			bufferBuilder.vertex(j, this.maxY, 0.0D).texture(1.0F, 1.0F).color(0, 0, 0, 255).nextVertex();
			bufferBuilder.vertex(j, this.minY, 0.0D).texture(1.0F, 0.0F).color(0, 0, 0, 255).nextVertex();
			bufferBuilder.vertex(scrollPosX, this.minY, 0.0D).texture(0.0F, 0.0F).color(0, 0, 0, 255).nextVertex();
			bufferBuilder.vertex(scrollPosX, (q + p), 0.0D).texture(0.0F, 1.0F).color(128, 128, 128, 255).nextVertex();
			bufferBuilder.vertex(j, (q + p), 0.0D).texture(1.0F, 1.0F).color(128, 128, 128, 255).nextVertex();
			bufferBuilder.vertex(j, q, 0.0D).texture(1.0F, 0.0F).color(128, 128, 128, 255).nextVertex();
			bufferBuilder.vertex(scrollPosX, q, 0.0D).texture(0.0F, 0.0F).color(128, 128, 128, 255).nextVertex();
			bufferBuilder.vertex(scrollPosX, (q + p - 1), 0.0D).texture(0.0F, 1.0F).color(192, 192, 192, 255).nextVertex();
			bufferBuilder.vertex((j - 1), (q + p - 1), 0.0D).texture(1.0F, 1.0F).color(192, 192, 192, 255).nextVertex();
			bufferBuilder.vertex((j - 1), q, 0.0D).texture(1.0F, 0.0F).color(192, 192, 192, 255).nextVertex();
			bufferBuilder.vertex(scrollPosX, q, 0.0D).texture(0.0F, 0.0F).color(192, 192, 192, 255).nextVertex();
			tessellator.end();
        }

        this.renderDecorations(mouseX, mouseY);
        GlStateManager.enableTexture();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlphaTest();
        GlStateManager.disableBlend();
    }

    public void save() {
        children()
                .stream()
                .filter(e -> e instanceof OptionsEntryValue)
                .map(e -> (OptionsEntryValue) e)
                .forEach(OptionsEntryValue::save);
        if (diskWriter != null)
            diskWriter.run();
    }

    public void add(Entry entry) {
        if (entry instanceof OptionsEntryValue) {
            GuiEventListener element = ((OptionsEntryValue) entry).getListener();
            if (element != null)
                owner.addListener(element);
        }
        addEntry(entry);
    }

    public abstract static class Entry extends ObjectListWidget.Entry<Entry> {

        protected final Minecraft client;

        public Entry() {
            this.client = Minecraft.getInstance();
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }

        @Override
        public abstract void render(int index, int rowTop, int rowLeft, int width, int height, int mouseX, int mouseY, boolean hovered, float deltaTime);
    }
}
