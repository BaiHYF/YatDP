package com.badlogic.yatdp.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.yatdp.ui.MenuManager;
import com.badlogic.yatdp.ui.MinIcon;
import com.badlogic.yatdp.pet.SpinePet;
import com.badlogic.yatdp.input.WindowController;
import com.badlogic.yatdp.input.YatInputAdapter;

import java.util.function.Consumer;


/**
 * MainApp 是桌宠程序 YatDP 的核心类，负责应用生命周期的管理与组件初始化。
 * 该类继承自 LibGDX 的 {@link ApplicationAdapter}，在桌面运行时会自动被调用。
 *
 * <p>主要职责包括：</p>
 * <ul>
 *     <li>初始化桌宠模型（SpinePet）、菜单（MenuManager）、最小化图标（MinIcon）等核心组件</li>
 *     <li>管理不同应用状态（普通、菜单、全屏）之间的切换</li>
 *     <li>统一处理渲染循环、屏幕尺寸变化和资源释放</li>
 * </ul>
 *
 * <h3>模块结构</h3>
 * <pre>
 * MainApp
 * ├── SpinePet         // 动画模型：负责桌宠的 Spine 渲染与交互动画
 * ├── MenuManager      // 菜单交互管理器：右键菜单、内容展示、UI 切换等
 * ├── MinIcon          // 最小化图标：托盘图标或窗口缩小时的展示元素
 * ├── YatInputAdapter  // 输入处理器：统一接收鼠标点击、拖拽、快捷键输入等
 * └── WindowController // 窗口控制器：控制窗口的移动、最小化、恢复等行为
 * </pre>
 *
 * <h3>状态控制逻辑</h3>
 * 应用通过 {@link AppState} 枚举维护 UI 状态，状态转换如下：
 * <pre>
 * [ NORMAL ] ← toggleMenuMode() → [ MENU ] ← backToMenu() ← [ FULL_SCREEN ]
 * </pre>
 *
 * <h3>外部调用接口</h3>
 * <ul>
 *     <li>{@link #toggleMenuMode()} 切换菜单显示/隐藏</li>
 *     <li>{@link #showFullContent(String)} 显示全屏内容</li>
 *     <li>{@link #backToMenu()} 返回菜单主界面</li>
 *     <li>{@link #getAppState()} 获取当前状态</li>
 * </ul>
 *
 * <p>该类设计为单例，外部模块可通过 {@link #getInstance()} 访问其全局实例。</p>
 *
 * @author baiheyufei
 * @version 1.1
 * @see com.badlogic.yatdp.pet.SpinePet
 * @see com.badlogic.yatdp.ui.MenuManager
 * @see com.badlogic.yatdp.ui.MinIcon
 * @see com.badlogic.yatdp.input.YatInputAdapter
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

        if (inputAdapter.getIsMinimized()) {
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

    /**
     * 显示完整文本内容（如“程序说明”），进入 FULL_SCREEN 模式。
     *
     * @param content 要展示的完整文本
     */
    public void showFullContent(String content) {
        appState = AppState.FULL_SCREEN;
        menuManager.setCurrentContent(content);
        menuManager.createMenuUI(false);
    }

    /**
     * 从 FULL_SCREEN 返回菜单界面。
     */
    public void backToMenu() {
        appState = AppState.MENU;
        menuManager.createMenuUI(true);
    }

    public AppState getAppState() {
        return appState;
    }
}
