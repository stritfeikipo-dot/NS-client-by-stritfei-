package com.nsclient.hud;

/**
 * The small HUD stats that can be merged into / split out of the top "pill".
 * Icons are short plain-text tags (not emoji) since Minecraft's built-in font
 * does not reliably render colour emoji glyphs.
 */
public enum HudElement {
    FPS("fps", "FPS"),
    CPS("cps", "CPS"),
    PING("ping", "MS"),
    COORDS("coords", "XYZ"),
    WATERMARK("watermark", "NS");

    public final String id;
    public final String icon;

    HudElement(String id, String icon) {
        this.id = id;
        this.icon = icon;
    }
}
