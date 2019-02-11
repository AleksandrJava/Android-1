package ru.geekbrains.stargame.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import java.util.List;

import ru.geekbrains.stargame.base.Base2DScreen;
import ru.geekbrains.stargame.math.Rect;
import ru.geekbrains.stargame.pool.BulletPool;
import ru.geekbrains.stargame.pool.EnemyPool;
import ru.geekbrains.stargame.pool.ExplosionPool;
import ru.geekbrains.stargame.sprite.Background;
import ru.geekbrains.stargame.sprite.Star;
import ru.geekbrains.stargame.sprite.game.Bullet;
import ru.geekbrains.stargame.sprite.game.Enemy;
import ru.geekbrains.stargame.sprite.game.MainShip;
import ru.geekbrains.stargame.sprite.game.GameOver;
import ru.geekbrains.stargame.sprite.game.NewGame;
import ru.geekbrains.stargame.utils.EnemyEmitter;
import ru.geekbrains.stargame.utils.Font;

public class GameScreen extends Base2DScreen {

    private static final String FRAGS = "Frags: ";
    private static final String HP = "HP: ";
    private static final String LEVEL = "Level: ";

    private enum Status {PLAYING, GAME_OVER}

    private TextureAtlas atlas;
    private Texture bg;
    private Background background;
    private Star star[];
    private MainShip mainShip;
    private GameOver gameOver;
    private NewGame newGame;

    private BulletPool bulletPool;
    private ExplosionPool explosionPool;
    private EnemyPool enemyPool;

    private EnemyEmitter enemyEmitter;

    private Music music;

    private Status status;

    private Font font;
    private StringBuilder sbFrags = new StringBuilder();
    private StringBuilder sbHP = new StringBuilder();
    private StringBuilder sbLevel = new StringBuilder();

    int frags = 0;

    @Override
    public void show() {
        super.show();
        music = Gdx.audio.newMusic(Gdx.files.internal("sound/main.mp3"));
        music.setLooping(true);
        music.setVolume(0.8f);
        music.play();
        bg = new Texture("textures/bg.png");
        background = new Background(new TextureRegion(bg));
        atlas = new TextureAtlas("textures/mainAtlas.tpack");
        star = new Star[64];
        for (int i = 0; i < star.length; i++) {
            star[i] = new Star(atlas);
        }
        bulletPool = new BulletPool();
        explosionPool = new ExplosionPool(atlas);
        mainShip = new MainShip(atlas, bulletPool, explosionPool, worldBounds);
        enemyPool = new EnemyPool(bulletPool, worldBounds, explosionPool, mainShip);
        enemyEmitter = new EnemyEmitter(atlas, enemyPool, worldBounds);

        gameOver = new GameOver(atlas);
        newGame = new NewGame(atlas, this);
        this.font = new Font("font/font.fnt", "font/font.png");
        this.font.setSize(0.02f);
        startNewGame();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        update(delta);
        checkCollisions();
        deleteAllDestroyed();
        draw();
    }

    private void update(float delta) {
        for (Star aStar : star) {
            aStar.update(delta);
        }
        explosionPool.updateActiveSprites(delta);
        switch (status) {
            case PLAYING:
                mainShip.update(delta);
                bulletPool.updateActiveSprites(delta);
                enemyPool.updateActiveSprites(delta);
                enemyEmitter.generate(delta, frags);
                break;
            case GAME_OVER:
                break;
        }
    }

