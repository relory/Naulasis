package io.naulasis.components.impl;

import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import io.naulasis.components.Component;
import io.naulasis.utils.ColorConverter;
import lombok.RequiredArgsConstructor;

/**
 * TextComponent is a part of Naulasis
 * Which was created / maintained by
 *
 * @author MTR
 */

@RequiredArgsConstructor
public class TextComponent extends Component {

    private final String text;
    private final ImVec2 position;
    private final ImVec4 color;

    @Override
    public void draw() {
        ImDrawList drawList = ImGui.getWindowDrawList();
        ImVec2 finalPosition = new ImVec2(ImGui.getWindowPosX() + position.x, ImGui.getWindowPosY() + position.y);

        drawList.addText(ImGui.getFont(), 20, finalPosition, ColorConverter.colorToInt(color.x, color.y, color.z, color.w), text);
    }

    @Override
    public void destroy() {
    }
}
