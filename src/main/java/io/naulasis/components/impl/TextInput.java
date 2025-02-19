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
import java.util.ArrayList;
import static io.naulasis.utils.ImGuiInternal.ImLerp;

public class TextInput extends Component {
    private boolean selected, active, selectAll;

    @Getter @Setter
    private ImVec2 position = new ImVec2(0, 0), size = new ImVec2(100, 30);

    @Getter @Setter
    private String text = "", destroyedText;

    @Getter @Setter
    private float animationSpeed = 10f, rounding = 6f, outlineThickness = 0.5f;

    @Getter @Setter
    private int fontSize = 20;

    @Getter @Setter
    private boolean animated = true, outline = true, destroyed, hovered, clicked, pressed, released;

    @Getter @Setter
    private int backspaceKey = 259;

    @Getter @Setter
    private ImVec4 textColor = new ImVec4(150, 150, 150, 255), outlineColor = new ImVec4(35, 35, 35, 255), backgroundColor = new ImVec4(30,30,30,255), cursorColor = new ImVec4(150, 150, 150, 255);

    private boolean lastDestroyedState = true;
    private float currentCursorX, currentCursorOpacity = 255f;
    private boolean fadeIn = false;
    private long LastKeyTypedTime;
    ArrayList<Character> sample = new ArrayList<>();

    @Override
    public void draw(){
        ImDrawList drawList = ImGui.getWindowDrawList();

        ImVec2 minPos = new ImVec2(ImGui.getWindowPosX() + position.x, ImGui.getWindowPosY() - ImGui.getScrollY() + position.y);
        ImVec2 maxPos = new ImVec2(ImGui.getWindowPosX() + position.x + size.x, ImGui.getWindowPosY() - ImGui.getScrollY() + position.y + size.y);
        ImVec2 textSize = ImGui.getFont().calcTextSizeA(fontSize, Float.MAX_VALUE, 0, text);
        ImVec2 textPos = new ImVec2(minPos.x + ImGui.calcTextSize(" ").x, minPos.y + (size.y - textSize.y) / 2);

        drawList.addRectFilled(minPos, maxPos, ColorConverter.colorToInt(backgroundColor.x, backgroundColor.y, backgroundColor.z, backgroundColor.w), rounding, ImDrawFlags.RoundCornersAll);
        if(outline)
            drawList.addRect(minPos, maxPos, ColorConverter.colorToInt(outlineColor.x, outlineColor.y, outlineColor.z, outlineColor.w), rounding, ImDrawFlags.RoundCornersAll, outlineThickness);

        drawList.addText(ImGui.getFont(), fontSize, textPos, ColorConverter.colorToInt(textColor.x, textColor.y, textColor.z, textColor.w), text);

        float targetCursorPosition;
        float delta = ImGui.getIO().getDeltaTime();
        if (System.currentTimeMillis() - LastKeyTypedTime > 500) {
            active = false;
            if (fadeIn && currentCursorOpacity < 255) {
                currentCursorOpacity += 2f;
                if (currentCursorOpacity >= 255) {
                    currentCursorOpacity = 255;
                    fadeIn = false;
                }
            } else if (!fadeIn && currentCursorOpacity > 0) {
                currentCursorOpacity -= 2f;
                if (currentCursorOpacity <= 0) {
                    currentCursorOpacity = 0;
                    fadeIn = true;
                }
            }
        } else {
            active = true;
            currentCursorOpacity = 255f;
            fadeIn = false;
        }

        targetCursorPosition = minPos.x + ImGui.getFont().calcTextSizeA(fontSize, Float.MAX_VALUE, 0, text).x;
        if(destroyed) {
            targetCursorPosition = minPos.x;
            fadeIn = false;
            if (currentCursorOpacity > 0) {
                currentCursorOpacity -= 2;
            }
        }

        if(lastDestroyedState != destroyed){
            targetCursorPosition = minPos.x + ImGui.getFont().calcTextSizeA(fontSize, Float.MAX_VALUE, 0, text).x;
            currentCursorOpacity = 0;
            fadeIn = true;
        }

        if(animated)
            currentCursorX = ImLerp(currentCursorX, targetCursorPosition, delta * animationSpeed);
        else
            currentCursorX = targetCursorPosition;

        if(!text.isEmpty() || selected) {
            drawList.addRectFilled(new ImVec2(currentCursorX + 6, minPos.y + size.y / 8), new ImVec2(currentCursorX + 8, maxPos.y - size.y / 8), ColorConverter.colorToInt(cursorColor.x, cursorColor.y, cursorColor.z, currentCursorOpacity), 12f, ImDrawFlags.RoundCornersAll);
        }

        System.out.println("current destroyed status: " + destroyed + ", last destroyed status: " + lastDestroyedState);

        lastDestroyedState = destroyed;

        if(destroyed){
            return;
        }

        if(ImGui.isMouseHoveringRect(minPos, maxPos) && ImGui.isMouseClicked(0)){
            selected = true;
        }
        if(!ImGui.isMouseHoveringRect(minPos, maxPos) && ImGui.isMouseClicked(0)){
            selected = false;
        }

        hovered = ImGui.isMouseHoveringRect(minPos.x, minPos.y, maxPos.x, maxPos.y);
        clicked = ImGui.isMouseClicked(0) && ImGui.isMouseHoveringRect(minPos.x, minPos.y, maxPos.x, maxPos.y);
        pressed = ImGui.isMouseDown(0) && ImGui.isMouseHoveringRect(minPos.x, minPos.y, maxPos.x, maxPos.y);
        released = ImGui.isMouseReleased(0) && ImGui.isMouseHoveringRect(minPos.x, minPos.y, maxPos.x, maxPos.y);
    }

    @Override
    public void build() {
        destroyed = false;
    }

    @Override
    public void destroy() {
        destroyed = true;
    }

    public void onKeyboardChar(char key){
        if(!selected) return;
        LastKeyTypedTime = System.currentTimeMillis();
        sample.add(key);
        text = getStringRepresentation(sample);
    }

    public void onKeyboardInt(int key){
        if(!selected) return;
        if(key == backspaceKey && !sample.isEmpty()){
            LastKeyTypedTime = System.currentTimeMillis();
            currentCursorOpacity += 1;
            if(selectAll){
                sample.clear();
            }
            else{
                sample.remove(sample.size() - 1);
            }
            text = getStringRepresentation(sample);
        }
    }

    private String getStringRepresentation(ArrayList<Character> list) {
        StringBuilder builder = new StringBuilder(list.size());
        for(Character ch: list) {
            builder.append(ch);
        }
        return builder.toString();
    }
}