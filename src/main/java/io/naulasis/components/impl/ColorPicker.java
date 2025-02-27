package io.naulasis.components.impl;

import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import io.naulasis.components.Component;
import io.naulasis.utils.ColorConverter;
import lombok.Getter;
import lombok.Setter;

public class ColorPicker extends Component {

    @Getter @Setter
    private ImVec2 position = new ImVec2(0, 0);
    @Getter @Setter
    private ImVec2 size = new ImVec2(280, 230);

    @Getter @Setter
    private boolean destroyed;

    // HSV (hue ∈ [0..360], sat e bright ∈ [0..1])
    private float hue = 120f;
    private float saturation = 1f;
    private float brightness = 1f;

    // Cor resultante em RGBA ([0..255])
    @Getter @Setter
    private ImVec4 selectedColor = new ImVec4(255, 0, 0, 255); // Começa em vermelho

    // Paleta de cores pré-definidas (swatches)
    private static final ImVec4[] SWATCH_COLORS = {
            new ImVec4(255, 255,   0, 255), // Amarelo
            new ImVec4(255,   0, 255, 255), // Magenta
            new ImVec4(  0,   0, 255, 255), // Azul
            new ImVec4(  0, 255,   0, 255), // Verde
            new ImVec4(  0, 255, 255, 255), // Ciano
            new ImVec4(255, 255, 255, 255), // Branco
            new ImVec4(128, 128, 128, 255), // Cinza
            new ImVec4(  0,   0,   0, 255), // Preto
    };

    @Override
    public void draw() {
        if (destroyed) return;

        // Posição inicial (coordenadas de tela) onde o ImGui está
        ImVec2 origin = ImGui.getCursorScreenPos();
        float baseX = origin.x + position.x;
        float baseY = origin.y + position.y;

        ImDrawList drawList = ImGui.getWindowDrawList();

        float width  = size.x;
        float height = size.y;

        // Retângulo total do ColorPicker
        ImVec2 minPos = new ImVec2(baseX, baseY);
        ImVec2 maxPos = new ImVec2(baseX + width, baseY + height);

        // Layout interno
        float hueBarHeight  = 20f;    // Altura da barra de Hue
        float previewWidth  = 30f;    // Largura do quadrado de preview
        float swatchAreaH   = 30f;    // Altura da área de swatches
        float spacing       = 5f;     // Espaçamento

        ImVec2 hueBarMin = new ImVec2(baseX, baseY);
        ImVec2 hueBarMax = new ImVec2(baseX + width - previewWidth, baseY + hueBarHeight);

        ImVec2 previewMin = new ImVec2(hueBarMax.x, hueBarMin.y);
        ImVec2 previewMax = new ImVec2(previewMin.x + previewWidth, hueBarMax.y);

        ImVec2 colorAreaMin = new ImVec2(
                baseX,
                hueBarMax.y + spacing
        );
        ImVec2 colorAreaMax = new ImVec2(
                baseX + width,
                baseY + height - (swatchAreaH + spacing)
        );

        ImVec2 swatchAreaMin = new ImVec2(baseX, colorAreaMax.y + spacing);
        ImVec2 swatchAreaMax = new ImVec2(baseX + width, baseY + height);

        drawHueBar(drawList, hueBarMin, hueBarMax);
        drawColorPreview(drawList, previewMin, previewMax);
        drawSatBrightArea(drawList, colorAreaMin, colorAreaMax);
        drawIndicators(drawList, hueBarMin, hueBarMax, colorAreaMin, colorAreaMax);

        handleInput(hueBarMin, hueBarMax, colorAreaMin, colorAreaMax);
        selectedColor = HSVtoRGB(hue, saturation, brightness);

        drawSwatches(drawList, swatchAreaMin, swatchAreaMax);

        int borderColor = ColorConverter.colorToInt(40, 40, 40, 255);
        drawList.addRect(minPos, maxPos, borderColor, 0f, 0, 1f);


        ImGui.setCursorScreenPos(new ImVec2(baseX, baseY + height));
    }

