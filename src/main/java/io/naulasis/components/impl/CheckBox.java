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

public class CheckBox extends Component {

    private float miniRectRounding = 12, miniRectSize = 5, miniRectOpacity = 1;

    @Getter @Setter
    private boolean selected, destroyed, animated = true, hovered, clicked, pressed, released;

    @Getter @Setter
    private float rounding = 6, animationSpeed = 10;

    @Getter @Setter
    private ImVec2 position = new ImVec2(0,0), size = new ImVec2(20, 20);

    @Getter @Setter
    private ImVec4 color = new ImVec4(255, 0, 255, 255), outlineColor = new ImVec4(50, 50, 50, 150);

    @Override
    public void draw() {
        ImDrawList drawList = ImGui.getWindowDrawList();

        int converterOutlineColor = ColorConverter.colorToInt(outlineColor.x, outlineColor.y, outlineColor.z, outlineColor.w);
        int backgroundColor = ColorConverter.colorToInt(25, 25, 25, 150);

        if (destroyed) return;
        ImVec2 minPos = new ImVec2(ImGui.getWindowPosX() + position.x, ImGui.getWindowPosY() - ImGui.getScrollY() + position.y);
        ImVec2 maxPos = new ImVec2(ImGui.getWindowPosX() + position.x + size.x, ImGui.getWindowPosY() - ImGui.getScrollY() + position.y + size.x);

        drawList.addRectFilled(minPos, maxPos, backgroundColor, rounding, 240);
        drawList.addRect(minPos, maxPos, converterOutlineColor, rounding, 240);

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

        ImVec2 miniRectMin = new ImVec2(ImGui.getWindowPosX() + position.x + miniRectSize, ImGui.getWindowPosY() - ImGui.getScrollY() + position.y + miniRectSize);
        ImVec2 miniRectMax = new ImVec2(ImGui.getWindowPosX() + position.x + size.x - miniRectSize, ImGui.getWindowPosY() - ImGui.getScrollY() + position.y + size.y - miniRectSize);

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

        hovered = ImGui.isMouseHoveringRect(minPos.x, minPos.y, maxPos.x, maxPos.y);
        clicked = ImGui.isMouseClicked(0) && ImGui.isMouseHoveringRect(minPos.x, minPos.y, maxPos.x, maxPos.y);
        pressed = ImGui.isMouseDown(0) && ImGui.isMouseHoveringRect(minPos.x, minPos.y, maxPos.x, maxPos.y);
        released = ImGui.isMouseReleased(0) && ImGui.isMouseHoveringRect(minPos.x, minPos.y, maxPos.x, maxPos.y);

        int MiniRectColor = ColorConverter.colorToInt(color.x, color.y, color.z, miniRectOpacity);
        drawList.addRectFilled(miniRectMin, miniRectMax, MiniRectColor, miniRectRounding, 240);
    }

    @Override
    public void build() {

    }

    @Override
    public void destroy() {

    }
}
