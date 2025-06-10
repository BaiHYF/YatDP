package com.badlogic.yatdp.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.math.Vector2;

public class WindowController {
    private static final int MIN_WIDTH = 32, MIN_HEIGHT = 32;
    private static final int MENU_WIDTH = 300, MENU_HEIGHT = 150;
    private static final int DEFAULT_WIDTH = 150, DEFAULT_HEIGHT = 150;

    private final Vector2 lastWindowPosition = new Vector2();

    public void moveBy(Vector2 delta) {
        Lwjgl3Window window = getWindow();
        window.setPosition(window.getPositionX() + (int) delta.x,
            window.getPositionY() + (int) delta.y);
    }

    public void minimize() {
        saveCurrentPosition();
        Gdx.graphics.setWindowedMode(MIN_WIDTH, MIN_HEIGHT);
    }

    public void expand() {
        saveCurrentPosition();
        Gdx.graphics.setWindowedMode(MENU_WIDTH, MENU_HEIGHT);
    }

    public void restore() {
        Gdx.graphics.setWindowedMode(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        getWindow().setPosition((int) lastWindowPosition.x, (int) lastWindowPosition.y);
    }

    private void saveCurrentPosition() {
        Lwjgl3Window window = getWindow();
        lastWindowPosition.set(window.getPositionX(), window.getPositionY());
    }

    private Lwjgl3Window getWindow() {
        return ((Lwjgl3Graphics) Gdx.graphics).getWindow();
    }
}
