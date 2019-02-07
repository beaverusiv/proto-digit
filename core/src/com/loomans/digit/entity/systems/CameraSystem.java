package com.loomans.digit.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.loomans.digit.controller.KeyboardController;
import com.loomans.digit.entity.components.CameraTargetComponent;

// TODO: does there being a single entity mean this should just be a system?
public class CameraSystem extends IteratingSystem {
    public OrthographicCamera camera;

    private KeyboardController controller;
    private Array<Entity> cameraQueue;
    private ComponentMapper<CameraTargetComponent> tm = ComponentMapper.getFor(CameraTargetComponent.class);

    public CameraSystem (KeyboardController kc) {
        super(Family.all(CameraTargetComponent.class).get());
        controller = kc;
        this.cameraQueue = new Array<Entity>();
    }

    public void processEntity(Entity entity, float deltaTime) {
        cameraQueue.add(entity);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (controller.isDragged) {
            Vector2 delta = new Vector2(controller.touchStartLocation);
            delta.sub(controller.mouseLocation).scl(0.1f);
            camera.position.set(camera.position.add(new Vector3(delta.x, delta.y * -1.0f, 0)));
        } else {
            for (Entity entity : cameraQueue) {
                CameraTargetComponent cmp = tm.get(entity);
            }
            cameraQueue.clear();
        }
    }
}
