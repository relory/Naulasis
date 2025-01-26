package io.naulasis;

import lombok.Getter;
import static org.lwjgl.glfw.GLFW.*;

public class Naulasis {
    private static Naulasis instance;

    private float scale;
    @Getter
    private static long Window;
    public void Init(long window){
        this.Window = window;
        glfwSetCharCallback(window, (windowHandle, codepoint) -> {
            for (io.naulasis.component component : componentsManager.getInstance().getComponents()) {
                component.onKeyboardChar((char) codepoint);
            }
        });
        glfwSetKeyCallback(window, (windowHandle, key, scancode, action, mods) -> {
            if(action != GLFW_RELEASE) {
                for (io.naulasis.component component : componentsManager.getInstance().getComponents()) {
                    component.onKeyboardInt(key);
                }
            }
        });
    }
    public static void PushScale(){

    }
    public long getWindow(){
        return this.Window;
    }
    public static Naulasis getInstance() {
        if (instance == null) {
            instance = new Naulasis();
        }
        return instance;
    }
}
