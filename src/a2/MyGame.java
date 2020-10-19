package a2;

import myGameEngine.ThirdPersonCamera.*;
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
import ray.rage.rendersystem.Viewport;
import ray.rage.rendersystem.gl4.GL4RenderSystem;
import ray.rage.rendersystem.shader.GpuShaderProgram;
import ray.rage.rendersystem.states.FrontFaceState;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.scene.*;
import ray.rage.scene.controllers.RotationController;
import ray.rage.scene.controllers.WaypointController;
import ray.rage.util.BufferUtil;
import ray.rml.Radianf;
import ray.rml.Vector3;
import ray.rml.Vector3f;

import java.awt.*;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class MyGame extends VariableFrameRateGame {

    private GL4RenderSystem renderSystem; // render system for the game, is defined each update.
    private float elapsedTime = 0.0f;  // Used to keep track of how long the game has been running.
    private String displayStringPlayerOne; // Used to display the information on the HUD.
    private String displayStringPlayerTwo;


    private int scoreCounterPlayerOne = 0;
    private int scoreCounterPlayerTwo = 0;
    private int planetsVisited = 0;

    private boolean earthVisited = false;
    private boolean redVisited = false;
    private boolean moonVisited = false;


    private InputManager inputManager;
    private Camera3PController orbitController1, orbitController2;
    private Movement3PController movement3PController1;
    private PlayerOneNodeController playerOneNodeControllerEarth;
    private PlayerOneNodeController playerOneNodeControllerRed;
    private PlayerOneNodeController playerOneNodeControllerMoon;
    private PlayerTwoNodeController playerTwoNodeController;
    private RotationController solarSystemNodeController;


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

        SceneNode player1 = engine.getSceneManager().getSceneNode("DolphinNode");
        SceneNode player2 = engine.getSceneManager().getSceneNode("DolphinNode2");

        SceneNode earth = engine.getSceneManager().getSceneNode("EarthNode");
        SceneNode red = engine.getSceneManager().getSceneNode("RedPlanetNode");
        SceneNode moon = engine.getSceneManager().getSceneNode("MoonNode");

        checkIfPlayerVisitedPlanet(player1, earth);
        checkIfPlayerVisitedPlanet(player1, red);
        checkIfPlayerVisitedPlanet(player1, moon);

        checkIfPlayerVisitedPlanet(player2, earth);
        checkIfPlayerVisitedPlanet(player2, red);
        checkIfPlayerVisitedPlanet(player2, moon);

        if (planetsVisited == 3) {
            if(scoreCounterPlayerOne > scoreCounterPlayerTwo) {
                displayStringPlayerOne = "You are the winner!!!";
                solarSystemNodeController.addNode(engine.getSceneManager().getSceneNode("SolarSystemNode"));
            }
            else if (scoreCounterPlayerTwo > scoreCounterPlayerOne) {
                displayStringPlayerTwo = "You are the winner!!!";
                solarSystemNodeController.addNode(engine.getSceneManager().getSceneNode("SolarSystemNode"));
            }


        } else {
            displayStringPlayerOne = "Planets Visited: " + scoreCounterPlayerOne;
            displayStringPlayerTwo = "Planets Visited: " + scoreCounterPlayerTwo;

        }

        renderSystem.setHUD(displayStringPlayerOne, renderSystem.getRenderWindow().getViewport(0).getActualLeft(), renderSystem.getRenderWindow().getViewport(0).getActualBottom() + 5);
        renderSystem.setHUD2(displayStringPlayerTwo, renderSystem.getRenderWindow().getViewport(0).getActualLeft(), renderSystem.getRenderWindow().getViewport(1).getActualBottom() + 5);

        playerOneNodeControllerEarth.updateImpl(elapsedTime);
        playerOneNodeControllerMoon.updateImpl(elapsedTime);
        playerOneNodeControllerRed.updateImpl(elapsedTime);
        playerTwoNodeController.updateImpl(elapsedTime);
        inputManager.update(elapsedTime);

    }

    protected void checkIfPlayerVisitedPlanet(SceneNode playerNode, SceneNode planetNode) {
        Vector3 playerPosition = playerNode.getWorldPosition();
        Vector3 planetPosition = planetNode.getWorldPosition();

        // Calculate the distance between the player and planet
        float distanceFromPlanet = (float) Math.sqrt(
                Math.pow(playerPosition.x() - planetPosition.x(), 2) +
                        Math.pow(playerPosition.y() - planetPosition.y(), 2) +
                        Math.pow(playerPosition.z() - planetPosition.z(), 2));


        // Used when a player node is within 3 units to then let them gain points.
        if (distanceFromPlanet <= 3) {
            switch (planetNode.getName()) {
                case "EarthNode":
                    if (!earthVisited) {
                        if (playerNode.getName() == "DolphinNode") {
                            scoreCounterPlayerOne++;
                            planetsVisited++;
                            playerOneNodeControllerEarth.addNodeToWaypointController(planetNode);
                            earthVisited = true;
                        } else if (playerNode.getName() == "DolphinNode2") {
                            scoreCounterPlayerTwo++;
                            planetsVisited++;
                            playerTwoNodeController.addNodeToController(planetNode);
                            earthVisited = true;
                        }
                    }
                    break;
                case "RedPlanetNode":
                    if (!redVisited) {
                        if (playerNode.getName() == "DolphinNode") {
                            scoreCounterPlayerOne++;
                            planetsVisited++;
                            playerOneNodeControllerRed.addNodeToWaypointController(planetNode);
                            redVisited = true;
                        } else if (playerNode.getName() == "DolphinNode2") {
                            scoreCounterPlayerTwo++;
                            planetsVisited++;
                            playerTwoNodeController.addNodeToController(planetNode);
                            redVisited = true;
                        }
                    }
                    break;
                case "MoonNode":
                    if (!moonVisited) {
                        if (playerNode.getName() == "DolphinNode") {
                            scoreCounterPlayerOne++;
                            planetsVisited++;
                            playerOneNodeControllerMoon.addNodeToWaypointController(planetNode);
                            moonVisited = true;
                        } else if (playerNode.getName() == "DolphinNode2") {
                            scoreCounterPlayerTwo++;
                            planetsVisited++;
                            playerTwoNodeController.addNodeToController(planetNode);
                            moonVisited = true;
                        }
                        break;
                    }
            }
        }
    }

    @Override
    protected void setupCameras(SceneManager sceneManager, RenderWindow renderWindow) {
        // Creates the first players camera
        Camera camera = sceneManager.createCamera("MainCamera", Camera.Frustum.Projection.PERSPECTIVE);
        // Adds the first players camera to the top viewport.
        renderWindow.getViewport(0).setCamera(camera);

        camera.setMode('n');
        // Creates a new child node off of the root node.
        SceneNode cameraNode = sceneManager.getRootSceneNode().createChildSceneNode(camera.getName() + "Node");
        // Attaches the camera object to the camera node.
        cameraNode.attachObject(camera);

        // Creates the second players camera
        Camera camera2 = sceneManager.createCamera("MainCamera2", Camera.Frustum.Projection.PERSPECTIVE);
        // Adds the second players camera to the bottom viewport.
        renderWindow.getViewport(1).setCamera(camera2);
        camera2.setMode('n');
        // Creates a new child node off of the root node.
        SceneNode camera2Node = sceneManager.getRootSceneNode().createChildSceneNode(camera2.getName() + "Node");
        // Attaches the camera2 object to the camera2 node.
        camera2Node.attachObject(camera2);

        camera2.getFrustum().setFarClipDistance(1000.0f);

    }

    @Override
    protected void setupScene(Engine engine, SceneManager sceneManager) throws IOException {

        //Creates the dolphin and sets the render. Followed by the node creation and placement of the node in the world.
        // The entity is then attached to the node.
        SceneNode dolphinNode = createSceneNode(sceneManager,
                "DolphinNode", "dolphinHighPoly.obj", Vector3f.createFrom(-1.0f, 0.31f, 0.0f));

        SceneNode dolphinNode2 = createSceneNode(sceneManager,
                "DolphinNode2", "dolphinHighPoly.obj", Vector3f.createFrom(1.0f, 0.31f, 0.0f));

        setupOrbitCamera(engine, sceneManager);
        setupMovement(sceneManager);

        // Creates planet 1 and sets the render. Followed by the node creation and placement of the node in the world.
        // The entity is then attached to the node.
        SceneNode earthNode = createSceneNode(sceneManager,
                "EarthNode", "earth.obj", Vector3f.createFrom(-50.0f, 3.0f, -8.0f));
        earthNode.setLocalScale(1.5f, 1.5f, 1.5f);

        // Creates planet 2 and sets the render. Followed by the node creation and placement of the node in the world.
        // The entity is then attached to the node.
        SceneNode moonNode = createSceneNode(sceneManager,
                "MoonNode", "planet2.obj", Vector3f.createFrom(25.0f, 1.0f, 14.0f));
        moonNode.scale(1.0f, 1.0f, 1.0f);

        // Creates planet 3 and sets the render. Followed by the node creation and placement of the node in the world.
        // The entity is then attached to the node.
        SceneNode redPlanetNode = createSceneNode(sceneManager,
                "RedPlanetNode","planet3.obj", Vector3f.createFrom(3.0f, 2.0f, -30.0f));
        redPlanetNode.scale(2.0f, 2.0f, 2.0f);

        SceneNode solarSystemNode = sceneManager.getRootSceneNode().createChildSceneNode("SolarSystemNode");
        solarSystemNode.attachChild(earthNode);
        solarSystemNode.attachChild(redPlanetNode);
        solarSystemNode.attachChild(moonNode);

        // Create a pyramid ship manual object
        ManualObject pyramidShip = pyramidShip(engine, sceneManager);
        SceneNode pyramidShipNode = sceneManager.getRootSceneNode().createChildSceneNode("PyramidShipNode");
        pyramidShipNode.attachObject(pyramidShip);
        pyramidShipNode.setLocalPosition(5.0f, 7.0f, 8.0f);

        // Create a floor manual object, move it into place, and scale it to size
        ManualObject floor = floor(engine, sceneManager);
        SceneNode floorNode = sceneManager.getRootSceneNode().createChildSceneNode("floorNode");
        floorNode.attachObject(floor);
        floorNode.roll(Radianf.createFrom((float) Math.toRadians(180)));
        floorNode.scale(100.0f, 100.0f, 100.0f);

        // Create the second floor manual object, move it into place, and scale it to size.
        ManualObject floor2 = floor2(engine, sceneManager);
        SceneNode floor2Node = sceneManager.getRootSceneNode().createChildSceneNode("floor2Node");
        floor2Node.attachObject(floor2);
        floor2Node.roll(Radianf.createFrom((float) Math.toRadians(180)));
        floor2Node.rotate(Radianf.createFrom((float) Math.toRadians(180)), Vector3f.createFrom(0.0f,1.0f,0.0f));
        floor2Node.scale(100.0f, 100.0f, 100.0f);

        // Gets the ambient light and sets its intensity for the scene.
        sceneManager.getAmbientLight().setIntensity(new Color(0.2f, 0.2f, 0.2f));

        // Create a spot light
        Light positionalLight = sceneManager.createLight("PositionalLight", Light.Type.SPOT);
        positionalLight.setAmbient(new Color(0.1f, 0.1f, 0.1f));
        positionalLight.setDiffuse(new Color(0.7f, 0.7f, 0.7f));
        positionalLight.setSpecular(new Color(1.0f, 1.0f, 1.0f));
        positionalLight.setRange(10f);

        Light positionalLight2 = sceneManager.createLight("PositionalLight2", Light.Type.SPOT);
        positionalLight2.setAmbient(new Color(0.1f, 0.1f, 0.1f));
        positionalLight2.setDiffuse(new Color(0.7f, 0.7f, 0.7f));
        positionalLight2.setSpecular(new Color(1.0f, 1.0f, 1.0f));
        positionalLight2.setRange(10f);

        // Create the node for the light and attaches it to the dolphin node as a child.
        SceneNode positionalLightNode = sceneManager.getRootSceneNode().createChildSceneNode(positionalLight.getName() + "Node");
        positionalLightNode.attachObject(positionalLight);
        dolphinNode.attachChild(positionalLightNode);

        SceneNode positionalLightNode2 = sceneManager.getRootSceneNode().createChildSceneNode(positionalLight2.getName() + "Node");
        positionalLightNode2.attachObject(positionalLight2);
        dolphinNode2.attachChild(positionalLightNode2);

        setupInputs(sceneManager); // Setup the inputs


        playerOneNodeControllerEarth = new PlayerOneNodeController();
        playerOneNodeControllerRed = new PlayerOneNodeController();
        playerOneNodeControllerMoon = new PlayerOneNodeController();
        playerTwoNodeController = new PlayerTwoNodeController();

        solarSystemNodeController = new RotationController(Vector3f.createUnitVectorY(), 0.000006f);

        sceneManager.addController(playerTwoNodeController);
        sceneManager.addController(playerOneNodeControllerEarth);
        sceneManager.addController(playerOneNodeControllerMoon);
        sceneManager.addController(playerOneNodeControllerRed);
        sceneManager.addController(solarSystemNodeController);
    }

    @Override
    protected void setupWindow(RenderSystem renderSystem, GraphicsEnvironment graphicsEnvironment) {
        // Defines the window size of the game and make it so it is NOT in exclusive fullscreen.
        renderSystem.createRenderWindow(new DisplayMode(1000, 700, 24, 60), false);
    }

    @Override
    protected void setupWindowViewports(RenderWindow renderWindow) {
        renderWindow.addKeyListener(this);

        Viewport topViewport = renderWindow.getViewport(0);
        topViewport.setDimensions(0.51f, 0.0f, 1.0f, 0.49f);
        topViewport.setClearColor(new Color(0.67f, 0.84f, 0.90f));


        Viewport bottomViewport = renderWindow.createViewport(0.01f, 0.0f, 1.0f, 0.50f);
        bottomViewport.setClearColor(new Color(0.67f, 0.84f, 0.90f));
    }

    protected void setupOrbitCamera(Engine engine, SceneManager sceneManager) {
        SceneNode dolphinNode = sceneManager.getSceneNode("DolphinNode");
        SceneNode cameraNode = sceneManager.getSceneNode("MainCameraNode");
        orbitController1 = new Camera3PController(cameraNode, dolphinNode);

        SceneNode dolphinNode2 = sceneManager.getSceneNode("DolphinNode2");
        SceneNode cameraNode2 = sceneManager.getSceneNode("MainCamera2Node");
        orbitController2 = new Camera3PController(cameraNode2, dolphinNode2);
    }

    protected void setupMovement(SceneManager sceneManager) {
        SceneNode dolphinNode = sceneManager.getSceneNode("DolphinNode");
        movement3PController1 = new Movement3PController(dolphinNode);
    }

    protected void setupInputs(SceneManager sceneManager) {
        inputManager = new GenericInputManager();
        ArrayList controllers = inputManager.getControllers();

        Action moveForwardW = new ForwardThirdPersonAction(sceneManager.getSceneNode("DolphinNode2"), orbitController2);
        Action moveBackwardS = new BackwardThirdPersonAction(sceneManager.getSceneNode("DolphinNode2"), orbitController2);
        Action moveLeftA = new LeftThirdPersonAction(sceneManager.getSceneNode("DolphinNode2"), orbitController2);
        Action moveRightD = new RightThirdPersonAction(sceneManager.getSceneNode("DolphinNode2"), orbitController2);

        Action increaseElevation = new ThirdPersonElevationIncrease(sceneManager.getSceneNode("DolphinNode2"), orbitController2);
        Action decreaseElevation = new ThirdPersonElevationDecrease(sceneManager.getSceneNode("DolphinNode2"), orbitController2);

        Action orbitLeft = new ThirdPersonOrbitLeft(sceneManager.getSceneNode("DolphinNode2"), orbitController2);
        Action orbitRight = new ThirdPersonOrbitRight(sceneManager.getSceneNode("DolphinNode2"), orbitController2);

        Action radiasIncrease = new ThirdPersonRadiasIncrease(sceneManager.getSceneNode("DolphinNode2"), orbitController2);
        Action radiasDecrease = new ThirdPersonRadiasDecrease(sceneManager.getSceneNode("DolphinNode2"), orbitController2);

        Action turnLeft = new TurnLeftThirdPersonAction(sceneManager.getSceneNode("DolphinNode2"));
        Action turnRight = new TurnRightThirdPersonAction(sceneManager.getSceneNode("DolphinNode2"));



        for (Object controller : controllers) {
            Controller c = (Controller) controller;

            if (c.getType() == Controller.Type.KEYBOARD) {
                // Avatar movements
                inputManager.associateAction(c, Component.Identifier.Key.W, moveForwardW, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                inputManager.associateAction(c, Component.Identifier.Key.S, moveBackwardS, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                inputManager.associateAction(c, Component.Identifier.Key.A, moveLeftA, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                inputManager.associateAction(c, Component.Identifier.Key.D, moveRightD, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

                // Turn the dolphin
                inputManager.associateAction(c, Component.Identifier.Key.Q, turnLeft, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                inputManager.associateAction(c, Component.Identifier.Key.E, turnRight, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);


                // Camera Movements
                inputManager.associateAction(c, Component.Identifier.Key.UP, increaseElevation, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                inputManager.associateAction(c, Component.Identifier.Key.DOWN, decreaseElevation, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                inputManager.associateAction(c, Component.Identifier.Key.LEFT, orbitLeft, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                inputManager.associateAction(c, Component.Identifier.Key.RIGHT, orbitRight, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

                inputManager.associateAction(c, Component.Identifier.Key.Z, radiasIncrease, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                inputManager.associateAction(c, Component.Identifier.Key.X, radiasDecrease, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

            } else if (c.getType() == Controller.Type.GAMEPAD || c.getType() == Controller.Type.STICK) {
                orbitController1.setupInput(inputManager, c.getName());
                movement3PController1.setupInput(inputManager, c.getName());
            }
        }
    }

    protected SceneNode createSceneNode(SceneManager sceneManager, String nameofNode,
                                        String nameOfOBJFile, Vector3 spawnLocation) throws IOException {

        Entity entity = sceneManager.createEntity(nameofNode, nameOfOBJFile);
        entity.setPrimitive(Renderable.Primitive.TRIANGLES);

        SceneNode sceneNode = sceneManager.getRootSceneNode().createChildSceneNode(nameofNode);
        sceneNode.attachObject(entity);
        sceneNode.setLocalPosition(spawnLocation);

        return sceneNode;
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

    protected ManualObject floor(Engine engine, SceneManager sceneManager) throws IOException {

        ManualObject floor = sceneManager.createManualObject("floor");
        ManualObjectSection floorSection  = floor.createManualSection("floorSection");

        floor.setGpuShaderProgram(sceneManager.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));

        float[] vertices = new float[] {
                1.0f,0.0f,1.0f,     -1.0f,0.0f,-1.0f,   1.0f,0.0f,-1.0f

        };

        float [] textureCoordinates = new float[] {
                0.0f,0.0f,      0.5f,1.0f,      1.0f,0.0f
        };

        float[] normals = new float[] {
                0.0f,1.0f,0.0f,    0.0f,1.0f,0.0f,    0.0f,1.0f,0.0f
        };

        int[] indices = new int[] {0,1,2};

        FloatBuffer verticesBuffer = BufferUtil.directFloatBuffer(vertices);
        FloatBuffer textureBuffer = BufferUtil.directFloatBuffer(textureCoordinates);
        FloatBuffer normalsBuffer = BufferUtil.directFloatBuffer(normals);
        IntBuffer indexBuffer = BufferUtil.directIntBuffer(indices);

        floorSection.setVertexBuffer(verticesBuffer);
        floorSection.setTextureCoordsBuffer(textureBuffer);
        floorSection.setNormalsBuffer(normalsBuffer);
        floorSection.setIndexBuffer(indexBuffer);

        Texture texture = engine.getTextureManager().getAssetByPath("blue.jpeg");
        TextureState textureState = (TextureState)
                sceneManager.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);

        textureState.setTexture(texture);

        FrontFaceState faceState =  (FrontFaceState)
                sceneManager.getRenderSystem().createRenderState(RenderState.Type.FRONT_FACE);

        floor.setDataSource(Renderable.DataSource.INDEX_BUFFER);
        floor.setRenderState(textureState);
        floor.setRenderState(faceState);

        return floor;
    }

    protected ManualObject floor2(Engine engine, SceneManager sceneManager) throws IOException {

        ManualObject floor2 = sceneManager.createManualObject("floor2");
        ManualObjectSection floorSection  = floor2.createManualSection("floor2Section");

        floor2.setGpuShaderProgram(sceneManager.getRenderSystem().getGpuShaderProgram(GpuShaderProgram.Type.RENDERING));

        float[] vertices = new float[] {
                1.0f,0.0f,1.0f,     -1.0f,0.0f,-1.0f,   1.0f,0.0f,-1.0f

        };

        float [] textureCoordinates = new float[] {
                0.0f,0.0f,      0.5f,1.0f,      1.0f,0.0f
        };

        float[] normals = new float[] {
                0.0f,1.0f,0.0f,    0.0f,1.0f,0.0f,    0.0f,1.0f,0.0f
        };

        int[] indices = new int[] {0,1,2};

        FloatBuffer verticesBuffer = BufferUtil.directFloatBuffer(vertices);
        FloatBuffer textureBuffer = BufferUtil.directFloatBuffer(textureCoordinates);
        FloatBuffer normalsBuffer = BufferUtil.directFloatBuffer(normals);
        IntBuffer indexBuffer = BufferUtil.directIntBuffer(indices);

        floorSection.setVertexBuffer(verticesBuffer);
        floorSection.setTextureCoordsBuffer(textureBuffer);
        floorSection.setNormalsBuffer(normalsBuffer);
        floorSection.setIndexBuffer(indexBuffer);

        Texture texture = engine.getTextureManager().getAssetByPath("blue.jpeg");
        TextureState textureState = (TextureState)
                sceneManager.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);

        textureState.setTexture(texture);

        FrontFaceState faceState =  (FrontFaceState)
                sceneManager.getRenderSystem().createRenderState(RenderState.Type.FRONT_FACE);

        floor2.setDataSource(Renderable.DataSource.INDEX_BUFFER);
        floor2.setRenderState(textureState);
        floor2.setRenderState(faceState);

        return floor2;
    }
}
