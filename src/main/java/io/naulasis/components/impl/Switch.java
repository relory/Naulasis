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

@Getter @Setter
public class Switch extends Component {

    private float animationSpeed = 10, currentPos, rounding = 12, thumbRadius = 8;
    private ImVec4 thumbColor = new ImVec4(255, 255, 255, 255), disabledColor = new ImVec4(45, 45, 45, 45), enabledColor = new ImVec4(255, 10, 10, 255);
    private ImVec4 currentColor;
    private ImVec2 position = new ImVec2(0,0), size = new ImVec2(55, 25);
    private boolean toggled, animated = true, clicked, hovered, pressed, released;

    // Track the last window position;
    private float prevWindowX = -1, prevWindowY = -1;

    public Switch(boolean enabled) {
        this.toggled = enabled;
    }

    @Override
    public void draw() {
        if (currentColor == null) currentColor = disabledColor;
        ImDrawList windowDrawList = ImGui.getWindowDrawList();

        float windowX = ImGui.getWindowPosX();
        float windowY = ImGui.getWindowPosY() - ImGui.getScrollY();
        float deltaTime = ImGui.getIO().getDeltaTime();
        ImVec2 minPos = new ImVec2(windowX + position.x, windowY + position.y);
        ImVec2 maxPos = new ImVec2(windowX + position.x + size.x, windowY + position.y + size.y);
        // Check if the current window is moving
        if (prevWindowX != windowX) {
            currentPos = this.toggled ? maxPos.x - size.y / 2 : minPos.x + size.y / 2;
            prevWindowX = windowX;
        }

        if (animated && ImGui.isWindowFocused()) {
            currentPos = ImLerp(currentPos, this.toggled ? maxPos.x - size.y / 2 : minPos.x + size.y / 2, deltaTime * animationSpeed);

            currentColor = ImLerp(currentColor,
                    this.toggled ? new ImVec4(enabledColor.x, enabledColor.y, enabledColor.z, enabledColor.w) :
                            new ImVec4(disabledColor.x, disabledColor.y, disabledColor.z, disabledColor.w), deltaTime * animationSpeed);
        } else {
            currentPos = this.toggled ? maxPos.x - size.y / 2 : minPos.x + size.y / 2;
            currentColor = this.toggled ? enabledColor : disabledColor;
        }

        windowDrawList.addRectFilled(
                minPos, maxPos,
                ColorConverter.colorToInt(currentColor.x, currentColor.y, currentColor.z, currentColor.w),
                rounding, ImDrawFlags.RoundCornersAll
        );

        windowDrawList.addCircleFilled(
                currentPos, windowY + position.y + size.y / 2,
                thumbRadius, ColorConverter.colorToInt(thumbColor.x, thumbColor.y, thumbColor.z, thumbColor.w)
        );

        if (ImGui.isMouseClicked(0) && ImGui.isMouseHoveringRect(minPos.x, minPos.y, maxPos.x, maxPos.y)) {
            this.toggled = !this.toggled;
        }

        hovered = ImGui.isMouseHoveringRect(minPos.x, minPos.y, maxPos.x, maxPos.y);
        clicked = ImGui.isMouseClicked(0) && ImGui.isMouseHoveringRect(minPos.x, minPos.y, maxPos.x, maxPos.y);
        pressed = ImGui.isMouseDown(0) && ImGui.isMouseHoveringRect(minPos.x, minPos.y, maxPos.x, maxPos.y);
        released = ImGui.isMouseReleased(0) && ImGui.isMouseHoveringRect(minPos.x, minPos.y, maxPos.x, maxPos.y);
    }

    @Override
    public void destroy() {
    }
}
