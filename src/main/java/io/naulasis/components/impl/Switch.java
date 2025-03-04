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

public class Switch extends Component {
    @Getter @Setter
    private float animationSpeed = 10, rounding = 12, thumbRadius = 8;

    @Getter @Setter
    private ImVec4 thumbColor = new ImVec4(255, 255, 255, 255), disabledColor = new ImVec4(45, 45, 45, 45), enabledColor = new ImVec4(255, 10, 10, 255);

    @Getter @Setter
    private ImVec2 position = new ImVec2(0,0), size = new ImVec2(55, 25);

    @Getter @Setter
    private boolean toggled, animated = true, clicked, hovered, pressed, released, destroyedValue, fadeInAnimation = true, fadeOutAnimation = true;

    @Getter
    private boolean destroyed;

    private boolean lastDestroyedState = false;
    private float targetPos;
    private float currentPos;
    private ImVec4 currentColor = disabledColor;
    private ImVec4 currentThumbColor = thumbColor;

    @Setter
    private Runnable onClick;

    private float prevWindowX = -1, prevWindowY = -1;

    public Switch(boolean enabled) {
        this.toggled = enabled;
    }

    @Override
    public void draw() {
        if (currentColor == null)
            currentColor = disabledColor;

        ImDrawList drawList = ImGui.getWindowDrawList();

        float windowX = ImGui.getWindowPosX();
        float windowY = ImGui.getWindowPosY() - ImGui.getScrollY();
        float deltaTime = ImGui.getIO().getDeltaTime();

        ImVec2 minPos = new ImVec2(windowX + position.x, windowY + position.y);
        ImVec2 maxPos = new ImVec2(windowX + position.x + size.x, windowY + position.y + size.y);

        targetPos = this.isToggled() ? maxPos.x - size.y / 2 : minPos.x + size.y / 2;
        if(destroyed)
            onDestroyed(minPos, maxPos, deltaTime);

        if(destroyed != lastDestroyedState){
            if(!fadeInAnimation) {
                currentPos = this.toggled ? maxPos.x - size.y / 2 : minPos.x + size.y / 2;
                currentColor = this.toggled ? enabledColor : disabledColor;
                currentThumbColor = thumbColor;
            }
        }

        accuratePositions(windowX);
        render(drawList, minPos, maxPos, windowY);

        lastDestroyedState = destroyed;
        if(!destroyed) {
            if (ImGui.isWindowFocused()) {
                updateColors(deltaTime);
                updatePosition(deltaTime);
            }

            handleLogic();
            handleInput(minPos, maxPos);
        }
    }

    private void accuratePositions(float windowX){
        if (prevWindowX != windowX) {
            currentPos = targetPos;
            prevWindowX = windowX;
        }
    }

    private void onDestroyed(ImVec2 minPos, ImVec2 maxPos, float deltaTime){
        if(fadeOutAnimation) {
            currentPos = ImLerp(currentPos, this.destroyedValue ? maxPos.x - size.y / 2 : minPos.x + size.y / 2, deltaTime * animationSpeed);
            currentColor = ImLerp(currentColor, this.destroyedValue ? new ImVec4(enabledColor.x, enabledColor.y, enabledColor.z, 0) : new ImVec4(disabledColor.x, disabledColor.y, disabledColor.z, 0), deltaTime * animationSpeed);
            currentThumbColor = ImLerp(currentThumbColor, new ImVec4(0, 0, 0, 0), deltaTime * animationSpeed);
        }
        else{
            currentPos = this.destroyedValue ? maxPos.x - size.y / 2 : minPos.x + size.y / 2;
            currentColor = this.destroyedValue ? new ImVec4(enabledColor.x, enabledColor.y, enabledColor.z, 0) : new ImVec4(disabledColor.x, disabledColor.y, disabledColor.z, 0);
            currentThumbColor = new ImVec4(0, 0, 0, 0);
        }
    }

    private void updatePosition(float deltaTime){
        if (animated)
            currentPos = ImLerp(currentPos, targetPos, deltaTime * animationSpeed);
        else
            currentPos = targetPos;
    }

    private void handleLogic(){
        if (clicked)
            this.toggled = !this.toggled;

        if (clicked && onClick != null)
            onClick.run();
    }

    private void handleInput(ImVec2 minPos, ImVec2 maxPos){
        hovered = ImGui.isMouseHoveringRect(minPos.x, minPos.y, maxPos.x, maxPos.y);
        clicked = ImGui.isMouseClicked(0) && ImGui.isMouseHoveringRect(minPos.x, minPos.y, maxPos.x, maxPos.y);
        pressed = ImGui.isMouseDown(0) && ImGui.isMouseHoveringRect(minPos.x, minPos.y, maxPos.x, maxPos.y);
        released = ImGui.isMouseReleased(0) && ImGui.isMouseHoveringRect(minPos.x, minPos.y, maxPos.x, maxPos.y);
    }

    private void updateColors(float deltaTime){
        if (animated) {
            currentThumbColor = ImLerp(currentThumbColor, thumbColor, deltaTime * (animationSpeed / 2));
            currentColor = ImLerp(currentColor, this.toggled ? new ImVec4(enabledColor.x, enabledColor.y, enabledColor.z, enabledColor.w) : new ImVec4(disabledColor.x, disabledColor.y, disabledColor.z, disabledColor.w), deltaTime * (animationSpeed / 2));
        } else {
            currentColor = this.toggled ? enabledColor : disabledColor;
            currentThumbColor = thumbColor;
        }
    }

    private void render(ImDrawList drawList, ImVec2 minPos, ImVec2 maxPos, float windowY){
        drawList.addRectFilled(
                minPos, maxPos,
                ColorConverter.colorToInt(currentColor.x, currentColor.y, currentColor.z, currentColor.w),
                rounding, ImDrawFlags.RoundCornersAll
        );

        drawList.addCircleFilled(
                currentPos, windowY + position.y + size.y / 2,
                thumbRadius, ColorConverter.colorToInt(currentThumbColor.x, currentThumbColor.y, currentThumbColor.z, currentThumbColor.w)
        );
    }

    @Override
    public void destroy() {
        destroyed = true;
    }
    public void build(){
        destroyed = false;
    }
}
