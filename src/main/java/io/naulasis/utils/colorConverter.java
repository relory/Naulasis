package io.naulasis.utils;

public class colorConverter {
    public static int colorToInt(double Red, double Green, double Blue, double opacity) {
        int alpha = (int) (opacity);
        int red = (int) (Red);
        int green = (int) (Green);
        int blue = (int) (Blue);
        return (alpha << 24) | (blue << 16) | (green << 8) | red;
    }
}
