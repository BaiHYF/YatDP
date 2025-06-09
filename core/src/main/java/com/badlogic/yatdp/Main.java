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

        // 加载配置文件（支持 JSON 或 XML 格式）
        ConfigManager config = ConfigManager.loadConfig("config/config.json");
        // ConfigManager config = ConfigManager.loadConfig("config/config.xml");
        if (config != null) {
            // 配置加载成功，将配置中的参数应用到系统初始化中
            modelDirPath = config.modelDirPath;
            modelName    = config.modelName;
            log.info("配置文件加载成功，配置信息：");
            log.info("modelDirPath: " + config.modelDirPath);
            log.info("modelName: " + config.modelName);
            log.info("defaultWindowWidth: " + config.defaultWindowWidth);
            log.info("defaultWindowHeight: " + config.defaultWindowHeight);
            log.info("iconPath: " + config.iconPath);
            log.info("defaultAnimationName: " + config.defaultAnimationName);
            log.info("onClickedAnimationName: " + config.onClickedAnimationName);
        } else {
            log.error("配置文件加载失败，使用硬编码默认参数初始化！");
        }

        pet = new SpinePet(modelDirPath, modelName);

        inputAdapter = new YatInputAdapter();
        inputAdapter.setSpinePet(pet);
        Gdx.input.setInputProcessor(inputAdapter);

        log.info("APP initialized.");
        if (!Gdx.graphics.supportsDisplayModeChange()) {
            log.error("Display mode change is not supported.");
        }

        minIcon = new MinIcon();
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
        // Just dispose everything
        pet.dispose();
        minIcon.dispose();
        log.info("APP disposed.");
    }
}
