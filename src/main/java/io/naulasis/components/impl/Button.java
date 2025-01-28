package io.naulasis.components.impl;

import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImDrawFlags;
import io.naulasis.components.Component;
import io.naulasis.utils.ColorConverter;
import io.naulasis.utils.ImGuiInternal;
import lombok.Getter;
import lombok.Setter;

/**
 * Button is a part of Naulasis
 * Which was created / maintained by
 *
 * @author MTR
 */

@Getter
public class Button extends Component {

    private boolean clicked = false;
    private float currentOpacity = 50, targetOpacity = 50;
    @Setter
    private ImVec2 position = new ImVec2(), size = new ImVec2();

    @Override
    public void draw() {
        if(position == null) position = ImGui.getCursorPos();
        if(size == null) size = new ImVec2(50, 20);

        ImVec2 minPos = new ImVec2(ImGui.getWindowPosX() + position.x, ImGui.getWindowPosY() + position.y);
        ImVec2 maxPos = new ImVec2(ImGui.getWindowPosX() + position.x + size.x, ImGui.getWindowPosY() + position.y + size.y);

        ImDrawList drawList = ImGui.getWindowDrawList();

        drawList.addRectFilled(minPos, maxPos, ColorConverter.colorToInt(255, 255, 255, 50), 6f, ImDrawFlags.RoundCornersAll);
        drawList.addText(ImGui.getFont(), 20, new ImVec2(137, 105), ColorConverter.colorToInt(150, 150, 150, 255), "Button");
        drawList.addRect(minPos, maxPos, ColorConverter.colorToInt(75, 75, 75, 255), 6f, ImDrawFlags.RoundCornersAll, 0.5f);

        clicked = ImGui.isMouseHoveringRect(new ImVec2(100, 100), new ImVec2(225, 130)) && ImGui.isMouseClicked(0);
        currentOpacity = ImGuiInternal.ImLerp(currentOpacity, targetOpacity, ImGui.getIO().getDeltaTime() * 10);
        targetOpacity = clicked ? 255 : 50;


    }

    @Override
    public void destroy() {

    }
}