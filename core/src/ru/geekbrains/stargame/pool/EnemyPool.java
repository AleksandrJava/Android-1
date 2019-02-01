package ru.geekbrains.stargame.pool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import ru.geekbrains.stargame.base.SpritesPool;
import ru.geekbrains.stargame.sprite.game.Enemy;

public class EnemyPool extends SpritesPool<Enemy> {

    private Sound shootSound;
    private BulletPool bulletPool;

    public EnemyPool(BulletPool bulletPool) {
        this.shootSound = Gdx.audio.newSound(Gdx.files.internal("sound/bullet.wav"));
        this.bulletPool = bulletPool;
    }

    @Override
    protected Enemy newObject() {
        return new Enemy(shootSound, bulletPool);
    }

    @Override
    public void dispose() {
        super.dispose();
        shootSound.dispose();
    }
}