    private void drawHueBar(ImDrawList drawList, ImVec2 hueBarMin, ImVec2 hueBarMax) {
        int numSegments = 6;
        float barWidth  = hueBarMax.x - hueBarMin.x;
        float barHeight = hueBarMax.y - hueBarMin.y;
        float segmentWidth = barWidth / numSegments;

        for (int i = 0; i < numSegments; i++) {
            float h1 = i * (360f / numSegments);
            float h2 = (i + 1) * (360f / numSegments);

            ImVec4 col1 = HSVtoRGB(h1, 1f, 1f);
            ImVec4 col2 = HSVtoRGB(h2, 1f, 1f);

            int c1 = ColorConverter.colorToInt((int) col1.x, (int) col1.y, (int) col1.z, 255);
            int c2 = ColorConverter.colorToInt((int) col2.x, (int) col2.y, (int) col2.z, 255);

            float xMin = hueBarMin.x + i * segmentWidth;
            float xMax = xMin + segmentWidth;
            drawList.addRectFilledMultiColor(
                    new ImVec2(xMin, hueBarMin.y),
                    new ImVec2(xMax, hueBarMin.y + barHeight),
                    c1, c2, c2, c1
            );
        }

        int borderColor = ColorConverter.colorToInt(40, 40, 40, 255);
        drawList.addRect(hueBarMin, hueBarMax, borderColor, 0f, 0, 1f);
    }

    private void drawColorPreview(ImDrawList drawList, ImVec2 previewMin, ImVec2 previewMax) {
        int col = ColorConverter.colorToInt(
                (int) selectedColor.x,
                (int) selectedColor.y,
                (int) selectedColor.z,
                (int) selectedColor.w
        );
        drawList.addRectFilled(previewMin, previewMax, col, 0f, 0);
        int borderCol = ColorConverter.colorToInt(40, 40, 40, 255);
        drawList.addRect(previewMin, previewMax, borderCol, 0f, 0, 1f);
    }

    private void drawSatBrightArea(ImDrawList drawList, ImVec2 areaMin, ImVec2 areaMax) {

        ImVec4 pureHue = HSVtoRGB(hue, 1f, 1f);
        int colWhite   = ColorConverter.colorToInt(255, 255, 255, 255);
        int colPureHue = ColorConverter.colorToInt((int) pureHue.x, (int) pureHue.y, (int) pureHue.z, 255);

        drawList.addRectFilledMultiColor(
                areaMin, areaMax,
                colWhite,    // top-left
                colPureHue,  // top-right
                colPureHue,  // bottom-right
                colWhite     // bottom-left
        );

        int transparent = ColorConverter.colorToInt(0, 0, 0, 0);
        int black       = ColorConverter.colorToInt(0, 0, 0, 255);
        drawList.addRectFilledMultiColor(
                areaMin, areaMax,
                transparent, // top-left
                transparent, // top-right
                black,       // bottom-right
                black        // bottom-left
        );

        // Borda
        int borderColor = ColorConverter.colorToInt(40, 40, 40, 255);
        drawList.addRect(areaMin, areaMax, borderColor, 0f, 0, 1f);
    }

    private void drawIndicators(ImDrawList drawList,
                                ImVec2 hueBarMin, ImVec2 hueBarMax,
                                ImVec2 areaMin, ImVec2 areaMax) {

        // Barra de Hue (linha vertical)
        float hueRatio = hue / 360f;
        float hueX = hueBarMin.x + hueRatio * (hueBarMax.x - hueBarMin.x);
        int markerColor = ColorConverter.colorToInt(255, 255, 255, 255);
        drawList.addLine(hueX, hueBarMin.y, hueX, hueBarMax.y, markerColor, 2f);

        float satX = areaMin.x + saturation * (areaMax.x - areaMin.x);
        float brightY = areaMin.y + (1f - brightness) * (areaMax.y - areaMin.y);
        drawList.addCircle(satX, brightY, 5f, markerColor, 12, 2f);
    }


    private void handleInput(ImVec2 hueBarMin, ImVec2 hueBarMax,
                             ImVec2 areaMin, ImVec2 areaMax) {

        float mouseX = ImGui.getMousePosX();
        float mouseY = ImGui.getMousePosY();

        if (ImGui.isMouseHoveringRect(hueBarMin.x, hueBarMin.y, hueBarMax.x, hueBarMax.y)
                && ImGui.isMouseDown(0)) {
            float ratio = (mouseX - hueBarMin.x) / (hueBarMax.x - hueBarMin.x);
            ratio = clamp(ratio, 0f, 1f);
            hue = ratio * 360f;
        }

        if (ImGui.isMouseHoveringRect(areaMin.x, areaMin.y, areaMax.x, areaMax.y)
                && ImGui.isMouseDown(0)) {
            float satRatio = (mouseX - areaMin.x) / (areaMax.x - areaMin.x);
            satRatio = clamp(satRatio, 0f, 1f);
            saturation = satRatio;

            float brightRatio = (mouseY - areaMin.y) / (areaMax.y - areaMin.y);
            brightness = 1f - clamp(brightRatio, 0f, 1f);
        }
    }


