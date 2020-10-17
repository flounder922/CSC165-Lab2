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
    private String displayString; // Used to display the information on the HUD.


    private int score_counter = 0;



    private InputManager inputManager;
    private Camera camera;
    private Camera3PController orbitController1, orbitController2;
    private Movement3PController movement3PController1;

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

        if (score_counter == 3) {
            displayString = "YOU BEAT THE GAME!!!";
        } else {
            displayString = "Planets Visited: " + score_counter + "  Trophies: ";
        }

        renderSystem.setHUD(displayString, 10, 10);
        inputManager.update(elapsedTime);
    }

    @Override
    protected void setupCameras(SceneManager sceneManager, RenderWindow renderWindow) {
        // Creates the first players camera
        camera = sceneManager.createCamera("MainCamera", Camera.Frustum.Projection.PERSPECTIVE);
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
        SceneNode RedPlanetNode = createSceneNode(sceneManager,
                "RedPlanetNode","planet3.obj", Vector3f.createFrom(3.0f, 2.0f, -30.0f));
        RedPlanetNode.scale(2.0f, 2.0f, 2.0f);

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

        setupInputs(sceneManager); // Setup the inputs
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
        topViewport.setDimensions(0.51f, 0.01f, 0.99f, 0.49f);


        Viewport bottomViewport = renderWindow.createViewport(0.01f, 0.01f, 0.99f, 0.49f);
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



        for (Object controller : controllers) {
            Controller c = (Controller) controller;

            if (c.getType() == Controller.Type.KEYBOARD) {
                // Avatar movements
                inputManager.associateAction(c, Component.Identifier.Key.W, moveForwardW, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                inputManager.associateAction(c, Component.Identifier.Key.S, moveBackwardS, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                inputManager.associateAction(c, Component.Identifier.Key.A, moveLeftA, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                inputManager.associateAction(c, Component.Identifier.Key.D, moveRightD, InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);

                // Turn the dolphin



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
