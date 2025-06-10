package com.badlogic.yatdp.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.yatdp.ConfigManager;
import com.badlogic.yatdp.ui.MenuManager;
import com.badlogic.yatdp.ui.MinIcon;
import com.badlogic.yatdp.pet.SpinePet;
import com.badlogic.yatdp.input.WindowController;
import com.badlogic.yatdp.input.YatInputAdapter;

import java.util.function.Consumer;


/**
 * Main 类是 YatDP 程序的核心入口，继承自 LibGDX 的 `ApplicationAdapter`。
 * 负责初始化应用核心组件、处理渲染循环及资源管理。
 *
 * @author baiheyufei <BaiHeYuFei@outlook.com>
 * @version 1.0
 */
public class MainApp extends ApplicationAdapter {

    private static MainApp INSTANCE; // 单例
    private final Logger logger = new Logger("YatDP", Logger.DEBUG);
    private AppState appState = AppState.NORMAL;
    private SpinePet pet;
    private MinIcon minIcon;
    private MenuManager menuManager;
    private YatInputAdapter inputAdapter;

    public static MainApp getInstance() {
        return INSTANCE;
    }

    @Override
    public void create() {
        INSTANCE = this; // 初始化单例

        ConfigManager config = ConfigManager.loadConfig("config/config.json");
        if (config == null) throw new RuntimeException("Config load failed");

        pet = new SpinePet(config.modelDirPath, config.modelName);
        minIcon = new MinIcon();

        // 创建回调函数
        Consumer<String> showContentCallback = this::showFullContent;
        Runnable exitCallBack = () -> {
            Gdx.app.exit();
            System.exit(0);
        };
        WindowController windowController = new WindowController();

        // 初始化 MenuManager
        menuManager = new MenuManager(windowController, showContentCallback, exitCallBack);

        inputAdapter = new YatInputAdapter(windowController, pet, this);
        Gdx.input.setInputProcessor(new InputMultiplexer(menuManager.getStage(), inputAdapter));

        logger.info("YatDP initialized");
    }

    @Override
    public void render() {
        clearScreen();

        if (inputAdapter.isAppMinimized()) {
            minIcon.render();
            return;
        }

        float delta = Gdx.graphics.getDeltaTime();
        switch (appState) {
            case NORMAL:
                pet.render(delta);
                break;
            case MENU:
                pet.render(delta);
                menuManager.render();
                break;
            case FULL_SCREEN:
                menuManager.render();
                break;
        }

    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void resize(int width, int height) {
        pet.resize();
        minIcon.resize(width, height);
        boolean showMenuTable = appState == AppState.MENU;
        menuManager.resize(width, height, showMenuTable);
        logger.info("APP Resized.");
    }

    @Override
    public void dispose() {
        // Just dispose everything
        pet.dispose();
        minIcon.dispose();
        menuManager.dispose();
        logger.info("APP disposed.");
    }

    public void toggleMenuMode() {
        appState = (appState == AppState.NORMAL) ? AppState.MENU : AppState.NORMAL;
        menuManager.createMenuUI(appState == AppState.MENU);
    }

    public void showFullContent(String content) {
        appState = AppState.FULL_SCREEN;
        menuManager.setCurrentContent(content);
        menuManager.createMenuUI(false);
    }

    public void backToMenu() {
        appState = AppState.MENU;
        menuManager.createMenuUI(true);
    }

    public AppState getAppState() {
        return appState;
    }
}
