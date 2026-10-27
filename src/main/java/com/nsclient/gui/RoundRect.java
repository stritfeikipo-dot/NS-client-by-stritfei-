package com.nsclient.gui;

import net.minecraft.client.gui.DrawContext;

/**
 * Draws rounded rectangles using only vanilla {@link DrawContext#fill} calls
 * (one thin horizontal strip per row of the corner arc). No shaders, no custom
 * textures, so it works unchanged across Minecraft rendering-pipeline tweaks.
 */
public final class RoundRect {

    private RoundRect() {
    }

    public static void fill(DrawContext ctx, int x1, int y1, int x2, int y2, int radius, int color) {
        int r = clampRadius(radius, x2 - x1, y2 - y1);
        if (r <= 0) {
            ctx.fill(x1, y1, x2, y2, color);
            return;
        }
        // Center cross: full-width middle band + full-height middle column, avoids double-filling.
        ctx.fill(x1 + r, y1, x2 - r, y2, color);
        ctx.fill(x1, y1 + r, x1 + r, y2 - r, color);
        ctx.fill(x2 - r, y1 + r, x2, y2 - r, color);

        for (int row = 0; row < r; row++) {
            int dx = (int) Math.round(r - Math.sqrt((double) (r * r) - (double) ((r - row) * (r - row))));
            // top corners
            ctx.fill(x1 + dx, y1 + row, x2 - dx, y1 + row + 1, color);
            // bottom corners
            ctx.fill(x1 + dx, y2 - row - 1, x2 - dx, y2 - row, color);
        }
    }

    public static void outline(DrawContext ctx, int x1, int y1, int x2, int y2, int radius, int color) {
        int r = clampRadius(radius, x2 - x1, y2 - y1);
        // straight edges
        ctx.fill(x1 + r, y1, x2 - r, y1 + 1, color);
        ctx.fill(x1 + r, y2 - 1, x2 - r, y2, color);
        ctx.fill(x1, y1 + r, x1 + 1, y2 - r, color);
        ctx.fill(x2 - 1, y1 + r, x2, y2 - r, color);

        if (r <= 0) {
            return;
        }
        for (int row = 0; row < r; row++) {
            int dx = (int) Math.round(r - Math.sqrt((double) (r * r) - (double) ((r - row) * (r - row))));
            int dxNext = row + 1 < r
                    ? (int) Math.round(r - Math.sqrt((double) (r * r) - (double) ((r - row - 1) * (r - row - 1))))
                    : r;
            int w = Math.max(1, dxNext - dx + 1);
            // top-left / top-right pixels of the arc
            ctx.fill(x1 + dx, y1 + row, x1 + dx + w, y1 + row + 1, color);
            ctx.fill(x2 - dx - w, y1 + row, x2 - dx, y1 + row + 1, color);
            // bottom-left / bottom-right
            ctx.fill(x1 + dx, y2 - row - 1, x1 + dx + w, y2 - row, color);
            ctx.fill(x2 - dx - w, y2 - row - 1, x2 - dx, y2 - row, color);
        }
    }

    private static int clampRadius(int radius, int width, int height) {
        return Math.max(0, Math.min(radius, Math.min(width, height) / 2));
    }
}
