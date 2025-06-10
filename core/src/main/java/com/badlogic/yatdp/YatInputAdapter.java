package com.badlogic.yatdp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;

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

    public boolean isDragging = false;
    public boolean isAppMinimized = false;
    public boolean isMenunShown = false;
    // whether or not to show the menu
    public boolean showMenu = false;

    public Vector2 mousePosition = new Vector2();
    public Vector2 currentWindowPosition = new Vector2();
    public boolean isLeftButtonPressed = false;
    Logger logger = new Logger("YatInputAdapter", Logger.DEBUG);

    private SpinePet petInstance;

    public YatInputAdapter() {
        super();
        logger.info("InputAdapter initialized.");
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        logger.info("Touch down at {" + x + ", " + y + "}, button " + button);

        if (button == Input.Buttons.LEFT) {
            mousePosition.set(x, y);
            isLeftButtonPressed = true;
            return true;
        }
        if (button == Input.Buttons.MIDDLE) {
            if (!isAppMinimized) {
                Lwjgl3Graphics graphics = (Lwjgl3Graphics) Gdx.graphics;
                Lwjgl3Window window = graphics.getWindow();
                currentWindowPosition.set(window.getPositionX(), window.getPositionY());
                minimizeWindow();
                isAppMinimized = true;
            } else {
                restoreWindow();
                isAppMinimized = false;
            }
            return true;
        }
        if (button == Input.Buttons.RIGHT) {
            // 右键点击时显示/隐藏菜单

            if (!isMenunShown) {
                Lwjgl3Graphics graphics = (Lwjgl3Graphics) Gdx.graphics;
                Lwjgl3Window window = graphics.getWindow();
                currentWindowPosition.set(window.getPositionX(), window.getPositionY());
                expandWindow();
                isMenunShown = true;
            } else {
                restoreWindow();
                isMenunShown = false;
            }

            ((Main)Gdx.app.getApplicationListener()).toggleMenuMode();
            return false;
        }
        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        logger.debug("Touch up at {" + x + ", " + y + "}, button " + button);

        if (button == Input.Buttons.LEFT) {
            isLeftButtonPressed = false;
            if (!isDragging) {
                petInstance.onClicked();
            }
            isDragging = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        logger.info("Touch dragged at {" + x + ", " + y + "}");

        if (!isLeftButtonPressed) {
            return false;
        }

        isDragging = true;

        Vector2 currentPos = new Vector2(x, y);
        Vector2 delta = currentPos.cpy().sub(mousePosition);
        // Get window handle then reset the window position according to the delta
        Lwjgl3Graphics graphics = (Lwjgl3Graphics) Gdx.graphics;
        Lwjgl3Window window = graphics.getWindow();

        currentWindowPosition.set(window.getPositionX() + delta.x, window.getPositionY() + delta.y);
        window.setPosition((int) (window.getPositionX() + delta.x), (int) (window.getPositionY() + delta.y));

        return true;
    }

    public void setSpinePet(SpinePet pet) {
        this.petInstance = pet;
    }

    /// Shrink the Window to 32x32
    private void minimizeWindow() {
        logger.info("Window is shrinking to 32x32.");

        Lwjgl3Graphics graphics = (Lwjgl3Graphics) Gdx.graphics;
        Lwjgl3Window window = graphics.getWindow();

        int screenX = window.getPositionX();
        int screenY = window.getPositionY();
        Gdx.graphics.setWindowedMode(32, 32);
        window.setPosition(screenX, screenY);
    }

    ///  Expand the Window to 300 x 150
    private void expandWindow() {
        logger.info("Window is expanding to 300x150");

        Lwjgl3Graphics graphics = (Lwjgl3Graphics) Gdx.graphics;
        Lwjgl3Window window = graphics.getWindow();

        int screenX = window.getPositionX();
        int screenY = window.getPositionY();
        Gdx.graphics.setWindowedMode(300, 150);
        window.setPosition(screenX, screenY);
    }

    private void restoreWindow() {
        logger.info("Window is restoring.");

        Lwjgl3Graphics graphics = (Lwjgl3Graphics) Gdx.graphics;
        Lwjgl3Window window = graphics.getWindow();
//        if (((Main)Gdx.app.getApplicationListener()).currentState == Main.AppState.NORMAL) {
//            Gdx.graphics.setWindowedMode(150, 150);
//        } else {
//            Gdx.graphics.setWindowedMode(300, 150);
//        }
        Gdx.graphics.setWindowedMode(150, 150);
        window.setPosition((int) currentWindowPosition.x, (int) currentWindowPosition.y);
    }
}
