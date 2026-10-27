package com.nsclient.config;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Plain data holder for every persisted NS Client setting.
 * Serialized to / from config/nsclient/config.json by {@link ConfigManager}.
 */
public class NSConfig {

    // ---- Theme ----
    public String theme = "modern_purple";
    public boolean liquidGlass = true;
    public int accentColor = 0xFFB15CFF; // neon purple

    // ---- HUD toggles ----
    public boolean hudEnabled = true;
    public boolean showFps = true;
    public boolean showCps = true;
    public boolean showPing = true;
    public boolean showCoords = true;
    public boolean showWatermark = true;
    public boolean showArmorHud = true;
    public boolean showPotionHud = true;
    public boolean showTargetHud = true;
    public boolean showKeystrokes = false;

    public float hudX = 6f;
    public float hudY = 6f;
    public float hudScale = 1.0f;

    /** Which HUD elements are currently merged into one pill, by id ("fps","cps","ping","coords","watermark"). */
    public Set<String> mergedHudElements = new LinkedHashSet<>(Set.of("fps", "cps", "ping", "coords", "watermark"));

    // ---- Crosshair ----
    public boolean customCrosshair = true;
    public int crosshairStyle = 0; // 0=cross 1=dot 2=circle
    public float crosshairSize = 6f;
    public int crosshairColor = 0xFFFFFFFF;

    // ---- Combat visuals ----
    public boolean hitMarker = true;
    public boolean hitParticles = true;
    public boolean comboCounter = true;
    public boolean killEffects = true;

    // ---- Misc ----
    public int clickGuiKeycode = 344; // GLFW_KEY_RIGHT_SHIFT
}
