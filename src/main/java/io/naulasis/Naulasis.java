package io.naulasis;

import io.naulasis.model.Component;
import io.naulasis.model.ComponentManager;

import static org.lwjgl.glfw.GLFW.*;

public class Naulasis {

    private static Naulasis instance;

    // NOTE: Made this static because of JNI!
    private static long window;

    public void Init(long window){
        Naulasis.window = window;

        glfwSetCharCallback(window, (windowHandle, codepoint) -> {
            for (Component component : ComponentManager.getInstance().getComponents()) {
                component.onKeyboardChar((char) codepoint);
            }
        });

        glfwSetKeyCallback(window, (windowHandle, key, scancode, action, mods) -> {
            if(action != GLFW_RELEASE) {
                for (Component component : ComponentManager.getInstance().getComponents()) {
                    component.onKeyboardInt(key);
                }
            }
        });
    }

    public long getWindow(){
        return window;
    }

    public static Naulasis getInstance() {
        if (instance == null) {
            instance = new Naulasis();
        }
        return instance;
    }
}
