package io.naulasis.components.impl;

import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImDrawFlags;
import io.naulasis.components.Component;
import io.naulasis.utils.ColorConverter;
import lombok.Getter;
import lombok.Setter;

public class KeyInput extends Component {
    @Getter @Setter
    private ImVec2 position = new ImVec2(0,0);

    @Getter @Setter
    private ImVec2 size = new ImVec2(0,0);

    @Getter @Setter
    private int keyChar = 'F';

    @Getter @Setter
    private boolean selected, destroyed;

    public KeyInput() {

    }

    @Override
    public void draw() {
        ImDrawList drawList = ImGui.getWindowDrawList();

        ImVec2 charSize = ImGui.calcTextSize(String.valueOf(keyChar));

        float positionX = position.x + ImGui.getWindowPosX() - ImGui.getScrollX();
        float positionY = position.y + ImGui.getWindowPosY() - ImGui.getScrollY();

        ImVec2 minPos = new ImVec2(positionX - charSize.x / 2, positionY - charSize.y / 2);
        ImVec2 maxPos = new ImVec2((positionX + size.x) + charSize.x / 2, (positionY + size.y) + charSize.y / 2);

        drawList.addRectFilled(minPos, maxPos, ColorConverter.colorToInt(30, 30, 30, 255), 10, ImDrawFlags.RoundCornersAll);
        ImVec2 textPos = new ImVec2(minPos.x + (size.x - charSize.x) / 2, minPos.y + (size.y - charSize.y) / 2);
        drawList.addText(ImGui.getFont(), 20, textPos, ColorConverter.colorToInt(255, 255, 255, 255), String.valueOf(keyChar));

        if(destroyed) return;

        if (ImGui.isMouseHoveringRect(minPos, maxPos) && ImGui.isMouseClicked(0)) {
            selected = true;
        }
        if (!ImGui.isMouseHoveringRect(minPos, maxPos) && ImGui.isMouseClicked(0)) {
            selected = false;
        }
    }

    public void onKeyboardChar(char key) {
        if(!selected || destroyed) return;
        keyChar = key;
    }


    @Override
    public void destroy() {
        destroyed = true;
    }

    @Override
    public void build(){
        destroyed = false;
    }
}
