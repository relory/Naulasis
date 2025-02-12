import imgui.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import io.naulasis.Naulasis;
import io.naulasis.components.impl.*;
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
        ImFont font = ImGui.getIO().getFonts().addFontFromFileTTF(System.getProperty("user.home") + "/.salorid/Fonts/InterVariable.ttf", 20.0f);
        ImGuiIO io = ImGui.getIO();
        ImFontAtlas fontAtlas = io.getFonts();
        fontAtlas.build();
        io.setFontDefault(font);
        ImGui.setNextWindowSize(100, 100);

        Slider slider = new Slider( 1, 100, 50, 1);
        Button button = new Button();
        TextInput textInput = new TextInput();
        CheckBox checkbox = new CheckBox();
        Switch switcher = new Switch(false);
        Child child = new Child();
        child.setPosition(new ImVec2(50, 50));
        child.setSize(new ImVec2(500, 300));
        child.setBackgroundColor(new ImVec4(0,0,0,255));
        while(!glfwWindowShouldClose(glfwWindow)){
            glfwPollEvents();
            imGuiGlfw.newFrame();
            imGuiGl3.newFrame();
            ImGui.newFrame();
            Naulasis.begin("hello world");
            child.begin("showcase");
            int posY = 50;
            slider.setPosition(new ImVec2(100, posY));
            slider.draw();
            posY += 100;

            button.setPosition(new ImVec2(100, posY));
            button.setSize(new ImVec2(125, 35));
            button.setText("destroy Slider");
            if(button.isClicked()){
                if(slider.isDestroyed()){
                    slider.build();
                }
                else{
                    slider.destroy();
                }
            }
            if(button.isReleased()){
                System.out.println("released");
            }

            slider.setAnimationSpeed(5);

            slider.setThumbColor(slider.isSelected() ? new ImVec4(255, 20, 20, 255) : new ImVec4(255, 255, 255, 255));


            button.setHoldTime(250);
            button.setAnimated(false);
            button.draw();
            posY += 100;

            textInput.setPosition(new ImVec2(100, posY));
            textInput.setSize(new ImVec2(300, 30));
            textInput.draw();
            posY += 100;

            switcher.setPosition(new ImVec2(100, posY));
            switcher.draw();
            switcher.setAnimated(true);

            posY += 100;

            checkbox.setPosition(new ImVec2(100, posY));
            checkbox.setColor(new ImVec4(255, 25, 120, 255));
            checkbox.setPosition(new ImVec2(100, posY));
            checkbox.draw();
            posY += 100;

            ImGui.setCursorPosY(posY);
            child.end();
            Naulasis.end();
            ImGui.render();
            glClearColor(0.5f, 0.5f, 0.5f, 1f);
            glClear(GL_COLOR_BUFFER_BIT);
            imGuiGl3.renderDrawData(ImGui.getDrawData());

            glfwSwapBuffers(glfwWindow);
        }

    }
}
