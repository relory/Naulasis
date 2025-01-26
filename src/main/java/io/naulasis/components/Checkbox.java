package io.naulasis.components;

import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import io.naulasis.component;
import io.naulasis.utils.colorConverter;
import static io.naulasis.utils.ImGuiInternal.ImLerp;

public class Checkbox extends component {
    private boolean Enabled = false;
    float MiniRectRounding = 12;
    float MiniRectSize = 5;
    float MiniRectOpacity = 1;
    int height = 20;
    int weight = 20;
    public void Draw(){
        ImDrawList drawlist = ImGui.getWindowDrawList();
        int OutlineColor = colorConverter.colorToInt(50, 50, 50, 150);
        int color = colorConverter.colorToInt(25, 25, 25, 150);
        ImVec2 Position = new ImVec2(100 + weight, 275 - height);
        ImVec2 size = new ImVec2(Position.x + height, Position.y + weight);
        drawlist.addRectFilled(Position, size, color, 6, 240);
        drawlist.addRect(Position, size, OutlineColor, 6f, 240);
        if(ImGui.isMouseHoveringRect(Position, size) && ImGui.isMouseClicked(0)){
            Enabled =!Enabled;
        }

        float delta = ImGui.getIO().getDeltaTime();
        float TargetOpacity;
        float TargetSize;
        float TargetRounding;
        if(Enabled){
            TargetOpacity = 255;
            TargetSize = 0;
            TargetRounding = 4;
        }
        else{
            TargetOpacity = 0;
            TargetSize = 5;
            TargetRounding = 12;
        }
        ImVec2 MiniRectPosition = new ImVec2(Position.x + 3 + MiniRectSize, Position.y + 3 + MiniRectSize);
        ImVec2 MiniRectSizeVec = new ImVec2(Position.x + 17 - MiniRectSize, Position.y + 17 - MiniRectSize);
        MiniRectOpacity = ImLerp(MiniRectOpacity, TargetOpacity, delta * 7);
        MiniRectSize = ImLerp(MiniRectSize, TargetSize, delta * 7);
        MiniRectRounding = ImLerp(MiniRectRounding, TargetRounding, delta * 7);
        int MiniRectColor = colorConverter.colorToInt(255, 0, 255, MiniRectOpacity);
        drawlist.addRectFilled(MiniRectPosition, MiniRectSizeVec, MiniRectColor, MiniRectRounding, 240);
    }
}
