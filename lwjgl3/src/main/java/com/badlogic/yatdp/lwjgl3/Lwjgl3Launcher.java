package com.badlogic.yatdp.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.badlogic.gdx.tests.*;
import com.badlogic.yatdp.Main;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

/**
 * Launches the desktop (LWJGL3) application.
 */
public class Lwjgl3Launcher {

    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    private static void createApplication() {
        new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Yet Another Tiny Desktop pet.");
        //// Vsync limits the frames per second to what your hardware can display, and helps eliminate
        //// screen tearing. This setting doesn't always work on Linux, so the line after is a safeguard.
        configuration.useVsync(true);
        //// Limits FPS to the refresh rate of the currently active monitor, plus 1 to try to match fractional
        //// refresh rates. The Vsync setting above should limit the actual FPS to match the monitor.
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
        //// useful for testing performance, but can also be very stressful to some hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.

        configuration.setWindowedMode(150, 150);
        //// You can change these files; they are in lwjgl3/src/main/resources/ .
        //// They can also be loaded from the root of assets/ .
        configuration.setWindowIcon("赤色のチューリップx128.png", "赤色のチューリップx64.png", "赤色のチューリップx32.png", "赤色のチューリップx16.png");
//        configuration.setWindowedMode(150, 150);
        configuration.setWindowPosition(0, 50);   // TO BE SET IN CONFIG
//        configuration.setDecorated(true);
        configuration.setDecorated(false);
//        configuration.setResizable(true);
        configuration.setTransparentFramebuffer(true);
        configuration.setWindowListener(new Lwjgl3WindowAdapter() {
            @Override
            public void created(Lwjgl3Window window) {
                // 初始化时启用置顶，之后可通过配置文件来实现是否启用置顶的功能
                GLFW.glfwSetWindowAttrib(window.getWindowHandle(), GLFW.GLFW_FLOATING, GLFW.GLFW_TRUE);
                if (SystemTray.isSupported()) {
                    System.out.println("系统托盘支持已启用");
                    try {
                        SystemTray.getSystemTray().add(createTrayIcon());
                    } catch (AWTException e) {
//                        e.printStackTrace();
                    }
                }
            }
        });
        return configuration;
    }

    private static TrayIcon createTrayIcon() {
        Image icon = Toolkit.getDefaultToolkit().getImage(
            Lwjgl3Launcher.class.getResource("/赤色のチューリップx32.png")
        );

        if (icon == null) {
            System.err.println("托盘图标加载失败！请检查资源路径。");
            return null;
        }

        return getTrayIcon(icon);
    }

    private static TrayIcon getTrayIcon(Image icon) {
        TrayIcon trayIcon = new TrayIcon(icon, "Yet Another Tiny Desktop Pet");
        trayIcon.setImageAutoSize(true);

        PopupMenu popup = new PopupMenu();

        // 分隔线
        popup.addSeparator();

//        MenuItem's Label cannot print chinese characters
//        MenuItem exitItem = new MenuItem("退出应用");
        // TODO: Use Swing.JPopupMenu instead of AWT.PopupMenu
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> {
            Gdx.app.exit();
            System.exit(0);
        });
        popup.add(exitItem);
        popup.addSeparator();

        trayIcon.setPopupMenu(popup);

        popup.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // 设置字体
        trayIcon.setPopupMenu(popup);

        return trayIcon;
    }
}
