package io.naulasis.model.impl;

import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImDrawFlags;
import io.naulasis.model.Component;
import io.naulasis.utils.ColorConverter;
import io.naulasis.utils.ImGuiInternal;
import lombok.Getter;

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

    @Override
    public void draw() {
        ImDrawList drawList = ImGui.getWindowDrawList();

        drawList.addRectFilled(new ImVec2(100, 100), new ImVec2(225, 130), ColorConverter.colorToInt(currentOpacity, currentOpacity, currentOpacity, currentOpacity), 6f, ImDrawFlags.RoundCornersAll);
        drawList.addText(ImGui.getFont(), 20, new ImVec2(137, 105), ColorConverter.colorToInt(150, 150, 150, 255), "Button");
        drawList.addRect(new ImVec2(100, 100), new ImVec2(225, 130), ColorConverter.colorToInt(75, 75, 75, 255), 6f, ImDrawFlags.RoundCornersAll, 0.5f);

        clicked = ImGui.isMouseHoveringRect(new ImVec2(100, 100), new ImVec2(225, 130)) && ImGui.isMouseClicked(0);
        currentOpacity = ImGuiInternal.ImLerp(currentOpacity, targetOpacity, ImGui.getIO().getDeltaTime() * 5);
        targetOpacity = clicked ? 255 : 50;
    }
}