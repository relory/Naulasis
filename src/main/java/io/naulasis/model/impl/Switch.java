package io.naulasis.model.impl;

import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.ImDrawFlags;
import io.naulasis.model.Component;
import io.naulasis.utils.ColorConverter;

import static io.naulasis.utils.ImGuiInternal.ImLerp;

/**
 * Switch is a part of Naulasis
 * Which was created / maintained by
 *
 * @author MTR
 */
public class Switch extends Component {

    private float currentPos, currentOpacity = 255;
    private ImVec4 color = new ImVec4(80, 255, 80, 255);
    private boolean toggled;

    public Switch(boolean enabled) {
        this.toggled = enabled;
    }

    @Override
    public void draw() {
        ImVec2 position = new ImVec2(200, 100);
        ImDrawList windowDrawList = ImGui.getWindowDrawList();

        float windowX = ImGui.getWindowPosX();
        float windowY = ImGui.getWindowPosY();
        float deltaTime = ImGui.getIO().getDeltaTime();
        float animationSpeed = 7f;

        currentPos = ImLerp(currentPos, this.toggled ? windowX + position.x + 60 - 13 : windowX + position.x + 13, deltaTime * animationSpeed);
        currentOpacity = ImLerp(currentOpacity, this.toggled ? 255 : 200, deltaTime * animationSpeed);

        color = ImLerp(color,
                this.toggled ? new ImVec4(color.x, color.y, color.z, 200) :
                        new ImVec4(45F, 45F, 45F, 255), deltaTime * animationSpeed);

        windowDrawList.addRectFilled(windowX + position.x, windowY + position.y, windowX + position.x + 60, windowY + position.y + 26, ColorConverter.colorToInt(color.x, color.y, color.z, currentOpacity), 12, ImDrawFlags.RoundCornersAll);
        windowDrawList.addCircleFilled(currentPos, windowY + position.y + 13, 8, ColorConverter.colorToInt(255, 255, 255, currentOpacity));

        if (ImGui.isMouseClicked(0) && ImGui.isMouseHoveringRect(windowX + position.x, windowY + position.y, windowX + position.x + 60, windowY + position.y + 26)) {
            this.toggled = !this.toggled;
        }
    }
}
