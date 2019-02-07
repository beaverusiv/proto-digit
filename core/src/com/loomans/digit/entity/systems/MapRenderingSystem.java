package com.loomans.digit.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.loomans.digit.entity.components.MapComponent;

public class MapRenderingSystem extends IteratingSystem {
    private Array<Entity> renderQueue;

    public MapRenderingSystem() {
        super(Family.all(MapComponent.class).get());

        renderQueue = new Array<Entity>();
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        renderQueue.add(entity);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // loop through each entity in our render queue
        for (Entity entity : renderQueue) {
//            entity.setView(camera);
//            entity.render();
        }

        renderQueue.clear();
    }
}
