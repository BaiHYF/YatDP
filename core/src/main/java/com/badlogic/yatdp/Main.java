package com.badlogic.yatdp;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Logger;


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
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);

        pet.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        pet.setCamera();

        log.info("Resized.");
    }


    @Override
    public void dispose() {
        pet.dispose();
        log.info("YatDP core disposed.");
    }
}
