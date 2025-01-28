package io.naulasis.components.impl;

import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import io.naulasis.components.Component;
import io.naulasis.utils.ColorConverter;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TextLink extends Component {
    private String text = "";
    private int fontSize = 10;
    private ImVec2 position = new ImVec2(0,0);
    private ImVec4 hoveredColor, clickedColor, color = new ImVec4(50, 255, 50, 255);

    @Override
    public void draw() {
        ImDrawList drawList = ImGui.getWindowDrawList();
        drawList.addText(ImGui.getFont(), fontSize, position, ColorConverter.colorToInt(color.x, color.y, color.z, color.w), text);
        ImVec2 textSize = ImGui.getFont().calcTextSizeA(fontSize, Float.MAX_VALUE, 0, text);
        if(ImGui.isMouseHoveringRect(position, new ImVec2(position.x + textSize.x, position.y + textSize.y)) && ImGui.isMouseClicked(0)){
            System.out.println("hello world");
        }
    }

    @Override
    public void destroy() {

    }
}
