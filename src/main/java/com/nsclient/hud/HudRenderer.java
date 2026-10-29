package com.nsclient.hud;

import com.nsclient.config.ConfigManager;
import com.nsclient.config.NSConfig;
import com.nsclient.gui.RoundRect;
import com.nsclient.gui.Theme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

/** Draws the top-center HUD pill stack every frame. Registered from the client entrypoint. */
public final class HudRenderer {

    private HudRenderer() {
    }

    private static final int PAD_X = 8;
    private static final int PAD_Y = 4;
    private static final int GAP = 4;
    private static final int ROW_HEIGHT = 16;

    public static void render(DrawContext ctx, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        NSConfig cfg = ConfigManager.get();
        if (!cfg.hudEnabled || client.player == null || client.options.hudHidden || client.currentScreen != null) {
            return;
        }

        boolean[] enabled = {
                cfg.showFps,
                cfg.showCps,
                cfg.showPing,
                cfg.showCoords,
                cfg.showWatermark,
        };
        List<List<HudElement>> groups = HudGroup.resolveGroups(enabled);
        if (groups.isEmpty()) {
            return;
        }

        int screenW = client.getWindow().getScaledWidth();
        int y = 8;

        for (List<HudElement> group : groups) {
            int width = pillWidth(client, group);
            int x = (screenW - width) / 2;

            // soft drop shadow for depth
            RoundRect.fill(ctx, x + 1, y + 2, x + 1 + width, y + 2 + ROW_HEIGHT, ROW_HEIGHT / 2, 0x33000000);
            Theme.pill(ctx, x, y, width, ROW_HEIGHT);

            int textX = x + PAD_X;
            int textY = y + (ROW_HEIGHT - 8) / 2;
            for (int i = 0; i < group.size(); i++) {
                HudElement element = group.get(i);
                String value = formatValue(client, element);
                int labelColor = Theme.accent() | 0xFF000000;
                String iconText = element.icon;
                ctx.drawText(client.textRenderer, Text.literal(iconText), textX, textY, labelColor, true);
                textX += client.textRenderer.getWidth(iconText) + 4;
                ctx.drawText(client.textRenderer, Text.literal(value), textX, textY, Theme.TEXT, true);
                textX += client.textRenderer.getWidth(value) + 8;

                if (i < group.size() - 1) {
                    ctx.fill(textX, y + 4, textX + 1, y + ROW_HEIGHT - 4, 0x40FFFFFF);
                    textX += 9;
                }
            }
            y += ROW_HEIGHT + GAP;
        }
    }

    private static int pillWidth(MinecraftClient client, List<HudElement> group) {
        int width = PAD_X * 2;
        for (int i = 0; i < group.size(); i++) {
            HudElement element = group.get(i);
            String value = formatValue(client, element);
            width += client.textRenderer.getWidth(element.icon) + 4 + client.textRenderer.getWidth(value);
            if (i < group.size() - 1) {
                width += 17;
            } else {
                width += 8;
            }
        }
        return width;
    }

    private static String formatValue(MinecraftClient client, HudElement element) {
        return switch (element) {
            case FPS -> String.valueOf(client.getCurrentFps());
            case CPS -> String.valueOf(CpsTracker.getCps());
            case PING -> String.valueOf(pingOf(client));
            case COORDS -> coordsOf(client);
            case WATERMARK -> "Client";
        };
    }

    private static int pingOf(MinecraftClient client) {
        if (client.getNetworkHandler() == null || client.player == null) {
            return 0;
        }
        var entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
        return entry != null ? entry.getLatency() : 0;
    }

    private static String coordsOf(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return "0, 0, 0";
        }
        return (int) player.getX() + ", " + (int) player.getY() + ", " + (int) player.getZ();
    }
}
