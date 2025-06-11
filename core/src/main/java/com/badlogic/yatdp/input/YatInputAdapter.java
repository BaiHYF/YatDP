package com.badlogic.yatdp.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.yatdp.pet.SpinePet;
import com.badlogic.yatdp.core.MainApp;

/* About the InputAdapter, see: https://libgdx.com/wiki/input/event-handling */

/**
 * YatInputAdapter 类用于处理窗口拖拽相关的输入事件，继承自 LibGDX 的 InputAdapter。
 * 该类通过监听触摸事件实现窗口的拖拽移动功能，并记录相关日志信息。
 * <p>
 * 关于 LibGDX 的 InputAdapter，请参考：<a href="https://libgdx.com/wiki/input/event-handling">...</a>
 *
 * @author baiheyufei <BaiHeYuFei@outlook.com>
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
        if (isMinimized) {
            windowController.restore();
            isMinimized = false;
        } else {
            app.toggleMenuMode();
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
