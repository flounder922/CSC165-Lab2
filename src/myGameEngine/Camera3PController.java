package myGameEngine;

import net.java.games.input.Component;
import net.java.games.input.Event;
import ray.input.InputManager;
import ray.input.action.AbstractInputAction;
import ray.input.action.Action;
import ray.rage.scene.Camera;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class Camera3PController {

    // Camera and camera node that are being controlled.
    private Camera camera;
    private SceneNode cameraNode;

    // The target that the camera is looking at.
    private SceneNode target;

    // Camera position information relative to target.
    private float cameraAzimuth;
    private float cameraElevation;
    private float radias;

    // The targets position in the world and up in the world.
    private Vector3 targetPosition;
    private Vector3 worldUpVector;

    public Camera3PController(Camera camera, SceneNode cameraNode, SceneNode target,
                              String controllerName, InputManager inputManager) {

        this.camera = camera;
        this.cameraNode = cameraNode;
        this.target = target;

        cameraAzimuth = 225.0f;
        cameraElevation = 20f;
        radias = 2.0f;

        worldUpVector = Vector3f.createFrom(0.0f, 1.0f, 0.0f);

        setupInput(inputManager, controllerName);

        updateCameraPosition();

    }

    private void updateCameraPosition() {

        double theta = Math.toRadians(cameraAzimuth); // rotation around target
        double phi = Math.toRadians(cameraElevation); // altitude angle

        // Calculate the x,y,z to create a vector to be used for update the cameras position.
        double x = radias * Math.cos(phi) * Math.sin(theta);
        double y = radias * Math.sin(phi);
        double z = radias * Math.cos(phi) * Math.cos(theta);

        cameraNode.setLocalPosition(Vector3f.createFrom((float)x, (float)y, (float)z).add(target.getWorldPosition()));

        cameraNode.lookAt(target, worldUpVector);
    }

    private void setupInput(InputManager inputManager, String controllerName) {

        Action orbitAroundAction = new OrbitAroundAction();
        Action orbitElevationAction = new OrbitElevationAction();
        Action orbitRadiasAction = new OrbitRadiasAction();

        inputManager.associateAction(controllerName, Component.Identifier.Axis.RX, orbitAroundAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        inputManager.associateAction(controllerName, Component.Identifier.Axis.RY, orbitElevationAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        inputManager.associateAction(controllerName, Component.Identifier.Axis.X, orbitRadiasAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    }

    private class OrbitAroundAction extends AbstractInputAction {
        @Override
        public void performAction(float v, Event event) {

            float rotationAmount;

            if (event.getValue() < -0.2)
                rotationAmount = -0.2f;
            else if (event.getValue() > 0.2f)
                rotationAmount = 0.2f;
            else
                rotationAmount = 0.0f;

            cameraAzimuth += rotationAmount;
            cameraAzimuth = cameraAzimuth % 360;
            updateCameraPosition();
        }
    }

    private class OrbitRadiasAction extends AbstractInputAction {
        @Override
        public void performAction(float v, Event event) {

            float rotationAmount;

            if (event.getValue() < -0.2)
                rotationAmount = -0.2f;
            else if (event.getValue() > 0.2f)
                rotationAmount = 0.2f;
            else
                rotationAmount = 0.0f;

            radias += rotationAmount;
            radias = radias % 360;
            updateCameraPosition();
        }
    }

    private class OrbitElevationAction extends AbstractInputAction {
        @Override
        public void performAction(float v, Event event) {

            float rotationAmount;

            if (event.getValue() < -0.2)
                rotationAmount = -0.2f;
            else if (event.getValue() > 0.2f)
                rotationAmount = 0.2f;
            else
                rotationAmount = 0.0f;

            cameraElevation += rotationAmount;
            cameraElevation = cameraElevation % 360;
            updateCameraPosition();
        }
    }
}
