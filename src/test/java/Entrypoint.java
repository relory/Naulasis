import imgui.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import io.naulasis.Naulasis;
import io.naulasis.components.impl.*;
import io.naulasis.utils.MathUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Entrypoint {

    public static void main(String[] args) {
        GLFWErrorCallback.createPrint(System.err).set();
        if(!glfwInit()){
            System.out.println("oh shit, I did something wrong :( ");
        }
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        //glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);
        glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, GLFW_TRUE);

        long glfwWindow = glfwCreateWindow(800, 550, "Naulasis", NULL, NULL);
        if(glfwWindow == NULL){
            System.out.println("no Idea what I did wrong now");
        }
        glfwMakeContextCurrent(glfwWindow);
        glfwSwapInterval(1);
        glfwShowWindow(glfwWindow);
        GL.createCapabilities();
        ImGui.createContext();
        ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
        imGuiGlfw.init(glfwWindow, true);
        ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
        imGuiGl3.init("#version 130");
        Naulasis naulasis = new Naulasis();
        naulasis.Init(glfwWindow);
        /*
        glfwSetKeyCallback(glfwWindow, (windowHandle, key, scancode, action, mods) -> {
            if(action != GLFW_RELEASE) {
                System.out.println("hello " + key);
            }
        });

         */
        float currentSize = 0;
        float currentPosX = 0;
        float currentPosY = 0;

        float CursorPositionX = 0;
        float CursorPositionY = 0;
        float WindowPositionX = 0;
        float WindowPositionY = 0;

        CheckBox checkbox = new CheckBox();
        Slider slider = new Slider("hello", 1, 100, 5, 1);
        Slider slider1 = new Slider("hello1", 1, 300, 5, 1);

        Button button = new Button();
        Switch switcher = new Switch(false);

        TextLink textLink = new TextLink();
        TextComponent textComponent = new TextComponent("test", new ImVec2(250, 200), new ImVec4(50, 255, 50, 255));

        ImFont font = ImGui.getIO().getFonts().addFontFromFileTTF(System.getProperty("user.home") + "/.salorid/Fonts/InterVariable.ttf", 20.0f);
        ImGuiIO io = ImGui.getIO();
        ImFontAtlas fontAtlas = io.getFonts();
        fontAtlas.build();
        io.setFontDefault(font);
        //ImGui.setNextWindowPos(100, 100);
        ImGui.setNextWindowSize(100, 100);
        while(!glfwWindowShouldClose(glfwWindow)){
            glfwPollEvents();
            imGuiGlfw.newFrame();
            imGuiGl3.newFrame();
            ImGui.newFrame();

            ImGui.begin("hello world!");
            ImGui.getIO().setConfigWindowsMoveFromTitleBarOnly(false);

            textLink.setPosition(new ImVec2(400, 400));
            textLink.setText("HELLO WORLD");
            textLink.setColor(new ImVec4(50, 255, 50, 255));
            textLink.setFontSize(18);
            textLink.draw();



            checkbox.setPosition(new ImVec2(500, 400));
            checkbox.setSize(new ImVec2(20, 20));
            checkbox.setRounding(3);
            checkbox.setAnimationSpeed(10);
            checkbox.draw();

            switcher.setAnimationSpeed(10);
            switcher.setPosition(new ImVec2(100, 600));
            switcher.setEnabledColor(new ImVec4(255, 100, 10, 255));
            switcher.setThumbColor(new ImVec4(255, 255, 255, 255));
            switcher.setRounding(12);
            switcher.setThumbRadius(8);
            switcher.draw();

            button.setPosition(new ImVec2(50, 50));
            button.setSize(new ImVec2(150, 50));
            button.setBackgroundColor(new ImVec4(0, 255, 0, 255));
            button.setOutlineColor(new ImVec4(0, 0, 0, 255));
            button.setTextColor(new ImVec4(0, 0, 0, 255));
            button.setOutlineThickness(10);
            button.draw();

            slider.setSize(new ImVec2(500, 8));
            slider.setPosition(new ImVec2(200, 200));
            slider.setAnimationSpeed(7);
            slider.setHideThumb(false);
            slider.setThumbRadius(8);
            slider.setProgressColor(new ImVec4(45, 45, 45, 255));
            slider.setThumbColor(new ImVec4(255, 255, 255, 255));
            slider.setRounding(4);
            slider.draw();

            slider1.setSize(new ImVec2(200, 8));
            slider1.setPosition(new ImVec2(200, 400));
            slider1.setAnimationSpeed(13);
            slider1.setHideThumb(false);
            slider1.setThumbRadius(8);
            slider1.setProgressColor(new ImVec4(88, 45, 45, 255));
            slider1.setThumbColor(new ImVec4(255, 255, 255, 255));
            slider1.setRounding(4);
            slider1.draw();

            if(checkbox.isSelected()){
                slider.setAnimated(false);
                slider1.setAnimated(false);
                switcher.setAnimated(false);
            }
            else{
                slider.setAnimated(true);
                slider1.setAnimated(true);
                switcher.setAnimated(true);
            }

            textComponent.draw();
            /*
            float delta = ImGui.getIO().getDeltaTime();
            currentSize = ImLerp(currentSize, 800, delta);
            CursorPositionX = ImLerp(CursorPositionX, ImGui.getCursorScreenPosX(), delta);
            CursorPositionY = ImLerp(CursorPositionY, ImGui.getCursorScreenPosY(), delta);
            */
            ImGui.end();
            ImGui.render();
            glClearColor(0.5f, 0.5f, 0.5f, 1f);
            glClear(GL_COLOR_BUFFER_BIT);
            imGuiGl3.renderDrawData(ImGui.getDrawData());

            glfwSwapBuffers(glfwWindow);
        }

    }
}
