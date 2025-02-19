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
import java.util.Set;
import static io.naulasis.utils.ImGuiInternal.ImLerp;
import static io.naulasis.utils.ImGuiInternal.ImRotate;

public class Combobox extends Component {
    @Getter @Setter
    private ImVec2 position = new ImVec2(0,0), size = new ImVec2(400, 40);

    @Getter @Setter
    private ImVec4 backgroundColor = new ImVec4(30,30,30,255), borderColor = new ImVec4(40,40,40,255), textColor = new ImVec4(200,200,200,255);

    @Getter @Setter
    private Set<String> items;

    @Getter @Setter
    private String selectedItem;

    @Getter @Setter
    private boolean open, border;

    private float arrowRotation = 0, currentListSize = 0, listSize, lineOpacity, rounding = 6;

    public Combobox(Set<String> items, String selectedDefault){
        this.items = items;
        this.selectedItem = selectedDefault;
    }

    @Override
    public void draw() {
        ImDrawList drawList = ImGui.getForegroundDrawList();
        currentListSize = ImLerp(currentListSize, listSize, ImGui.getIO().getDeltaTime() * 7);
        if(open){
            listSize = items.size() * size.y;
        }
        else{
            listSize = 0;
        }

        ImVec2 minPos = new ImVec2(ImGui.getWindowPosX() + position.x, ImGui.getWindowPosY() + position.y - ImGui.getScrollY());
        ImVec2 maxPos = new ImVec2(ImGui.getWindowPosX() + position.x + size.x, ImGui.getWindowPosY() + position.y - ImGui.getScrollY() + size.y + currentListSize);
        ImVec2 textSize = ImGui.calcTextSize(selectedItem);
        float textY = minPos.y + (size.y - textSize.y) / 2;
        textY = Math.max(textY, minPos.y);
        ImVec2 textPos = new ImVec2(size.y / 2, textY);

        drawList.addRectFilled(minPos, maxPos, ColorConverter.colorToInt(backgroundColor.x, backgroundColor.y, backgroundColor.z, backgroundColor.w), rounding, ImDrawFlags.RoundCornersAll);

        if(!open) {
            drawList.addText(minPos.x + size.y / 2, textPos.y, ColorConverter.colorToInt(textColor.x, textColor.y, textColor.z, textColor.w), selectedItem);
        }
        if(open) {
            float currentMinus = listSize - currentListSize;
            float currentY = size.y;
            for (String item : items) {
                boolean hovered = ImGui.isMouseHoveringRect(minPos.x, minPos.y + currentY - currentMinus, maxPos.x, minPos.y + currentY + size.y - currentMinus);
                ImVec2 itemTextSize = ImGui.calcTextSize(item);
                float itemTextY = minPos.y + currentY - currentMinus + (size.y - itemTextSize.y) / 2;
                itemTextY = Math.max(itemTextY, minPos.y);
                ImVec2 itemTextPos = new ImVec2(minPos.x + (size.x - itemTextSize.x) / 2, itemTextY);
                if(hovered && minPos.y + currentY - currentMinus >= minPos.y){
                    drawList.addRectFilled(minPos.x, minPos.y + currentY - currentMinus, maxPos.x, minPos.y + currentY + size.y - currentMinus, ColorConverter.colorToInt(backgroundColor.x * 1.5,backgroundColor.y * 1.5,backgroundColor.z * 1.5,backgroundColor.w * 1.5), rounding, ImDrawFlags.RoundCornersAll);
                }
                drawList.addText(minPos.x + size.y / 2, itemTextPos.y, ColorConverter.colorToInt(hovered ? textColor.x : textColor.x / 2, hovered ? textColor.y : textColor.y / 2, hovered ? textColor.z : textColor.z / 2, 255), item);

                if(ImGui.isMouseHoveringRect(minPos.x, minPos.y + currentY - currentMinus, maxPos.x, minPos.y + currentY + size.y - currentMinus) && ImGui.isMouseClicked(0)){
                    selectedItem = item;
                }
                currentY += size.y;
            }
        }

        if(open){
            drawList.addRectFilled(minPos.x, minPos.y, minPos.x + size.x, minPos.y + size.y, ColorConverter.colorToInt(backgroundColor.x, backgroundColor.y, backgroundColor.z, backgroundColor.w), rounding, ImDrawFlags.RoundCornersAll);
            lineOpacity = open ? borderColor.w : 0;
            drawList.addLine(new ImVec2(minPos.x, maxPos.y - currentListSize), new ImVec2(maxPos.x, maxPos.y - currentListSize), ColorConverter.colorToInt(borderColor.x, borderColor.y, borderColor.z, lineOpacity), 1);
            drawList.addText(minPos.x + size.y / 2, textPos.y, ColorConverter.colorToInt(textColor.x, textColor.y, textColor.z, textColor.w), selectedItem);
        }
        drawList.addRect(minPos, maxPos, ColorConverter.colorToInt(borderColor.x, borderColor.y, borderColor.z, borderColor.w), rounding, ImDrawFlags.RoundCornersAll);

        arrowRotation = ImLerp(arrowRotation, open ? 180 : 0, ImGui.getIO().getDeltaTime() * 14);
        drawArrow(new ImVec2(maxPos.x - size.y / 2, textPos.y + 7), 14, arrowRotation, ColorConverter.colorToInt(textColor.x, textColor.y, textColor.z, textColor.w), drawList);

        if (ImGui.isMouseHoveringRect(minPos, maxPos) && ImGui.isMouseClicked(0)) {
            open = !open;
        }
        if (!ImGui.isMouseHoveringRect(minPos, maxPos) && ImGui.isMouseClicked(0)) {
            open = false;
        }
    }

    public static void drawArrow(ImVec2 position, float size, float rotation, int color, ImDrawList drawList) {
        double radians = Math.toRadians(rotation);
        float cos_a = (float) Math.cos(radians);
        float sin_a = (float) Math.sin(radians);

        float halfWidth = size / 2;
        float height = size / 4;
        ImVec2 topLeft = new ImVec2(-halfWidth, -height);
        ImVec2 topRight = new ImVec2(halfWidth, -height);
        ImVec2 bottomCenter = new ImVec2(0, height);

        ImVec2 rotatedTopLeft = ImRotate(topLeft, cos_a, sin_a);
        ImVec2 rotatedTopRight = ImRotate(topRight, cos_a, sin_a);
        ImVec2 rotatedBottomCenter = ImRotate(bottomCenter, cos_a, sin_a);

        rotatedTopLeft.x += position.x;
        rotatedTopLeft.y += position.y;
        rotatedTopRight.x += position.x;
        rotatedTopRight.y += position.y;
        rotatedBottomCenter.x += position.x;
        rotatedBottomCenter.y += position.y;

        drawList.addLine(rotatedTopLeft.x, rotatedTopLeft.y, rotatedBottomCenter.x, rotatedBottomCenter.y, color, 1.1f);
        drawList.addLine(rotatedTopRight.x, rotatedTopRight.y, rotatedBottomCenter.x, rotatedBottomCenter.y, color, 1.1f);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void build() {

    }
}