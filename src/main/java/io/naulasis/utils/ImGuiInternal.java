package io.naulasis.utils;

import imgui.ImVec2;
import imgui.ImVec4;

import static java.lang.Math.*;

/// ported from the original ImGui github repository (https://github.com/ocornut/imgui/blob/master/imgui_internal.h). I do not take any credit on this class
public class ImGuiInternal {
    public static ImVec2 ImClamp(ImVec2 v, ImVec2 mn, ImVec2 mx) {
        return new ImVec2(
                Math.max(mn.x, Math.min(mx.x, v.x)),
                Math.max(mn.y, Math.min(mx.y, v.y))
        );
    }

    public static ImVec2 ImRotate(ImVec2 v, float cos_a, float sin_a) {
        float x = v.x * cos_a - v.y * sin_a;
        float y = v.x * sin_a + v.y * cos_a;
        return new ImVec2(x, y);
    }

    public static ImVec2 ImLerp(ImVec2 a, ImVec2 b, float t) {
        return new ImVec2(
                a.x + (b.x - a.x) * t,
                a.y + (b.y - a.y) * t
        );
    }

    public static float ImDot(ImVec2 a, ImVec2 b){
        return a.x * b.x + a.y * b.y;
    }

    public static <T extends Comparable<T>> T ImMin(T lhs, T rhs) {
        return lhs.compareTo(rhs) < 0 ? lhs : rhs;
    }

    public static <T extends Comparable<T>> T ImMax(T lhs, T rhs) {
        return lhs.compareTo(rhs) >= 0 ? lhs : rhs;
    }

    public static float ImLinearSweep(float current, float target, float speed){
        if (current < target) return ImMin(current + speed, target); if (current > target) return ImMax(current - speed, target); return current;
    }

    public static float ImLerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static ImVec2 ImLerp(ImVec2 a, ImVec2 b, ImVec2 t) {
        return new ImVec2(
                a.x + (b.x - a.x) * t.x,
                a.y + (b.y - a.y) * t.y
        );
    }

    public static ImVec4 ImLerp(ImVec4 a, ImVec4 b, float t) {
        return new ImVec4(
                a.x + (b.x - a.x) * t,
                a.y + (b.y - a.y) * t,
                a.z + (b.z - a.z) * t,
                a.w + (b.w - a.w) * t
        );
    }

    public static float ImFloor(float f){
        return (float)((f >= 0 || (float)(int)f == f) ? (int)f : (int)f - 1);
    }

    public static ImVec2 ImFloor(ImVec2 v){
        return new ImVec2(ImFloor(v.x), ImFloor(v.y));
    }

    public static double ImPow(double x, double y) {
        return pow(x, y);
    }

    public static double ImLog(double x){
        return log(x);
    }

    public static int ImAbs(int x){
        return x < 0 ? -x : x;
    }

    public static float ImSign(float x){
        return (x < 0.0f) ? -1.0f : (x > 0.0f) ? 1.0f : 0.0f;
    }
    public static double ImSign(double x){
        return (x < 0.0) ? -1.0 : (x > 0.0) ? 1.0 : 0.0;
    }

    public static float ImSaturate(float f){
        return (f < 0.0f) ? 0.0f : (f > 1.0f) ? 1.0f : f;
    }

    public static float ImLinearRemapClamp(float s0, float s1, float d0, float d1, float x){
        return ImSaturate((x - s0) / (s1 - s0)) * (d1 - d0) + d0;
    }

    public static double ImRsqrt(double x){
        return 1.0 / sqrt(x);
    }
}