    private void drawSwatches(ImDrawList drawList, ImVec2 swatchAreaMin, ImVec2 swatchAreaMax) {
        int backgroundColor = ColorConverter.colorToInt(25, 25, 25, 255);
        drawList.addRectFilled(swatchAreaMin, swatchAreaMax, backgroundColor, 0, 0);

        float swatchSize = 24f;
        float spacing    = 5f;
        float totalWidth = (SWATCH_COLORS.length * swatchSize) + (SWATCH_COLORS.length - 1) * spacing;

        float availableWidth  = swatchAreaMax.x - swatchAreaMin.x;
        float availableHeight = swatchAreaMax.y - swatchAreaMin.y;

        float startX = swatchAreaMin.x + (availableWidth - totalWidth) / 2f;
        float startY = swatchAreaMin.y + (availableHeight - swatchSize) / 2f;

        for (int i = 0; i < SWATCH_COLORS.length; i++) {
            float xMin = startX + i * (swatchSize + spacing);
            float yMin = startY;
            float xMax = xMin + swatchSize;
            float yMax = yMin + swatchSize;

            ImVec4 c = SWATCH_COLORS[i];
            int col = ColorConverter.colorToInt((int) c.x, (int) c.y, (int) c.z, (int) c.w);

            drawList.addRectFilled(new ImVec2(xMin, yMin), new ImVec2(xMax, yMax), col, 6f, 0);

            int borderCol = ColorConverter.colorToInt(40, 40, 40, 255);
            drawList.addRect(new ImVec2(xMin, yMin), new ImVec2(xMax, yMax), borderCol, 6f, 0, 1f);

            if (ImGui.isMouseHoveringRect(xMin, yMin, xMax, yMax) && ImGui.isMouseDown(0)) {
                setColorFromRGB(c);
            }
        }
    }

    private void setColorFromRGB(ImVec4 col) {
        float r = col.x / 255f;
        float g = col.y / 255f;
        float b = col.z / 255f;

        float[] hsv = rgbToHSV(r, g, b);
        hue        = hsv[0];
        saturation = hsv[1];
        brightness = hsv[2];

        selectedColor = new ImVec4(col.x, col.y, col.z, 255);
    }

    private float[] rgbToHSV(float r, float g, float b) {
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float h, s;
        float v = max;

        float d = max - min;
        s = (max == 0) ? 0 : (d / max);

        if (max == min) {
            h = 0; // Achromatic
        } else {
            if (max == r) {
                h = (g - b) / d + (g < b ? 6 : 0);
            } else if (max == g) {
                h = (b - r) / d + 2;
            } else {
                h = (r - g) / d + 4;
            }
            h *= 60f;
        }
        return new float[]{ h, s, v };
    }

    private ImVec4 HSVtoRGB(float h, float s, float v) {
        float c = v * s;
        float x = c * (1 - Math.abs(((h / 60f) % 2) - 1));
        float m = v - c;

        float r1 = 0, g1 = 0, b1 = 0;
        if      (h < 60)  { r1 = c; g1 = x; b1 = 0; }
        else if (h < 120) { r1 = x; g1 = c; b1 = 0; }
        else if (h < 180) { r1 = 0; g1 = c; b1 = x; }
        else if (h < 240) { r1 = 0; g1 = x; b1 = c; }
        else if (h < 300) { r1 = x; g1 = 0; b1 = c; }
        else              { r1 = c; g1 = 0; b1 = x; }

        int rr = (int)((r1 + m) * 255);
        int gg = (int)((g1 + m) * 255);
        int bb = (int)((b1 + m) * 255);
        return new ImVec4(rr, gg, bb, 255);
    }

    private float clamp(float val, float min, float max) {
        return (val < min) ? min : (val > max ? max : val);
    }

    @Override
    public void build() {
        destroyed = false;
    }

    @Override
    public void destroy() {
        destroyed = true;
    }
}
