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

    public void render(int int_1, int int_2, float float_1) {
        this.renderBackground();
        int int_3 = this.getScrollbarPosition();
        int int_4 = int_3 + 6;
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        Tessellator tessellator_1 = Tessellator.getInstance();
        BufferBuilder bufferBuilder_1 = tessellator_1.getBuilder();
        this.minecraft.getTextureManager().bind(GuiElement.BACKGROUND_LOCATION);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        int int_5 = this.getRowLeft();
        int int_6 = this.minY + 4 - (int)this.getScrollAmount();

        this.renderList(int_5, int_6, int_1, int_2, float_1);
        GlStateManager.disableDepthTest();
        this.renderHoleBackground(0, this.minY, 255, 255);
        this.renderHoleBackground(this.maxY, this.height, 255, 255);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        GlStateManager.disableAlphaTest();
        GlStateManager.shadeModel(7425);
        GlStateManager.disableTexture();
        bufferBuilder_1.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferBuilder_1.vertex((double)this.minX, (double)(this.minY + 4), 0.0D).texture(0.0D, 1.0D).color(0, 0, 0, 0).nextVertex();
        bufferBuilder_1.vertex((double)this.maxX, (double)(this.minY + 4), 0.0D).texture(1.0D, 1.0D).color(0, 0, 0, 0).nextVertex();
        bufferBuilder_1.vertex((double)this.maxX, (double)this.minY, 0.0D).texture(1.0D, 0.0D).color(0, 0, 0, 255).nextVertex();
        bufferBuilder_1.vertex((double)this.minX, (double)this.minY, 0.0D).texture(0.0D, 0.0D).color(0, 0, 0, 255).nextVertex();
        bufferBuilder_1.vertex((double)this.minX, (double)this.maxY, 0.0D).texture(0.0D, 1.0D).color(0, 0, 0, 255).nextVertex();
        bufferBuilder_1.vertex((double)this.maxX, (double)this.maxY, 0.0D).texture(1.0D, 1.0D).color(0, 0, 0, 255).nextVertex();
        bufferBuilder_1.vertex((double)this.maxX, (double)(this.maxY - 4), 0.0D).texture(1.0D, 0.0D).color(0, 0, 0, 0).nextVertex();
        bufferBuilder_1.vertex((double)this.minX, (double)(this.maxY - 4), 0.0D).texture(0.0D, 0.0D).color(0, 0, 0, 0).nextVertex();
        tessellator_1.end();
        int int_8 = Math.max(0, this.getMaxPosition() - (this.maxY - this.minY - 4));
        if (int_8 > 0) {
            int int_9 = (int)((float)((this.maxY - this.minY) * (this.maxY - this.minY)) / (float)this.getMaxPosition());
            int_9 = MathHelper.clamp(int_9, 32, this.maxY - this.minY - 8);
            int int_10 = (int)this.getScrollAmount() * (this.maxY - this.minY - int_9) / int_8 + this.minY;
            if (int_10 < this.minY) {
                int_10 = this.minY;
            }

            bufferBuilder_1.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferBuilder_1.vertex((double)int_3, (double)this.maxY, 0.0D).texture(0.0D, 1.0D).color(0, 0, 0, 255).nextVertex();
            bufferBuilder_1.vertex((double)int_4, (double)this.maxY, 0.0D).texture(1.0D, 1.0D).color(0, 0, 0, 255).nextVertex();
            bufferBuilder_1.vertex((double)int_4, (double)this.minY, 0.0D).texture(1.0D, 0.0D).color(0, 0, 0, 255).nextVertex();
            bufferBuilder_1.vertex((double)int_3, (double)this.minY, 0.0D).texture(0.0D, 0.0D).color(0, 0, 0, 255).nextVertex();
            bufferBuilder_1.vertex((double)int_3, (double)(int_10 + int_9), 0.0D).texture(0.0D, 1.0D).color(128, 128, 128, 255).nextVertex();
            bufferBuilder_1.vertex((double)int_4, (double)(int_10 + int_9), 0.0D).texture(1.0D, 1.0D).color(128, 128, 128, 255).nextVertex();
            bufferBuilder_1.vertex((double)int_4, (double)int_10, 0.0D).texture(1.0D, 0.0D).color(128, 128, 128, 255).nextVertex();
            bufferBuilder_1.vertex((double)int_3, (double)int_10, 0.0D).texture(0.0D, 0.0D).color(128, 128, 128, 255).nextVertex();
            bufferBuilder_1.vertex((double)int_3, (double)(int_10 + int_9 - 1), 0.0D).texture(0.0D, 1.0D).color(192, 192, 192, 255).nextVertex();
            bufferBuilder_1.vertex((double)(int_4 - 1), (double)(int_10 + int_9 - 1), 0.0D).texture(1.0D, 1.0D).color(192, 192, 192, 255).nextVertex();
            bufferBuilder_1.vertex((double)(int_4 - 1), (double)int_10, 0.0D).texture(1.0D, 0.0D).color(192, 192, 192, 255).nextVertex();
            bufferBuilder_1.vertex((double)int_3, (double)int_10, 0.0D).texture(0.0D, 0.0D).color(192, 192, 192, 255).nextVertex();
            tessellator_1.end();
        }

        this.renderDecorations(int_1, int_2);
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