    private void checkCollisions() {
        if (status == Status.PLAYING) {
            List<Enemy> enemyList = enemyPool.getActiveObjects();
            for (Enemy enemy : enemyList) {
                if (enemy.isDestroyed()) {
                    continue;
                }
                float minDist = enemy.getHalfWidth() + mainShip.getHalfWidth();
                if (enemy.pos.dst2(mainShip.pos) < minDist * minDist) {
                    enemy.destroy();
                    mainShip.damage(enemy.getDamage());
                    return;
                }
            }
            List<Bullet> bulletList = bulletPool.getActiveObjects();

            for (Bullet bullet : bulletList) {
                if (bullet.getOwner() == mainShip || bullet.isDestroyed()) {
                    continue;
                }
                if (mainShip.isBulletCollision(bullet)) {
                    mainShip.damage(bullet.getDamage());
                    bullet.destroy();
                }
            }

            for (Enemy enemy : enemyList) {
                if (enemy.isDestroyed()) {
                    continue;
                }
                for (Bullet bullet : bulletList) {
                    if (bullet.getOwner() != mainShip || bullet.isDestroyed()) {
                        continue;
                    }
                    if (enemy.isBulletCollision(bullet)) {
                        enemy.damage(mainShip.getDamage());
                        if(enemy.isDestroyed()){
                            frags++;
                        }
                        bullet.destroy();
                    }
                }
            }
        }
    }

    private void deleteAllDestroyed() {
        if (mainShip.isDestroyed()) {
            status = Status.GAME_OVER;
        }
        bulletPool.freeAllDestroyedActiveSprites();
        explosionPool.freeAllDestroyedActiveSprites();
        enemyPool.freeAllDestroyedActiveSprites();
    }

    private void draw() {
        Gdx.gl.glClearColor(0.5f, 0.2f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        background.draw(batch);
        for (Star aStar : star) {
            aStar.draw(batch);
        }
        switch (status) {
            case PLAYING:
                mainShip.draw(batch);
                bulletPool.drawActiveSprites(batch);
                enemyPool.drawActiveSprites(batch);
                break;
            case GAME_OVER:
                gameOver.draw(batch);
                newGame.draw(batch);
                break;
        }
        explosionPool.drawActiveSprites(batch);
        printInfo();
        batch.end();
    }

    public void printInfo() {
        sbFrags.setLength(0);
        sbHP.setLength(0);
        sbLevel.setLength(0);
        font.draw(batch, sbFrags.append(FRAGS).append(frags), worldBounds.getLeft(), worldBounds.getTop());
        font.draw(batch, sbHP.append(HP).append(mainShip.getHp()), worldBounds.pos.x, worldBounds.getTop(), Align.center);
        font.draw(batch, sbLevel.append(LEVEL).append(enemyEmitter.getLevel()), worldBounds.getRight(), worldBounds.getTop(), Align.right);
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        background.resize(worldBounds);
        for (Star aStar : star) {
            aStar.resize(worldBounds);
        }
        mainShip.resize(worldBounds);
    }

    @Override
    public void dispose() {
        bg.dispose();
        atlas.dispose();
        bulletPool.dispose();
        explosionPool.dispose();
        enemyPool.dispose();
        mainShip.dispose();
        music.dispose();
        font.dispose();
        super.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (!mainShip.isDestroyed()) {
            mainShip.keyDown(keycode);
        }
        return super.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        if (!mainShip.isDestroyed()) {
            mainShip.keyUp(keycode);
        }
        return super.keyUp(keycode);
    }

    @Override
    public boolean touchDown(Vector2 touch, int pointer) {
        if (status == Status.PLAYING) {
            mainShip.touchDown(touch, pointer);
        } else {
            newGame.touchDown(touch, pointer);
        }
        return super.touchDown(touch, pointer);
    }

    @Override
    public boolean touchUp(Vector2 touch, int pointer) {
        if (status == Status.PLAYING) {
            mainShip.touchUp(touch, pointer);
        } else {
            newGame.touchUp(touch, pointer);
        }
        return super.touchUp(touch, pointer);
    }

    public void startNewGame() {
        status = Status.PLAYING;
        mainShip.startNewGame();
        frags = 0;
        enemyEmitter.setLevel(1);
        bulletPool.freeAllActiveObjects();
        enemyPool.freeAllActiveObjects();
        explosionPool.freeAllActiveObjects();
    }
}
