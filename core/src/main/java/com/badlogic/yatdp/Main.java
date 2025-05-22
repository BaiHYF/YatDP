package com.badlogic.yatdp;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.FitViewport;


/**
 * Main 类是 YatDP 程序的核心入口，继承自 LibGDX 的 `ApplicationAdapter`。
 * 负责初始化应用核心组件、处理渲染循环及资源管理。
 *
 * @author baiheyufei <BaiHeYuFei@outlook.com>
 * @version 1.0
 */
public class Main extends ApplicationAdapter {
    Logger log = new Logger("YatDP_CORE", Logger.DEBUG);
    YatInputAdapter inputAdapter;

    String modelDirPath = "test/test_spine_model";
    String modelName = "build_char_002_amiya_winter#1";
    SpinePet pet;

    Texture icon;   // icon texture to be drawn when app is minimized
    SpriteBatch batchMinimized; // batch for drawing icon when app is minimized
    FitViewport viewportMinimized; // viewport for drawing icon when app is minimized

    @Override
    public void create() {
        Gdx.app.setLogLevel(Logger.DEBUG);
        inputAdapter = new YatInputAdapter();
        Gdx.input.setInputProcessor(inputAdapter);


        pet = new SpinePet(modelDirPath, modelName);

        log.info("APP initialized.");
        if (!Gdx.graphics.supportsDisplayModeChange()) {
            log.error("Display mode change is not supported.");
        }

        batchMinimized = new SpriteBatch();
        icon = new Texture("icon/coffee-cup-icon.png");
        icon.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        viewportMinimized = new FitViewport(32, 32);
    }

    @Override
    public void render() {
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);

        // if the app is not minimized, render the pet
        if (!inputAdapter.isAppMinimized) {
            float delta = Gdx.graphics.getDeltaTime();
            pet.render(delta);
        } else {
            // draw a 32x32 pixel icon in the center of the screen
            float windowWidth = Gdx.graphics.getWidth();
            float windowHeight = Gdx.graphics.getHeight();
            viewportMinimized.apply();
            batchMinimized.setProjectionMatrix(viewportMinimized.getCamera().combined);
            batchMinimized.begin();
            batchMinimized.draw(icon, 0, 0, viewportMinimized.getWorldWidth(), viewportMinimized.getWorldHeight());
            batchMinimized.end();
        }
    }

    @Override
    public void resize(int width, int height) {
        pet.setCamera();
        viewportMinimized.update(width, height, true);
        log.info("Resized.");
    }


    @Override
    public void dispose() {
        pet.dispose();
        log.info("YatDP core disposed.");
    }
}
