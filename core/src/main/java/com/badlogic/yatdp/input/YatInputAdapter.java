package com.badlogic.yatdp.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.yatdp.pet.SpinePet;
import com.badlogic.yatdp.core.MainApp;

/* About the InputAdapter, see: https://libgdx.com/wiki/input/event-handling */

/**
 * YatInputAdapter 是桌宠程序 YatDP 的输入处理核心类，继承自 LibGDX 的 {@link InputAdapter}。
 * 主要负责监听并响应鼠标输入事件，实现窗口拖拽、点击交互及右键菜单控制等功能。
 *
 * <h3>功能概述</h3>
 * <ul>
 *     <li>窗口拖拽：监听鼠标左键按下与拖动，调用 {@link WindowController#moveBy(Vector2)} 实现窗口随鼠标移动</li>
 *     <li>桌宠点击：在鼠标左键点击但未拖动时，调用 {@link SpinePet#onClicked()} 响应点击动画</li>
 *     <li>右键菜单：右键点击时切换应用状态（菜单展开/收起），并控制窗口尺寸显示</li>
 *     <li>最小化恢复：若当前处于最小化状态，右键点击将恢复窗口显示</li>
 * </ul>
 *
 * <h3>状态变量说明</h3>
 * <ul>
 *     <li><code>isLeftPressed</code>：记录左键是否按下</li>
 *     <li><code>isDragging</code>：判断当前是否在拖动中</li>
 *     <li><code>isMinimized</code>：记录窗口是否处于最小化状态</li>
 *     <li><code>isMenuShown</code>：菜单是否处于展开状态</li>
 * </ul>
 *
 * <h3>模块协作结构</h3>
 * <pre>
 * YatInputAdapter
 * ├── WindowController  // 控制窗口的位置、大小与恢复逻辑
 * ├── SpinePet          // 响应用户点击动作
 * └── MainApp           // 控制菜单切换与状态跳转
 * </pre>
 *
 * <h3>核心逻辑示意</h3>
 * <pre>
 * 鼠标左键按下 → 记录位置
 * 鼠标拖动    → 计算偏移量并移动窗口
 * 鼠标释放    → 若未拖动，则触发宠物点击动画
 *
 * 鼠标右键点击：
 *   - 若最小化 → 恢复窗口
 *   - 否则     → 切换菜单显示状态
 * </pre>
 *
 * <h3>参考</h3>
 * 关于 LibGDX 的输入处理系统，参见：
 * <a href="https://libgdx.com/wiki/input/event-handling">LibGDX Input Event Handling</a>
 *
 * @author baiheyufei
 * @version 1.1
 */
public class YatInputAdapter extends InputAdapter {

    private static YatInputAdapter INSTANCE;
    private final WindowController windowController;
    private final SpinePet pet;
    private final MainApp app;

    private final Vector2 mouseDownPos = new Vector2();
    private boolean isLeftPressed = false;
    private boolean isDragging = false;
    private boolean isMinimized = false;
    private boolean isMenuShown = false;

    private final Logger logger = new Logger("InputAdapter", Logger.DEBUG);

    public YatInputAdapter(WindowController controller, SpinePet pet, MainApp app) {
        super();
        this.windowController = controller;
        this.pet = pet;
        this.app = app;

        INSTANCE = this;

        logger.info("YatInputAdapter initialized");
    }

    public static YatInputAdapter getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            mouseDownPos.set(x, y);
            isLeftPressed = true;
            return true;
        }
        if (button == Input.Buttons.RIGHT) {
            handleRightClick();
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            isLeftPressed = false;
            if (!isDragging) pet.onClicked();
            isDragging = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        if (!isLeftPressed) return false;

        isDragging = true;
        Vector2 delta = new Vector2(x, y).sub(mouseDownPos);
        windowController.moveBy(delta);
        return true;
    }

    private void handleRightClick() {
        app.toggleMenuMode();
        if (isMinimized) {
            isMenuShown = false;
            logger.info("Menu hidden from minimize.");
            windowController.restore();
            isMinimized = false;
        } else {
            if (!isMenuShown) {
                logger.info("Menu shown.");
                windowController.expand();
            } else {
                logger.info("Menu hidden.");
                windowController.restore();
            }
            isMenuShown = !isMenuShown;
        }
    }

    // Getter and Setter methods

    public boolean getIsMinimized() {
        return isMinimized;
    }
    public void setIsMinimized(boolean isMinimized) {
        this.isMinimized = isMinimized;
    }
}
