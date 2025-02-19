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

public class RangeSlider extends Component {
    @Setter @Getter
    private float rounding = 12, thumbRadius = 8, animationSpeed = 10, destroyedHighValue = this.minimumValue, destroyedLowValue = this.minimumValue, currentMinPosX, currentMaxPosX, lastMousePosX, sliderPosX, minimumValue, maximumValue, ticks, lowValue, highValue;

    private boolean MinSelected, MaxSelected;

    @Setter @Getter
    private boolean selected = false, hideThumb, animated = true, fadeInAnimation = true, fadeOutAnimation = true, startFromMinimum = true, hovered, clicked, pressed, released;

    @Setter @Getter
    private ImVec2 position = new ImVec2(0,0), size = new ImVec2(500, 7);

    @Setter @Getter
    private ImVec4 progressColor = new ImVec4(255, 0, 100, 100), thumbColor = new ImVec4(255, 255, 255, 255), unprogressedColor = new ImVec4(45, 45, 45, 255);

    private ImVec4 currentProgressColor = new ImVec4(0,0,0,0);
    private ImVec4 currentThumbColor = new ImVec4(0,0,0,0);
    private ImVec4 currentUnprogressedColor = new ImVec4(0,0,0,0);

    @Getter
    private boolean destroyed;

    public RangeSlider(float minimum, float maximum, float minimumDefault, float maximumDefault , float ticks)  {
        this.minimumValue = minimum;
        this.maximumValue = maximum;
        this.lowValue = minimumDefault;
        this.highValue = maximumDefault;
        this.ticks = ticks;

        this.currentMinPosX = this.CalculateLocation(lowValue);
        this.currentMaxPosX = this.CalculateLocation(highValue);
        this.lastMousePosX = this.CalculateLocation((lowValue + highValue) / 2);
    }

