package io.naulasis.components.impl;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import io.naulasis.components.Component;
import io.naulasis.utils.ColorConverter;
import io.naulasis.utils.ImGuiInternal;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Child extends Component {

    private ImVec2 position = new ImVec2(0, 0), size = new ImVec2(0,0);
    private ImVec4 backgroundColor = new ImVec4(30, 30, 30, 255), borderColor = new ImVec4(55, 55, 55, 255);

    private float rounding = 15f, scrollY, scrollX, animationSpeed = 10f, sensitivity = 1.0f, stopScrollValue = 0.1f;
    private boolean border, hovered, pressed, clicked, released, animated;

    public void begin(String ID){
        ImVec2 lastCursorPosition = ImGui.getCursorPos();
        ImGui.setCursorPos(position);

        ImGui.pushStyleColor(ImGuiCol.ChildBg, ColorConverter.colorToInt(backgroundColor.x, backgroundColor.y, backgroundColor.z, backgroundColor.w));
        ImGui.pushStyleColor(ImGuiCol.Border, ColorConverter.colorToInt(borderColor.x, borderColor.y, borderColor.z, borderColor.w));

        ImGui.beginChild("##" + ID, size.x, size.y, border, ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoScrollbar);
        ImGui.setCursorPos(lastCursorPosition);

        updatePositions();
        handleInput();
    }

    private void handleInput() {
        hovered = ImGui.isMouseHoveringRect(position.x, position.y, position.x + size.x, position.y + size.x);
        clicked = ImGui.isMouseHoveringRect(position.x, position.y, position.x + size.x, position.y + size.x) && ImGui.isMouseClicked(0);
        pressed = ImGui.isMouseHoveringRect(position.x, position.y, position.x + size.x, position.y + size.x) && ImGui.isMouseDown(0);
        released = ImGui.isMouseHoveringRect(position.x, position.y, position.x + size.x, position.y + size.x) && ImGui.isMouseReleased(0);
    }

    private void updatePositions(){
        float currentScrollY = ImGui.getScrollY();
        float maxScrollY = ImGui.getScrollMaxY();
        if (ImGui.getIO().getMouseWheel() != 0 && hovered) {
            scrollY -= ImGui.getIO().getMouseWheel() * 100 * sensitivity;
            scrollY = Math.max(0, Math.min(maxScrollY, scrollY));
        }
        if (Math.abs(currentScrollY - scrollY) > stopScrollValue) {
            if(animated){
                currentScrollY = ImGuiInternal.ImLerp(currentScrollY, scrollY,  ImGui.getIO().getDeltaTime() * animationSpeed);
            }
            else{
                currentScrollY = scrollY;
            }
            ImGui.setScrollY(currentScrollY);
        }
    }

    public void end(){
        ImGui.endChild();
        ImGui.popStyleColor(2);
    }

    @Override
    public void build() {

    }

    @Override
    public void destroy() {

    }
}
