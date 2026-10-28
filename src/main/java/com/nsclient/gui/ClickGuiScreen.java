package com.nsclient.gui;

import com.nsclient.config.ConfigManager;
import com.nsclient.config.NSConfig;
import com.nsclient.gui.widget.SliderWidget;
import com.nsclient.gui.widget.ToggleSwitchWidget;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * NS Client settings menu: a left icon-tab sidebar (Visuals / HUD / Misc) and a
 * right glass panel listing that category's settings as "cards".
 * Rebuilds its widget list whenever the category changes.
 */
public class ClickGuiScreen extends Screen {

    private enum Category {
        VISUALS("Visuals", 0),
        HUD("HUD", 1),
        MISC("Misc", 2);

        final String label;
        final int iconId;

        Category(String label, int iconId) {
            this.label = label;
            this.iconId = iconId;
        }
    }

    private Category current = Category.VISUALS;

    private final List<ToggleSwitchWidget> toggles = new ArrayList<>();
    private final List<SliderWidget> sliders = new ArrayList<>();
    /** y-top of each content card, for the alternating card background. */
    private final List<Integer> cardTops = new ArrayList<>();

    private int panelX, panelY, panelW, panelH;
    private final int sidebarW = 58;
    private final int tabSize = 42;
    private final int tabGap = 8;

    public ClickGuiScreen() {
        super(Text.literal("NS Client"));
    }

    @Override
    protected void init() {
        panelW = 360;
        panelH = 260;
        panelX = (this.width - panelW) / 2;
        panelY = (this.height - panelH) / 2;
        rebuild();
    }

    private void rebuild() {
        toggles.clear();
        sliders.clear();
        cardTops.clear();
        NSConfig cfg = ConfigManager.get();

        int contentX = panelX + sidebarW + 18;
        int contentY = panelY + 44;
        int rowH = 27;
        int row = 0;
        int widgetW = panelW - sidebarW - 36;

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
        t.x = x + 10;
        t.y = y + row * rowH + 5;
        t.width = w - 20;
        toggles.add(t);
        cardTops.add(y + row * rowH);
    }

