package io.naulasis.components.impl;

import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
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
    private ImVec4 currentBackgroundColor = new ImVec4(255, 255, 255, 50);

    @Setter
    private ImVec2 position = new ImVec2(), size = new ImVec2();
    @Setter
    private ImVec4 outlineColor = new ImVec4(75, 75, 75, 255), backgroundColor = new ImVec4(255, 255, 255, 50), textColor = new ImVec4(150, 150, 150, 255);
    @Setter
    private float outlineThickness = 0.5f;
    @Setter
    private ImVec4 hoverColor = new ImVec4(0, 55, 0, 255);

    @Override
    public void draw() {
        if(position == null) position = new ImVec2(ImGui.getWindowPosX(), ImGui.getWindowPosY());
        if(size == null) size = new ImVec2(50, 20);

        ImVec2 minPos = new ImVec2(ImGui.getWindowPosX() + position.x, ImGui.getWindowPosY() + position.y);
        ImVec2 maxPos = new ImVec2(ImGui.getWindowPosX() + position.x + size.x, ImGui.getWindowPosY() + position.y + size.y);
        ImVec4 finalBackgroundColor = clicked ? hoverColor : backgroundColor;

        ImDrawList drawList = ImGui.getWindowDrawList();
        ImVec2 textSize = ImGui.calcTextSize("Button");
        ImVec2 textPos = new ImVec2(minPos.x + (size.x - textSize.x) / 2, minPos.y + (size.y - textSize.y) / 2);

        drawList.addRectFilled(minPos, maxPos, ColorConverter.colorToInt(currentBackgroundColor.x, currentBackgroundColor.y, currentBackgroundColor.z, currentBackgroundColor.w), 6f, ImDrawFlags.RoundCornersAll);
        drawList.addText(ImGui.getFont(), 20, new ImVec2(textPos), ColorConverter.colorToInt(textColor.x, textColor.y, textColor.z, textColor.w), "Button");
        drawList.addRect(minPos, maxPos, ColorConverter.colorToInt(outlineColor.x, outlineColor.y, outlineColor.z, outlineColor.w), 6f, ImDrawFlags.RoundCornersAll, outlineThickness);

        clicked = ImGui.isMouseHoveringRect(new ImVec2(minPos.x + 100, minPos.y + 100), new ImVec2(minPos.y + 225, maxPos.y + 130)) && ImGui.isMouseClicked(0);
        currentOpacity = ImGuiInternal.ImLerp(currentOpacity, targetOpacity, ImGui.getIO().getDeltaTime() * 10);
        targetOpacity = clicked ? 255 : 50;

        backgroundColor.set(finalBackgroundColor.x, finalBackgroundColor.y, finalBackgroundColor.z, targetOpacity);
        currentBackgroundColor = ImGuiInternal.ImLerp(currentBackgroundColor, backgroundColor, ImGui.getIO().getDeltaTime() * 10);
    }

    @Override
    public void destroy() {

    }
}