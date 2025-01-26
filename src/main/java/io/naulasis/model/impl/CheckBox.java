package io.naulasis.model.impl;

import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import io.naulasis.model.Component;
import io.naulasis.utils.ColorConverter;

import static io.naulasis.utils.ImGuiInternal.ImLerp;

public class CheckBox extends Component {

    private boolean enabled = false;
    private float miniRectRounding = 12, miniRectSize = 5, miniRectOpacity = 1;
    private int height = 20, weight = 20;

    @Override
    public void draw() {
        ImDrawList drawList = ImGui.getWindowDrawList();

        int outlineColor = ColorConverter.colorToInt(50, 50, 50, 150);
        int color = ColorConverter.colorToInt(25, 25, 25, 150);

        ImVec2 position = new ImVec2(100 + weight, 275 - height);
        ImVec2 size = new ImVec2(position.x + height, position.y + weight);

        drawList.addRectFilled(position, size, color, 6, 240);
        drawList.addRect(position, size, outlineColor, 6f, 240);

        if(ImGui.isMouseHoveringRect(position, size) && ImGui.isMouseClicked(0)){
            enabled =! enabled;
        }

        float delta = ImGui.getIO().getDeltaTime();
        float targetOpacity;
        float targetSize;
        float targetRounding;

        if(enabled) {
            targetOpacity = 255;
            targetSize = 0;
            targetRounding = 4;
        } else {
            targetOpacity = 0;
            targetSize = 5;
            targetRounding = 12;
        }

        ImVec2 miniRectPosition = new ImVec2(position.x + 3 + miniRectSize, position.y + 3 + miniRectSize);
        ImVec2 miniRectSizeVec = new ImVec2(position.x + 17 - miniRectSize, position.y + 17 - miniRectSize);

        miniRectOpacity = ImLerp(miniRectOpacity, targetOpacity, delta * 7);
        miniRectSize = ImLerp(miniRectSize, targetSize, delta * 7);
        miniRectRounding = ImLerp(miniRectRounding, targetRounding, delta * 7);

        int MiniRectColor = ColorConverter.colorToInt(255, 0, 255, miniRectOpacity);
        drawList.addRectFilled(miniRectPosition, miniRectSizeVec, MiniRectColor, miniRectRounding, 240);
    }
}
