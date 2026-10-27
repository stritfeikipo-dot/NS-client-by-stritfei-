package com.nsclient;

import com.nsclient.config.ConfigManager;
import com.nsclient.gui.ClickGuiScreen;
import com.nsclient.hud.CpsTracker;
import com.nsclient.hud.CrosshairRenderer;
import com.nsclient.hud.HudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class NSClientClient implements ClientModInitializer {

    private static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        ConfigManager.load();

        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.nsclient.open_gui",
                InputUtil.Type.KEYSYM,
                ConfigManager.get().clickGuiKeycode,
                KeyBinding.Category.create(Identifier.of(NSClient.MOD_ID, "general"))
        ));

        HudRenderCallback.EVENT.register((ctx, tickCounter) -> {
            float tickDelta = tickCounter.getTickProgress(true);
            HudRenderer.render(ctx, tickDelta);
            CrosshairRenderer.render(ctx);
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openGuiKey.wasPressed() && client.currentScreen == null) {
                client.setScreen(new ClickGuiScreen());
            }

            long handle = client.getWindow() != null ? client.getWindow().getHandle() : 0L;
            if (handle != 0L) {
                boolean left = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
                boolean right = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
                CpsTracker.tick(left, right);
            }
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            // Reserved for future per-tick visual-effect updates (hit markers, combo timers).
        });

        NSClient.LOGGER.info("NS Client initialized");
    }
}
