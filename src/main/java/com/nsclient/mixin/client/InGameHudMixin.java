package com.nsclient.mixin.client;

import com.nsclient.config.ConfigManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cancels vanilla's own crosshair render so {@code CrosshairRenderer} can draw
 * NS Client's custom crosshair in its place when the setting is enabled.
 *
 * Confirmed signature for 1.21.11:
 * {@code private void renderCrosshair(DrawContext context, RenderTickCounter tickCounter)}.
 */
@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(
            method = "renderCrosshair(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void nsclient$cancelVanillaCrosshair(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (ConfigManager.get().customCrosshair) {
            ci.cancel();
        }
    }
}
