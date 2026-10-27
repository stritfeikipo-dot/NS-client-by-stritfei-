package com.nsclient.hud;

import java.util.ArrayDeque;
import java.util.Deque;

/** Rolling 1-second window of left/right click timestamps, polled once a tick. */
public final class CpsTracker {

    private static final Deque<Long> leftClicks = new ArrayDeque<>();
    private static final Deque<Long> rightClicks = new ArrayDeque<>();

    private static boolean wasLeftDown = false;
    private static boolean wasRightDown = false;

    private CpsTracker() {
    }

    public static void tick(boolean leftDown, boolean rightDown) {
        long now = System.currentTimeMillis();
        if (leftDown && !wasLeftDown) {
            leftClicks.addLast(now);
        }
        if (rightDown && !wasRightDown) {
            rightClicks.addLast(now);
        }
        wasLeftDown = leftDown;
        wasRightDown = rightDown;

        trim(leftClicks, now);
        trim(rightClicks, now);
    }

    private static void trim(Deque<Long> deque, long now) {
        while (!deque.isEmpty() && now - deque.peekFirst() > 1000) {
            deque.pollFirst();
        }
    }

    public static int getCps() {
        return Math.max(leftClicks.size(), rightClicks.size());
    }
}
