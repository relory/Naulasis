package io.naulasis.components;

import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.ImDrawFlags;
import io.naulasis.component;
import io.naulasis.utils.colorConverter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import static io.naulasis.utils.ImGuiInternal.ImLerp;

public class Slider extends component {
    private float currentPos;
    private float LastMousePosX;
    private boolean selected = false;
    private float SliderPosX;

    private ImVec4 color = new ImVec4(255, 0, 100, 100);
    private float MinimumValue;
    private float MaximumValue;
    private float Ticks;
    private float Value;
    private String Name;
    private final float Default;

    public Slider(String name, float minimum, float maximum, float aDefault, float ticks)  {
        this.Name = name;
        this.MinimumValue = minimum;
        this.MaximumValue = maximum;
        this.Default = aDefault;
        this.Value = minimum + maximum / 2;
        this.Ticks = ticks;
    }

    @Override
    public void Draw() {
        ImVec2 position = ImGui.getCursorPos();
        ImDrawList windowDrawList = ImGui.getWindowDrawList();
        float windowX = ImGui.getWindowPosX();
        float windowY = ImGui.getWindowPosY();
        SliderPosX = windowX + position.x;
        if (currentPos == 0 && LastMousePosX == 0) {
            LastMousePosX = windowX + position.x;
            currentPos = windowX + position.x;
        }
        windowDrawList.addRectFilled(currentPos, windowY + position.y, windowX + position.x + 500, windowY + position.y + 7,
                colorConverter.colorToInt(45F, 45F, 45F, 255), 12, ImDrawFlags.RoundCornersRight);

        windowDrawList.addRectFilled(windowX + position.x, windowY + position.y, currentPos, windowY + position.y + 7,
                colorConverter.colorToInt(color.x, color.y, color.z, color.w), 12, ImDrawFlags.RoundCornersLeft);

        windowDrawList.addCircleFilled(currentPos, windowY + position.y +3.5f, 8, colorConverter.colorToInt(255, 255, 255, 255));

        float deltaTime = ImGui.getIO().getDeltaTime();
            float animationSpeed = 7;
        currentPos = ImLerp(currentPos, CalculateLocation(this.Value), deltaTime * animationSpeed);

        float sliderWidth = 500;
        double d = Math.min(sliderWidth, Math.max(0, LastMousePosX - (windowX + position.x)));
        double value = (d / sliderWidth) * (this.MaximumValue - this.MinimumValue) + this.MinimumValue;
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
                LastMousePosX = mouseX;
            }

            if (mouseX >= windowX + position.x + sliderWidth) {
                LastMousePosX = windowX + position.x + sliderWidth;
            } else if (mouseX <= windowX + position.x) {
                LastMousePosX = windowX + position.x;
            }
        }
    }

    public float CalculateLocation(float value) {
        value = Math.max(this.MinimumValue, Math.min(this.MaximumValue, value));
        return SliderPosX + ((value - this.MinimumValue) / (this.MaximumValue - MinimumValue)) * 500;
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

    private float calc(double n){
        n = check(n, this.MinimumValue, this.MaximumValue);
        n = Math.round(n * (1.0D / this.Ticks)) / (1.0D / this.Ticks);
        return (float) n;
    }

    private void updateValue(double n){
        n = check(n, this.MinimumValue, this.MaximumValue);
        n = Math.round(n * (1.0D / this.Ticks)) / (1.0D / this.Ticks);
        this.Value = (float) n;
    }

    public float getValue() {
        return Round(Value, 2);
    }

    public float getMaximumValue() {
        return MaximumValue;
    }

    public float getMinimumValue() {
        return MinimumValue;
    }

    public float getTicks() {
        return Ticks;
    }

    public String getName() {
        return Name;
    }

    public void setValue(float value) {
        Value = value;
    }
}
