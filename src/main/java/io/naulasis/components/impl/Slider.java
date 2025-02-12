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

public class Slider extends Component {
    @Setter @Getter
    private float rounding = 12, thumbRadius = 8, animationSpeed = 10, currentPosX, lastMousePosX, sliderPosX, minimumValue, maximumValue, ticks, value;

    @Setter @Getter
    private boolean selected = false, hideThumb, animated = true, fadeInAnimation = true, fadeOutAnimation = true, startFromMinimum = true, hovered, clicked, pressed, released;

    @Setter @Getter
    private ImVec2 position = new ImVec2(0,0), size = new ImVec2(500, 7);

    @Setter @Getter
    private ImVec4 progressColor = new ImVec4(255, 0, 100, 100), thumbColor = new ImVec4(255, 255, 255, 255), unprogressedColor = new ImVec4(45, 45, 45, 255);

    @Getter @Setter
    private ImVec4 currentProgressColor = new ImVec4(0,0,0,0);

    @Getter @Setter
    private ImVec4 currentThumbColor = new ImVec4(0,0,0,0);

    @Getter @Setter
    private ImVec4 currentUnprogressedColor = new ImVec4(0,0,0,0);

    @Getter
    private boolean destroyed;

    public Slider(float minimum, float maximum, float aDefault, float ticks)  {
        this.minimumValue = minimum;
        this.maximumValue = maximum;
        this.value = aDefault;
        this.ticks = ticks;
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
                currentPosX = ImLerp(currentPosX, CalculateLocation(minimumValue), deltaTime * animationSpeed);
            }
            else{
                currentPosX = CalculateLocation(minimumValue);
            }
        }

        if(!startFromMinimum){
            currentPosX = CalculateLocation(this.value);
        }

        if(destroyed && !fadeOutAnimation){
            currentProgressColor = new ImVec4(0, 0, 0, 0);
            currentThumbColor = new ImVec4(0, 0, 0, 0);
            currentUnprogressedColor = new ImVec4(0, 0, 0, 0);
        }

        if(!destroyed && !fadeInAnimation){
            currentProgressColor = progressColor;
            currentThumbColor = thumbColor;
            currentUnprogressedColor = unprogressedColor;
        }

        currentProgressColor = ImLerp(currentProgressColor, destroyed ? new ImVec4(0, 0, 0, 0) : progressColor, deltaTime * animationSpeed);
        currentThumbColor = ImLerp(currentThumbColor, destroyed ? new ImVec4(0, 0, 0, 0) : thumbColor, deltaTime * animationSpeed);
        currentUnprogressedColor = ImLerp(currentUnprogressedColor, destroyed ? new ImVec4(0, 0, 0, 0) : unprogressedColor, deltaTime * animationSpeed);

        if (currentPosX == 0 && lastMousePosX == 0) {
            lastMousePosX = this.CalculateLocation(value);
            currentPosX = this.CalculateLocation(0);
        }

        windowDrawList.addRectFilled(currentPosX, windowY + position.y, windowX + position.x + size.x, windowY + position.y + size.y,
                ColorConverter.colorToInt(currentUnprogressedColor.x, currentUnprogressedColor.y, currentUnprogressedColor.z, currentUnprogressedColor.w), rounding, ImDrawFlags.RoundCornersRight);

        windowDrawList.addRectFilled(windowX + position.x, windowY + position.y, currentPosX, windowY + position.y + size.y,
                ColorConverter.colorToInt(currentProgressColor.x, currentProgressColor.y, currentProgressColor.z, currentProgressColor.w), rounding, ImDrawFlags.RoundCornersLeft);

        if(!hideThumb) windowDrawList.addCircleFilled(currentPosX, windowY + position.y + size.y / 2, thumbRadius, ColorConverter.colorToInt(currentThumbColor.x, currentThumbColor.y, currentThumbColor.z, currentThumbColor.w));

        if(destroyed) return;



        if(animated) {
            currentPosX = ImLerp(currentPosX, this.CalculateLocation(value), deltaTime * animationSpeed);
        }
        else {
            currentPosX = this.CalculateLocation(value);
        }

        if (ImGui.isMouseReleased(0)) {
            selected = false;
        }

        if (ImGui.isWindowFocused() && ImGui.isMouseClicked(0) && ImGui.isMouseHoveringRect(windowX + position.x - 15, windowY + position.y - 15, windowX + position.x + size.x + 15, windowY + position.y + size.y + 15)) {
            selected = true;
        }

        if (selected) {
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
            this.updateValue(newValue);
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

    private void updateValue(double n){
        n = check(n, this.minimumValue, this.maximumValue);
        n = Math.round(n * (1.0D / this.ticks)) / (1.0D / this.ticks);

        this.value = (float) n;
    }

    public float getValue() {
        return Round(value, 2);
    }
}