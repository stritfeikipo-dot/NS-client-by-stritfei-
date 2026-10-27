package com.nsclient.hud;

import com.nsclient.config.ConfigManager;
import com.nsclient.config.NSConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class CrosshairRenderer {

    private CrosshairRenderer() {
    }

    public static void render(DrawContext ctx) {
        NSConfig cfg = ConfigManager.get();
        if (!cfg.customCrosshair) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        int cx = client.getWindow().getScaledWidth() / 2;
        int cy = client.getWindow().getScaledHeight() / 2;
        int size = (int) cfg.crosshairSize;
        int color = cfg.crosshairColor;

        switch (cfg.crosshairStyle) {
            case 1 -> ctx.fill(cx - 1, cy - 1, cx + 1, cy + 1, color); // dot
            case 2 -> drawCircleOutline(ctx, cx, cy, size, color);     // circle
            default -> {                                               // cross
                ctx.fill(cx - size, cy, cx + size, cy + 1, color);
                ctx.fill(cx, cy - size, cx + 1, cy + size, color);
            }
        }
    }

    private static void drawCircleOutline(DrawContext ctx, int cx, int cy, int r, int color) {
        int steps = Math.max(16, r * 4);
        for (int i = 0; i < steps; i++) {
            double angle = (2 * Math.PI * i) / steps;
            int px = cx + (int) Math.round(r * Math.cos(angle));
            int py = cy + (int) Math.round(r * Math.sin(angle));
            ctx.fill(px, py, px + 1, py + 1, color);
        }
    }
}
