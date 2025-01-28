package io.naulasis.components.impl;

import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.ImDrawFlags;
import io.naulasis.components.Component;
import io.naulasis.utils.ColorConverter;
import lombok.Getter;
import lombok.Setter;

import static io.naulasis.utils.ImGuiInternal.ImLerp;

/**
 * Switch is a part of Naulasis
 * Which was created / maintained by
 *
 * @author MTR
 */
@Getter @Setter
public class Switch extends Component {

    private float animationSpeed, currentPos, rounding = 12, thumbRadius = 8;
    private ImVec4 thumbColor = new ImVec4(255, 255, 255, 255), disabledColor = new ImVec4(45, 45, 45, 45), enabledColor = new ImVec4(255, 10, 10, 255);
    private ImVec4 currentColor;
    private ImVec2 position, size;
    private boolean toggled, animated;

    // Track the last window position;
    private float prevWindowX = -1, prevWindowY = -1;

    public Switch(boolean enabled) {
        this.toggled = enabled;
    }

    @Override
    public void draw() {
        if (currentColor == null) currentColor = disabledColor;
        ImVec2 position = new ImVec2(200, 100);
        ImDrawList windowDrawList = ImGui.getWindowDrawList();

        float windowX = ImGui.getWindowPosX();
        float windowY = ImGui.getWindowPosY();
        float deltaTime = ImGui.getIO().getDeltaTime();

        // Check if the current window is moving
        if (prevWindowX != windowX || prevWindowY != windowY) {
            currentPos = this.toggled ? windowX + position.x + 60 - 13 : windowX + position.x + 13;
            prevWindowX = windowX;
            prevWindowY = windowY;
        }

        if (animated && ImGui.isWindowFocused()) {
            currentPos = ImLerp(currentPos, this.toggled ? windowX + position.x + 60 - 13 : windowX + position.x + 13, deltaTime * animationSpeed);

            currentColor = ImLerp(currentColor,
                    this.toggled ? new ImVec4(enabledColor.x, enabledColor.y, enabledColor.z, enabledColor.w) :
                            new ImVec4(disabledColor.x, disabledColor.y, disabledColor.z, disabledColor.w), deltaTime * animationSpeed);
        } else {
            currentColor = this.toggled ? enabledColor : disabledColor;
        }

        windowDrawList.addRectFilled(
                windowX + position.x, windowY + position.y,
                windowX + position.x + 60, windowY + position.y + 26,
                ColorConverter.colorToInt(currentColor.x, currentColor.y, currentColor.z, currentColor.w),
                rounding, ImDrawFlags.RoundCornersAll
        );

        windowDrawList.addCircleFilled(
                currentPos, windowY + position.y + 13,
                thumbRadius, ColorConverter.colorToInt(thumbColor.x, thumbColor.y, thumbColor.z, thumbColor.w)
        );

        if (ImGui.isMouseClicked(0) && ImGui.isMouseHoveringRect(windowX + position.x, windowY + position.y, windowX + position.x + 60, windowY + position.y + 26)) {
            this.toggled = !this.toggled;
        }
    }

    @Override
    public void destroy() {
    }
}
