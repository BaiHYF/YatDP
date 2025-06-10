package com.badlogic.yatdp.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.yatdp.core.AppState;
import com.badlogic.yatdp.core.MainApp;
import com.badlogic.yatdp.input.WindowController;

import java.util.function.Consumer;

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

        createCustomButtonStyle();
    }

    public void render() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void resize(int width, int height, boolean showMenu) {
        stage.getViewport().update(width, height, true);
        if (showMenu) {
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
        style.font = skin.getFont("default-font");
        style.fontColor = Color.WHITE;
        style.downFontColor = Color.RED;
        style.overFontColor = Color.YELLOW;

        skin.add("custom-button", style, TextButton.TextButtonStyle.class);
    }

    private void createMenuTable() {
        Table table = new Table();
        table.setSize(150, 150);
        table.setPosition(150, 0);

        table.add(createButton("Minimize", windowController::minimize)).width(120).height(40).pad(5);
        table.row();

        table.add(createButton("App Description", () -> onContentRequested.accept(
            "Here is the program description...\nThis is a desktop pet program, made with LibGDX and Spine animations.")
        )).width(120).height(40).pad(5);
        table.row();

        table.add(createButton("Exit", onExit)).width(120).height(40).pad(5);

        stage.addActor(table);
    }

    private void createContentWindow() {
        stage.clear();

        Window contentWindow = new Window("Content", skin);
        contentWindow.setSize(300, 150);
        contentWindow.setPosition(0, 0);
        contentWindow.setTouchable(Touchable.enabled);

        Label contentLabel = new Label(currentContent, skin);
        contentLabel.setWrap(true);

        TextButton closeButton = new TextButton("Close", skin, "custom-button");
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                windowController.expand();
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
