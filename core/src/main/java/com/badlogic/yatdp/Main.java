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


    /* Utils for drawing pet model~ */

    // TODO: 添加配置文件模块，模型路径写在配置文件中，模型初始化时读取。
    String modelDirPath = "test/test_spine_model";
    String modelName = "build_char_002_amiya_winter#1";
    SpinePet pet;


    /* Utils for minimizing the app~ */
    MinIcon minIcon;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Logger.DEBUG);
        inputAdapter = new YatInputAdapter();
        Gdx.input.setInputProcessor(inputAdapter);
        pet = new SpinePet(modelDirPath, modelName);
        minIcon = new MinIcon();

        if (!Gdx.graphics.supportsDisplayModeChange()) {
            log.error("Display mode change is not supported.");
        }
        log.info("APP initialized.");
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
            minIcon.render();
        }
    }

    @Override
    public void resize(int width, int height) {
        pet.resize();
        minIcon.resize(width, height);
        log.info("APP Resized.");
    }


    @Override
    public void dispose() {
        pet.dispose();
        minIcon.dispose();
        log.info("APP disposed.");
    }
}
