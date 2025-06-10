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
        Lwjgl3Window window = getWindow();
        int screenX = window.getPositionX();
        int screenY = window.getPositionY();
        Gdx.graphics.setWindowedMode(MIN_WIDTH, MIN_HEIGHT);
        window.setPosition(screenX, screenY);
    }

    public void expand() {
        Lwjgl3Window window = getWindow();
        int screenX = window.getPositionX();
        int screenY = window.getPositionY();
        Gdx.graphics.setWindowedMode(MENU_WIDTH, MENU_HEIGHT);
        window.setPosition(screenX, screenY);
    }

    public void restore() {
        Lwjgl3Window window = getWindow();
        int screenX = window.getPositionX();
        int screenY = window.getPositionY();
        Gdx.graphics.setWindowedMode(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        window.setPosition(screenX, screenY);
    }


    private Lwjgl3Window getWindow() {
        return ((Lwjgl3Graphics) Gdx.graphics).getWindow();
    }
}
