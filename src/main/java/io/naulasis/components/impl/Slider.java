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

import java.math.BigDecimal;
import java.math.RoundingMode;

import static io.naulasis.utils.ImGuiInternal.ImLerp;

@Getter @Setter
public class Slider extends Component {

    private final float Default;
    private float rounding = 12, thumbRadius = 8, animationSpeed = 7, currentPosX, lastMousePosX, sliderPosX, minimumValue, maximumValue, ticks, value;
    private boolean selected = false, hideThumb, animated;
    private ImVec2 position, size;
    private ImVec4 progressColor = new ImVec4(255, 0, 100, 100), thumbColor = new ImVec4(255, 255, 255, 255);
    private String name;

    public Slider(String name, float minimum, float maximum, float aDefault, float ticks)  {
        this.name = name;
        this.minimumValue = minimum;
        this.maximumValue = maximum;
        this.Default = aDefault;
        this.value = minimum + maximum / 2;
        this.ticks = ticks;
    }

    @Override
    public void draw() {
        if(position == null) position = ImGui.getCursorPos();
        if(size == null) size = new ImVec2(500, 7);
        ImDrawList windowDrawList = ImGui.getWindowDrawList();

        float windowX = ImGui.getWindowPosX();
        float windowY = ImGui.getWindowPosY();
        sliderPosX = windowX + position.x;

        if (currentPosX == 0 && lastMousePosX == 0) {
            lastMousePosX = windowX + position.x;
            currentPosX = windowX + position.x;
        }

        windowDrawList.addRectFilled(currentPosX, windowY + position.y, windowX + position.x + size.x, windowY + position.y + size.y,
                ColorConverter.colorToInt(45F, 45F, 45F, 255F), rounding, ImDrawFlags.RoundCornersRight);

        windowDrawList.addRectFilled(windowX + position.x, windowY + position.y, currentPosX, windowY + position.y + size.y,
                ColorConverter.colorToInt(progressColor.x, progressColor.y, progressColor.z, progressColor.w), rounding, ImDrawFlags.RoundCornersLeft);

        if(!hideThumb) windowDrawList.addCircleFilled(currentPosX, windowY + position.y + size.y / 2, thumbRadius, ColorConverter.colorToInt(thumbColor.x, thumbColor.y, thumbColor.z, thumbColor.w));

        float deltaTime = ImGui.getIO().getDeltaTime();
        if(animated) {
            currentPosX = ImLerp(currentPosX, CalculateLocation(this.value), deltaTime * animationSpeed);
        }
        else {
            currentPosX = CalculateLocation(this.value);

        }
        float sliderWidth = size.x;
        double d = Math.min(sliderWidth, Math.max(0, lastMousePosX - (windowX + position.x)));
        double value = (d / sliderWidth) * (this.maximumValue - this.minimumValue) + this.minimumValue;
        this.updateValue(value);

        if (ImGui.isMouseReleased(0)) {
            selected = false;
            this.setValue((float) value);
        }

        if (ImGui.isWindowFocused() && ImGui.isMouseClicked(0) && ImGui.isMouseHoveringRect(windowX + position.x - 15, windowY + position.y - 15, windowX + position.x + sliderWidth + 15, windowY + position.y + 15)) {
            selected = true;
        }

        if (selected) {
            float mouseX = ImGui.getMousePos().x;
            if (mouseX > windowX + position.x && mouseX < windowX + position.x + sliderWidth) {
                lastMousePosX = mouseX;
            }

            if (mouseX >= windowX + position.x + sliderWidth) {
                lastMousePosX = windowX + position.x + sliderWidth;
            } else if (mouseX <= windowX + position.x) {
                lastMousePosX = windowX + position.x;
            }
        }
    }

    @Override
    public void destroy() {

    }

    public float CalculateLocation(float value) {
        return sliderPosX + ((Math.max(this.minimumValue, Math.min(this.maximumValue, value)) - this.minimumValue) / (this.maximumValue - minimumValue)) * size.x;
    }

    private float Round(double v, int p){
        if (p < 0) {
            return 0.0F;
        } else {
            BigDecimal bd = new BigDecimal(v);
            bd = bd.setScale(p, RoundingMode.HALF_UP);

            return bd.floatValue();
        }
    }

    public static double check(double v, double i, double a) {
        v = Math.max(i, v);
        v = Math.min(a, v);

        return v;
    }

    private void updateValue(double n){
        n = check(n, this.minimumValue, this.maximumValue);
        n = Math.round(n * (1.0D / this.ticks)) / (1.0D / this.ticks);

        this.value = (float) n;
    }

    public float getValue() {
        return Round(value, 2);
    }
}
