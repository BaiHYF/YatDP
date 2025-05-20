package com.badlogic.yatdp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;

/* About the InputAdapter, see: https://libgdx.com/wiki/input/event-handling */
public class YatInputAdapter extends InputAdapter {

    public boolean isDragging = false;
    public Vector2 mousePosition = new Vector2();
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
            window.setPosition((int) (window.getPositionX() + delta.x), (int) (window.getPositionY() + delta.y));

            return true;
        }
        return false;
    }
}
