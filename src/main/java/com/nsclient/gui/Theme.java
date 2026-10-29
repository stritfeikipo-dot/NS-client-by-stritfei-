package com.nsclient.gui;

import com.nsclient.config.ConfigManager;
import net.minecraft.client.gui.DrawContext;

/**
 * Central palette + drawing helpers for the NS Client look: a flat, near-black
 * panel (like PulseVisuals) with purple used only as an accent on icons,
 * toggles and the active tab — not as a tint over the whole panel.
 */
public final class Theme {

    private Theme() {
    }

    public static final int BG_DARK = 0xF50B0710;       // panel / pill background: solid near-black
    public static final int BG_DARK_LIGHT = 0xFF17111F; // card background (slightly lighter than panel)
    public static final int BORDER = 0x1AFFFFFF;        // barely-visible hairline border
    public static final int TEXT = 0xFFEDE7FA;
    public static final int TEXT_DIM = 0xFF75707F;

    public static int accent() {
        return ConfigManager.get().accentColor;
    }

    public static int accentWithAlpha(int alpha) {
        int rgb = accent() & 0x00FFFFFF;
        return (alpha << 24) | rgb;
    }

    /** Flat, solid near-black rounded panel with a barely-visible hairline border. No tint, no sheen. */
    public static void glassPanel(DrawContext ctx, int x, int y, int width, int height, int radius) {
        RoundRect.fill(ctx, x, y, x + width, y + height, radius, BG_DARK);
        RoundRect.outline(ctx, x, y, x + width, y + height, radius, BORDER);
    }

    public static void pill(DrawContext ctx, int x, int y, int width, int height) {
        glassPanel(ctx, x, y, width, height, height / 2);
    }
}
