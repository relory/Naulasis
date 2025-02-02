package io.naulasis.components.impl;

import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import io.naulasis.components.Component;
import io.naulasis.utils.ColorConverter;
import lombok.Getter;
import lombok.Setter;
import static io.naulasis.utils.ImGuiInternal.ImLerp;

@Getter @Setter
public class CheckBox extends Component {

    private boolean selected, animated = true;
    private float rounding = 6, miniRectRounding = 12, miniRectSize = 5, miniRectOpacity = 1, animationSpeed = 10;
    private ImVec2 position, size;
    private ImVec4 color = new ImVec4(255, 0, 255, 255);

    @Override
    public void draw() {
        if(position == null) position = ImGui.getCursorPos();
        if(size == null) size = new ImVec2(20, 20);
        ImDrawList drawList = ImGui.getWindowDrawList();

        int outlineColor = ColorConverter.colorToInt(50, 50, 50, 150);
        int backgroundColor = ColorConverter.colorToInt(25, 25, 25, 150);

        ImVec2 minPos = new ImVec2(ImGui.getWindowPosX() + position.x, ImGui.getWindowPosY() + position.y);
        ImVec2 maxPos = new ImVec2(ImGui.getWindowPosX() + position.x + size.x, ImGui.getWindowPosY() + position.y + size.x);

        drawList.addRectFilled(minPos, maxPos, backgroundColor, rounding, 240);
        drawList.addRect(minPos, maxPos, outlineColor, rounding, 240);

        if(ImGui.isMouseHoveringRect(minPos, maxPos) && ImGui.isMouseClicked(0)){
            selected =! selected;
        }

        float delta = ImGui.getIO().getDeltaTime();
        float targetOpacity;
        float targetSize;
        float targetRounding;

        if(selected) {
            targetOpacity = color.w;
            targetSize = size.x / 8;
            targetRounding = rounding;
        } else {
            targetOpacity = 0;
            targetSize = size.x / 2;
            targetRounding = 2;
        }

        ImVec2 miniRectMin = new ImVec2(ImGui.getWindowPosX() + position.x + miniRectSize, ImGui.getWindowPosY() + position.y + miniRectSize);
        ImVec2 miniRectMax = new ImVec2(ImGui.getWindowPosX() + position.x + size.x - miniRectSize, ImGui.getWindowPosY() + position.y + size.y - miniRectSize);
        if(animated) {
            miniRectOpacity = ImLerp(miniRectOpacity, targetOpacity, delta * animationSpeed);
            miniRectSize = ImLerp(miniRectSize, targetSize, delta * animationSpeed);
            miniRectRounding = ImLerp(miniRectRounding, targetRounding, delta * animationSpeed);
        }
        else{
            miniRectOpacity = targetOpacity;
            miniRectSize = targetSize;
            miniRectRounding = targetRounding;
        }
        int MiniRectColor = ColorConverter.colorToInt(color.x, color.y, color.z, miniRectOpacity);
        drawList.addRectFilled(miniRectMin, miniRectMax, MiniRectColor, miniRectRounding, 240);
    }

    @Override
    public void destroy() {

    }
}
