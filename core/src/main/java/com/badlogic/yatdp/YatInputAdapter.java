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
    // whether or not to show the menu
    public boolean showMenu = false;

    public Vector2 mousePosition = new Vector2();
    public Vector2 currentWindowPosition = new Vector2();

    Logger logger = new Logger("YatInputAdapter", Logger.DEBUG);


    public YatInputAdapter() {
        super();
        logger.info("InputAdapter initialized.");
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        logger.debug("Touch down at {" + x + ", " + y + "}, button " + button);

        if (button == Input.Buttons.LEFT) {
            isDragging = true;
            mousePosition.set(x, y);
            logger.info("click left mouse button");
            return true;
        }
        if (button == Input.Buttons.RIGHT) {
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
        if (button == Input.Buttons.MIDDLE) {
            // 中键点击切换菜单模式
            ((Main)Gdx.app.getApplicationListener()).toggleMenuMode();
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        logger.debug("Touch up at {" + x + ", " + y + "}, button " + button);

        if (button == Input.Buttons.LEFT) {
            isDragging = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        logger.debug("Touch dragged at {" + x + ", " + y + "}");

        if (isDragging) {
            Vector2 currentPos = new Vector2(x, y);
            Vector2 delta = currentPos.cpy().sub(mousePosition);
            // Get window handle then reset the window position according to the delta
            Lwjgl3Graphics graphics = (Lwjgl3Graphics) Gdx.graphics;
            Lwjgl3Window window = graphics.getWindow();

            currentWindowPosition.set(window.getPositionX() + delta.x, window.getPositionY() + delta.y);
            window.setPosition((int) (window.getPositionX() + delta.x), (int) (window.getPositionY() + delta.y));


            return true;
        }
        return false;
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

    private void restoreWindow() {
        logger.info("Window is restoring.");

        Lwjgl3Graphics graphics = (Lwjgl3Graphics) Gdx.graphics;
        Lwjgl3Window window = graphics.getWindow();
        Gdx.graphics.setWindowedMode(150, 150);
        window.setPosition((int) currentWindowPosition.x, (int) currentWindowPosition.y);
    }
}
