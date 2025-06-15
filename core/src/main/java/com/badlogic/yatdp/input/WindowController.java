package com.badlogic.yatdp.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.math.Vector2;

/**
 * WindowController 是桌宠程序 YatDP 的窗口管理模块，
 * 负责控制窗口在不同状态下的位置与大小，包括拖拽移动、最小化、菜单展开与还原等操作。
 * 该类封装了对底层 {@link Lwjgl3Window} 的调用，使得窗口行为与应用逻辑解耦。
 *
 * <h3>功能概述</h3>
 * <ul>
 *     <li><b>moveBy</b>：根据偏移量移动窗口位置，通常由鼠标拖拽触发</li>
 *     <li><b>minimize</b>：将窗口缩小为图标状态（32×32），并标记为最小化</li>
 *     <li><b>expand</b>：展开窗口用于展示菜单界面（300×150）</li>
 *     <li><b>restore</b>：将窗口恢复为默认尺寸（150×150），并标记为未最小化</li>
 * </ul>
 *
 * <h3>尺寸定义</h3>
 * <ul>
 *     <li><code>MIN_WIDTH / MIN_HEIGHT</code>：最小化窗口的宽高</li>
 *     <li><code>MENU_WIDTH / MENU_HEIGHT</code>：菜单展开时窗口的尺寸</li>
 *     <li><code>DEFAULT_WIDTH / DEFAULT_HEIGHT</code>：正常状态窗口的尺寸</li>
 * </ul>
 *
 * <h3>模块协作结构</h3>
 * <pre>
 * WindowController
 * ├── 调用 Gdx.graphics.setWindowedMode(...) 控制窗口大小
 * ├── 获取 Lwjgl3Window 控制窗口位置（setPosition）
 * └── 由 YatInputAdapter 调用响应拖拽与菜单切换
 * </pre>
 *
 * <h3>状态控制示意</h3>
 * <pre>
 * [ NORMAL ] ← restore ← [ MENU ] ← expand ← [ FULL_SCREEN ]
 *     ↑                                 ↓
 *  minimize → [ MINIMIZED ] → restore
 * </pre>
 *
 * <h3>注意事项</h3>
 * <ul>
 *     <li>窗口移动基于像素坐标，需确保调用线程为渲染主线程</li>
 *     <li>该类依赖于 LWJGL3 后端，暂不支持其他平台</li>
 * </ul>
 *
 * @author baiheyufei
 * @version 1.1
 */
public class WindowController {
    private static final int MIN_WIDTH = 32, MIN_HEIGHT = 32;
    private static final int MENU_WIDTH = 300, MENU_HEIGHT = 150;
    private static final int DEFAULT_WIDTH = 150, DEFAULT_HEIGHT = 150;

    public void moveBy(Vector2 delta) {
        Lwjgl3Window window = getWindow();
        window.setPosition(window.getPositionX() + (int) delta.x,
            window.getPositionY() + (int) delta.y);
    }

    public void minimize() {
        YatInputAdapter.getInstance().setIsMinimized(true);
        Lwjgl3Window window = getWindow();
        int screenX = window.getPositionX();
        int screenY = window.getPositionY();
        Gdx.graphics.setWindowedMode(MIN_WIDTH, MIN_HEIGHT);
        window.setPosition(screenX, screenY);
    }

    public void expand() {
        Lwjgl3Window window = getWindow();
        int screenX = window.getPositionX();
        int screenY = window.getPositionY();
        Gdx.graphics.setWindowedMode(MENU_WIDTH, MENU_HEIGHT);
        window.setPosition(screenX, screenY);
    }

    public void restore() {
        YatInputAdapter.getInstance().setIsMinimized(false);
        Lwjgl3Window window = getWindow();
        int screenX = window.getPositionX();
        int screenY = window.getPositionY();
        Gdx.graphics.setWindowedMode(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        window.setPosition(screenX, screenY);
    }


    private Lwjgl3Window getWindow() {
        return ((Lwjgl3Graphics) Gdx.graphics).getWindow();
    }
}
