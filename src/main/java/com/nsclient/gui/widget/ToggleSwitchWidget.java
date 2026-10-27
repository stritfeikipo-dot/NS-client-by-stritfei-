package com.nsclient.gui.widget;

import com.nsclient.gui.Theme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * A row with a label on the left and a small ring-style toggle on the right
 * (hollow ring when off, filled with the accent colour when on).
 * Managed manually by the owning screen rather than extending Minecraft's
 * ClickableWidget, so it has no dependency on widget-framework internals
 * that shift between versions.
 */
public class ToggleSwitchWidget {

    public int x, y, width, height = 18;
    public final String label;
    private final BooleanSupplier getter;
    private final Consumer<Boolean> setter;

    public ToggleSwitchWidget(String label, BooleanSupplier getter, Consumer<Boolean> setter) {
        this.label = label;
        this.getter = getter;
        this.setter = setter;
    }

    public void render(DrawContext ctx) {
        MinecraftClient client = MinecraftClient.getInstance();
        ctx.drawText(client.textRenderer, Text.literal(label), x, y + 5, Theme.TEXT, false);

        boolean on = getter.getAsBoolean();
        int trackW = 28, trackH = 14;
        int trackX = x + width - trackW;
        int trackY = y + (height - trackH) / 2;

        int trackColor = on ? Theme.accentWithAlpha(140) : 0x55FFFFFF;
        com.nsclient.gui.RoundRect.fill(ctx, trackX, trackY, trackX + trackW, trackY + trackH, trackH / 2, trackColor);
        com.nsclient.gui.RoundRect.outline(ctx, trackX, trackY, trackX + trackW, trackY + trackH, trackH / 2, Theme.BORDER);

        int knobD = trackH - 4;
        int knobX = on ? trackX + trackW - knobD - 2 : trackX + 2;
        int knobColor = on ? (Theme.accent() | 0xFF000000) : 0xFFCFC7E0;
        com.nsclient.gui.RoundRect.fill(ctx, knobX, trackY + 2, knobX + knobD, trackY + 2 + knobD, knobD / 2, knobColor);
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public boolean mouseClicked(double mouseX, double mouseY) {
        if (isHovered(mouseX, mouseY)) {
            setter.accept(!getter.getAsBoolean());
            return true;
        }
        return false;
    }
}
