package mcp.mobius.waila.gui.config.value;

import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.unmapped.C_0818095;

import java.util.function.Consumer;

public class OptionsEntryValueBoolean extends OptionsEntryValue<Boolean> {

    private final C_0818095 button;

    public OptionsEntryValueBoolean(String optionName, boolean value, Consumer<Boolean> save) {
        super(optionName, save);

        this.button = new C_0818095(0, 0, 100, 20, I18n.translate("gui." + (value ? "yes" : "no")), w -> {
            this.value = !this.value;
        });
        this.value = value;
    }

    @Override
    protected void drawValue(int entryWidth, int entryHeight, int x, int y, int mouseX, int mouseY, boolean selected, float partialTicks) {
        this.button.x = x + 135;
        this.button.y = y + entryHeight / 6;
        this.button.setMessage(I18n.translate("gui." + (value ? "yes" : "no")));
        this.button.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public GuiEventListener getListener() {
        return button;
    }
}
