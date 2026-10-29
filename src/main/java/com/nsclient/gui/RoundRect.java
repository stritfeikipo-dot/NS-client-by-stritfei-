package com.nsclient.gui;

import net.minecraft.client.gui.DrawContext;

/**
 * Draws rounded rectangles using only vanilla {@link DrawContext#fill} calls.
 * Each corner row is anti-aliased by hand: the exact (fractional) edge
 * position is computed, the solid interior is filled up to the nearest whole
 * pixel, and the single boundary pixel on each side gets a partial-alpha fill
 * proportional to how much of that pixel the circle actually covers. Without
 * this, round corners drawn with flat fill() calls look stair-stepped /
 * "Minecraft-pixelated" instead of smooth like a modern UI.
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
        int alpha = (color >>> 24) & 0xFF;

        // Center cross: full-width middle band + full-height middle column, avoids double-filling.
        ctx.fill(x1 + r, y1, x2 - r, y2, color);
        ctx.fill(x1, y1 + r, x1 + r, y2 - r, color);
        ctx.fill(x2 - r, y1 + r, x2, y2 - r, color);

        for (int row = 0; row < r; row++) {
            double dEdge = r - Math.sqrt((double) (r * r) - (double) ((r - row) * (r - row)));
            int dxFloor = (int) Math.floor(dEdge);
            double frac = dEdge - dxFloor; // 0 = pixel fully inside, ~1 = pixel fully outside

            // solid interior of this row (both top and bottom, both corners)
            ctx.fill(x1 + dxFloor + 1, y1 + row, x2 - dxFloor - 1, y1 + row + 1, color);
            ctx.fill(x1 + dxFloor + 1, y2 - row - 1, x2 - dxFloor - 1, y2 - row, color);

            // boundary pixel: partial alpha = how much of it the curve still covers
            int boundaryAlpha = (int) Math.round(alpha * (1.0 - frac));
            if (boundaryAlpha > 0) {
                int edgeColor = (boundaryAlpha << 24) | (color & 0x00FFFFFF);
                ctx.fill(x1 + dxFloor, y1 + row, x1 + dxFloor + 1, y1 + row + 1, edgeColor);
                ctx.fill(x2 - dxFloor - 1, y1 + row, x2 - dxFloor, y1 + row + 1, edgeColor);
                ctx.fill(x1 + dxFloor, y2 - row - 1, x1 + dxFloor + 1, y2 - row, edgeColor);
                ctx.fill(x2 - dxFloor - 1, y2 - row - 1, x2 - dxFloor, y2 - row, edgeColor);
            }
        }
    }

    public static void outline(DrawContext ctx, int x1, int y1, int x2, int y2, int radius, int color) {
        int r = clampRadius(radius, x2 - x1, y2 - y1);
        int alpha = (color >>> 24) & 0xFF;

        // straight edges
        ctx.fill(x1 + r, y1, x2 - r, y1 + 1, color);
        ctx.fill(x1 + r, y2 - 1, x2 - r, y2, color);
        ctx.fill(x1, y1 + r, x1 + 1, y2 - r, color);
        ctx.fill(x2 - 1, y1 + r, x2, y2 - r, color);

        if (r <= 0) {
            return;
        }
        for (int row = 0; row < r; row++) {
            double dEdge = r - Math.sqrt((double) (r * r) - (double) ((r - row) * (r - row)));
            double dEdgeNext = row + 1 < r
                    ? r - Math.sqrt((double) (r * r) - (double) ((r - row - 1) * (r - row - 1)))
                    : r;
            int dx = (int) Math.floor(dEdge);
            double frac = dEdge - dx;
            int dxNext = (int) Math.floor(dEdgeNext);
            int w = Math.max(1, dxNext - dx);

            int edgeAlpha = (int) Math.round(alpha * (1.0 - frac));
            int edgeColor = (edgeAlpha << 24) | (color & 0x00FFFFFF);

            // top-left / top-right arc pixels
            ctx.fill(x1 + dx, y1 + row, x1 + dx + w, y1 + row + 1, color);
            ctx.fill(x2 - dx - w, y1 + row, x2 - dx, y1 + row + 1, color);
            if (edgeAlpha > 0) {
                ctx.fill(x1 + dx - 1, y1 + row, x1 + dx, y1 + row + 1, edgeColor);
                ctx.fill(x2 - dx, y1 + row, x2 - dx + 1, y1 + row + 1, edgeColor);
            }
            // bottom-left / bottom-right arc pixels
            ctx.fill(x1 + dx, y2 - row - 1, x1 + dx + w, y2 - row, color);
            ctx.fill(x2 - dx - w, y2 - row - 1, x2 - dx, y2 - row, color);
            if (edgeAlpha > 0) {
                ctx.fill(x1 + dx - 1, y2 - row - 1, x1 + dx, y2 - row, edgeColor);
                ctx.fill(x2 - dx, y2 - row - 1, x2 - dx + 1, y2 - row, edgeColor);
            }
        }
    }

    private static int clampRadius(int radius, int width, int height) {
        return Math.max(0, Math.min(radius, Math.min(width, height) / 2));
    }
                         }
