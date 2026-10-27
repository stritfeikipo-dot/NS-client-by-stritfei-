package com.nsclient.hud;

import com.nsclient.config.ConfigManager;
import com.nsclient.config.NSConfig;
import com.nsclient.gui.Theme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

/** Draws the top-left HUD pill stack every frame. Registered from the client entrypoint. */
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
        if (!cfg.hudEnabled || client.player == null || client.options.hudHidden) {
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

        int x = (int) cfg.hudX;
        int y = (int) cfg.hudY;

        for (List<HudElement> group : groups) {
            int width = pillWidth(client, group);
            Theme.pill(ctx, x, y, width, ROW_HEIGHT);

            int textX = x + PAD_X;
            int textY = y + (ROW_HEIGHT - 8) / 2;
            for (HudElement element : group) {
                String value = formatValue(client, element);
                String label = element.icon + " " + value;
                ctx.drawText(client.textRenderer, Text.literal(label), textX, textY, Theme.TEXT, true);
                textX += client.textRenderer.getWidth(label) + 10;
            }
            y += ROW_HEIGHT + GAP;
        }
    }

    private static int pillWidth(MinecraftClient client, List<HudElement> group) {
        int width = PAD_X * 2;
        for (int i = 0; i < group.size(); i++) {
            String label = group.get(i).icon + " " + formatValue(client, group.get(i));
            width += client.textRenderer.getWidth(label);
            if (i < group.size() - 1) {
                width += 10;
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
