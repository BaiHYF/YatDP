package com.badlogic.yatdp;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.utils.Logger;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    Logger log = new Logger("YatDP_CORE", Logger.DEBUG);

    @Override
    public void create() {
        log.info("YatDP core initialized.");
        // TODO: Implement
    }

    @Override
    public void render() {
        // TODO: Implement
    }

    @Override
    public void resize(int width, int height) {
        log.info("Resized.");
        // TODO: Implement
    }


    @Override
    public void dispose() {
        log.info("YatDP core disposed.");
    }
}
