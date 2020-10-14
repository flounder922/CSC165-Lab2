package a2;

import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.Engine;
import ray.rage.scene.SceneNode;

public class MoveForwardAction3P extends AbstractInputAction {

    private final Engine engine;
    private final SceneNode mainActorNode;

    public MoveForwardAction3P(Engine engine, String mainActor) {
        this.engine = engine;
        this.mainActorNode = engine.getSceneManager().getSceneNode(mainActor);
    }


    @Override
    public void performAction(float time, Event event) {

        mainActorNode.moveForward(0.05f);

    }
}
