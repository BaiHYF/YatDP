package com.badlogic.yatdp;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Logger;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    Logger log = new Logger("YatDP_CORE", Logger.DEBUG);

    String modelDirPath = "test/test_spine_model";
    String modelName = "build_char_002_amiya_winter#1";

    SpinePet pet;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Logger.INFO);
        pet = new SpinePet(modelDirPath, modelName);

        log.info("APP initialized.");
    }

    @Override
    public void render() {
        log.debug("Rendering...");

        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);

        pet.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        // TODO: Implement
        pet.setCamera();

        log.info("Resized.");
    }


    @Override
    public void dispose() {
        pet.dispose();
        log.info("YatDP core disposed.");
    }
}
