package ru.geekbrains.stargame.sprite.menu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import ru.geekbrains.stargame.math.Rect;
import ru.geekbrains.stargame.screen.GameScreen;

public class Play extends ScaledTouchUpButton {

    private Game game;
    private Rect worldBounds;

    public Play(TextureAtlas atlas, Game game) {
        super(atlas.findRegion("btPlay"));
        this.game = game;
        setHeightProportion(0.2f);

    }

    @Override
    public void resize(Rect worldBounds) {
        this.worldBounds = worldBounds;
        float posX = worldBounds.getLeft()+0.1f;
        float posY = 0.4f;
        pos.set(posX, posY);
    }

    @Override
    public void action() {
        game.setScreen(new GameScreen());
    }
}
