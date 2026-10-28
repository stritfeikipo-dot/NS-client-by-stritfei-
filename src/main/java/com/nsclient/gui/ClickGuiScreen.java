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
 * NS Client settings menu, styled after the PulseVisuals layout the person
 * referenced: a floating "NS Client" wordmark above the panel, a plain
 * breadcrumb-style category row ("Visuals / HUD / Misc") inside the panel,
 * a 2-column grid of flat dark cards (label + simple toggle), and a row of
 * round category icon-buttons floating below the panel instead of a sidebar.
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
    private final List<int[]> cardRects = new ArrayList<>(); // {x, y, w, h} for card backgrounds

    private int panelX, panelY, panelW, panelH;
    private static final int COLS = 2;
    private static final int CARD_H = 46;
    private static final int CARD_GAP = 10;
    private static final int PAD = 18;
    private static final int LOGO_H = 30;
    private static final int TABS_H = 34;
    private static final int NAV_GAP = 14;
    private static final int NAV_BTN = 40;

    public ClickGuiScreen() {
        super(Text.literal("NS Client"));
    }

    private record ToggleBuild(String label, java.util.function.BooleanSupplier getter, java.util.function.Consumer<Boolean> setter) {}

    private record SliderBuild(String label, double min, double max, java.util.function.DoubleSupplier getter, java.util.function.DoubleConsumer setter) {}

    @Override
    protected void init() {
        panelW = 520;
        List<ToggleBuild> toggleBuilds = new ArrayList<>();
        List<SliderBuild> sliderBuilds = new ArrayList<>();
        collectItems(toggleBuilds, sliderBuilds);

        int itemCount = toggleBuilds.size() + sliderBuilds.size();
        int rows = (int) Math.ceil(Math.max(itemCount, 1) / (double) COLS);
        panelH = TABS_H + PAD + rows * CARD_H + (rows - 1) * CARD_GAP + PAD;
        int totalH = LOGO_H + 10 + panelH + NAV_GAP + NAV_BTN;
        panelX = (this.width - panelW) / 2;
        panelY = (this.height - totalH) / 2 + LOGO_H + 10;

        rebuild(toggleBuilds, sliderBuilds);
    }

    private void collectItems(List<ToggleBuild> toggleBuilds, List<SliderBuild> sliderBuilds) {
        NSConfig cfg = ConfigManager.get();
        switch (current) {
            case VISUALS -> {
                toggleBuilds.add(new ToggleBuild("Liquid glass style", () -> cfg.liquidGlass, v -> cfg.liquidGlass = v));
                toggleBuilds.add(new ToggleBuild("Custom crosshair", () -> cfg.customCrosshair, v -> cfg.customCrosshair = v));
                sliderBuilds.add(new SliderBuild("Crosshair size", 2, 16, () -> cfg.crosshairSize, v -> cfg.crosshairSize = (float) v));
                toggleBuilds.add(new ToggleBuild("Hit marker", () -> cfg.hitMarker, v -> cfg.hitMarker = v));
                toggleBuilds.add(new ToggleBuild("Hit particles", () -> cfg.hitParticles, v -> cfg.hitParticles = v));
                toggleBuilds.add(new ToggleBuild("Combo counter", () -> cfg.comboCounter, v -> cfg.comboCounter = v));
                toggleBuilds.add(new ToggleBuild("Kill effects", () -> cfg.killEffects, v -> cfg.killEffects = v));
            }
            case HUD -> {
                toggleBuilds.add(new ToggleBuild("HUD enabled", () -> cfg.hudEnabled, v -> cfg.hudEnabled = v));
                toggleBuilds.add(new ToggleBuild("FPS", () -> cfg.showFps, v -> cfg.showFps = v));
                toggleBuilds.add(new ToggleBuild("CPS", () -> cfg.showCps, v -> cfg.showCps = v));
                toggleBuilds.add(new ToggleBuild("Ping", () -> cfg.showPing, v -> cfg.showPing = v));
                toggleBuilds.add(new ToggleBuild("Coordinates", () -> cfg.showCoords, v -> cfg.showCoords = v));
                toggleBuilds.add(new ToggleBuild("Watermark", () -> cfg.showWatermark, v -> cfg.showWatermark = v));
                sliderBuilds.add(new SliderBuild("HUD scale", 0.5, 2.0, () -> (double) cfg.hudScale, v -> cfg.hudScale = (float) v));
            }
            case MISC -> {
                toggleBuilds.add(new ToggleBuild("Armor HUD", () -> cfg.showArmorHud, v -> cfg.showArmorHud = v));
                toggleBuilds.add(new ToggleBuild("Potion HUD", () -> cfg.showPotionHud, v -> cfg.showPotionHud = v));
                toggleBuilds.add(new ToggleBuild("Target HUD", () -> cfg.showTargetHud, v -> cfg.showTargetHud = v));
                toggleBuilds.add(new ToggleBuild("Keystrokes", () -> cfg.showKeystrokes, v -> cfg.showKeystrokes = v));
            }
        }
    }

    private void rebuild(List<ToggleBuild> toggleBuilds, List<SliderBuild> sliderBuilds) {
        toggles.clear();
        sliders.clear();
        cardRects.clear();

        int cardW = (panelW - PAD * 2 - CARD_GAP * (COLS - 1)) / COLS;
        int gridTop = panelY + TABS_H + PAD;
        int itemCount = toggleBuilds.size() + sliderBuilds.size();
        for (int i = 0; i < itemCount; i++) {
            int col = i % COLS;
            int row = i / COLS;
            int cx = panelX + PAD + col * (cardW + CARD_GAP);
            int cy = gridTop + row * (CARD_H + CARD_GAP);
            cardRects.add(new int[]{cx, cy, cardW, CARD_H});
        }

        int idx = 0;
        for (ToggleBuild b : toggleBuilds) {
            int[] r = cardRects.get(idx++);
            ToggleSwitchWidget t = new ToggleSwitchWidget(b.label(), b.getter(), b.setter());
            t.x = r[0] + 14;
            t.y = r[1];
            t.height = r[3];
            t.width = r[2] - 28;
            toggles.add(t);
        }
        for (SliderBuild b : sliderBuilds) {
            int[] r = cardRects.get(idx++);
            SliderWidget s = new SliderWidget(b.label(), b.min(), b.max(), b.getter(), b.setter());
            s.x = r[0] + 14;
            s.y = r[1] + (r[3] - 26) / 2;
            s.width = r[2] - 28;
            sliders.add(s);
        }
    }

    private void rebuild() {
        List<ToggleBuild> toggleBuilds = new ArrayList<>();
        List<SliderBuild> sliderBuilds = new ArrayList<>();
        collectItems(toggleBuilds, sliderBuilds);
        rebuild(toggleBuilds, sliderBuilds);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Floating wordmark above the panel
        String title = "NS Client";
        int titleW = this.textRenderer.getWidth(title);
        int logoY = panelY - LOGO_H - 10;
        RoundRect.fill(ctx, this.width / 2 - 10, logoY + 6, this.width / 2 + titleW / 2 + 14, logoY + 24, 7, Theme.accentWithAlpha(220));
        ctx.drawText(this.textRenderer, Text.literal(title), this.width / 2 - 10 + 10, logoY + 12,
                0xFF0A0614, false);

        Theme.glassPanel(ctx, panelX, panelY, panelW, panelH, 14);

        renderTabsRow(ctx, mouseX, mouseY);
        renderCards(ctx, mouseX, mouseY);

        for (ToggleSwitchWidget t : toggles) {
            t.render(ctx);
        }
        for (SliderWidget s : sliders) {
            s.render(ctx);
        }

        renderNavRow(ctx, mouseX, mouseY);

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void renderTabsRow(DrawContext ctx, int mouseX, int mouseY) {
        int tx = panelX + PAD;
        int ty = panelY + 12;
        Category[] all = Category.values();
        for (int i = 0; i < all.length; i++) {
            Category cat = all[i];
            boolean selected = cat == current;
            int color = selected ? Theme.TEXT : Theme.TEXT_DIM;
            ctx.drawText(this.textRenderer, Text.literal(cat.label), tx, ty, color, false);
            tx += this.textRenderer.getWidth(cat.label);
            if (i < all.length - 1) {
                tx += 6;
                ctx.drawText(this.textRenderer, Text.literal("/"), tx, ty, 0xFF4A4458, false);
                tx += this.textRenderer.getWidth("/") + 6;
            }
        }

        int closeCx = panelX + panelW - 18;
        int closeCy = panelY + 18;
        boolean closeHovered = isInside(mouseX, mouseY, closeCx - 9, closeCy - 9, 18, 18);
        ctx.drawText(this.textRenderer, Text.literal("x"), closeCx - 2, closeCy - 4,
                closeHovered ? (Theme.accent() | 0xFF000000) : Theme.TEXT_DIM, false);

        ctx.fillGradient(panelX + PAD, panelY + TABS_H - 2, panelX + panelW - PAD, panelY + TABS_H - 1, Theme.BORDER, 0x00000000);
    }

    private void renderCards(DrawContext ctx, int mouseX, int mouseY) {
        for (int[] r : cardRects) {
            boolean hovered = isInside(mouseX, mouseY, r[0], r[1], r[2], r[3]);
            int bg = hovered ? Theme.BG_DARK_LIGHT : 0x40120A1F;
            RoundRect.fill(ctx, r[0], r[1], r[0] + r[2], r[1] + r[3], 9, bg);
        }
    }

    private void renderNavRow(DrawContext ctx, int mouseX, int mouseY) {
        Category[] all = Category.values();
        int totalW = all.length * NAV_BTN + (all.length - 1) * 10;
        int nx = this.width / 2 - totalW / 2;
        int ny = panelY + panelH + NAV_GAP;

        RoundRect.fill(ctx, nx - 10, ny - 8, nx + totalW + 10, ny + NAV_BTN + 8, 16, Theme.BG_DARK);
        RoundRect.outline(ctx, nx - 10, ny - 8, nx + totalW + 10, ny + NAV_BTN + 8, 16, Theme.BORDER);

        for (Category cat : all) {
            boolean selected = cat == current;
            boolean hovered = !selected && isInside(mouseX, mouseY, nx, ny, NAV_BTN, NAV_BTN);
            int bg = selected ? (Theme.accent() | 0xFF000000) : hovered ? 0x2AFFFFFF : 0x00000000;
            RoundRect.fill(ctx, nx, ny, nx + NAV_BTN, ny + NAV_BTN, 12, bg);
            drawCategoryIcon(ctx, cat, nx + NAV_BTN / 2, ny + NAV_BTN / 2, selected);
            nx += NAV_BTN + 10;
        }
    }

    private void drawCategoryIcon(DrawContext ctx, Category cat, int cx, int cy, boolean active) {
        int color = active ? 0xFF0A0614 : Theme.TEXT_DIM;
        switch (cat.iconId) {
            case 0 -> { // Visuals: eye
                RoundRect.fill(ctx, cx - 7, cy - 3, cx + 7, cy + 3, 3, color);
                ctx.fill(cx - 1, cy - 1, cx + 1, cy + 1, active ? (Theme.accent() | 0xFF000000) : 0xFF2A2338);
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

    private boolean isInside(int px, int py, int x, int y, int w, int h) {
        return px >= x && px <= x + w && py >= y && py <= y + h;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        int mouseX = (int) click.x();
        int mouseY = (int) click.y();

        int closeCx = panelX + panelW - 18;
        int closeCy = panelY + 18;
        if (isInside(mouseX, mouseY, closeCx - 9, closeCy - 9, 18, 18)) {
            this.close();
            return true;
        }

        Category[] all = Category.values();
        int totalW = all.length * NAV_BTN + (all.length - 1) * 10;
        int nx = this.width / 2 - totalW / 2;
        int ny = panelY + panelH + NAV_GAP;
        for (Category cat : all) {
            if (isInside(mouseX, mouseY, nx, ny, NAV_BTN, NAV_BTN)) {
                current = cat;
                init();
                return true;
            }
            nx += NAV_BTN + 10;
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
