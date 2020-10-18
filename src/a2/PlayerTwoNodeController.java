package a2;

import ray.rage.scene.SceneNode;
import ray.rage.scene.controllers.AbstractController;
import ray.rage.scene.controllers.RotationController;
import ray.rml.Vector3f;

public class PlayerTwoNodeController extends AbstractController {

    RotationController rotationController;

    public PlayerTwoNodeController() {
        rotationController = new RotationController(Vector3f.createUnitVectorY(), 0.00001f);
    }

    public void addNodeToController (SceneNode node) {
        rotationController.addNode(node);
    }

    @Override
    protected void updateImpl(float v) {
        rotationController.update(v);
    }
}
