package io.naulasis.utils;

public class colorConverter {

    public static int colorToInt(double Red, double Green, double Blue, double opacity) {
        int alpha = (int) Math.round(opacity);
        int red = (int) Math.round(Red);
        int green = (int) Math.round(Green);
        int blue = (int) Math.round(Blue);

        return (alpha << 24) | (blue << 16) | (green << 8) | red;
    }
}