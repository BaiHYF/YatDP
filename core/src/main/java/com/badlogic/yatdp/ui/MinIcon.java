package com.badlogic.yatdp.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.Logger;

public class MinIcon {

    /* Utils for minimizing the app~ */

    Logger logger = new Logger("MinIcon", Logger.DEBUG);

    /// icon texture to be drawn when app is minimized
    Texture iconMinimized;
    /// batch for drawing icon when app is minimized
    SpriteBatch batchMinimized;
    /// viewport for drawing icon when app is minimized
    FitViewport viewportMinimized;

    public MinIcon() {
        init();
    }

    public void render() {
        // draw a 32x32 pixel icon in the center of the screen
        viewportMinimized.apply();
        batchMinimized.setProjectionMatrix(viewportMinimized.getCamera().combined);
        batchMinimized.begin();
        batchMinimized.draw(iconMinimized, 0, 0, viewportMinimized.getWorldWidth(), viewportMinimized.getWorldHeight());
        batchMinimized.end();
    }

    public void resize(int width, int height) {
        viewportMinimized.update(width, height, true);
    }

    public void dispose() {
        iconMinimized.dispose();
        batchMinimized.dispose();
    }

    void init() {
        batchMinimized = new SpriteBatch();
        iconMinimized = new Texture("icon/coffee-cup-icon.png");
        iconMinimized.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        viewportMinimized = new FitViewport(32, 32);
        logger.info("MinIcon inited.");
    }
}
