package mcp.mobius.waila.overlay;

import com.google.common.collect.Lists;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.ITaggableList;
import mcp.mobius.waila.api.RenderableTextComponent;
import mcp.mobius.waila.api.event.WailaTooltipEvent;
import mcp.mobius.waila.api.impl.DataAccessor;
import mcp.mobius.waila.api.impl.TaggableList;
import mcp.mobius.waila.api.impl.TaggedTextComponent;
import mcp.mobius.waila.api.impl.config.WailaConfig;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.text.Text;
import net.minecraft.resource.Identifier;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;

public class Tooltip {

    private final Minecraft client;
    private final List<Line> lines;
    private final boolean showItem;
    private final Dimension totalSize;

    public Tooltip(List<Text> components, boolean showItem) {
        WailaTooltipEvent event = new WailaTooltipEvent(components, DataAccessor.INSTANCE);
        WailaTooltipEvent.WAILA_HANDLE_TOOLTIP.invoker().onTooltip(event);

        this.client = Minecraft.getInstance();
        this.lines = Lists.newArrayList();
        this.showItem = showItem;
        this.totalSize = new Dimension();

        computeLines(components);
        addPadding();
    }

    public void computeLines(List<Text> components) {
        components.forEach(c -> {
            Dimension size = getLineSize(c, components);
            totalSize.setSize(Math.max(totalSize.width, size.width), totalSize.height + size.height);
            Text Text = c;
            if (Text instanceof TaggedTextComponent)
                Text = ((ITaggableList<Identifier, Text>) components).getTag(((TaggedTextComponent) Text).getTag());

            lines.add(new Line(Text, size));
        });
    }

    public void addPadding() {
        totalSize.width += hasItem() ? 30 : 10;
        totalSize.height += 8;
    }

    public void draw() {
        Rectangle position = getPosition();
        WailaConfig.ConfigOverlay.ConfigOverlayColor color = Waila.CONFIG.get().getOverlay().getColor();

        position.x += hasItem() ? 26 : 6;
        position.width += hasItem() ? 24 : 4;
        position.y += 6;

        for (Line line : lines) {
            if (line.getComponent() instanceof RenderableTextComponent) {
                RenderableTextComponent Text = (RenderableTextComponent) line.getComponent();
                int xOffset = 0;
                for (RenderableTextComponent.RenderContainer container : Text.getRenderers()) {
                    Dimension size = container.getRenderer().getSize(container.getData(), DataAccessor.INSTANCE);
                    container.getRenderer().draw(container.getData(), DataAccessor.INSTANCE, position.x + xOffset, position.y);
                    xOffset += size.width;
                }
            } else {
                client.textRenderer.drawWithShadow(line.getComponent().getFormattedString(), position.x, position.y, color.getFontColor());
            }
            position.y += line.size.height;
        }
    }

    private Dimension getLineSize(Text Text, List<Text> components) {
        if (Text instanceof RenderableTextComponent) {
            RenderableTextComponent renderable = (RenderableTextComponent) Text;
            List<RenderableTextComponent.RenderContainer> renderers = renderable.getRenderers();
            if (renderers.isEmpty())
                return new Dimension(0, 0);

            int width = 0;
            int height = 0;
            for (RenderableTextComponent.RenderContainer container : renderers) {
                Dimension iconSize = container.getRenderer().getSize(container.getData(), DataAccessor.INSTANCE);
                width += iconSize.width;
                height = Math.max(height, iconSize.height);
            }

            return new Dimension(width, height);
        } else if (Text instanceof TaggedTextComponent) {
            TaggedTextComponent tagged = (TaggedTextComponent) Text;
            if (components instanceof TaggableList) {
                Text taggedLine = ((TaggableList<Identifier, Text>) components).getTag(tagged.getTag());
                return taggedLine == null ? new Dimension(0, 0) : getLineSize(taggedLine, components);
            }
        }

        return new Dimension(client.textRenderer.getWidth(Text.getFormattedString()), client.textRenderer.fontHeight + 1);
    }

    public List<Line> getLines() {
        return lines;
    }

    public boolean hasItem() {
        return showItem && Waila.CONFIG.get().getGeneral().shouldShowItem() && !RayTracing.INSTANCE.getIdentifierStack().isEmpty();
    }

    public Rectangle getPosition() {
        Window window = Minecraft.getInstance().window;
        return new Rectangle(
                (int) (window.getGuiScaledWidth() * Waila.CONFIG.get().getOverlay().getOverlayPosX() - totalSize.width / 2), // Center it
                (int) (window.getGuiScaledHeight() * (1.0F - Waila.CONFIG.get().getOverlay().getOverlayPosY())),
                totalSize.width,
                totalSize.height
        );
    }

    public static class Line {

        private final Text Text;
        private final Dimension size;

        public Line(Text Text, Dimension size) {
            this.Text = Text;
            this.size = size;
        }

        public Text getComponent() {
            return Text;
        }

        public Dimension getSize() {
            return size;
        }
    }
}
