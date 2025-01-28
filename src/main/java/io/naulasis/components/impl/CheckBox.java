package io.naulasis.components.impl;

import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import io.naulasis.components.Component;
import io.naulasis.utils.ColorConverter;
import lombok.Getter;
import lombok.Setter;

import static io.naulasis.utils.ImGuiInternal.ImLerp;
@Getter @Setter
public class CheckBox extends Component {

    private boolean selected = false;
    private float rounding = 0, miniRectRounding = 12, miniRectSize = 5, miniRectOpacity = 1, animationSpeed = 7;
    private ImVec2 position, size;


    @Override
    public void draw() {
        if(position == null) position = ImGui.getCursorPos();
        if(size == null) size = new ImVec2(20, 20);
        ImDrawList drawList = ImGui.getWindowDrawList();

        int outlineColor = ColorConverter.colorToInt(50, 50, 50, 150);
        int color = ColorConverter.colorToInt(25, 25, 25, 150);

        ImVec2 minPos = new ImVec2(ImGui.getWindowPosX() + position.x, ImGui.getWindowPosY() + position.y);
        ImVec2 maxPos = new ImVec2(ImGui.getWindowPosX() + position.x + size.x, ImGui.getWindowPosY() + position.y + size.x);

        drawList.addRectFilled(minPos, maxPos, color, rounding, 240);
        drawList.addRect(minPos, maxPos, outlineColor, rounding, 240);

        if(ImGui.isMouseHoveringRect(minPos, maxPos) && ImGui.isMouseClicked(0)){
            selected =! selected;
        }

        float delta = ImGui.getIO().getDeltaTime();
        float targetOpacity;
        float targetSize;
        float targetRounding;

        if(selected) {
            targetOpacity = 255;
            targetSize = size.x / 8;
            targetRounding = 2;
        } else {
            targetOpacity = 0;
            targetSize = size.x / 2;
            targetRounding = rounding * 2;
        }

        ImVec2 miniRectMin = new ImVec2(ImGui.getWindowPosX() + position.x + miniRectSize, ImGui.getWindowPosY() + position.y + miniRectSize);
        ImVec2 miniRectMax = new ImVec2(ImGui.getWindowPosX() + position.x + size.x - miniRectSize, ImGui.getWindowPosY() + position.y + size.y - miniRectSize);

        miniRectOpacity = ImLerp(miniRectOpacity, targetOpacity, delta * animationSpeed);
        miniRectSize = ImLerp(miniRectSize, targetSize, delta * animationSpeed);
        miniRectRounding = ImLerp(miniRectRounding, targetRounding, delta * animationSpeed);
        int MiniRectColor = ColorConverter.colorToInt(255, 0, 255, miniRectOpacity);
        drawList.addRectFilled(miniRectMin, miniRectMax, MiniRectColor, miniRectRounding, 240);
    }

    @Override
    public void destroy() {

    }
}
