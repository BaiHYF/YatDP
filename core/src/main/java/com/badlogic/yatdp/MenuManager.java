package com.badlogic.yatdp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.Logger;

public class MenuManager {
    private final Main main;
    private Stage stage;
    private Skin skin;
    private String currentContent;

    Logger log = new Logger("MenuManager", Logger.DEBUG);

    public MenuManager(Main main) {
        this.main = main;
        skin = new Skin(Gdx.files.internal("data/uiskin.json")); // 需要uiskin.json文件
        stage = new Stage(new ScreenViewport());

        // 初始化内容
        currentContent = "Menu";
    }

    public void renderMenu() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void renderFullContent() {
        // 清屏
        Gdx.gl.glClear(Gdx.gl20.GL_COLOR_BUFFER_BIT);

        // 创建全屏内容窗口
        Window contentWindow = new Window("Content", skin);
        contentWindow.setSize(300, 150);
        contentWindow.setPosition(0, 0);
        contentWindow.setTouchable(Touchable.enabled); // 👈👈 必须加上这一句！

        Label contentLabel = new Label(currentContent, skin);
        contentLabel.setWrap(true);

        TextButton closeButton = new TextButton("exit", skin);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                log.info("close the text");
                main.backToMenu();
            }
        });

        contentWindow.add(contentLabel).expand().fill().pad(10);
        contentWindow.row();
        contentWindow.add(closeButton).padBottom(10);

        stage.clear();
        stage.addActor(contentWindow);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        createMenuUI();
    }

    private void createMenuUI() {
        stage.clear();

        if (main.currentState == Main.AppState.MENU) {
            Table menuTable = new Table();
            menuTable.setSize(150, 150);
            menuTable.setPosition(150, 0);

            TextButton aboutButton = new TextButton("App Description", skin);
            aboutButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    log.info("click the 'App Description' button");
                    main.showFullContent("Here is the program description...\n" +
                        "This is a desktop pet program, made with LibGDX and Spine animations.");
                }
            });

            TextButton contactButton = new TextButton("About Us", skin);
            contactButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    log.info("click the 'About Us' button");
                    main.showFullContent("About us:\nDeveloper: 114514\nContact: 10086");
                }
            });

            menuTable.add(aboutButton).width(120).height(40).pad(5);
            menuTable.row();
            menuTable.add(contactButton).width(120).height(40).pad(5);

            stage.addActor(menuTable);
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
}
