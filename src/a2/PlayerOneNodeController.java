package a2;

import ray.rage.scene.SceneNode;
import ray.rage.scene.controllers.AbstractController;
import ray.rage.scene.controllers.WaypointController;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class PlayerOneNodeController extends AbstractController {

    WaypointController waypointController;

    public PlayerOneNodeController() {
        waypointController = new WaypointController();

    }

    public void addNodeToWaypointController(SceneNode node) {
        waypointController.addNode(node);

        waypointController.addWaypoint(node.getWorldPosition());

        Vector3 nodeWorldPosition = Vector3f.createFrom(node.getWorldPosition().x(), node.getWorldPosition().y() + 5.0f, node.getWorldPosition().z());

        waypointController.addWaypoint(nodeWorldPosition);

        waypointController.setIntervalTimeMillis(10000000);
    }

    @Override
    protected void updateImpl(float v) {
        waypointController.update(v);
    }
}
