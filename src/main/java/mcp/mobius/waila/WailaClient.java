package mcp.mobius.waila;

import mcp.mobius.waila.api.impl.config.WailaConfig;
import mcp.mobius.waila.gui.GuiConfigHome;
import mcp.mobius.waila.network.ClientNetworkHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.KeyBinding;
import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;
import net.ornithemc.osl.keybinds.api.KeyBindingEvents;

import java.lang.reflect.Method;

public class WailaClient implements ClientModInitializer {

    public static KeyBinding openConfig;
    public static KeyBinding showOverlay;
    public static KeyBinding toggleLiquid;

    @Override
    public void initClient() {
        ClientNetworkHandler.init();

		KeyBindingEvents.REGISTER_KEYBINDS.register(registry -> {
			openConfig = new KeyBinding("key.waila.config", 320, Waila.NAME);
			showOverlay = new KeyBinding("key.waila.show_overlay", 321, Waila.NAME);
			toggleLiquid = new KeyBinding("key.waila.toggle_liquid", 322, Waila.NAME);
			registry.register(openConfig);
			registry.register(showOverlay);
			registry.register(toggleLiquid);
		});

        if (FabricLoader.getInstance().isModLoaded("modmenu") && FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            enableModMenuConfig();
    }

    private static void enableModMenuConfig() {
        try {
            Class<?> modMenuApi_ = Class.forName("io.github.prospector.modmenu.api.ModMenuApi");
            Method addConfigOverride_ = modMenuApi_.getMethod("addConfigOverride", String.class, Runnable.class);
            addConfigOverride_.invoke(null, Waila.MODID, (Runnable) () -> Minecraft.getInstance().openScreen(new GuiConfigHome(null)));
        } catch (Exception e) {
            Waila.LOGGER.error("Error enabling the Mod Menu config button for Hwyla", e);
        }
    }

    public static void handleKeybinds() {
        if (openConfig == null || showOverlay == null || toggleLiquid == null)
            return;

        while (openConfig.consumeClick()) {
            Minecraft.getInstance().openScreen(new GuiConfigHome(null));
        }

        while (showOverlay.consumeClick()) {
            if (Waila.CONFIG.get().getGeneral().getDisplayMode() == WailaConfig.DisplayMode.TOGGLE) {
                Waila.CONFIG.get().getGeneral().setDisplayTooltip(!Waila.CONFIG.get().getGeneral().shouldDisplayTooltip());
            }
        }

        while (toggleLiquid.consumeClick()) {
            Waila.CONFIG.get().getGeneral().setDisplayFluids(!Waila.CONFIG.get().getGeneral().shouldDisplayFluids());
        }
    }
}
