package com.nsclient.gui.widget;

import com.nsclient.gui.RoundRect;
import com.nsclient.gui.Theme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

/** A labelled horizontal slider between {@code min} and {@code max}. */
public class SliderWidget {

    public int x, y, width, height = 26;
    public final String label;
    private final double min, max;
    private final DoubleSupplier getter;
    private final DoubleConsumer setter;
    private boolean dragging = false;

    public SliderWidget(String label, double min, double max, DoubleSupplier getter, DoubleConsumer setter) {
        this.label = label;
        this.min = min;
        this.max = max;
        this.getter = getter;
        this.setter = setter;
    }

    public void render(DrawContext ctx) {
        MinecraftClient client = MinecraftClient.getInstance();
        double value = getter.getAsDouble();
        String text = label + ": " + trim(value);
        ctx.drawText(client.textRenderer, Text.literal(text), x, y, Theme.TEXT, false);

        int trackY = y + 14;
        int trackH = 4;
        RoundRect.fill(ctx, x, trackY, x + width, trackY + trackH, trackH / 2, 0x55FFFFFF);

        double t = clamp((value - min) / (max - min));
        int fillW = (int) (width * t);
        RoundRect.fill(ctx, x, trackY, x + fillW, trackY + trackH, trackH / 2, Theme.accentWithAlpha(220));

        int knobX = x + fillW;
        int knobD = 10;
        RoundRect.fill(ctx, knobX - knobD / 2, trackY + trackH / 2 - knobD / 2, knobX + knobD / 2, trackY + trackH / 2 + knobD / 2, knobD / 2, 0xFFEDE7FA);
    }

    private static String trim(double v) {
        if (v == Math.floor(v)) {
            return String.valueOf((long) v);
        }
        return String.format("%.2f", v);
    }

    private double clamp(double t) {
        return Math.max(0, Math.min(1, t));
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public boolean mouseClicked(double mouseX, double mouseY) {
        if (isHovered(mouseX, mouseY)) {
            dragging = true;
            applyFromMouseX(mouseX);
            return true;
        }
        return false;
    }

    public void mouseDragged(double mouseX) {
        if (dragging) {
            applyFromMouseX(mouseX);
        }
    }

    public void mouseReleased() {
        dragging = false;
    }

    private void applyFromMouseX(double mouseX) {
        double t = clamp((mouseX - x) / width);
        setter.accept(min + t * (max - min));
    }
                               }
