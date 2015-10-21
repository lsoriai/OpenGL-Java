/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.nio.ByteBuffer;
import org.lwjgl.Sys;
import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWvidmode;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.opengl.GLContext;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 *
 * @author Agustin
 */
public class OpenGLHelper {
    private Shader vertexShader;
    private Shader fragmentShader;
    private ShaderProgram shaderProgram;
    
    
    private int uniView;
    
    // We need to strongly reference callback instances.
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback keyCallback;
    private GLFWCursorPosCallback cpCallback;
    
    private static final int WIDTH = 700;
    private static final int HEIGHT = 700;
    
    // The window handle
    private long window;
    
    private FPCameraController camera = new FPCameraController(-5, -5, -10);
    
    private float deltaTime       = 0.0f; //length of frame
    private double lastFrame; // when the last frame was
    
    private final float movementSpeed = 30.0f; //move 10 units per second
    private double lastX = 400, lastY = 300;
    
    private String tutorialName = "Tutorial";
    
    public static final int FLOAT_SIZE = 4;
    
    public OpenGLHelper(String tutorialName)
    {
        this.tutorialName = tutorialName;
    }
    public OpenGLHelper(String tutorialName, FPCameraController camera)
    {
        this.tutorialName = tutorialName;
        this.camera = camera;
    }
    
    public void run(Drawable drawable) {
        System.out.println("Hello LWJGL " + Sys.getVersion() + "!. " + tutorialName);

        try {
            loop(drawable);

            // Release window and window callbacks
            glfwDestroyWindow(window);
            keyCallback.release();
        } finally {
            // Terminate GLFW and release the GLFWerrorfun
            glfwTerminate();
            errorCallback.release();
        }
    }
    
    public void initGL(String vertexFileName, String fragmentFileName)
    {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (glfwInit() != GL_TRUE) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, tutorialName, NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
        
        camera.setMouseSensitivity(0.09f);
        
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                    glfwSetWindowShouldClose(window, GL_TRUE); // We will detect this in our rendering loop
                    return;
                }
               
                //when passing in the distance to move
                //we times the movementSpeed with dt this is a time scale
                //so if its a slow frame u move more then a fast frame
                //so on a slow computer you move just as fast as on a fast computer
                if (key == GLFW_KEY_W)//move forward
                {
                    camera.walkForward(movementSpeed*deltaTime);
                }
                if (key == GLFW_KEY_S)//move backwards
                {
                    camera.walkBackwards(movementSpeed*deltaTime);
                }
                if (key == GLFW_KEY_A)//strafe left
                {
                    camera.strafeLeft(movementSpeed*deltaTime);
                }
                if (key == GLFW_KEY_D)//strafe right
                {
                    camera.strafeRight(movementSpeed*deltaTime);
                }
                if (key == GLFW_KEY_UP) 
                {
                    camera.pitch(70.0f  *deltaTime);
                }
                if (key == GLFW_KEY_DOWN) 
                {
                    camera.pitch(-70.0f  *deltaTime);
                }
                if (key == GLFW_KEY_RIGHT) 
                {
                    camera.yaw(70.0f  *deltaTime);
                }
                if (key == GLFW_KEY_LEFT) 
                {
                    camera.yaw(-70.0f  *deltaTime);
                }
            }
        });

        glfwSetCursorPosCallback(window, cpCallback = new GLFWCursorPosCallback() {
                        private boolean firstMouse = true;
                        private double centerX = WIDTH/2.0;
                        private double centerY = HEIGHT/2.0;
                        
			@Override
			public void invoke(long window, double xpos, double ypos) {
				if(firstMouse)
                                {
                                    lastX = xpos;
                                    lastY = ypos;
                                    firstMouse = false;
                                }

                                float xoffset = (float)(xpos - lastX);
                                float yoffset = (float)(lastY - ypos); 

                                lastX = xpos;
                                lastY = ypos;

                                camera.ProcessMouseMovement(xoffset, yoffset);
			}
		});
        
        // Get the resolution of the primary monitor
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(
                window,
                (GLFWvidmode.width(vidmode) - WIDTH) / 2,
                (GLFWvidmode.height(vidmode) - HEIGHT) / 2
        );

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
        
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the ContextCapabilities instance and makes the OpenGL
        // bindings available for use.
        GLContext.createFromCurrent();
        
        glEnable( GL_DEPTH_TEST);
        
        prepareShader(vertexFileName, fragmentFileName);
    }
    
    private ShaderProgram prepareShader(String vertexFileName, String fragmentFileName)
    {
    	/*Creamos una variable de tal manera que se obtenga la informacion a cerca
    	 * de los vertices, colores e incidencia de iluminacion, con lo que trabajará
    	 * el Shader
    	 */
        vertexShader = Shader.loadShader(GL_VERTEX_SHADER, vertexFileName);
        fragmentShader = Shader.loadShader(GL_FRAGMENT_SHADER, fragmentFileName);
        
        shaderProgram = new ShaderProgram();
        shaderProgram.attachShader(vertexShader);
        shaderProgram.attachShader(fragmentShader);
        shaderProgram.link();
        shaderProgram.use();
        
        int uniProjection = shaderProgram.getUniformLocation("projection");
        float ratio = (float) WIDTH / (float) HEIGHT;
        Matrix4f projection = Matrix4f.perspective(60, ratio, 0.1f, 10000.0f);
        glUniformMatrix4(uniProjection, false, projection.getBuffer());

        uniView = shaderProgram.getUniformLocation("view");
        
        return shaderProgram;
    }
    
    public static ShaderProgram computeShader(String vertexFileName, String fragmentFileName)
    {
        Shader vertexShader = Shader.loadShader(GL_VERTEX_SHADER, vertexFileName);
        Shader fragmentShader = Shader.loadShader(GL_FRAGMENT_SHADER, fragmentFileName);
        
        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.attachShader(vertexShader);
        shaderProgram.attachShader(fragmentShader);
        shaderProgram.link();
        shaderProgram.use();

        return shaderProgram;
    }
    
    public ShaderProgram getShaderProgram()
    {
        return shaderProgram;
    }
    
    public Matrix4f getViewMatrix()
    {
        return camera.lookThrough();
    }
    
    private void loop(Drawable drawable)
    {
        // Set the clear color
        glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (glfwWindowShouldClose(window) == GL_FALSE) {

            double currentFrame = glfwGetTime();
            deltaTime = (float) (currentFrame - lastFrame);
            lastFrame = currentFrame;

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            /* update camera position */
            Matrix4f view = camera.lookThrough();
            glUniformMatrix4(uniView, false, view.getBuffer());

            /* Render */
            drawable.draw();

            /* Swap buffers and poll Events */
            glfwSwapBuffers(window); // swap the color buffers  
        }
    }
    
    public float getDeltaTime()
    {
        return deltaTime;
    }
}
