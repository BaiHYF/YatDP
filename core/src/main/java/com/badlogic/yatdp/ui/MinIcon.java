package com.badlogic.yatdp.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.Logger;

/**
 * MinIcon 类负责在桌宠程序 YatDP 进入最小化状态时绘制简化图标。
 * 该图标作为缩小状态的可视反馈，居中显示在窗口中，用于提示用户应用仍在运行。
 *
 * <h3>功能概述</h3>
 * <ul>
 *     <li><b>render()</b>：使用 SpriteBatch 绘制 32x32 图标</li>
 *     <li><b>resize()</b>：在窗口尺寸改变时更新视口 {@link FitViewport}</li>
 *     <li><b>dispose()</b>：释放图像纹理和绘制资源</li>
 * </ul>
 *
 * <h3>模块协作结构</h3>
 * <pre>
 * MainApp
 *  └── MinIcon
 *       ├── Texture iconMinimized     // 图标资源
 *       ├── SpriteBatch batchMinimized // 绘制图标
 *       └── FitViewport viewportMinimized // 保证图标适配窗口缩放
 * </pre>
 *
 * <h3>使用场景</h3>
 * <ul>
 *     <li>应用通过 {@link com.badlogic.yatdp.input.WindowController#minimize()} 进入最小化状态</li>
 *     <li>在最小化模式中调用 {@link #render()} 显示 32x32 图标</li>
 *     <li>窗口大小变更时调用 {@link #resize(int, int)}</li>
 * </ul>
 *
 * <h3>状态控制示意</h3>
 * <pre>
 * [ NORMAL ] ← minimize ← [ MENU ]
 *     ↑                       ↓
 *    restore → [ MINIMIZED ] ← toggle
 * </pre>
 *
 * <h3>注意事项</h3>
 * <ul>
 *     <li>图标资源加载路径为 {@code icon/coffee-cup-icon.png}</li>
 *     <li>当前图标大小固定为 32x32，适配窗口通过 {@link FitViewport} 控制</li>
 *     <li>请在渲染主循环中调用 {@code render()} 方法</li>
 * </ul>
 *
 * <h3>日志标签</h3>
 * <pre>
 * Logger 标签为 "MinIcon"，用于追踪初始化状态
 * </pre>
 *
 * @author baiheyufei
 * @version 1.1
 */
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
        logger.info("MinIcon initialized.");
    }
}