    @Override
    public void draw() {
        ImDrawList windowDrawList = ImGui.getWindowDrawList();
        float deltaTime = ImGui.getIO().getDeltaTime();
        float windowX = ImGui.getWindowPosX();
        float windowY = ImGui.getWindowPosY() - ImGui.getScrollY();
        sliderPosX = windowX + position.x;

        if(destroyed){
            if(fadeOutAnimation) {
                currentMinPosX = ImLerp(currentMinPosX, CalculateLocation(destroyedLowValue), deltaTime * animationSpeed);
                currentMaxPosX = ImLerp(currentMaxPosX, CalculateLocation(destroyedHighValue), deltaTime * animationSpeed);

            }
            else{
                currentMinPosX = CalculateLocation(destroyedLowValue);
                currentMaxPosX = CalculateLocation(destroyedHighValue);
            }
        }

        currentProgressColor = ImLerp(currentProgressColor, destroyed ? new ImVec4(0, 0, 0, 0) : progressColor, deltaTime * animationSpeed);
        currentThumbColor = ImLerp(currentThumbColor, destroyed ? new ImVec4(0, 0, 0, 0) : thumbColor, deltaTime * animationSpeed);
        currentUnprogressedColor = ImLerp(currentUnprogressedColor, destroyed ? new ImVec4(0, 0, 0, 0) : unprogressedColor, deltaTime * animationSpeed);

        windowDrawList.addRectFilled(sliderPosX, windowY + position.y, windowX + position.x + size.x, windowY + position.y + size.y,
                ColorConverter.colorToInt(currentUnprogressedColor.x, currentUnprogressedColor.y, currentUnprogressedColor.z, currentUnprogressedColor.w), rounding, ImDrawFlags.RoundCornersAll);

        windowDrawList.addRectFilled(currentMinPosX, windowY + position.y, currentMaxPosX, windowY + position.y + size.y,
                ColorConverter.colorToInt(currentProgressColor.x, currentProgressColor.y, currentProgressColor.z, currentProgressColor.w), rounding, ImDrawFlags.RoundCornersAll);

        if(!hideThumb) windowDrawList.addCircleFilled(currentMinPosX, windowY + position.y + size.y / 2, thumbRadius, ColorConverter.colorToInt(currentThumbColor.x, currentThumbColor.y, currentThumbColor.z, currentThumbColor.w));
        if(!hideThumb) windowDrawList.addCircleFilled(currentMaxPosX, windowY + position.y + size.y / 2, thumbRadius, ColorConverter.colorToInt(currentThumbColor.x, currentThumbColor.y, currentThumbColor.z, currentThumbColor.w));

        if(destroyed) return;

        currentMinPosX = animated ? ImLerp(currentMinPosX, this.CalculateLocation(lowValue), deltaTime * animationSpeed) : this.CalculateLocation(lowValue);
        currentMaxPosX = animated ? ImLerp(currentMaxPosX, this.CalculateLocation(highValue), deltaTime * animationSpeed) : this.CalculateLocation(highValue);

        if (ImGui.isMouseReleased(0)) {
            MinSelected = false;
            MaxSelected = false;
        }

        if (ImGui.isWindowFocused() && ImGui.isMouseClicked(0) && ImGui.isMouseHoveringRect(windowX + position.x - 15, windowY + position.y - 15, windowX + position.x + size.x + 15, windowY + position.y + size.y + 15)) {
            float mouseX = ImGui.getMousePos().x;
            float minPosX = CalculateLocation(lowValue);
            float maxPosX = CalculateLocation(highValue);

            if (Math.abs(mouseX - minPosX) < Math.abs(mouseX - maxPosX)) {
                MinSelected = true;
                MaxSelected = false;
            } else {
                MaxSelected = true;
                MinSelected = false;
            }
        }

        if (MinSelected) {
            float mouseX = ImGui.getMousePos().x;
            if (mouseX > windowX + position.x && mouseX < windowX + position.x + size.x) {
                lastMousePosX = mouseX;
            }

            if (mouseX >= windowX + position.x + size.x) {
                lastMousePosX = windowX + position.x + size.x;
            } else if (mouseX <= windowX + position.x) {
                lastMousePosX = windowX + position.x;
            }

            double d = Math.min(size.x, Math.max(0, lastMousePosX - (windowX + position.x)));
            double newValue = (d / size.x) * (this.maximumValue - this.minimumValue) + this.minimumValue;
            this.updateMinValue(newValue);
        }

        if (MaxSelected) {
            float mouseX = ImGui.getMousePos().x;
            if (mouseX > windowX + position.x && mouseX < windowX + position.x + size.x) {
                lastMousePosX = mouseX;
            }

            if (mouseX >= windowX + position.x + size.x) {
                lastMousePosX = windowX + position.x + size.x;
            } else if (mouseX <= windowX + position.x) {
                lastMousePosX = windowX + position.x;
            }

            double d = Math.min(size.x, Math.max(0, lastMousePosX - (windowX + position.x)));
            double newValue = (d / size.x) * (this.maximumValue - this.minimumValue) + this.minimumValue;
            this.updateMaxValue(newValue);
        }

        hovered = ImGui.isMouseHoveringRect(windowX + position.x - 15, windowY + position.y - 15, windowX + position.x + size.x + 15, windowY + position.y + size.y + 15);
        clicked = ImGui.isMouseClicked(0) && ImGui.isMouseHoveringRect(windowX + position.x - 15, windowY + position.y - 15, windowX + position.x + size.x + 15, windowY + position.y + size.y + 15);
        pressed = ImGui.isMouseDown(0) && ImGui.isMouseHoveringRect(windowX + position.x - 15, windowY + position.y - 15, windowX + position.x + size.x + 15, windowY + position.y + size.y + 15);
        released = ImGui.isMouseReleased(0) && ImGui.isMouseHoveringRect(windowX + position.x - 15, windowY + position.y - 15, windowX + position.x + size.x + 15, windowY + position.y + size.y + 15);

    }

    @Override
    public void destroy() {
        destroyed = true;
    }

    public void build(){
        destroyed = false;
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

    private void updateMaxValue(double n){
        n = check(n, this.minimumValue, this.maximumValue);
        n = Math.round(n * (1.0D / this.ticks)) / (1.0D / this.ticks);

        this.highValue = (float) n;

        if (this.lowValue > this.highValue) {
            this.lowValue = this.highValue;
        }
    }

    private void updateMinValue(double n){
        n = check(n, this.minimumValue, this.maximumValue);
        n = Math.round(n * (1.0D / this.ticks)) / (1.0D / this.ticks);

        this.lowValue = (float) n;

        if (this.highValue < this.lowValue) {
            this.highValue = this.lowValue;
        }
    }

    public float getMinValue() {
        return Round(lowValue, 2);
    }

    public float getMaxValue() {
        return Round(highValue, 2);
    }
}