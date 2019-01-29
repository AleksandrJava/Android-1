package ru.geekbrains.stargame.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;


import ru.geekbrains.stargame.math.Rect;
import ru.geekbrains.stargame.sprite.menu.ScaledTouchUpButton;


public class BtExit extends ScaledTouchUpButton {


    private Rect worldBounds;

    public BtExit(TextureAtlas atlas) {
        super(atlas.findRegion("btExit"));
        setHeightProportion(0.2f);

    }

    @Override
    public void resize(Rect worldBounds) {
        this.worldBounds = worldBounds;
        float posX = worldBounds.getRight()-0.1f;
        float posY = 0.4f;
        pos.set(posX, posY);
    }
    @Override
    public void action() {
        Gdx.app.exit();
    }

}
