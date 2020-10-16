package myGameEngine;

import net.java.games.input.Event;
import ray.input.action.AbstractInputAction;
import ray.rage.scene.SceneNode;

public class LeftThirdPersonAction extends AbstractInputAction {

    private Camera3PController controller;
    private SceneNode cameraNode;
    private SceneNode actorNode;

    public LeftThirdPersonAction(SceneNode cameraNode, SceneNode actorNode, Camera3PController controller) {
        this.cameraNode = cameraNode;
        this.actorNode = actorNode;
        this.controller = controller;
    }

    @Override
    public void performAction(float v, Event event) {
        actorNode.moveRight(0.05f);
        controller.updateCameraPosition(cameraNode, actorNode);
    }
}
