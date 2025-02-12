package io.naulasis.messageboxes.impl;

import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import io.naulasis.Naulasis;
import io.naulasis.messageboxes.MessageBox;
import io.naulasis.utils.ColorConverter;
import io.naulasis.utils.ImVec3;
import lombok.Getter;
import lombok.Setter;

import static io.naulasis.utils.ImGuiInternal.ImLerp;

@Getter @Setter
public class WarningBox extends MessageBox {

    private ImVec2 position = new ImVec2(100, 100), targetPosition = new ImVec2(150, 150), size = new ImVec2(200, 25);
    private ImVec3 currentBackgroundColor = new ImVec3(30, 30, 30);

    private boolean render = true, reset = true, opacityDecreasion = false;
    private float animationProgress = 0.0f;
    private int secondsUntilDisappearance = 5;
    private long startedAt = 0L;
    private float currentOpacity = 255;

    @Override
    public void show() {
        if (reset) {
            startedAt = System.currentTimeMillis();
            reset = false;
        }

        if (opacityDecreasion) {
            if ((currentOpacity -= 2) <= 0) {
                render = false;
            }
        }

        if (render) {
            ImDrawList drawList = ImGui.getWindowDrawList();
            float multiplier = YesNoBox.isEnabled() ? 1.0f : 10f;

            ImVec2 minPos = new ImVec2(10 + ImGui.getWindowPosX(), ImGui.getWindowHeight() - 10 - size.y + ImGui.getWindowPosY() * multiplier);
            ImVec2 maxPos = new ImVec2(10 + ImGui.getWindowPosX() + size.x, ImGui.getWindowHeight() - 10 + ImGui.getWindowPosY() * multiplier);

            drawList.addRectFilled(minPos, maxPos, ColorConverter.colorToInt(currentBackgroundColor.x, currentBackgroundColor.y, currentBackgroundColor.z, currentOpacity));

            ImVec2 textSize = ImGui.calcTextSize("Warning");
            ImVec2 textPos = new ImVec2(minPos.x + (size.x - textSize.x) / 2f, minPos.y + (size.y - textSize.y) / 2);

            drawList.addText(textPos, ColorConverter.colorToInt(255, 255, 255, currentOpacity), "Yes");

            ImVec2 lineStart = new ImVec2(minPos.x, maxPos.y);
            ImVec2 lineEnd = new ImVec2(minPos.x + animationProgress, maxPos.y);

            animationProgress = ImLerp(animationProgress, size.x, ImGui.getIO().getDeltaTime());
            drawList.addLine(lineStart, lineEnd, ColorConverter.colorToInt(0, 255, 0, currentOpacity), 2.0f);

            if (((System.currentTimeMillis() - startedAt) / 1000) >= secondsUntilDisappearance) {
                opacityDecreasion = true;
            }
        }
    }

    public void reset() {
        render = true;
        reset = true;
        opacityDecreasion = false;
        currentOpacity = 255;
        startedAt = 0L;
        animationProgress = 0.0f;
    }

    @Override
    public void kill() {
        render = false;
    }
}
