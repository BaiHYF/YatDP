package com.badlogic.yatdp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
        TextButtonStyle style = new TextButton.TextButtonStyle();

        // 创建纯色背景（需要 TextureRegionDrawable）
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.GRAY);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        style.up = new TextureRegionDrawable(new TextureRegion(texture));
        style.down = new TextureRegionDrawable(new TextureRegion(texture)).tint(Color.DARK_GRAY);
        style.over = new TextureRegionDrawable(new TextureRegion(texture)).tint(Color.LIGHT_GRAY);

        style.font = skin.getFont("default-font"); // ✅ 正确！
        style.fontColor = Color.WHITE;
        style.downFontColor = Color.RED;
        style.overFontColor = Color.YELLOW;
        skin.add("custom-button", style, TextButtonStyle.class);
    }

    public void renderMenu() {
//        log.info("renderMenu");
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        createMenuUI();
    }

    public void createMenuUI() {
        log.info("createMenuUI " + main.currentState);
        stage.clear();


        if (main.currentState == Main.AppState.MENU) {
            Table menuTable = new Table();

            menuTable.setSize(150, 150);
            menuTable.setPosition(150, 0);

            // create buttons on menu

            TextButton aboutButton = new TextButton("App Description", skin, "custom-button");

            aboutButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    log.info("click the 'App Description' button");
                    main.showFullContent("Here is the program description...\n" +
                        "This is a desktop pet program, made with LibGDX and Spine animations.");
                }
            });

            TextButton contactButton = new TextButton("About Us", skin, "custom-button");
            contactButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    log.info("click the 'About Us' button");
                    main.showFullContent("About us:\nDeveloper: 114514\nContact: 10086");
                }
            });

            TextButton minimizeButton = new TextButton("Minimize", skin, "custom-button");
            minimizeButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    log.info("click the 'Minimize' button");
                    main.inputAdapter.handleMinimizedAndRestore();
                }
            });

            TextButton exitButton = new TextButton("Exit", skin, "custom-button");
            exitButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    // 退出程序
                    main.dispose(); // 不知道这样写合不合适，但确实能实现退出
                }
            });


            // Add buttons to menuTable
            menuTable.add(minimizeButton).width(120).height(40).pad(5);
            menuTable.row();

            menuTable.add(aboutButton).width(120).height(40).pad(5);
//            menuTable.row();
//            menuTable.add(contactButton).width(120).height(40).pad(5);

            menuTable.row();
            menuTable.add(exitButton).width(120).height(40).pad(5);

            stage.addActor(menuTable);
        } else {
            Window contentWindow = new Window("Content", skin);
            contentWindow.setSize(300, 150);
            log.info("create contentWindow");
            contentWindow.setPosition(0, 0);
            contentWindow.setTouchable(Touchable.enabled); // 👈👈 必须加上这一句！

            Label contentLabel = new Label(currentContent, skin);
            contentLabel.setWrap(true);

            TextButton closeButton = new TextButton("exit", skin, "custom-button");
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

            stage.addActor(contentWindow);
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
