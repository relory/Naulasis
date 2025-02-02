package io.naulasis.messageboxes.impl;

import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import io.naulasis.messageboxes.MessageBox;
import io.naulasis.utils.ColorConverter;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class YesNoBox extends MessageBox {
    private ImVec2 size = new ImVec2(100, 200);

    @Override
    public void show() {
        ImDrawList drawList = ImGui.getForegroundDrawList();

        ImVec2 minPos = new ImVec2();
        ImVec2 maxPos = new ImVec2();

        drawList.addRectFilled(minPos, maxPos, ColorConverter.colorToInt(30, 30, 30, 255));
    }

    @Override
    public void kill() {

    }
}