    private void addSlider(int x, int y, int row, int rowH, int w, String label, double min, double max,
                            java.util.function.DoubleSupplier getter, java.util.function.DoubleConsumer setter) {
        SliderWidget s = new SliderWidget(label, min, max, getter, setter);
        s.x = x + 10;
        s.y = y + row * rowH + 5;
        s.width = w - 20;
        sliders.add(s);
        cardTops.add(y + row * rowH);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // outer glow behind the whole panel
        RoundRect.fill(ctx, panelX - 2, panelY - 2, panelX + panelW + 2, panelY + panelH + 2, 16, Theme.accentWithAlpha(25));
        Theme.glassPanel(ctx, panelX, panelY, panelW, panelH, 14);

        renderSidebar(ctx, mouseX, mouseY);
        renderHeader(ctx, mouseX, mouseY);
        renderCards(ctx);

        for (ToggleSwitchWidget t : toggles) {
            t.render(ctx);
        }
        for (SliderWidget s : sliders) {
            s.render(ctx);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void renderSidebar(DrawContext ctx, int mouseX, int mouseY) {
        Theme.glassPanel(ctx, panelX, panelY, sidebarW, panelH, 14);

        // brand mark
        int brandCx = panelX + sidebarW / 2;
        RoundRect.fill(ctx, brandCx - 12, panelY + 14, brandCx + 12, panelY + 32, 6, Theme.accentWithAlpha(200));
        ctx.drawText(this.textRenderer, Text.literal("NS"), brandCx - 7, panelY + 19, 0xFF0A0614, false);

        int tabY = panelY + 48;
        int tabX = panelX + (sidebarW - tabSize) / 2;
        for (Category cat : Category.values()) {
            boolean selected = cat == current;
            boolean hovered = !selected && isInside(mouseX, mouseY, tabX, tabY, tabSize, tabSize);

            if (selected) {
                RoundRect.fill(ctx, tabX - 2, tabY - 2, tabX + tabSize + 2, tabY + tabSize + 2, 12, Theme.accentWithAlpha(45));
            }
            int bg = selected ? Theme.accentWithAlpha(130) : hovered ? 0x2AFFFFFF : 0x14FFFFFF;
            RoundRect.fill(ctx, tabX, tabY, tabX + tabSize, tabY + tabSize, 10, bg);
            if (selected) {
                RoundRect.outline(ctx, tabX, tabY, tabX + tabSize, tabY + tabSize, 10, Theme.accent() | 0xFF000000);
            }

            drawCategoryIcon(ctx, cat, tabX + tabSize / 2, tabY + tabSize / 2 - 5, selected);
            int labelColor = selected ? Theme.TEXT : Theme.TEXT_DIM;
            int labelW = this.textRenderer.getWidth(cat.label);
            ctx.drawText(this.textRenderer, Text.literal(cat.label), tabX + tabSize / 2 - labelW / 2, tabY + tabSize - 11, labelColor, false);

            tabY += tabSize + tabGap;
        }
    }

    private void drawCategoryIcon(DrawContext ctx, Category cat, int cx, int cy, boolean active) {
        int color = active ? (Theme.accent() | 0xFF000000) : Theme.TEXT_DIM;
        switch (cat.iconId) {
            case 0 -> { // Visuals: eye
                RoundRect.fill(ctx, cx - 7, cy - 3, cx + 7, cy + 3, 3, color);
                ctx.fill(cx - 1, cy - 1, cx + 1, cy + 1, active ? 0xFF0A0614 : 0xFF2A2338);
            }
            case 1 -> { // HUD: little screen
                RoundRect.outline(ctx, cx - 7, cy - 5, cx + 7, cy + 5, 2, color);
                ctx.fill(cx - 4, cy - 2, cx - 1, cy + 1, color);
            }
            default -> { // Misc: three dots
                ctx.fill(cx - 6, cy - 1, cx - 4, cy + 1, color);
                ctx.fill(cx - 1, cy - 1, cx + 1, cy + 1, color);
                ctx.fill(cx + 4, cy - 1, cx + 6, cy + 1, color);
            }
        }
    }

    private void renderHeader(DrawContext ctx, int mouseX, int mouseY) {
        int hx = panelX + sidebarW + 18;
        ctx.drawText(this.textRenderer, Text.literal(current.label), hx, panelY + 16, Theme.accent() | 0xFF000000, true);
        ctx.drawText(this.textRenderer, Text.literal("NS Client settings"), hx, panelY + 28, Theme.TEXT_DIM, false);
        ctx.fillGradient(hx, panelY + 40, panelX + panelW - 16, panelY + 41, Theme.BORDER, 0x00000000);

        int closeCx = panelX + panelW - 20;
        int closeCy = panelY + 20;
        boolean closeHovered = isInside(mouseX, mouseY, closeCx - 10, closeCy - 10, 20, 20);
        RoundRect.fill(ctx, closeCx - 10, closeCy - 10, closeCx + 10, closeCy + 10, 10, closeHovered ? Theme.accentWithAlpha(160) : 0x1AFFFFFF);
        ctx.drawText(this.textRenderer, Text.literal("x"), closeCx - 2, closeCy - 4, closeHovered ? 0xFF0A0614 : Theme.TEXT_DIM, false);
    }

    private void renderCards(DrawContext ctx) {
        int cardX = panelX + sidebarW + 16;
        int cardW = panelW - sidebarW - 32;
        for (int i = 0; i < cardTops.size(); i++) {
            int top = cardTops.get(i);
            int fill = (i % 2 == 0) ? Theme.BG_DARK_LIGHT : 0x00000000;
            if (fill != 0) {
                RoundRect.fill(ctx, cardX, top, cardX + cardW, top + 27 - 3, 6, fill);
            }
        }
    }

    private boolean isInside(int px, int py, int x, int y, int w, int h) {
        return px >= x && px <= x + w && py >= y && py <= y + h;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();

        int closeCx = panelX + panelW - 20;
        int closeCy = panelY + 20;
        if (isInside((int) mouseX, (int) mouseY, closeCx - 10, closeCy - 10, 20, 20)) {
            this.close();
            return true;
        }

        int tabY = panelY + 48;
        int tabX = panelX + (sidebarW - tabSize) / 2;
        for (Category cat : Category.values()) {
            if (isInside((int) mouseX, (int) mouseY, tabX, tabY, tabSize, tabSize)) {
                current = cat;
                rebuild();
                return true;
            }
            tabY += tabSize + tabGap;
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
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        double mouseX = click.x();
        for (SliderWidget s : sliders) {
            s.mouseDragged(mouseX);
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        for (SliderWidget s : sliders) {
            s.mouseReleased();
        }
        ConfigManager.save();
        return super.mouseReleased(click);
    }

    @Override
    public void close() {
        ConfigManager.save();
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
                     }
