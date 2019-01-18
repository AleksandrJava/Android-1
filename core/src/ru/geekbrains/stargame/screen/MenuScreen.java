package ru.geekbrains.stargame.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import ru.geekbrains.stargame.base.Base2DScreen;

public class MenuScreen extends Base2DScreen {
    SpriteBatch batch;
    Texture img;
    Texture background;

    Vector2 pos;//вектор положения
    Vector2 dest;//вектор конечной точки
    Vector2 speed;//вектор скорости и направления
    float line;

    @Override
    public void show() {
        super.show();
        batch = new SpriteBatch();
        background = new Texture("new.jpg");
        img = new Texture("bee2.png");
        pos = new Vector2(0, 0);
        dest = new Vector2(0,0);
        speed = new Vector2(0,0);
    }


    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(0.5f, 0.2f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(background, 0, 0);
        batch.draw(img, pos.x, pos.y);
        batch.end();

        if(Gdx.graphics.getWidth() - 64 > pos.x && Gdx.graphics.getHeight() - 64 > pos.y && pos.x >= 0 && pos.y >= 0) {
            pos.add(speed);
        }
        if (Gdx.graphics.getWidth() - 64 > pos.x && Gdx.graphics.getHeight() - 64 > pos.y && pos.x > 0 && pos.y > 0) {

            pos.add(speed);
            System.out.println("Текущие координаты " + pos.x + " " + pos.y);
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
        super.dispose();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        System.out.println("touchDown " + screenX + " " + (Gdx.graphics.getHeight() - screenY));

        dest.set(screenX-32-pos.x, (Gdx.graphics.getHeight() - screenY-32-pos.y));
        speed.set(dest).nor().scl(1.5f);
        pos.add(speed);
        line = dest.len();

        return super.touchDown(screenX, screenY, pointer, button);
    }

}
