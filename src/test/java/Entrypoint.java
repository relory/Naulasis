import imgui.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import io.naulasis.Naulasis;
import io.naulasis.components.impl.*;
import io.naulasis.messageboxes.impl.WarningBox;
import io.naulasis.messageboxes.impl.YesNoBox;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import java.util.HashSet;
import java.util.Set;

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
        naulasis.init(glfwWindow);
        ImFont font = ImGui.getIO().getFonts().addFontFromFileTTF(System.getProperty("user.home") + "/.salorid/Fonts/InterVariable.ttf", 20.0f);
        ImGuiIO io = ImGui.getIO();
        ImFontAtlas fontAtlas = io.getFonts();
        fontAtlas.build();
        io.setFontDefault(font);
        ImGui.setNextWindowSize(100, 100);

        Slider slider = new Slider( 1, 100, 50, 1);
        Button button = new Button();
        TextInput textInput = new TextInput();
        Checkbox checkbox = new Checkbox();
        Switch switcher = new Switch(false);
        Child child = new Child();
        child.setPosition(new ImVec2(50, 50));
        child.setSize(new ImVec2(500, 300));
        child.setBackgroundColor(new ImVec4(0,0,0,255));

        YesNoBox yesNoBox = new YesNoBox();
        WarningBox warningBox = new WarningBox();

        KeyInput keyInput = new KeyInput();

        RangeSlider rangeSlider = new RangeSlider(1,100,25,75,10);
        Set<String> fruits = new HashSet<>();


        fruits.add("Basic");
        fruits.add("Advanced");
        fruits.add("Silent");

        Combobox combobox = new Combobox(fruits, "Orange");
        combobox.setBackgroundColor(new ImVec4(30,30,30,255));


        ColorPicker colorPicker = new ColorPicker();

        colorPicker.setPosition(new ImVec2(100, 50));

        while(!glfwWindowShouldClose(glfwWindow)){
            glfwPollEvents();
            imGuiGlfw.newFrame();
            imGuiGl3.newFrame();
            ImGui.newFrame();
            Naulasis.begin("hello world");

            rangeSlider.setPosition(new ImVec2(50, 50));
            rangeSlider.setDestroyedLowValue(slider.getMinimumValue());
            rangeSlider.setDestroyedHighValue(slider.getMaximumValue());
            rangeSlider.draw();

            combobox.setPosition(new ImVec2(100, 300));
            combobox.draw();

            System.out.println(ImGui.getIO().getFramerate());
            switcher.setPosition(new ImVec2(400, 280));
            switcher.setDestroyedValue(!switcher.isToggled());
            switcher.draw();

            keyInput.setSize(new ImVec2(50, 50));
            keyInput.setPosition(new ImVec2(50, 50));

            button.setPosition(new ImVec2(50, 280));
            button.setText("toggle Destroyed");
            button.setSize(new ImVec2(160, 50));
            button.setFontSize(20);
            button.draw();

            textInput.setPosition(new ImVec2(100, 400));
            textInput.draw();

            glfwSetCharCallback(Naulasis.getInstance().getWindow(), (windowHandle, codepoint)->{
                textInput.onKeyboardChar((char) codepoint);
            });
            glfwSetKeyCallback(Naulasis.getInstance().getWindow(), (windowHandle, key, scancode, action, mods) -> {
                if(action != GLFW_RELEASE) {
                    textInput.onKeyboardInt(key);
                }
            });
            if(button.isClicked()){
                if(textInput.isDestroyed()){
                    textInput.build();
                }
                else{
                    textInput.destroy();
                }

                if(switcher.isDestroyed()){
                    switcher.build();
                }
                else{
                    switcher.destroy();
                }
            }
            if(combobox.getSelectedItem().equals("Silent")){
                if(rangeSlider.isDestroyed()){
                    rangeSlider.build();
                }
            }
            else{
                if(!rangeSlider.isDestroyed())
                    rangeSlider.destroy();
            }

            colorPicker.draw();

            Naulasis.end();
            ImGui.render();
            glClearColor(0.5f, 0.5f, 0.5f, 1f);
            glClear(GL_COLOR_BUFFER_BIT);
            imGuiGl3.renderDrawData(ImGui.getDrawData());

            glfwSwapBuffers(glfwWindow);
        }

    }
}
