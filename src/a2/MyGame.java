package a2;

import myGameEngine.ToggleMountAction;
import myGameEngine.movement.*;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import ray.input.GenericInputManager;
import ray.input.InputManager;
import ray.input.action.Action;
import ray.rage.Engine;
import ray.rage.asset.texture.Texture;
import ray.rage.game.Game;
import ray.rage.game.VariableFrameRateGame;
import ray.rage.rendersystem.RenderSystem;
import ray.rage.rendersystem.RenderWindow;
import ray.rage.rendersystem.Renderable;
import ray.rage.rendersystem.gl4.GL4RenderSystem;
import ray.rage.rendersystem.shader.GpuShaderProgram;
import ray.rage.rendersystem.states.FrontFaceState;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.scene.*;
import ray.rage.scene.controllers.RotationController;
import ray.rage.util.BufferUtil;
import ray.rml.Vector3f;

import java.awt.*;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class MyGame extends VariableFrameRateGame {

    private GL4RenderSystem renderSystem; // render system for the game, is defined each update.
    private float elapsedTime = 0.0f;  // Used to keep track of how long the game has been running.
    private String displayString; // Used to display the information on the HUD.


    private int score_counter = 0;
    private boolean visitedEarth = false;
    private boolean visitedMoon = false;
    private boolean visitedRed = false;

    private int trophiesCollected = 0;
    private boolean trophy1Collected = false;
    private boolean trophy2Collected = false;
    private boolean trophy3Collected = false;
    private boolean trophy4Collected = false;



    private InputManager inputManager;
    private Camera camera;
    private SceneNode dolphinNode;
    private Action moveForwardAction, moveBackwardAction, moveLeftAction,
            moveRightAction, toggleMountAction, decreaseGlobalYawAction,
            increaseGlobalYawAction, globalYawControllerAction, increaseLocalPitchAction,
            decreaseLocalPitchAction, localPitchControllerAction, moveXAxisJoystickAction,
            moveYAxisJoystickAction;

    public MyGame() {
        super();
    }

    public static void main(String[] args) {
        Game game = new MyGame();
        try {
            game.startup();
            game.run();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            game.shutdown();
            game.exit();
        }
    }

    @Override
    protected void update(Engine engine) {
        renderSystem = (GL4RenderSystem) engine.getRenderSystem();
        elapsedTime += engine.getElapsedTimeMillis();

        if (score_counter == 3 && trophiesCollected == 4) {
            displayString = "YOU BEAT THE GAME!!!";
        } else {
            displayString = "Planets Visited: " + score_counter + "  Trophies: " + trophiesCollected;
        }

        if (camera.getMode() == 'c') {
            checkPlanetVisited(engine.getSceneManager().getSceneNode("EarthNode"));
            checkPlanetVisited(engine.getSceneManager().getSceneNode("MoonNode"));
            checkPlanetVisited(engine.getSceneManager().getSceneNode("RedPlanetNode"));
            cameraDistanceLimiter(dolphinNode);
        }

        pickedUpTrophy(engine.getSceneManager().getSceneNode("DolphinTrophy1Node"));
        pickedUpTrophy(engine.getSceneManager().getSceneNode("DolphinTrophy2Node"));
        pickedUpTrophy(engine.getSceneManager().getSceneNode("DolphinTrophy3Node"));
        pickedUpTrophy(engine.getSceneManager().getSceneNode("DolphinTrophy4Node"));

        renderSystem.setHUD(displayString, 10, 10);
        inputManager.update(elapsedTime);
    }

    // Keep the user from going to far from the dolphin
    protected void cameraDistanceLimiter(SceneNode node) {

        Vector3f cameraPosition = camera.getPo();
        Vector3f dolphinLocation = (Vector3f) node.getWorldPosition();

        float distanceFromNode = (float) Math.sqrt(
                Math.pow((cameraPosition.x() - dolphinLocation.x()), 2) +
                        Math.pow((cameraPosition.y() - dolphinLocation.y()), 2) +
                        Math.pow((cameraPosition.z() - dolphinLocation.z()), 2));

        if (distanceFromNode > 10.0f)
            camera.setPo((Vector3f) Vector3f.createFrom(dolphinLocation.x() * 0.5f, dolphinLocation.y(), dolphinLocation.z()));
    }

    // Checks the distance from planets to see if the user should score.
    protected void checkPlanetVisited(SceneNode planet) {

        Vector3f cameraPosition = camera.getPo();
        Vector3f planetLocation = (Vector3f) planet.getWorldPosition();

        float distanceFromPlanet = (float) Math.sqrt(
                        Math.pow((cameraPosition.x() - planetLocation.x()), 2) +
                        Math.pow((cameraPosition.y() - planetLocation.y()), 2) +
                        Math.pow((cameraPosition.z() - planetLocation.z()), 2));

        if (distanceFromPlanet <= 3) {

            switch (planet.getName()) {
                case "EarthNode":
                    if (!visitedEarth)
                        score_counter += 1;
                    visitedEarth = true;
                    break;
                case "MoonNode":
                    if (!visitedMoon)
                        score_counter += 1;
                    visitedMoon = true;
                    break;
                case "RedPlanetNode":
                    if (!visitedRed)
                        score_counter += 1;
                    visitedRed = true;
                    break;
            }
        }
    }

    protected void pickedUpTrophy(SceneNode trophy) {

        Vector3f dolphinLocation = (Vector3f) dolphinNode.getLocalPosition();
        Vector3f trophyLocation = (Vector3f) trophy.getWorldPosition();

        float distanceFromTrophy = (float) Math.sqrt(
                        Math.pow((dolphinLocation.x() - trophyLocation.x()), 2) +
                        Math.pow((dolphinLocation.y() - trophyLocation.y()), 2) +
                        Math.pow((dolphinLocation.z() - trophyLocation.z()), 2));

        if (distanceFromTrophy <= 0.5f) {
            switch (trophy.getName()) {
                case "DolphinTrophy1Node":
                    if (!trophy1Collected) {
                        trophiesCollected++;
                        trophy.setLocalPosition(-50.0f, 0.0f, -8.0f);
                        trophy1Collected = true;
                    }
                    break;
                case "DolphinTrophy2Node":
                    if (!trophy2Collected) {
                        trophiesCollected++;
                        trophy.setLocalPosition(-50.0f, 0.0f, -8.0f);
                        trophy2Collected = true;
                    }
                    break;
                case "DolphinTrophy3Node":
                    if (!trophy3Collected) {
                        trophiesCollected++;
                        trophy.setLocalPosition(-50.0f, 0.0f, -8.0f);
                        trophy3Collected = true;
                    }
                    break;
                case "DolphinTrophy4Node":
                    if (!trophy4Collected) {
                        trophiesCollected++;
                        trophy.setLocalPosition(-50.0f, 0.0f, -8.0f);
                        trophy4Collected = true;
                    }
                    break;
            }
        }
    }

    @Override
    protected void setupCameras(SceneManager sceneManager, RenderWindow renderWindow) {
        camera = sceneManager.createCamera("MainCamera", Camera.Frustum.Projection.PERSPECTIVE); // Create Main Camera with perspective projection
        renderWindow.getViewport(0).setCamera(camera);

        camera.setMode('c');
        camera.setPo((Vector3f) Vector3f.createFrom(0.0f, 0.0f, 0.0f)); // Sets the cameras position at the origin.
        camera.setRt((Vector3f) Vector3f.createFrom(1.0f, 0.0f, 0.0f)); // Sets the cameras right(U) vector.
        camera.setUp((Vector3f) Vector3f.createFrom(0.0f, 1.0f, 0.0f)); // Sets the cameras vertical(V) vector.
        camera.setFd((Vector3f) Vector3f.createFrom(0.0f, 0.0f, -1.0f)); // Sets the cameras forward(N) vector.

        //SceneNode cameraNode = rootNode.createChildSceneNode(camera.getName() + "Node"); // Creates a new child node off of the root node.
        //cameraNode.attachObject(camera);  // Attaches the camera object to the camera node.
    }

    @Override
    protected void setupScene(Engine engine, SceneManager sceneManager) throws IOException {

        SceneNode cameraNode = sceneManager.getRootSceneNode().createChildSceneNode(camera.getName() + "Node");
        cameraNode.attachObject(camera);

        /*
            Creates the dolphin and sets the render.
            Followed by the node creation and placement of the node in the world.
            The entity is then attached to the node.
         */
        Entity dolphinEntity = sceneManager.createEntity("Dolphin", "dolphinHighPoly.obj");
        dolphinEntity.setPrimitive(Renderable.Primitive.TRIANGLES);

        dolphinNode = sceneManager.getRootSceneNode().createChildSceneNode(dolphinEntity.getName() + "Node");
        dolphinNode.moveBackward(2.0f);
        dolphinNode.attachObject(dolphinEntity);

        // Adds the camera node to the dolphin node as a child.
        dolphinNode.attachChild(cameraNode);
        dolphinNode.getChild("MainCameraNode").moveUp(0.35f);
        dolphinNode.getChild("MainCameraNode").moveBackward(0.2f);
        cameraNode.notifyAttached(dolphinNode);


        /*
            Creates planet 1 and sets the render.
            Followed by the node creation and placement of the node in the world.
            The entity is then attached to the node.
         */
        Entity earthEntity = sceneManager.createEntity("Earth", "earth.obj");
        earthEntity.setPrimitive(Renderable.Primitive.TRIANGLES);

        SceneNode earthNode = sceneManager.getRootSceneNode().createChildSceneNode(earthEntity.getName() + "Node");
        earthNode.attachObject(earthEntity);
        earthNode.setLocalPosition(-50.0f, 0.0f, -8.0f);
        earthNode.setLocalScale(1.5f, 1.5f, 1.5f);

        /*
            Creates planet 2 and sets the render.
            Followed by the node creation and placement of the node in the world.
            The entity is then attached to the node.
         */
        Entity moonEntity = sceneManager.createEntity("Moon", "planet2.obj");
        moonEntity.setPrimitive(Renderable.Primitive.TRIANGLES);

        SceneNode moonNode = sceneManager.getRootSceneNode().createChildSceneNode(moonEntity.getName() + "Node");
        moonNode.setLocalPosition(25.0f, 0.0f, 14.0f);
        moonNode.scale(1.0f, 1.0f, 1.0f);
        moonNode.attachObject(moonEntity);

        /*
            Creates planet 3 and sets the render.
            Followed by the node creation and placement of the node in the world.
            The entity is then attached to the node.
         */
        Entity redPlanetEntity = sceneManager.createEntity("RedPlanet", "planet3.obj");
        redPlanetEntity.setPrimitive(Renderable.Primitive.TRIANGLES);

        SceneNode RedPlanetNode = sceneManager.getRootSceneNode().createChildSceneNode(redPlanetEntity.getName() + "Node");
        RedPlanetNode.setLocalPosition(3.0f, 0.0f, -30.0f);
        RedPlanetNode.scale(2.0f, 2.0f, 2.0f);
        RedPlanetNode.attachObject(redPlanetEntity);

        // Rotation controller to get the planets rotating.
        RotationController planetRotationController = new RotationController(Vector3f.createUnitVectorY(), .006f);
        planetRotationController.addNode(earthNode);
        planetRotationController.addNode(RedPlanetNode);
        sceneManager.addController(planetRotationController);

        /*
            Creates the X/Y/Z axes in the game
         */
        ManualObject worldAxes = worldAxesObject(sceneManager);
        SceneNode worldAxesNode = sceneManager.getRootSceneNode().createChildSceneNode("WorldAxesNode");
        worldAxesNode.attachObject(worldAxes);


        Vector3f trophySize = (Vector3f) Vector3f.createFrom(0.1f, 0.1f, 0.1f);


        // Trophy above the earth
        Entity trophy1 = sceneManager.createEntity("DolphinTrophy1", "dolphinHighPoly.obj");
        trophy1.setPrimitive(Renderable.Primitive.TRIANGLES);
        SceneNode trophyNode1 = sceneManager.getRootSceneNode().createChildSceneNode(trophy1.getName() + "Node");
        trophyNode1.attachObject(trophy1);
        trophyNode1.setLocalPosition(-50.0f, 4.0f, -8.0f);
        trophyNode1.scale(trophySize);


        // Trophy above the moon
        Entity trophy2 = sceneManager.createEntity("DolphinTrophy2", "dolphinHighPoly.obj");
        trophy2.setPrimitive(Renderable.Primitive.TRIANGLES);
        SceneNode trophyNode2 = sceneManager.getRootSceneNode().createChildSceneNode(trophy2.getName() + "Node");
        trophyNode2.attachObject(trophy2);
        trophyNode2.setLocalPosition(25.0f, 1.7f, 14.0f);
        trophyNode2.scale(trophySize);


        // Trophy above the red planet
        Entity trophy3 = sceneManager.createEntity("DolphinTrophy3", "dolphinHighPoly.obj");
        trophy3.setPrimitive(Renderable.Primitive.TRIANGLES);
        SceneNode trophyNode3 = sceneManager.getRootSceneNode().createChildSceneNode(trophy3.getName() + "Node");
        trophyNode3.attachObject(trophy3);
        trophyNode3.setLocalPosition(3.0f, 3.0f, -30.0f);
        trophyNode3.scale(trophySize);


        // Trophy under the pyramid ship
        Entity trophy4 = sceneManager.createEntity("DolphinTrophy4", "dolphinHighPoly.obj");
        trophy4.setPrimitive(Renderable.Primitive.TRIANGLES);
        SceneNode trophyNode4 = sceneManager.getRootSceneNode().createChildSceneNode(trophy4.getName() + "Node");
        trophyNode4.attachObject(trophy4);
        trophyNode4.setLocalPosition(5.0f, 6.7f, 8.0f);
        trophyNode4.scale(trophySize);

        RotationController trophyRotationController = new RotationController(Vector3f.createUnitVectorY(), 0.05f);
        trophyRotationController.addNode(trophyNode1);
        trophyRotationController.addNode(trophyNode2);
        trophyRotationController.addNode(trophyNode3);
        trophyRotationController.addNode(trophyNode4);
        sceneManager.addController(trophyRotationController);



        // Create a pyramid ship manual object
        ManualObject pyramidShip = pyramidShip(engine, sceneManager);
        SceneNode pyramidShipNode = sceneManager.getRootSceneNode().createChildSceneNode("PyramidShipNode");
        pyramidShipNode.attachObject(pyramidShip);
        pyramidShipNode.setLocalPosition(5.0f, 7.0f, 8.0f);

        // Gets the ambient light and sets its intensity for the scene.
        sceneManager.getAmbientLight().setIntensity(new Color(0.1f, 0.1f, 0.1f));


        // Create a spot light
        Light positionalLight = sceneManager.createLight("PositionalLight", Light.Type.SPOT);
        positionalLight.setAmbient(new Color(0.5f, 0.5f, 0.5f));
        positionalLight.setDiffuse(new Color(0.7f, 0.7f, 0.7f));
        positionalLight.setSpecular(new Color(1.0f, 1.0f, 1.0f));
        positionalLight.setRange(10f);

        // Create the node for the light and attaches it to the dolphin node as a child.
        SceneNode positionalLightNode = sceneManager.getRootSceneNode().createChildSceneNode(positionalLight.getName() + "Node");
        positionalLightNode.attachObject(positionalLight);
        dolphinNode.attachChild(positionalLightNode);

        setupInputs(); // Setup the inputs
    }

    @Override
    protected void setupWindow(RenderSystem renderSystem, GraphicsEnvironment graphicsEnvironment) {
        // Defines the window size of the game and make it so it is NOT in exclusive fullscreen.
        renderSystem.createRenderWindow(new DisplayMode(1000, 700, 24, 60), false);
    }

    protected void setupInputs() {
        inputManager = new GenericInputManager();
        ArrayList controllers = inputManager.getControllers();


        moveForwardAction = new MoveForwardAction(camera, dolphinNode);
        moveBackwardAction = new MoveBackwardAction(camera, dolphinNode);
        moveLeftAction = new MoveLeftAction(camera, dolphinNode);
        moveRightAction = new MoveRightAction(camera, dolphinNode);
        moveXAxisJoystickAction = new MoveXAxisJoystickAction(camera, dolphinNode);
        moveYAxisJoystickAction = new MoveYAxisJoystickAction(camera, dolphinNode);
        toggleMountAction = new ToggleMountAction(camera, dolphinNode);
        decreaseGlobalYawAction = new DecreaseGlobalYawAction(camera, dolphinNode);
        increaseGlobalYawAction = new IncreaseGlobalYawAction(camera, dolphinNode);
        globalYawControllerAction = new GlobalYawControllerAction(camera, dolphinNode);
        increaseLocalPitchAction = new IncreaseLocalPitchAction(camera, dolphinNode);
        decreaseLocalPitchAction = new DecreaseLocalPitchAction(camera, dolphinNode);
        localPitchControllerAction = new LocalPitchControllerAction(camera, dolphinNode);

        for (Object controller : controllers) {

            Controller c = (Controller) controller;

            if (c.getType() == Controller.Type.KEYBOARD && camera.getMode() == 'c') {
                inputManager.associateAction(c, Component.Identifier.Key.W, moveForwardAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                inputManager.associateAction(c, Component.Identifier.Key.S, moveBackwardAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                inputManager.associateAction(c, Component.Identifier.Key.A, moveLeftAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                inputManager.associateAction(c, Component.Identifier.Key.D, moveRightAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                inputManager.associateAction(c, Component.Identifier.Key.SPACE, toggleMountAction, InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
                inputManager.associateAction(c, Component.Identifier.Key.LEFT, decreaseGlobalYawAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                inputManager.associateAction(c, Component.Identifier.Key.RIGHT, increaseGlobalYawAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                inputManager.associateAction(c, Component.Identifier.Key.UP, increaseLocalPitchAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                inputManager.associateAction(c, Component.Identifier.Key.DOWN, decreaseLocalPitchAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            } else if (c.getType() == Controller.Type.GAMEPAD || c.getType() == Controller.Type.STICK) {
                inputManager.associateAction(c, Component.Identifier.Axis.X, moveXAxisJoystickAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                inputManager.associateAction(c, Component.Identifier.Axis.Y, moveYAxisJoystickAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                inputManager.associateAction(c, Component.Identifier.Button._5, toggleMountAction, InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
                inputManager.associateAction(c, Component.Identifier.Axis.RX, globalYawControllerAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                inputManager.associateAction(c, Component.Identifier.Axis.RY, localPitchControllerAction, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

            }
        }
    }

    // Builds the world axes.
    protected ManualObject worldAxesObject(SceneManager sceneManager) {

        ManualObject worldAxes = sceneManager.createManualObject("WorldAxes"); // Creates the manual object and names it WorldAxe.
        ManualObjectSection worldAxesSection = worldAxes.createManualSection("LineSegments"); // Creates a manual object section.

        //
        worldAxes.setGpuShaderProgram(sceneManager.getRenderSystem().
                getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));

        worldAxes.setPrimitive(Renderable.Primitive.LINES);

        // The vertices for the lines along the world axes.
        float[] vertices = new float[]{
                1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, // X
                0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, // Y
                0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f  // Z
        };

        FloatBuffer verticesBuffer = BufferUtil.directFloatBuffer(vertices);
        worldAxesSection.setVertexBuffer(verticesBuffer);

        return worldAxes;
    }

    // Not rendering correctly
    protected ManualObject saucerObject(Engine engine, SceneManager sceneManager) throws IOException {

        ManualObject saucer = sceneManager.createManualObject("Saucer");
        ManualObjectSection saucerSection = saucer.createManualSection("SaucerSection");

        saucer.setGpuShaderProgram(sceneManager.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));


        float[] vertices = new float[]{
                // Top of the saucer 8 triangles.
                0.0f, 0.25f, 0.0f, 1.0f, 0.0f, 0.0f, 0.75f, 0.0f, 0.75f,   // First top section triangle
                0.0f, 0.25f, 0.0f, 0.75f, 0.0f, 0.75f, 0.0f, 0.0f, 1.0f,     // Second top section triangle
                0.0f, 0.25f, 0.0f, 0.0f, 0.0f, 1.0f, -0.75f, 0.0f, 0.75f,  // Third top section triangle
                0.0f, 0.25f, 0.0f, -0.75f, 0.0f, 0.75f, -1.0f, 0.0f, 0.0f,    // Fourth top section triangle
                0.0f, 0.25f, 0.0f, -1.0f, 0.0f, 0.0f, -0.75f, 0.0f, -0.75f, // Fifth top section triangle
                0.0f, 0.25f, 0.0f, -0.75f, 0.0f, -0.75f, 0.0f, 0.0f, -1.0f,    // Sixth top section triangle
                0.0f, 0.25f, 0.0f, 0.0f, 0.0f, -1.0f, 0.75f, 0.0f, -0.75f,  // Seventh top section triangle
                0.0f, 0.25f, 0.0f, 0.75f, 0.0f, -0.75f, 1.0f, 0.0f, 0.0f,     // Eighth top section triangle


                // Bottom of the saucer 8 triangles.
                0.0f, -0.25f, 0.0f, 1.0f, 0.0f, 0.0f, 0.75f, 0.0f, 0.75f,   // First bottom section triangle
                0.0f, -0.25f, 0.0f, 0.75f, 0.0f, 0.75f, 0.0f, 0.0f, 1.0f,     // Second bottom section triangle
                0.0f, -0.25f, 0.0f, 0.0f, 0.0f, 1.0f, -0.75f, 0.0f, 0.75f,  // Third bottom section triangle
                0.0f, -0.25f, 0.0f, -0.75f, 0.0f, 0.75f, -1.0f, 0.0f, 0.0f,    // Fourth bottom section triangle
                0.0f, -0.25f, 0.0f, -1.0f, 0.0f, 0.0f, -0.75f, 0.0f, -0.75f, // Fifth bottom section triangle
                0.0f, -0.25f, 0.0f, -0.75f, 0.0f, -0.75f, 0.0f, 0.0f, -1.0f,    // Sixth bottom section triangle
                0.0f, -0.25f, 0.0f, 0.0f, 0.0f, -1.0f, 0.75f, 0.0f, -0.75f,  // Seventh bottom section triangle
                0.0f, -0.25f, 0.0f, 0.75f, 0.0f, -0.75f, 1.0f, 0.0f, 0.0f      // Eighth bottom section triangle
        };

        float[] textureCoordinates = new float[]{
                0.0f, 0.0f, 0.5f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.5f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.5f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.5f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.5f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.5f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.5f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.5f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.5f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.5f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.5f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.5f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.5f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.5f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.5f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.5f, 1.0f, 0.0f, 1.0f,
        };

        float[] normals = new float[]{

                // Top normals
                0.5f, 0.5f, 0.25f, 0.5f, 0.5f, 0.25f, 0.5f, 0.5f, 0.25f,    // First top normal
                0.25f, 0.5f, 0.5f, 0.25f, 0.5f, 0.5f, 0.25f, 0.5f, 0.5f,    // Second top normal
                -0.25f, 0.5f, 0.5f, -0.25f, 0.5f, 0.5f, -0.25f, 0.5f, 0.5f,   // Third top normal
                -0.5f, 0.5f, 0.25f, -0.5f, 0.5f, 0.25f, -0.5f, 0.5f, 0.25f,   // Fourth top normal
                -0.5f, 0.5f, -0.25f, -0.5f, 0.5f, -0.25f, -0.5f, 0.5f, -0.25f,   // Fifth top normal
                -0.25f, 0.5f, -0.5f, -0.25f, 0.5f, -0.5f, -0.25f, 0.5f, -0.5f,  // Sixth top normal
                0.25f, 0.5f, -0.5f, 0.25f, 0.5f, -0.5f, 0.25f, 0.5f, -0.5f,   // Seventh top normal
                0.5f, 0.5f, -0.25f, 0.5f, 0.5f, -0.25f, 0.5f, 0.5f, -0.25f,   // Eighth top normal

                // Bottom normals
                0.5f, -0.5f, 0.25f, 0.5f, -0.5f, 0.25f, 0.5f, -0.5f, 0.25f,    // First bottom normal
                0.25f, -0.5f, 0.5f, 0.25f, -0.5f, 0.5f, 0.25f, -0.5f, 0.5f,    // Second bottom normal
                -0.25f, -0.5f, 0.5f, -0.25f, -0.5f, 0.5f, -0.25f, -0.5f, 0.5f,   // Third bottom normal
                -0.5f, -0.5f, 0.25f, -0.5f, -0.5f, 0.25f, -0.5f, -0.5f, 0.25f,   // Fourth bottom normal
                -0.5f, -0.5f, -0.25f, -0.5f, -0.5f, -0.25f, -0.5f, -0.5f, -0.25f,   // Fifth bottom normal
                -0.25f, -0.5f, -0.5f, -0.25f, -0.5f, -0.5f, -0.25f, -0.5f, -0.5f,  // Sixth bottom normal
                0.25f, -0.5f, -0.5f, 0.25f, -0.5f, -0.5f, 0.25f, -0.5f, -0.5f,   // Seventh bottom normal
                0.5f, -0.5f, -0.25f, 0.5f, -0.5f, -0.25f, 0.5f, -0.5f, -0.25f,   // Eighth bottom normal
        };

        int[] indices = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,
                25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47};

        FloatBuffer verticesBuffer = BufferUtil.directFloatBuffer(vertices);
        FloatBuffer textureBuffer = BufferUtil.directFloatBuffer(textureCoordinates);
        FloatBuffer normalsBuffer = BufferUtil.directFloatBuffer(normals);
        IntBuffer indexBuffer = BufferUtil.directIntBuffer(indices);

        saucerSection.setVertexBuffer(verticesBuffer);
        saucerSection.setTextureCoordsBuffer(textureBuffer);
        saucerSection.setNormalsBuffer(normalsBuffer);
        saucerSection.setIndexBuffer(indexBuffer);

        Texture texture = engine.getTextureManager().getAssetByPath("hexagons.jpeg");
        TextureState textureState = (TextureState)
                sceneManager.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);

        textureState.setTexture(texture);

        FrontFaceState faceState = (FrontFaceState)
                sceneManager.getRenderSystem().createRenderState(RenderState.Type.FRONT_FACE);


        saucer.setDataSource(Renderable.DataSource.INDEX_BUFFER);
        saucer.setRenderState(textureState);
        saucer.setRenderState(faceState);

        return saucer;
    }


    protected ManualObject pyramidShip(Engine engine, SceneManager sceneManager) throws IOException {

        ManualObject pyramidShip = sceneManager.createManualObject("PyramidShip");
        ManualObjectSection pyramidShipSelection = pyramidShip.createManualSection("PyramidShipSection");

        pyramidShip.setGpuShaderProgram(
                sceneManager.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));

        float[] vertices = new float[] {
                -1.0f,0.0f,1.0f,    2.0f,0.0f,0.0f,     0.0f,0.5f,0.0f,     // Left
                2.0f,0.0f,0.0f,     -1.0f,0.0f,-1.0f,    0.0f,0.5f,0.0f,    // Right
                -1.0f,0.0f,-1.0f,   -1.0f,0.0f,1.0f,    0.0f,0.5f,0.0f,     // Back
                -1.0f,0.0f,-1.0f,   2.0f,0.0f,0.0f,     -1.0f,0.0f,1.0f,    // Bottom
        };

        float[] textureCoordinates = new float[] {
                1.0f,1.0f,  0.0f,0.0f,  1.0f, 0.0f, // Left
                1.0f,1.0f,  0.0f,0.0f,  1.0f, 0.0f, // Right
                0.0f,0.0f,  1.0f,0.0f,  0.5f,1.0f,  // Back
                0.0f,0.0f,  1.0f,1.0f,  0.0f,1.0f,  // Bottom
        };

        float[] normals = new float[] {
                0.0f,1.0f,1.0f,     0.0f,1.0f,1.0f,     0.0f,1.0f,1.0f,     // Left
                1.0f, 1.0f,0.0f,    1.0f,1.0f,0.0f,     1.0f,1.0f,0.0f,     // Right
                -1.0f,1.0f,0.0f,    -1.0f,1.0f,0.0f,    -1.0f,1.0f,0.0f,    // Back
                0.0f,-1.0f,0.0f,    0.0f,-1.0f,0.0f,    0.0f,-1.0f,0.0f,    // Bottom
        };

        int[] indices = new int[] {0,1,2,3,4,5,6,7,8,9,10,11};

        FloatBuffer verticesBuffer = BufferUtil.directFloatBuffer(vertices);
        FloatBuffer textureBuffer = BufferUtil.directFloatBuffer(textureCoordinates);
        FloatBuffer normalsBuffer = BufferUtil.directFloatBuffer(normals);
        IntBuffer indexBuffer = BufferUtil.directIntBuffer(indices);

        pyramidShipSelection.setVertexBuffer(verticesBuffer);
        pyramidShipSelection.setTextureCoordsBuffer(textureBuffer);
        pyramidShipSelection.setNormalsBuffer(normalsBuffer);
        pyramidShipSelection.setIndexBuffer(indexBuffer);

        Texture texture = engine.getTextureManager().getAssetByPath("hexagons.jpeg");
        TextureState textureState = (TextureState)
                sceneManager.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);

        textureState.setTexture(texture);

        FrontFaceState faceState =  (FrontFaceState)
                sceneManager.getRenderSystem().createRenderState(RenderState.Type.FRONT_FACE);

        pyramidShip.setDataSource(Renderable.DataSource.INDEX_BUFFER);
        pyramidShip.setRenderState(textureState);
        pyramidShip.setRenderState(faceState);

        return pyramidShip;
    }
}
