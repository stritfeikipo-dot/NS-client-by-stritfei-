package com.nsclient.gui;

import com.nsclient.config.ConfigManager;
import com.nsclient.config.NSConfig;
import com.nsclient.gui.widget.SliderWidget;
import com.nsclient.gui.widget.ToggleSwitchWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * NS Client settings menu: a left icon sidebar (Visuals / HUD / Misc) and a
 * right glass panel listing that category's toggles & sliders.
 * Rebuilds its widget list whenever the category changes.
 */
public class ClickGuiScreen extends Screen {

    private enum Category {
        VISUALS("Visuals"),
        HUD("HUD"),
        MISC("Misc");

        final String label;

        Category(String label) {
            this.label = label;
        }
    }

    private Category current = Category.VISUALS;

    private final List<ToggleSwitchWidget> toggles = new ArrayList<>();
    private final List<SliderWidget> sliders = new ArrayList<>();

    private int panelX, panelY, panelW, panelH;
    private int sidebarW = 70;

    public ClickGuiScreen() {
        super(Text.literal("NS Client"));
    }

    @Override
    protected void init() {
        panelW = 320;
        panelH = 220;
        panelX = (this.width - panelW) / 2;
        panelY = (this.height - panelH) / 2;
        rebuild();
    }

    private void rebuild() {
        toggles.clear();
        sliders.clear();
        NSConfig cfg = ConfigManager.get();

        int contentX = panelX + sidebarW + 16;
        int contentY = panelY + 16;
        int rowH = 24;
        int row = 0;
        int widgetW = panelW - sidebarW - 32;

        switch (current) {
            case VISUALS -> {
                addToggle(contentX, contentY, row++, rowH, widgetW, "Liquid glass style", () -> cfg.liquidGlass, v -> cfg.liquidGlass = v);
                addToggle(contentX, contentY, row++, rowH, widgetW, "Custom crosshair", () -> cfg.customCrosshair, v -> cfg.customCrosshair = v);
                addSlider(contentX, contentY, row++, rowH, widgetW, "Crosshair size", 2, 16, () -> cfg.crosshairSize, v -> cfg.crosshairSize = (float) v);
                addToggle(contentX, contentY, row++, rowH, widgetW, "Hit marker", () -> cfg.hitMarker, v -> cfg.hitMarker = v);
                addToggle(contentX, contentY, row++, rowH, widgetW, "Hit particles", () -> cfg.hitParticles, v -> cfg.hitParticles = v);
                addToggle(contentX, contentY, row++, rowH, widgetW, "Combo counter", () -> cfg.comboCounter, v -> cfg.comboCounter = v);
                addToggle(contentX, contentY, row++, rowH, widgetW, "Kill effects", () -> cfg.killEffects, v -> cfg.killEffects = v);
            }
            case HUD -> {
                addToggle(contentX, contentY, row++, rowH, widgetW, "HUD enabled", () -> cfg.hudEnabled, v -> cfg.hudEnabled = v);
                addToggle(contentX, contentY, row++, rowH, widgetW, "FPS", () -> cfg.showFps, v -> cfg.showFps = v);
                addToggle(contentX, contentY, row++, rowH, widgetW, "CPS", () -> cfg.showCps, v -> cfg.showCps = v);
                addToggle(contentX, contentY, row++, rowH, widgetW, "Ping", () -> cfg.showPing, v -> cfg.showPing = v);
                addToggle(contentX, contentY, row++, rowH, widgetW, "Coordinates", () -> cfg.showCoords, v -> cfg.showCoords = v);
                addToggle(contentX, contentY, row++, rowH, widgetW, "Watermark", () -> cfg.showWatermark, v -> cfg.showWatermark = v);
                addSlider(contentX, contentY, row++, rowH, widgetW, "HUD scale", 0.5, 2.0, () -> (double) cfg.hudScale, v -> cfg.hudScale = (float) v);
            }
            case MISC -> {
                addToggle(contentX, contentY, row++, rowH, widgetW, "Armor HUD", () -> cfg.showArmorHud, v -> cfg.showArmorHud = v);
                addToggle(contentX, contentY, row++, rowH, widgetW, "Potion HUD", () -> cfg.showPotionHud, v -> cfg.showPotionHud = v);
                addToggle(contentX, contentY, row++, rowH, widgetW, "Target HUD", () -> cfg.showTargetHud, v -> cfg.showTargetHud = v);
                addToggle(contentX, contentY, row++, rowH, widgetW, "Keystrokes", () -> cfg.showKeystrokes, v -> cfg.showKeystrokes = v);
            }
        }
    }

    private void addToggle(int x, int y, int row, int rowH, int w, String label,
                            java.util.function.BooleanSupplier getter, java.util.function.Consumer<Boolean> setter) {
        ToggleSwitchWidget t = new ToggleSwitchWidget(label, getter, setter);
        t.x = x;
        t.y = y + row * rowH;
        t.width = w;
        toggles.add(t);
    }

    private void addSlider(int x, int y, int row, int rowH, int w, String label, double min, double max,
                            java.util.function.DoubleSupplier getter, java.util.function.DoubleConsumer setter) {
        SliderWidget s = new SliderWidget(label, min, max, getter, setter);
        s.x = x;
        s.y = y + row * rowH;
        s.width = w;
        sliders.add(s);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.renderBackground(ctx, mouseX, mouseY, delta);

        Theme.glassPanel(ctx, panelX, panelY, panelW, panelH, 10);

        // Sidebar
        Theme.glassPanel(ctx, panelX, panelY, sidebarW, panelH, 10);
        ctx.drawText(this.textRenderer, Text.literal("NS Client"), panelX + 10, panelY + 10, Theme.accent() | 0xFF000000, true);

        int tabY = panelY + 32;
        for (Category cat : Category.values()) {
            boolean selected = cat == current;
            int bg = selected ? Theme.accentWithAlpha(90) : 0x00000000;
            RoundRect.fill(ctx, panelX + 6, tabY, panelX + sidebarW - 6, tabY + 20, 6, bg);
            int color = selected ? Theme.TEXT : Theme.TEXT_DIM;
            ctx.drawText(this.textRenderer, Text.literal(cat.label), panelX + 14, tabY + 6, color, false);
            tabY += 24;
        }

        // Content header
        ctx.drawText(this.textRenderer, Text.literal(current.label), panelX + sidebarW + 16, panelY + 10,
                Theme.accent() | 0xFF000000, true);

        for (ToggleSwitchWidget t : toggles) {
            t.render(ctx);
        }
        for (SliderWidget s : sliders) {
            s.render(ctx);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();

        int tabY = panelY + 32;
        for (Category cat : Category.values()) {
            if (mouseX >= panelX + 6 && mouseX <= panelX + sidebarW - 6 && mouseY >= tabY && mouseY <= tabY + 20) {
                current = cat;
                rebuild();
                return true;
            }
            tabY += 24;
        }

        for (ToggleSwitchWidget t : toggles) {
            if (t.mouseClicked(mouseX, mouseY)) {
                ConfigManager.save();
                return true;
            }
        }
        for (SliderWidget s : sliders) {
            if (s.mouseClicked(mouseX, mouseY)) {
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent click, double deltaX, double deltaY) {
        double mouseX = click.x();
        for (SliderWidget s : sliders) {
            s.mouseDragged(mouseX);
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
        for (SliderWidget s : sliders) {
            s.mouseReleased();
        }
        ConfigManager.save();
        return super.mouseReleased(click);
    }

    @Override
    public void onClose() {
        ConfigManager.save();
        super.onClose();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
