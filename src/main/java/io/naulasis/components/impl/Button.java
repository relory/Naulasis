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


public class Button extends Component {
    @Getter
    private long lastClickedTime = 0L;

    private ImVec4 currentBackgroundColor = new ImVec4(255, 255, 255, 50);

    @Getter @Setter
    private boolean clicked, hovered, pressed, released;

    @Getter
    private boolean destroyed;

    @Getter @Setter
    private ImVec2 position = new ImVec2(0,0), size = new ImVec2(75, 25);

    @Getter @Setter
    private ImVec4 outlineColor = new ImVec4(40, 40, 40, 255), backgroundColor = new ImVec4(30, 30, 30, 50), textColor = new ImVec4(90, 90, 90, 255);

    @Getter @Setter
    private float outlineThickness = 0.5f, rounding = 8, animationSpeed = 10, holdTime = 500;

    @Getter @Setter
    private int fontSize = 20;

    @Getter @Setter
    private ImVec4 clickedColor = new ImVec4(40, 40, 40, 255);

    @Getter @Setter
    private boolean animated = true;

    @Getter @Setter
    private String text = "";

    @Setter
    private Runnable onClick = null;

    @Override
    public void draw() {
        ImDrawList drawList = ImGui.getWindowDrawList();

        ImVec2 minPos = new ImVec2(ImGui.getWindowPosX() + position.x, ImGui.getWindowPosY() - ImGui.getScrollY() + position.y);
        ImVec2 maxPos = new ImVec2(ImGui.getWindowPosX() + position.x + size.x, ImGui.getWindowPosY() - ImGui.getScrollY() + position.y + size.y);

        render(drawList, minPos, maxPos);
        if(!destroyed) {
            handleInput(minPos, maxPos);
        }
        handleHoldTime();
    }

    private void render(ImDrawList drawList, ImVec2 minPos, ImVec2 maxPos){
        ImVec2 textSize = ImGui.getFont().calcTextSizeA(fontSize, Float.MAX_VALUE, 0,text);
        ImVec2 textPos = new ImVec2(minPos.x + (size.x - textSize.x) / 2, minPos.y + (size.y - textSize.y) / 2);

        drawList.addRectFilled(minPos, maxPos, ColorConverter.colorToInt(currentBackgroundColor.x, currentBackgroundColor.y, currentBackgroundColor.z, currentBackgroundColor.w), rounding, ImDrawFlags.RoundCornersAll);
        drawList.addText(ImGui.getFont(), fontSize, new ImVec2(textPos), ColorConverter.colorToInt(textColor.x, textColor.y, textColor.z, textColor.w), text);
        drawList.addRect(minPos, maxPos, ColorConverter.colorToInt(outlineColor.x, outlineColor.y, outlineColor.z, outlineColor.w), rounding, ImDrawFlags.RoundCornersAll, outlineThickness);
    }

    private void handleHoldTime(){
        lastClickedTime = clicked ? System.currentTimeMillis() : lastClickedTime;

        if(System.currentTimeMillis() - lastClickedTime > holdTime) {
            updateColors(backgroundColor);
        } else {
            updateColors(clickedColor);
        }
    }

    private void updateColors(ImVec4 targetbackgroundColor){
        if(animated) {
            currentBackgroundColor = ImGuiInternal.ImLerp(currentBackgroundColor, targetbackgroundColor, ImGui.getIO().getDeltaTime() * animationSpeed);
        } else {
            currentBackgroundColor = targetbackgroundColor;
        }
    }

    private void handleInput(ImVec2 minPos, ImVec2 maxPos){
        hovered = ImGui.isMouseHoveringRect(minPos.x, minPos.y, maxPos.x, maxPos.y);
        clicked = ImGui.isMouseClicked(0) && ImGui.isMouseHoveringRect(minPos.x, minPos.y, maxPos.x, maxPos.y);
        pressed = ImGui.isMouseDown(0) && ImGui.isMouseHoveringRect(minPos.x, minPos.y, maxPos.x, maxPos.y);
        released = ImGui.isMouseReleased(0) && ImGui.isMouseHoveringRect(minPos.x, minPos.y, maxPos.x, maxPos.y);

        if (clicked && onClick != null) {
            onClick.run();
        }
    }


    @Override
    public void build() {
        destroyed = false;
    }

    @Override
    public void destroy() {
        destroyed = true;
    }
}