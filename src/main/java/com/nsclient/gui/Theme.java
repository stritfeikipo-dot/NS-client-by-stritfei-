package com.nsclient.gui;

import com.nsclient.config.ConfigManager;
import net.minecraft.client.gui.DrawContext;

/**
 * Central palette + drawing helpers for the "Modern Purple / Liquid Glass" look
 * shared by the HUD pills and the ClickGUI panels.
 *
 * There is no real backdrop blur here (that needs a shader pass this mod doesn't
 * ship), so "liquid glass" is approximated with a translucent dark-purple fill,
 * a 1px bright top edge ("sheen"), and soft rounded corners.
 */
public final class Theme {

    private Theme() {
    }

    public static final int BG_DARK = 0xE6120A1F;      // panel / pill background
    public static final int BG_DARK_LIGHT = 0xE61C1030; // slightly lighter variant (hovered rows etc.)
    public static final int BORDER = 0x33B15CFF;        // faint purple border
    public static final int SHEEN = 0x40FFFFFF;         // top-edge highlight line
    public static final int TEXT = 0xFFEDE7FA;
    public static final int TEXT_DIM = 0xFFA79BC4;

    public static int accent() {
        return ConfigManager.get().accentColor;
    }

    public static int accentWithAlpha(int alpha) {
        int rgb = accent() & 0x00FFFFFF;
        return (alpha << 24) | rgb;
    }

    /** Rounded "glass" panel: translucent fill + faint border + a bright 1px top sheen line. */
    public static void glassPanel(DrawContext ctx, int x, int y, int width, int height, int radius) {
        boolean glass = ConfigManager.get().liquidGlass;
        int fill = glass ? BG_DARK : 0xF0100820;
        RoundRect.fill(ctx, x, y, x + width, y + height, radius, fill);
        RoundRect.outline(ctx, x, y, x + width, y + height, radius, BORDER);
        if (glass) {
            // top sheen: a thin brighter strip just inside the top edge
            ctx.fillGradient(x + radius / 2, y + 1, x + width - radius / 2, y + 2, SHEEN, 0x00FFFFFF);
        }
    }

    public static void pill(DrawContext ctx, int x, int y, int width, int height) {
        glassPanel(ctx, x, y, width, height, height / 2);
    }
}
