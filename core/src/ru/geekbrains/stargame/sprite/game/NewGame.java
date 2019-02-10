package ru.geekbrains.stargame.sprite.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ru.geekbrains.stargame.pool.TouchUpPool;
import ru.geekbrains.stargame.screen.GameScreen;

public class NewGame extends TouchUpPool {

    private GameScreen gameScreen;

    public NewGame(TextureAtlas atlas, GameScreen gameScreen) {
        super(atlas.findRegion("button_new_game"));
        this.gameScreen = gameScreen;
        setHeightProportion(0.1f);
        setTop(-0.01f);
    }

    @Override
    public void action() {
        gameScreen.startNewGame();
    }
}
