package io.naulasis.utils;

import imgui.ImGui;

public class MathUtils {
    public static float getWindowMousePosX(){
        return ImGui.getMousePosX() - ImGui.getWindowPosX();
    }
    public static float getWindowMousePosY(){
        return ImGui.getMousePosY() - ImGui.getWindowPosY();
    }
}
