package com.loomans.digit.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.loomans.digit.CanYouDigIt;
import com.loomans.digit.LoadingBarPart;

/**
 * Created by beaverusiv on 24/03/18.
 */

public class LoadingScreen implements Screen {
    private CanYouDigIt parent;
    private TextureAtlas atlas;
    private AtlasRegion title;
    private Animation<TextureRegion> flameAnimation;
    private AtlasRegion dash;
    private int currentLoadingStage = 0;
    private Stage stage;
    private Table table;
    private Table loadingTable;
    private Image titleImage;

    public final int IMAGE = 0;		// loading images
    public final int FONT = 1;		// loading fonts
    public final int PARTY = 2;		// loading particle effects
    public final int SOUND = 3;		// loading sounds
    public final int MUSIC = 4;		// loading music

    public float countDown = 0.1f;

    public LoadingScreen(CanYouDigIt game) {
        parent = game;
        stage = new Stage(new ScreenViewport());
        loadAssets();
        parent.assMan.queueAddImages();
    }

    @Override
    public void show() {
        titleImage = new Image(title);

        table = new Table();
        table.setFillParent(true);
        table.setDebug(false);

        loadingTable = new Table();
        loadingTable.add(new LoadingBarPart(dash,flameAnimation));
        loadingTable.add(new LoadingBarPart(dash,flameAnimation));
        loadingTable.add(new LoadingBarPart(dash,flameAnimation));
        loadingTable.add(new LoadingBarPart(dash,flameAnimation));
        loadingTable.add(new LoadingBarPart(dash,flameAnimation));
        loadingTable.add(new LoadingBarPart(dash,flameAnimation));
        loadingTable.add(new LoadingBarPart(dash,flameAnimation));
        loadingTable.add(new LoadingBarPart(dash,flameAnimation));
        loadingTable.add(new LoadingBarPart(dash,flameAnimation));
        loadingTable.add(new LoadingBarPart(dash,flameAnimation));


        table.add(titleImage).align(Align.center).pad(10, 0, 0, 0).colspan(10);
        table.row(); // move to next row
        table.add(loadingTable).width(400);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (parent.assMan.manager.update()) {
            currentLoadingStage+= 1;
            if(currentLoadingStage <= 5){
                loadingTable.getCells().get((currentLoadingStage-1)*2).getActor().setVisible(true);
                loadingTable.getCells().get((currentLoadingStage-1)*2+1).getActor().setVisible(true);
            }
            switch(currentLoadingStage){
                case FONT:
                    System.out.println("Loading fonts....");
                    parent.assMan.queueAddFonts();
                    break;
                case PARTY:
                    System.out.println("Loading Particle Effects....");
                    parent.assMan.queueAddParticleEffects();
                    break;
                case SOUND:
                    System.out.println("Loading Sounds....");
                    parent.assMan.queueAddSounds();
                    break;
                case MUSIC:
                    System.out.println("Loading fonts....");
                    parent.assMan.queueAddMusic();
                    break;
                case 5:
                    System.out.println("Finished");
                    break;
            }
            if (currentLoadingStage >5){
                countDown -= delta;
                currentLoadingStage = 5;
                if(countDown < 0){
                    parent.changeScreen(CanYouDigIt.MENU);
                }
            }
        }

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

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

    }

    private void loadAssets() {
        // load loading images and wait until finished
        parent.assMan.queueAddLoadingImages();
        parent.assMan.manager.finishLoading();

        // get images used to display loading progress
        atlas = parent.assMan.manager.get("images/loading.atlas");
        title = atlas.findRegion("staying-alight-logo");
        dash = atlas.findRegion("loading-dash");
        flameAnimation = new Animation<TextureRegion>(0.07f, atlas.findRegions("flames/flames"), PlayMode.LOOP);
    }
}
