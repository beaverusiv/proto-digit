package com.loomans.digit.views;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.loomans.digit.B2dContactListener;
import com.loomans.digit.BodyFactory;
import com.loomans.digit.CanYouDigIt;
import com.loomans.digit.controller.KeyboardController;
import com.loomans.digit.entity.components.B2dBodyComponent;
import com.loomans.digit.entity.components.CollisionComponent;
import com.loomans.digit.entity.components.MapComponent;
import com.loomans.digit.entity.components.PlayerComponent;
import com.loomans.digit.entity.components.StateComponent;
import com.loomans.digit.entity.components.TextureComponent;
import com.loomans.digit.entity.components.TransformComponent;
import com.loomans.digit.entity.components.TypeComponent;
import com.loomans.digit.entity.systems.AnimationSystem;
import com.loomans.digit.entity.systems.CameraSystem;
import com.loomans.digit.entity.systems.CollisionSystem;
import com.loomans.digit.entity.systems.PhysicsDebugSystem;
import com.loomans.digit.entity.systems.PhysicsSystem;
import com.loomans.digit.entity.systems.PlayerControlSystem;
import com.loomans.digit.entity.systems.RenderingSystem;

/**
 * Created by beaverusiv on 24/03/18.
 */

public class MainScreen implements Screen {
    private CanYouDigIt parent;
    private OrthographicCamera cam;
    private KeyboardController controller;
    private SpriteBatch sb;
    private TextureAtlas atlas;
    public World world;
    private BodyFactory bodyFactory;
    private PooledEngine engine;
    TiledMap tiledMap;
    TiledMapRenderer tiledMapRenderer;

    // this gets the height and width of our camera frustrum based off the width and height of the screen and our pixel per meter ratio
    static final float FRUSTUM_WIDTH = Gdx.graphics.getWidth();
    static final float FRUSTUM_HEIGHT = Gdx.graphics.getHeight();

    public MainScreen(CanYouDigIt game) {
        parent = game;
        controller = new KeyboardController();
        world = new World(new Vector2(0,-10f), true);
        world.setContactListener(new B2dContactListener());
        bodyFactory = BodyFactory.getInstance(world);

        parent.assMan.manager.finishLoading();
        atlas = parent.assMan.manager.get("images/game.atlas", TextureAtlas.class);

        sb = new SpriteBatch();
        // Create our new rendering system
        CameraSystem cameraSystem = new CameraSystem(controller);

        // set up the camera to match our screen size
        cam = cameraSystem.camera = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
        cam.viewportHeight = FRUSTUM_HEIGHT;
        cam.viewportWidth = FRUSTUM_WIDTH;

        RenderingSystem renderingSystem = new RenderingSystem(cameraSystem, sb);
        sb.setProjectionMatrix(cam.combined);

        //create a pooled engine
        engine = new PooledEngine();

        // add all the relevant systems our engine should run
        engine.addSystem(new AnimationSystem());
        engine.addSystem(cameraSystem);
        engine.addSystem(renderingSystem);
        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(new PhysicsDebugSystem(world, cameraSystem.camera));
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new PlayerControlSystem(controller));

        createMap();
    }

    @Override
    public void show() {
        cam.setToOrtho(false,FRUSTUM_WIDTH,FRUSTUM_HEIGHT);
        engine.getSystem(CameraSystem.class).camera.update();
        tiledMap = new TmxMapLoader().load("maps/first.tmx");
        MapProperties prop = tiledMap.getProperties();

        int mapWidth = prop.get("width", Integer.class);
        int tilePixelWidth = prop.get("tilewidth", Integer.class);

        float mapCentreWidth = mapWidth * tilePixelWidth / 2.0f;
        cam.position.set(mapCentreWidth, 0, 0);
        tiledMapRenderer = new IsometricTiledMapRenderer(tiledMap);

        Gdx.input.setInputProcessor(controller);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        engine.getSystem(CameraSystem.class).camera.update();
        tiledMapRenderer.setView(engine.getSystem(CameraSystem.class).camera);
        tiledMapRenderer.render();

        engine.update(delta);
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

    private void createPlayer() {
        // Create the Entity and all the components that will go in the entity
        Entity entity = engine.createEntity();
        B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        PlayerComponent player = engine.createComponent(PlayerComponent.class);
        CollisionComponent colComp = engine.createComponent(CollisionComponent.class);
        TypeComponent type = engine.createComponent(TypeComponent.class);
        StateComponent stateCom = engine.createComponent(StateComponent.class);

        // create the data for the components and add them to the components
        b2dbody.body = bodyFactory.makeCirclePolyBody(10,10,1, BodyFactory.STONE, BodyType.DynamicBody,true);
        // set object position (x,y,z) z used to define draw order 0 first drawn
        position.position.set(10,10,0);
        texture.region = atlas.findRegion("player");
        type.type = TypeComponent.PLAYER;
        stateCom.set(StateComponent.STATE_NORMAL);
        b2dbody.body.setUserData(entity);

        // add the components to the entity
        entity.add(b2dbody);
        entity.add(position);
        entity.add(texture);
        entity.add(player);
        entity.add(colComp);
        entity.add(type);
        entity.add(stateCom);

        // add the entity to the engine
        engine.addEntity(entity);
    }

    private void createPlatform(float x, float y){
        Entity entity = engine.createEntity();
        B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
        b2dbody.body = bodyFactory.makeBoxPolyBody(x, y, 3, 0.2f, BodyFactory.STONE, BodyType.StaticBody);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        texture.region = atlas.findRegion("player");
        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.type = TypeComponent.SCENERY;
        b2dbody.body.setUserData(entity);

        entity.add(b2dbody);
        entity.add(texture);
        entity.add(type);

        engine.addEntity(entity);

    }

    private void createFloor(){
        Entity entity = engine.createEntity();
        B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
        b2dbody.body = bodyFactory.makeBoxPolyBody(0, 0, 100, 0.2f, BodyFactory.STONE, BodyType.StaticBody);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        texture.region = atlas.findRegion("player");
        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.type = TypeComponent.SCENERY;

        b2dbody.body.setUserData(entity);

        entity.add(b2dbody);
        entity.add(texture);
        entity.add(type);

        engine.addEntity(entity);
    }

    private void createMap(){
        Entity entity = engine.createEntity();
        MapComponent map = engine.createComponent(MapComponent.class);
    }
}
