package com.badlogic.yatdp.ui;

/* MenuManager目前还存在一个潜在的非预期行为。
 *  当用户点击右键进入菜单，然后点击Minimize按钮将应用缩小，之后再通过
 *  点击右键复原应用 -- 这些都没有问题。
 *  问题在于经过上述操作后，当用户再次点击右键希望进入菜单界面时，
 *  第一次点击是无效的。即需要点两下右键才能重新进入菜单。
 *  暂时还没找到这个问题产生的原因。*/

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.yatdp.core.MainApp;
import com.badlogic.yatdp.input.WindowController;

import java.util.function.Consumer;

/**
 * MenuManager 负责管理桌宠程序 YatDP 的菜单界面与内容展示，
 * 提供最小化、应用说明与退出等交互选项，并在菜单与内容窗口之间进行切换。
 * 该类基于 LibGDX Scene2D UI 构建，封装了菜单 UI 的渲染与响应逻辑。
 *
 * <h3>功能概述</h3>
 * <ul>
 *     <li><b>render()</b>：调用 {@link Stage} 渲染 UI</li>
 *     <li><b>resize()</b>：窗口尺寸变更时重建 UI 元素</li>
 *     <li><b>createMenuUI()</b>：创建菜单或内容窗口</li>
 *     <li><b>dispose()</b>：释放 UI 资源</li>
 * </ul>
 *
 * <h3>交互行为</h3>
 * <ul>
 *     <li>点击「Minimize」按钮触发 {@link WindowController#minimize()}</li>
 *     <li>点击「App Description」触发自定义回调 {@code onContentRequested}</li>
 *     <li>点击「Exit」执行传入的 {@code onExit} 回调</li>
 *     <li>点击「Close」按钮调用 {@link MainApp#backToMenu()}</li>
 * </ul>
 *
 * <h3>模块协作结构</h3>
 * <pre>
 * MenuManager
 * ├── 控制 UI 渲染：Stage + Table + Window
 * ├── 调用 WindowController 进行窗口缩放
 * ├── 通过 Consumer<String> 提供内容展示回调
 * └── 与 MainApp、YatInputAdapter 配合进行状态切换
 * </pre>
 *
 * <h3>状态控制示意</h3>
 * <pre>
 * [ MENU ] ← backToMenu ← [ FULL_SCREEN ]
 *     ↑                     ↓
 *    toggle → [ NORMAL ] → minimize
 * </pre>
 *
 * <h3>已知问题</h3>
 * <blockquote>
 * 存在一个潜在 Bug：当用户右键打开菜单 → 点击「Minimize」→ 再次右键恢复时，
 * 再次尝试右键进入菜单会失效一次（需要点击两次），原因尚未定位。
 * </blockquote>
 *
 * <h3>注意事项</h3>
 * <ul>
 *     <li>所有 UI 绘制基于 {@link com.badlogic.gdx.scenes.scene2d.Stage}，需在主渲染线程调用</li>
 *     <li>按钮样式通过 Pixmap 创建灰色背景并注册为 "custom-button"</li>
 *     <li>文本内容展示通过 Label 实现自动换行</li>
 * </ul>
 *
 * @author baiheyufei
 * @version 1.1
 */
public class MenuManager {
    private static final Logger logger = new Logger("MenuManager", Logger.DEBUG);

    private final Stage stage;
    private final Skin skin;
    private final WindowController windowController;
    private final Consumer<String> onContentRequested;
    private final Runnable onExit;
    private String currentContent = "Menu";

    public MenuManager(WindowController windowController,
                       Consumer<String> onContentRequested,
                       Runnable onExit) {

        this.windowController = windowController;
        this.onContentRequested = onContentRequested;
        this.onExit = onExit;

        this.skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        this.stage = new Stage(new ScreenViewport());

        loadCustomFont();
        createCustomButtonStyle();
    }

    public void render() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void resize(int width, int height, boolean showMenuTable) {
        stage.getViewport().update(width, height, true);
        createMenuUI(showMenuTable);
    }

    public void createMenuUI(boolean showMenuTable) {
        stage.clear();
        if (showMenuTable) {
            createMenuTable();
        } else {
            createContentWindow();
        }
    }

    public void setCurrentContent(String content) {
        this.currentContent = content;
    }

    public Stage getStage() {
        return stage;
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    private void createCustomButtonStyle() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GRAY);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = new TextureRegionDrawable(new TextureRegion(texture));
        style.up = new TextureRegionDrawable(new TextureRegion(texture));
        style.down = new TextureRegionDrawable(new TextureRegion(texture)).tint(Color.DARK_GRAY);
        style.over = new TextureRegionDrawable(new TextureRegion(texture)).tint(Color.LIGHT_GRAY);
//        style.font = skin.getFont("default-font");
        style.font = skin.getFont("custom-font");
        style.fontColor = Color.WHITE;
        style.downFontColor = Color.RED;
        style.overFontColor = Color.YELLOW;

        skin.add("custom-button", style, TextButton.TextButtonStyle.class);
    }

    private void loadCustomFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
            Gdx.files.internal("fonts/MapleMono-NF-CN-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        BitmapFont customFont = generator.generateFont(parameter);
        skin.add("custom-font", customFont);

        generator.dispose();
    }

    private void createMenuTable() {
        Table table = new Table();
        table.setSize(150, 150);
        table.setPosition(150, 0);

        table.add(createButton("Minimize", windowController::minimize)).width(120).height(40).pad(5);
        table.row();

        table.add(createButton("About", () -> onContentRequested.accept(
                "Yet Another Tiny Desktop Pet\n" +
                    "Right click to restore from minimized."
            )
        )).width(120).height(40).pad(5);
        table.row();

        table.add(createButton("Exit", onExit)).width(120).height(40).pad(5);

        stage.addActor(table);
    }

    private void createContentWindow() {
        stage.clear();

        Window.WindowStyle dialogStyle = skin.get("dialog", Window.WindowStyle.class);
        dialogStyle.titleFont = skin.getFont("custom-font");
        dialogStyle.titleFontColor = Color.LIGHT_GRAY;

        Window contentWindow = new Window("About", dialogStyle);
        contentWindow.setSize(300, 150);
        contentWindow.setPosition(0, 0);
        contentWindow.setTouchable(Touchable.enabled);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("custom-font");
        labelStyle.fontColor = Color.LIGHT_GRAY;

        Label contentLabel = new Label(currentContent, labelStyle);
        contentLabel.setWrap(true);
        contentLabel.setAlignment(1);

        TextButton closeButton = new TextButton("[Ok]", skin, "custom-button");
        closeButton.getLabel().setAlignment(1);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Close this content window
                MainApp.getInstance().backToMenu();
            }

        });

        contentWindow.add(contentLabel).expand().fill().pad(10);
        contentWindow.row();
        contentWindow.add(closeButton).padBottom(10);

        stage.addActor(contentWindow);
    }

    private TextButton createButton(String text, Runnable action) {
        TextButton button = new TextButton(text, skin, "custom-button");
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                logger.info("Button clicked: " + text);
                action.run();
            }
        });
        return button;
    }
}
