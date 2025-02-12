package io.naulasis;

import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImDrawFlags;
import imgui.flag.ImGuiWindowFlags;
import io.naulasis.utils.ColorConverter;

public class Naulasis {
    private static boolean allowDragging;
    private static ImVec2 firstPosition = null;
    private static Naulasis instance;

    // NOTE: Made this static because of JNI!
    private static long window;

    public void Init(long window){
        Naulasis.window = window;
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
    //, boolean resizable, boolean moveable, boolean closeable, boolean hideable
    public static void begin(String title){
        ImGui.begin(title, ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoMove);
        ImGui.getStyle().setWindowRounding(10);
        ImDrawList drawList = ImGui.getForegroundDrawList();
        drawList.addRectFilled(new ImVec2(ImGui.getWindowPosX(),ImGui.getWindowPosY()), new ImVec2(ImGui.getWindowPosX() + ImGui.getWindowSizeX(), ImGui.getWindowPosY() + 30), ColorConverter.colorToInt(30, 30, 30, 255), 10, ImDrawFlags.RoundCornersAll);
        if(ImGui.isMouseHoveringRect(new ImVec2(ImGui.getWindowPosX(), ImGui.getWindowPosY()), new ImVec2(ImGui.getWindowPosX() + ImGui.getWindowSizeX(), ImGui.getWindowPosY() + 30)) && ImGui.isMouseClicked(0)) {
            if (firstPosition == null) {
                firstPosition = ImGui.getMousePos();
            }
            allowDragging = true;
        }

        if(ImGui.isMouseReleased(0)) {
            firstPosition = null;
            allowDragging = false;
        }
        if(firstPosition != null)
            System.out.println("X: " + firstPosition.x + ", Y: " + firstPosition.y);

        if(false){
            ImVec2 newPosition = new ImVec2(firstPosition.x + ImGui.getMousePosX(), firstPosition.y + ImGui.getMousePosY());
            ImGui.setWindowPos(new ImVec2(ImGui.getWindowPosX() + newPosition.x / ImGui.getWindowSizeX(), ImGui.getWindowPosY() + newPosition.y /ImGui.getWindowSizeY()));
        }
    }

    public static void end(){
        ImGui.end();
    }

    public static float getWindowMousePosX(){
        return ImGui.getMousePosX() - ImGui.getWindowPosX();
    }

    public static float getWindowMousePosY(){
        return ImGui.getMousePosY() - ImGui.getWindowPosY();
    }

}
