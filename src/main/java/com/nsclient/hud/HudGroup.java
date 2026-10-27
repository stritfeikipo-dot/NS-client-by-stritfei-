package com.nsclient.hud;

import com.nsclient.config.ConfigManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Resolves the current set of enabled {@link HudElement}s into one or more
 * ordered "pills", based on which element ids are marked as merged in the config.
 * Elements whose id is NOT in the merged set are rendered as their own standalone pill.
 */
public final class HudGroup {

    private HudGroup() {
    }

    public static List<List<HudElement>> resolveGroups(boolean[] enabledFlags) {
        Set<String> merged = ConfigManager.get().mergedHudElements;
        List<HudElement> enabled = new ArrayList<>();
        HudElement[] all = HudElement.values();
        for (int i = 0; i < all.length; i++) {
            if (enabledFlags[i]) {
                enabled.add(all[i]);
            }
        }

        List<List<HudElement>> groups = new ArrayList<>();
        List<HudElement> mergedGroup = new ArrayList<>();
        for (HudElement element : enabled) {
            if (merged.contains(element.id)) {
                mergedGroup.add(element);
            } else {
                groups.add(List.of(element));
            }
        }
        if (!mergedGroup.isEmpty()) {
            groups.add(0, mergedGroup);
        }
        return groups;
    }

    public static void toggleMerged(HudElement element) {
        Set<String> merged = ConfigManager.get().mergedHudElements;
        if (merged.contains(element.id)) {
            merged.remove(element.id);
        } else {
            merged.add(element.id);
        }
        ConfigManager.save();
    }

    public static boolean isMerged(HudElement element) {
        return ConfigManager.get().mergedHudElements.contains(element.id);
    }
}
