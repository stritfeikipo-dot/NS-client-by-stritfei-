package com.nsclient.mixin.client;

import com.nsclient.config.ConfigManager;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cancels vanilla's own crosshair render so {@code CrosshairRenderer} can draw
 * NS Client's custom crosshair in its place when the setting is enabled.
 *
 * NOTE: {@code renderCrosshair}'s parameter types have changed across recent
 * Minecraft versions (the tick-delta argument became a RenderTickCounter in
 * some 1.20.x+ releases). If this mixin fails to apply on 1.21.11, check the
 * real method signature in InGameHud via your IDE / a decompiler and adjust
 * the method name + @Inject target here; it is the one place in this project
 * that was written from memory of the general pattern rather than verified
 * against 1.21.11 sources directly.
 */
@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void nsclient$cancelVanillaCrosshair(CallbackInfo ci) {
        if (ConfigManager.get().customCrosshair) {
            ci.cancel();
        }
    }
}
