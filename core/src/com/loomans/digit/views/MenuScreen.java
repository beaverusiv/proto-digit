package com.loomans.digit.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.loomans.digit.CanYouDigIt;

/**
 * Created by beaverusiv on 24/03/18.
 */

public class MenuScreen implements Screen {
    private CanYouDigIt parent;
    private Stage stage;
    private Skin skin;

    public MenuScreen(CanYouDigIt game) {
        parent = game;
        stage = new Stage(new ScreenViewport());

        parent.assMan.queueAddSkin();
        parent.assMan.manager.finishLoading();
        skin = parent.assMan.manager.get("skin/uiskin.json");
    }

    @Override
    public void show() {
        Table table = new Table();
        table.setFillParent(true);
        stage.clear();
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        TextButton newGame = new TextButton("New Game", skin);
        TextButton preferences = new TextButton("Preferences", skin);
        TextButton exit = new TextButton("Exit", skin);

        newGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.changeScreen(CanYouDigIt.APPLICATION);
            }
        });

        preferences.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.changeScreen(CanYouDigIt.PREFERENCES);
            }
        });

        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        table.add(newGame).fillX().uniformX();
        table.row().pad(10, 0, 10, 0);
        table.add(preferences).fillX().uniformX();
        table.row();
        table.add(exit).fillX().uniformX();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
