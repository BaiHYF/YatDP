package com.badlogic.yatdp;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
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
    public AppState currentState = AppState.NORMAL;
    Logger log = new Logger("YatDP_CORE", Logger.DEBUG);


    /* Utils for drawing pet model~ */
    YatInputAdapter inputAdapter;
    String modelDirPath = "test/test_spine_model";
    String modelName = "build_char_002_amiya_winter#1";
    SpinePet pet;
    /* Utils for minimizing the app~ */
    MinIcon minIcon;
    MenuManager menuManager;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Logger.DEBUG);
        ConfigManager config = ConfigManager.loadConfig("config/config.json");
        if (config != null) {
            modelDirPath = config.modelDirPath;
            modelName = config.modelName;
            log.info("配置文件加载成功，配置信息：");
            log.info("modelDirPath: " + config.modelDirPath);
            log.info("modelName: " + config.modelName);
            log.info("defaultWindowWidth: " + config.defaultWindowWidth);
            log.info("defaultWindowHeight: " + config.defaultWindowHeight);
            log.info("iconPath: " + config.iconPath);
            log.info("defaultAnimationName: " + config.defaultAnimationName);
            log.info("onClickedAnimationName: " + config.onClickedAnimationName);
            pet = new SpinePet(config.modelDirPath, config.modelName);
        }
        inputAdapter = new YatInputAdapter();
        inputAdapter.setSpinePet(pet);
        menuManager = new MenuManager(this);
        minIcon = new MinIcon();
        // 创建输入多路复用器
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(menuManager.getStage());
        multiplexer.addProcessor(inputAdapter);

        Gdx.input.setInputProcessor(multiplexer); // 设置为统一处理器

        if (!Gdx.graphics.supportsDisplayModeChange()) {
            log.error("Display mode change is not supported.");
        }
        log.info("APP initialized.");
    }

    @Override
    public void render() {
        if (currentState == AppState.FULL_SCREEN) {
            Gdx.input.setInputProcessor(menuManager.getStage());
        } else {
            InputMultiplexer multiplexer = new InputMultiplexer();
            multiplexer.addProcessor(menuManager.getStage());
            multiplexer.addProcessor(inputAdapter);
            Gdx.input.setInputProcessor(multiplexer);
        }

        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);

        // if the app is not minimized, render the pet
        if (!inputAdapter.isAppMinimized) {
            float delta = Gdx.graphics.getDeltaTime();

            switch (currentState) {
                case NORMAL:
                    pet.render(delta);
                    break;
                case MENU:
                    pet.render(delta);
                    menuManager.renderMenu();
                    break;
                case FULL_SCREEN:
                    menuManager.renderMenu();
                    break;
            }

        } else {
            minIcon.render();
        }
    }

    @Override
    public void resize(int width, int height) {
        pet.resize();
        minIcon.resize(width, height);
        menuManager.resize(width, height);
        log.info("APP Resized.");
    }

    @Override
    public void dispose() {
        // Just dispose everything
        pet.dispose();
        minIcon.dispose();
        menuManager.dispose();
        log.info("APP disposed.");
    }

    public void toggleMenuMode() {
        if (currentState == AppState.NORMAL) {
            currentState = AppState.MENU;
        } else if (currentState == AppState.MENU) {
            currentState = AppState.NORMAL;
        }
        log.info("Now app state is " + currentState);
        // FULL_SCREEN状态只能通过菜单按钮退出
    }

    public void showFullContent(String content) {
        log.info("show full content");
        currentState = AppState.FULL_SCREEN;
        menuManager.setCurrentContent(content);
        menuManager.createMenuUI();
    }

    public void backToMenu() {
        log.info("back to menu");
        currentState = AppState.MENU;
        menuManager.createMenuUI();
    }

    public enum AppState {
        NORMAL,
        MENU,
        FULL_SCREEN
    }
}
