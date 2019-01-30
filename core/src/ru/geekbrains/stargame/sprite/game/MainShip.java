package ru.geekbrains.stargame.sprite.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.stargame.base.Sprite;
import ru.geekbrains.stargame.math.Rect;
import ru.geekbrains.stargame.pool.BulletPool;

public class MainShip extends Sprite {

    Sound sound = Gdx.audio.newSound(Gdx.files.internal("sound/shoot.mp3"));
    private Rect worldBounds;

    private final Vector2 v0 = new Vector2(0.5f, 0);
    private Vector2 v = new Vector2();

    private boolean isPressedLeft;
    private boolean isPressedRight;

    private boolean touchPressedRight;
    private boolean touchPressedLeft;

    private BulletPool bulletPool;

    private TextureRegion bulletRegion;

    public MainShip(TextureAtlas atlas, BulletPool bulletPool) {
        super(atlas.findRegion("main_ship"), 1, 2, 2);
        this.bulletRegion = atlas.findRegion("bulletMainShip");
        this.bulletPool = bulletPool;
        setHeightProportion(0.15f);
    }

    @Override
    public void resize(Rect worldBounds) {
        super.resize(worldBounds);
        this.worldBounds = worldBounds;
        setBottom(worldBounds.getBottom() + 0.05f);
    }

    @Override
    public void update(float delta) {
        float rightBorder = this.worldBounds.getRight() - this.getWidth() / 2;
        float leftBorder  = this.worldBounds.getLeft()  + this.getWidth() / 2;

        if ( rightBorder > this.pos.x && touchPressedRight ) {
            this.pos.mulAdd( v, delta );
        }

        if ( leftBorder < this.pos.x && touchPressedLeft ) {
            this.pos.mulAdd( v, delta );
        }

        if ( rightBorder > this.pos.x && isPressedRight) {
            this.pos.mulAdd( v, delta );
        }

        if ( leftBorder < this.pos.x && isPressedLeft) {
            this.pos.mulAdd( v, delta );
        }
    }

    public boolean touchDown( Vector2 touch, int pointer ) {

        if ( touch.x > 0 ) {
            this.touchPressedLeft  = false;
            this.touchPressedRight = true;
            this.moveRight();
        }
        else {
            this.touchPressedLeft  = true;
            this.touchPressedRight = false;
            this.moveLeft();
        }
        return false;
    }

    public boolean touchUp( Vector2 touch, int pointer ) {
        touchPressedLeft = false;
        touchPressedRight = false;
        return false;
    }

    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.A:
            case Input.Keys.LEFT:
                isPressedLeft = true;
                moveLeft();
                break;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                isPressedRight = true;
                moveRight();
                break;
            case Input.Keys.UP:
                shoot();
                break;
        }
        return false;
    }

    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.A:
            case Input.Keys.LEFT:
                isPressedLeft = false;
                if (isPressedRight) {
                    moveRight();
                } else {
                    stop();
                }
                break;
            case Input.Keys.D:
            case Input.Keys.RIGHT:
                isPressedRight = false;
                if (isPressedLeft) {
                    moveLeft();
                } else {
                    stop();
                }
                break;
        }
        return false;
    }

    private void moveRight() {
        v.set(v0);
    }

    private void moveLeft() {
        v.set(v0).rotate(180);
    }

    private void stop() {
        v.setZero();
    }

    private void shoot() {
        sound.play(1.0f);
        Bullet bullet = bulletPool.obtain();
        bullet.set(this, bulletRegion, pos, new Vector2(0, 0.5f), 0.01f, worldBounds, 1);
    }

}
