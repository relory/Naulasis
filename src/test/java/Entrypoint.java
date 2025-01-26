
import imgui.*;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import io.naulasis.Naulasis;
import io.naulasis.components.Checkbox;
import io.naulasis.components.Slider;
import io.naulasis.components.TextInput;
import io.naulasis.utils.colorConverter;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import static io.naulasis.utils.ImGuiInternal.ImLerp;
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
        glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);
        glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, GLFW_TRUE);

        long glfwWindow = glfwCreateWindow(800, 550, "Salorid Loader", NULL, NULL);
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
        Checkbox checkbox = new Checkbox();
        TextInput textInput = new TextInput();
        Slider slider = new Slider("hello", 1, 100, 5, 1);
        ImFont font = ImGui.getIO().getFonts().addFontFromFileTTF(System.getProperty("user.home") + "/.salorid/Fonts/InterVariable.ttf", 20.0f);
        ImGuiIO io = ImGui.getIO();
        ImFontAtlas fontAtlas = io.getFonts();
        fontAtlas.build();
        io.setFontDefault(font);

        while(!glfwWindowShouldClose(glfwWindow)){
            glfwPollEvents();
            imGuiGlfw.newFrame();
            imGuiGl3.newFrame();
            ImGui.newFrame();

            ImGui.begin("hello world!", ImGuiWindowFlags.NoMove);
            ImGui.setWindowPos(currentPosX, currentPosY);
            ImGui.setWindowSize(currentSize, currentSize);
            /// Animation
            checkbox.Draw();
            textInput.Draw();
            slider.Draw();
            float delta = ImGui.getIO().getDeltaTime();
            currentSize = ImLerp(currentSize, 800, delta);
            CursorPositionX = ImLerp(CursorPositionX, ImGui.getCursorScreenPosX(), delta);
            CursorPositionY = ImLerp(CursorPositionY, ImGui.getCursorScreenPosY(), delta);
            ImDrawList drawlist = ImGui.getForegroundDrawList();
            drawlist.addRect(new ImVec2(0, 0), new ImVec2(100, 100), colorConverter.colorToInt(1,1,1,1));
            if(ImGui.isMouseHoveringRect(new ImVec2(0,0), new ImVec2(100,100)) && ImGui.isMouseClicked(0)){
                System.out.println("clicked!");
            }
            ImGui.end();
            ImGui.render();
            glClearColor(0.0f, 0.0f, 0.0f, 1f);
            glClear(GL_COLOR_BUFFER_BIT);
            imGuiGl3.renderDrawData(ImGui.getDrawData());

            glfwSwapBuffers(glfwWindow);
        }

    }
}
