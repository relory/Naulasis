package io.naulasis.components.impl;

import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImDrawFlags;
import io.naulasis.Naulasis;
import io.naulasis.components.Component;
import io.naulasis.utils.ColorConverter;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import static io.naulasis.utils.ImGuiInternal.ImLerp;
public class TextInput extends Component {
    private static boolean SelectAll = false;
    private static boolean Selected = false;
    private static boolean Active = false;
    private static String text = "";
    private static float currentCursorX;
    private static float currentCursorOpacity = 255;
    private static boolean fadeIn = false;
    private static long LastKeyTypedTime;
    ArrayList<Character> sample = new ArrayList<>();

    @Override
    public void draw(){
        ImDrawList drawList = ImGui.getWindowDrawList();
        drawList.addRectFilled(new ImVec2(100,100), new ImVec2(225, 130), ColorConverter.colorToInt(25, 25, 25, 255), 6f, ImDrawFlags.RoundCornersAll);
        drawList.addText(ImGui.getFont(), 20, new ImVec2(100, 100), ColorConverter.colorToInt(150, 150, 150, 255), text);
        float targetCursorPosition = 100 + ImGui.getFont().calcTextSizeA(20, Float.MAX_VALUE, 0, text).x;
        float delta = ImGui.getIO().getDeltaTime();
        currentCursorX = ImLerp(currentCursorX, targetCursorPosition, delta * 10);
        if(System.currentTimeMillis() - LastKeyTypedTime > 500){
            if (fadeIn && currentCursorOpacity < 255) {
                currentCursorOpacity += 1f;
                if (currentCursorOpacity >= 255) {
                    currentCursorOpacity = 255;
                    fadeIn = false;
                }
            } else if (!fadeIn && currentCursorOpacity > 50) {
                currentCursorOpacity -= 1f;
                if (currentCursorOpacity <= 50) {
                    currentCursorOpacity = 50;
                    fadeIn = true;
                }
            }
        }
        else {
            currentCursorOpacity = 255f;
            fadeIn = false;
        }
        drawList.addRectFilled(new ImVec2(currentCursorX + 1,100), new ImVec2(currentCursorX + 3, 120), ColorConverter.colorToInt(150, 150, 150, currentCursorOpacity), 12f, ImDrawFlags.RoundCornersAll);
        if(ImGui.isMouseHoveringRect(new ImVec2(100, 100), new ImVec2(255, 130)) && ImGui.isMouseClicked(0)){
            Selected = true;
        }
        if(!ImGui.isMouseHoveringRect(new ImVec2(100, 100), new ImVec2(255, 130)) && ImGui.isMouseClicked(0)){
            Selected = false;
        }
        int leftCtrlState = GLFW.glfwGetKey(Naulasis.getInstance().getWindow(), GLFW.GLFW_KEY_LEFT_CONTROL);
        int rightCtrlState = GLFW.glfwGetKey(Naulasis.getInstance().getWindow(), GLFW.GLFW_KEY_RIGHT_CONTROL);
        if (leftCtrlState == GLFW.GLFW_PRESS || rightCtrlState == GLFW.GLFW_PRESS) {
            SelectAll = true;
        }
        else{
            SelectAll = false;
        }
    }


    @Override
    public void destroy() {

    }

    @Override
    public void onKeyboardChar(char key){
        if(!Selected) return;
        LastKeyTypedTime = System.currentTimeMillis();
        sample.add(key);
        text = getStringRepresentation(sample);
    }
    @Override
    public void onKeyboardInt(int key){
        if(!Selected) return;
        if(key == GLFW.GLFW_KEY_BACKSPACE && !sample.isEmpty()){
            LastKeyTypedTime = System.currentTimeMillis();
            currentCursorOpacity += 1;
            if(SelectAll){
                sample.clear();
            }
            else{
                sample.remove(sample.size() - 1);
            }
            text = getStringRepresentation(sample);
        }
    }
    private String getStringRepresentation(ArrayList<Character> list) {
        StringBuilder builder = new StringBuilder(list.size());
        for(Character ch: list) {
            builder.append(ch);
        }
        return builder.toString();
    }
}