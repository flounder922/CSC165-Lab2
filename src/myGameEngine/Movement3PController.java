package myGameEngine;

import net.java.games.input.Component;
import net.java.games.input.Event;
import ray.input.InputManager;
import ray.input.action.AbstractInputAction;
import ray.input.action.Action;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;

public class Movement3PController {

    private SceneNode mainActorNode;

    private Vector3 mainActorPosition;


    public Movement3PController(SceneNode mainActorNode) {

        this.mainActorNode = mainActorNode;

    }

    public void setupInput(InputManager inputManager, String controllerName) {
        Action moveForwardAction = new MoveForwardAction();
        Action moveBackwardAction = new MoveBackwardAction();
        Action moveLeftAction = new MoveLeftAction();
        Action moveRightAction = new MoveRightAction();

        inputManager.associateAction(controllerName, Component.Identifier.Button._0, moveForwardAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        inputManager.associateAction(controllerName, Component.Identifier.Button._1, moveBackwardAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        inputManager.associateAction(controllerName, Component.Identifier.Button._2, moveLeftAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
        inputManager.associateAction(controllerName, Component.Identifier.Button._3, moveRightAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
    }

    private class MoveForwardAction extends AbstractInputAction {

        @Override
        public void performAction(float v, Event event) {



        }
    }

    private class MoveBackwardAction extends AbstractInputAction {

        @Override
        public void performAction(float v, Event event) {

        }
    }

    private class MoveLeftAction extends AbstractInputAction {

        @Override
        public void performAction(float v, Event event) {

        }
    }

    private class MoveRightAction extends AbstractInputAction {

        @Override
        public void performAction(float v, Event event) {

        }
    }
}
