package com.releasemeshtoheightmap;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.awt.image.WritableRaster;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL43;
import org.lwjgl.system.MemoryStack;
import java.nio.file.Path;
import javafx.application.Platform;



public class BetaApp {
    private static long window;

    public static int progress ;
    public static int maxProgress ;
    public static void main(String[] args) {
        createDirectoriesIfNotExist();
        Ui(args);
        Settings settings=ConsoleUi();
        startcalculation(settings,Consolemanager.CalculateonCpu());
    }
    public static void startcalculation(Settings settings,int mode){
        
        if(mode==0){
            CalculateonGpu(settings,Preparemesh(settings));
        }
        else{
            runonCpu(settings, mode, Preparemesh(settings));
        }
    }
    public static void runonCpu(Settings settings,int mode,Mesh mesh){
        calculate(calculationpreperation(settings,mesh.data),settings,mesh,mode);
    }
    
    public static BufferedImage calculate(Vector3 scale, Settings settings, Mesh mesh,int mode) {
        System.out.println("Scale:" + scale.debugVector(2));
        float[][] storeyvalue = new float[settings.TexturesizeX][settings.TexturesizeY];
        if(mode ==2||mode ==3){
            mesh.triangels=mesh.sorttriangelsP1tominx(mesh.triangels);
        }
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        maxProgress = settings.pixelstocalculate;
        for (int x = 0; x < settings.TexturesizeX; x++) {
            for (int y = 0; y < settings.TexturesizeY; y++) {
                final int finalX = x;
                final int finalY = y;
                executor.submit(() -> {
                    // Your thread work here
                    new NewThread(storeyvalue, mesh, finalX, finalY, settings, scale,mode).run();
                });
            }
        }
        executor.shutdown();
        try {
            // Wait for all tasks to complete or timeout after 60 minutes
            if (!executor.awaitTermination(60, TimeUnit.MINUTES)) {
                executor.shutdownNow(); // Cancel currently executing tasks
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Executor did not terminate");
                }
            }

        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        BufferedImage img = new BufferedImage(settings.TexturesizeX, settings.TexturesizeY,BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = img.getRaster();
        for (int y = 0; y < settings.TexturesizeY; y++) {
            for (int x = 0; x < settings.TexturesizeX; x++) {

                raster.setSample(x, y, 0, (int) (storeyvalue[x][y] * 255));
            }
        }
        try {
            ImageIO.write(img, "png", new File(settings.OutputfilePath+new Date().getTime()+".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void CalculateonGpu(Settings settings,Mesh mesh){
        Vector3 scaleing = calculationpreperation(settings, mesh.data);
        System.out.println(scaleing.debugVector(2));
        //Autoscaling on Gpu prevent weird results
        //scaleing= new Vector3(1,scaleing.y*2,1);
        scaleing= new Vector3(1,1,1);
        mesh.verts = mesh.Transform(scaleing, new Vector3(0,0,0), new Vector3(0,0,0));
        initGLFW();
        float[] data = prepareRunComputeShader(mesh, settings);
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
        settexture(settings, data);
    }
    private static void initGLFW() {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Create windowed mode window and its OpenGL context
        window = GLFW.glfwCreateWindow(800, 600, "Compute Shader", 0, 0);
        if (window == 0) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(window);

        // Enable v-sync
        GLFW.glfwSwapInterval(1);

        // Make the window visible
        GLFW.glfwShowWindow(window);
        //GLFW.glfwDestroyWindow(window);

        // Initialize OpenGL bindings
        GL.createCapabilities();

    }

    public static void Ui(String[] args){
        Consolemanager.Setui(args);
        //doesnt terminate if Gui is selected
    }
    public static Settings ConsoleUi(){
        Settings settings;
        Consolemanager.ShowProgramminfos();

        if (Consolemanager.Showasktoimportsettings() == false) {
            settings = Consolemanager.SetnewSettings();
            Consolemanager.Savesettings(settings);
        } else {
            settings = Consolemanager.Loadsettingsfromfile();
        }
        return settings;
    }
    public static Mesh Preparemesh(Settings settings){
        SupportedFiletype supportedFiletype = readandcreatemesh(settings, settings.getFiletype());
        Mesh mesh = new Mesh(supportedFiletype.getmesh().verts.clone(),supportedFiletype.getmesh().indicies.clone());
        Vector3[] transformedVerts = mesh.Transform(settings.scale, settings.rotation, settings.position);
        mesh.Applytransformedverts(transformedVerts);
        mesh.settriangels();
        return mesh;
    }
    public static SupportedFiletype readandcreatemesh(Settings settings, Filetype filetype) {
        System.out.println("Processing File...");
        switch (filetype) {
            case obj:
                Obj obj = new Obj();
                obj.setmesh(obj.readmesh(settings.MeshFile));
                return obj;
            case Unknow:
                System.out.println("There is a Unknown or unsupported Filetype");
                break;
            default:
                System.out.println("There is a Programmer mistake");
                break;
        }
        return null;
    }
    public static Vector3 calculationpreperation(Settings settings, Filedata data) {
        Vector3 Factor = new Vector3();
        Factor.x = settings.TexturesizeX / (data.maxX.x - data.minX.x);
        Factor.y = 1 / (data.maxY.y - data.minY.y);
        Factor.z = settings.TexturesizeY / (data.maxZ.z - data.minZ.z);
        return Factor;
    }
    private static float[] prepareRunComputeShader(Mesh mesh, Settings settings) {
        int numTriangles = mesh.triangels.length;
        int numRays = settings.TexturesizeX * settings.TexturesizeY;

        float[] triangles = new float[numTriangles * 12];
        float[] rays = new float[numRays * 8];
        //float[] intersections = new float[numRays * 3];

        for (int i = 0; i < numTriangles; i++) {
            Triangel t = mesh.triangels[i];
            triangles[i * 12] = t.a.x;
            triangles[i * 12 + 1] = t.a.y;
            triangles[i * 12 + 2] = t.a.z;
            triangles[i * 12 + 3] = 17;
            triangles[i * 12 + 4] = t.b.x;
            triangles[i * 12 + 5] = t.b.y;
            triangles[i * 12 + 6] = t.b.z;
            triangles[i * 12 + 7] = 17;
            triangles[i * 12 + 8] = t.c.x;
            triangles[i * 12 + 9] = t.c.y;
            triangles[i * 12 + 10] = t.c.z;
            triangles[i * 12 + 11] = 17;
        }
        Vector3 scale =calculationpreperation(settings,mesh.data);
        ArrayList<Vector3> rayOrigins = new ArrayList<Vector3>();
        for (int x = 0; x < settings.TexturesizeX; x++) {
            for (int y = 0; y < settings.TexturesizeY; y++) {
                rayOrigins.add(new Vector3((x - (settings.TexturesizeX / 2)) / scale.x, mesh.data.maxY.y+1f,
                        (y - (settings.TexturesizeY / 2)) / scale.z));
            }
        }
        for (int i = 0; i < numRays; i++) {
            Vector3 origin = rayOrigins.get(i);
            Vector3 direction = new Vector3(0, -1000, 0); // Example direction, modify as needed
            rays[i * 8] = origin.x;
            rays[i * 8 + 1] = origin.y;
            rays[i * 8 + 2] = origin.z;
            rays[i * 8 + 3] = 0;
            //System.out.println(origin.debugVector(0));
            rays[i * 8 + 4] = 0;
            rays[i * 8 + 5] = direction.y;
            rays[i * 8 + 6] = direction.z;
            rays[i * 8 + 7] = direction.z;
           // System.out.println("Scale:");
        }
    // Fake Batch processing
    
        int batchSize = getMaxWorkGroupSizes()[0]*10; // Adjust this size as needed
         int numBatches = (numRays + batchSize - 1) / batchSize;
        float[] result = new float[numRays * 3];
        maxProgress = numBatches;
    for (int batch = 0; batch < numBatches; batch++) {
        progress = batch;
        showprogress();
        int offset = batch * batchSize;
        int currentBatchSize = Math.min(batchSize, numRays - offset);

        // Copy the current batch of rays
        float[] cuttedRays = new float[currentBatchSize * 8]; // 8 = 2 vectors * 3 components + 2 padding values
        System.arraycopy(rays, offset * 8, cuttedRays, 0, currentBatchSize * 8);

        // Prepare intersection array for the current batch
        float[] batchIntersections = new float[currentBatchSize * 3];

        // Run compute shader for the current batch
        float[] batchResult = runComputeShader(triangles, cuttedRays, batchIntersections, currentBatchSize, numTriangles);

        // Copy the results back to the main result array
        System.arraycopy(batchResult, 0, result, offset * 3, currentBatchSize * 3);


    }
    
    System.out.println("rayssize:"+rays.length*Float.SIZE+"trianglessize:"+triangles.length*Float.SIZE+"intersize"+result.length*Float.SIZE);
    System.out.println("Gesammt"+rays.length*Float.SIZE+triangles.length*Float.SIZE+result.length*Float.SIZE);
    return result;
    }
    public static void showprogress() {
        Platform.runLater(() -> Gui.controller.updateProgressBar());
    }
    private static float[] runComputeShader(float[] triangles, float[] rays, float[] intersections, int numRays, int numTriangles) {
        int shaderProgram = createShaderProgram();
        GL43.glUseProgram(shaderProgram);
        
        // Create and bind buffers
        int triangleBuffer = GL43.glGenBuffers();
        int rayBuffer = GL43.glGenBuffers();
        int intersectionBuffer = GL43.glGenBuffers();
        
        // Query maximum buffer size
        int[] maxBufferSize = new int[1];
        GL43.glGetIntegerv(GL43.GL_MAX_SHADER_STORAGE_BLOCK_SIZE, maxBufferSize);
        //System.out.println("Max Shader Storage Block Size: " + maxBufferSize[0]);

        // Triangle Buffer
        GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, triangleBuffer);
        FloatBuffer triangleBufferData = BufferUtils.createFloatBuffer(triangles.length);
        triangleBufferData.put(triangles).flip();
        GL43.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, triangleBufferData, GL43.GL_STATIC_DRAW);
    
        // Ray Buffer
        GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, rayBuffer);
        FloatBuffer rayBufferData = BufferUtils.createFloatBuffer(rays.length);
        rayBufferData.put(rays).flip();
        GL43.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, rayBufferData, GL43.GL_STATIC_DRAW);
    
        // Intersection Buffer
        int intersectionCount = intersections.length / 3;
        GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, intersectionBuffer);
        GL43.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, intersectionCount * 4 * Float.BYTES, GL43.GL_DYNAMIC_READ);
    
        // Bind buffers to shader storage
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 0, triangleBuffer);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 1, rayBuffer);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 2, intersectionBuffer);
    
        // Set uniforms
        int numRaysLocation = GL43.glGetUniformLocation(shaderProgram, "numRays");
        int numTrianglesLocation = GL43.glGetUniformLocation(shaderProgram, "numTriangles");
    
        GL43.glUniform1i(numRaysLocation, numRays);
        GL43.glUniform1i(numTrianglesLocation, numTriangles);
    
        // Dispatch compute shader
        GL43.glDispatchCompute((numRays + 255) / 256, 1, 1);
        GL43.glMemoryBarrier(GL43.GL_SHADER_STORAGE_BARRIER_BIT);
    
        // Read back results using glMapBuffer
        GL43.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, intersectionBuffer);
        FloatBuffer mappedBuffer = GL43.glMapBuffer(GL43.GL_SHADER_STORAGE_BUFFER, GL43.GL_READ_ONLY).asFloatBuffer();
        if (mappedBuffer != null) {
            // Account for padding
            for (int i = 0; i < intersectionCount; i++) {
                intersections[i * 3] = mappedBuffer.get(i * 4);       // x
                intersections[i * 3 + 1] = mappedBuffer.get(i * 4 + 1); // y
                intersections[i * 3 + 2] = mappedBuffer.get(i * 4 + 2); // z
                //System.out.println(mappedBuffer.get(i * 4)+"   "+mappedBuffer.get(i * 4+1)+"   "+mappedBuffer.get(i * 4+2)+"   "+mappedBuffer.get(i * 4+3)+"   "+mappedBuffer.get(i * 4));
                // Skip the padding value
            }
            GL43.glUnmapBuffer(GL43.GL_SHADER_STORAGE_BUFFER);
        } else {
            System.err.println("Failed to map buffer.");
        }
    
        // Print the first few values for debugging
        
    
        // Cleanup
        GL43.glDeleteBuffers(triangleBuffer);
        GL43.glDeleteBuffers(rayBuffer);
        GL43.glDeleteBuffers(intersectionBuffer);
        GL43.glDeleteProgram(shaderProgram);
    
        return intersections;
    }
    private static int createShaderProgram() {


        int computeShader = GL43.glCreateShader(GL43.GL_COMPUTE_SHADER);
        //System.out.println("Shader created successfully");
        String shaderpath="Shader/intersection.comp";
        String shaderSource;
        try {
            shaderSource = new String(Files.readAllBytes(Paths.get(shaderpath)));
        } catch (IOException e) {
            shaderSource = "#version 440\n" +
            "\n" +
            "layout (local_size_x = 256) in;\n" +
            "\n" +
            "layout (std430, binding = 0) buffer TriangleBuffer {\n" +
            "    vec3 triangles[]; // Array of triangles, each triangle has 3 vertices\n" +
            "};\n" +
            "\n" +
            "layout (std430, binding = 1) buffer RayBuffer {\n" +
            "    vec3 rays[]; // Array of ray origins and directions\n" +
            "};\n" +
            "\n" +
            "layout (std430, binding = 2) buffer IntersectionBuffer {\n" +
            "    vec3 intersections[]; // Array to store intersection points\n" +
            "};\n" +
            "\n" +
            "uniform int numRays; // Number of rays\n" +
            "uniform int numTriangles; // Number of triangles\n" +
            "\n" +
            "void main() {\n" +
            "    uint id = gl_GlobalInvocationID.x;\n" +
            "    if (id >= numRays) return;\n" +
            "\n" +
            "    const float EPSILON = 1e-6;\n" +
            "\n" +
            "    vec3 rayOrigin = rays[id * 2];     // Each ray has an origin and direction\n" +
            "    vec3 rayDirection = rays[id * 2 + 1];\n" +
            "\n" +
            "    vec3 highestIntersection = vec3(-1.0, -1.0, -1.0); // Default to invalid point\n" +
            "\n" +
            "    for (int i = 0; i < numTriangles; i++) {\n" +
            "        vec3 a = triangles[i * 3];     // Each triangle has 3 vertices\n" +
            "        vec3 b = triangles[i * 3 + 1];\n" +
            "        vec3 c = triangles[i * 3 + 2];\n" +
            "\n" +
            "        vec3 edge1 = b - a;\n" +
            "        vec3 edge2 = c - a;\n" +
            "\n" +
            "        vec3 h = cross(rayDirection, edge2);\n" +
            "        float dotProduct = dot(edge1, h);\n" +
            "\n" +
            "        if (dotProduct > -EPSILON && dotProduct < EPSILON) {\n" +
            "            continue; // Ray is parallel to the triangle\n" +
            "        }\n" +
            "\n" +
            "        float f = 1.0 / dotProduct;\n" +
            "        vec3 s = rayOrigin - a;\n" +
            "        float u = f * dot(s, h);\n" +
            "\n" +
            "        if (u + EPSILON < 0.0 || u > 1.0 - EPSILON) {\n" +
            "            continue; // Intersection point is outside the triangle\n" +
            "        }\n" +
            "\n" +
            "        vec3 q = cross(s, edge1);\n" +
            "        float v = f * dot(rayDirection, q);\n" +
            "\n" +
            "        if (v < 0.0 || u + v > 1.0 + EPSILON) {\n" +
            "            continue; // Intersection point is outside the triangle\n" +
            "        }\n" +
            "\n" +
            "        float t = f * dot(edge2, q);\n" +
            "\n" +
            "        if (t > EPSILON) {\n" +
            "            // Ray intersection\n" +
            "            vec3 currentIntersection = rayOrigin + rayDirection * t;\n" +
            "\n" +
            "            // Check if this intersection has a higher y-coordinate than the previous highest\n" +
            "            if (currentIntersection.y > highestIntersection.y) {\n" +
            "                highestIntersection = currentIntersection;\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    // Output for debugging: \n" +
            "    // Output highestIntersection to the intersection buffer\n" +
            "    intersections[id] = highestIntersection;\n" +
            "}\n";
            System.out.println("NoShaderfile(create one...)");
            File shaderFile = new File(shaderpath);
            try (FileWriter fileWriter = new FileWriter(shaderFile)) {
                fileWriter.write(shaderSource);
                System.out.println("Shader file created: " + shaderFile.getAbsolutePath());
            } catch (IOException ee) {
                //throw new RuntimeException("Failed to create shader file", ee);
                System.out.println("Failed to create shader file");
            }
        }
        GL43.glShaderSource(computeShader, shaderSource);
        GL43.glCompileShader(computeShader);
    
        if (GL43.glGetShaderi(computeShader, GL43.GL_COMPILE_STATUS) == GL43.GL_FALSE) {
            System.err.println("Failed to compile compute shader");
            System.err.println(GL43.glGetShaderInfoLog(computeShader));
            System.exit(1);
        }
    
        int shaderProgram = GL43.glCreateProgram();
        GL43.glAttachShader(shaderProgram, computeShader);
        GL43.glLinkProgram(shaderProgram);
    
        if (GL43.glGetProgrami(shaderProgram, GL43.GL_LINK_STATUS) == GL43.GL_FALSE) {
            System.err.println("Failed to link shader program");
            System.err.println(GL43.glGetProgramInfoLog(shaderProgram));
            System.exit(1);
        }
    
        GL43.glDeleteShader(computeShader);
        return shaderProgram;
    }
    private static void settexture(Settings settings, float[] value) {
        BufferedImage img = new BufferedImage(settings.TexturesizeX, settings.TexturesizeY,
                BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = img.getRaster();
        int index =1;
            for (int x = 0; x < settings.TexturesizeX; x++) {
               for (int y = 0; y < settings.TexturesizeY; y++) {
                raster.setSample(x, y, 0, (int) (value[index] * 255));
                   index+=3;
                }
            }
        try {
            ImageIO.write(img, "png", new File(
                    settings.OutputfilePath+"Output"+new Date().getTime()+".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int[] getMaxWorkGroupSizes() {
        int[] maxSizes = new int[3];

        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Create a buffer to hold the values
            IntBuffer buffer = stack.mallocInt(3);

            // Query maximum work group sizes
            GL43.glGetIntegeri_v(GL43.GL_MAX_COMPUTE_WORK_GROUP_SIZE, 0, buffer);
            maxSizes[0] = buffer.get(0);
            GL43.glGetIntegeri_v(GL43.GL_MAX_COMPUTE_WORK_GROUP_SIZE, 1, buffer);
            maxSizes[1] = buffer.get(1);
            GL43.glGetIntegeri_v(GL43.GL_MAX_COMPUTE_WORK_GROUP_SIZE, 2, buffer);
            maxSizes[2] = buffer.get(2);
        }

        return maxSizes;
    }
    public static void createDirectoriesIfNotExist() {
        // Define the directories you want to create
        String[] directories = {"Output", "Settings", "Shader"};

        for (String dirName : directories) {
            Path path = Paths.get(dirName);
            if (Files.notExists(path)) {
                try {
                    Files.createDirectory(path);
                    System.out.println("Created directory: " + dirName);
                } catch (IOException e) {
                    System.err.println("Failed to create directory: " + dirName);
                    e.printStackTrace();
                }
            } else {
                System.out.println("Directory already exists: " + dirName);
            }
        }
    }
}