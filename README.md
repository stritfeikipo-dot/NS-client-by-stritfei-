# NS Client

PvP visuals client for the Holyworld server. Fabric, Minecraft 1.21.11, Java 21.
No aimbot, no auto-attack, no combat cheats — visuals only.

## What's actually implemented right now

- **HUD pill** (top-left): FPS, CPS, ping, coordinates, watermark. Merge/split
  which stats share one pill via **ClickGUI → HUD** (checkbox per stat; a
  checked stat is part of the merged group, unchecked stats get their own pill).
- **ClickGUI** (default key: **Right Shift**): sidebar with Visuals / HUD / Misc,
  ring-style toggles, sliders, "liquid glass" translucent-purple panels with a
  top sheen line.
- **Custom crosshair**: cross / dot / circle, size + on/off, replaces the
  vanilla crosshair via a mixin.
- **Config persistence**: `config/nsclient/config.json`, loaded on startup,
  saved whenever you change a setting or close the menu.
- **CPS tracking** for the HUD.

## What's a stub / not wired up yet

These toggles exist in the GUI and the config, but the actual visual effect
behind them (hit marker flash, hit/crit particles, combo counter logic, kill
effects, armor HUD, potion HUD, target HUD, keystroke HUD) is **not yet
implemented** — flipping the switch won't crash anything, it just doesn't draw
anything extra yet. Say which one you want next and I'll build that specific
piece properly instead of guessing at all of them at once.

## One real risk, flagged honestly

`InGameHudMixin` targets `InGameHud#renderCrosshair` to hide the vanilla
crosshair. That method's parameter types have changed across recent Minecraft
versions, and 1.21.11 is newer than what I can verify from memory — **this is
the one place in the project most likely to fail to apply** on first build. If
Actions fails specifically on that mixin, open the real `InGameHud` class for
1.21.11 (in your IDE after the first successful Gradle sync, or via a
decompiler) and tell me the actual method signature — it's a one-line fix.

Dependency versions in `gradle.properties` (Yarn mappings, Fabric API, Loom)
are current as of my last check; if the very first build fails on "could not
resolve dependency", the version numbers are the most likely cause — check
https://fabricmc.net/develop/template/ for the current numbers for 1.21.11 and
I'll update `gradle.properties`.

## Building locally
