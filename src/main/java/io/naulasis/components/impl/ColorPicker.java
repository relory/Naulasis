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
    private ImVec2 size = new ImVec2(430, 25);

    @Getter @Setter
    private boolean destroyed;

    @Getter @Setter
    private String label = "Text color";

    @Getter @Setter
    private boolean expanded = false;

    private float hue = 30f;
    private float saturation = 1f;
    private float brightness = 1f;

    private float currentHue = hue;
    private float currentSaturation = saturation;
    private float currentBrightness = brightness;
    private float colorAnimationSpeed = 10f;

    @Getter @Setter
    private ImVec4 selectedColor = new ImVec4(255, 128, 0, 255);

    private static final ImVec4[] SWATCH_COLORS = {
            new ImVec4(255, 255,   0, 255),
            new ImVec4(255,   0, 255, 255),
            new ImVec4(  0,   0, 255, 255),
            new ImVec4(  0, 255,   0, 255),
            new ImVec4(  0, 255, 255, 255),
            new ImVec4(255, 255, 255, 255),
            new ImVec4(128, 128, 128, 255),
    };

    private final float collapsedHeight = 25f;
    private final float panelHeight = 180f;

    private float currentPanelExtraHeight = 0f;
    private float currentPanelAlpha = 0f;
    private float animationSpeed = 10f;

    @Override
    public void draw() {
        if (destroyed) return;

        ImVec2 origin = ImGui.getCursorScreenPos();
        float baseX = origin.x + position.x;
        float baseY = origin.y + position.y;

        float deltaTime = ImGui.getIO().getDeltaTime();

        // Animação de abrir/fechar
        if(expanded) {
            currentPanelExtraHeight += (panelHeight - currentPanelExtraHeight) * deltaTime * animationSpeed;
            currentPanelAlpha += (1f - currentPanelAlpha) * deltaTime * animationSpeed;
        } else {
            currentPanelExtraHeight -= currentPanelExtraHeight * deltaTime * animationSpeed * 3;
            currentPanelAlpha -= currentPanelAlpha * deltaTime * animationSpeed * 3;
        }
        if(currentPanelExtraHeight < 0) currentPanelExtraHeight = 0;
        if(currentPanelAlpha < 0) currentPanelAlpha = 0;

        // Animações suaves da cor
        currentHue += (hue - currentHue) * deltaTime * colorAnimationSpeed;
        currentSaturation += (saturation - currentSaturation) * deltaTime * colorAnimationSpeed;
        currentBrightness += (brightness - currentBrightness) * deltaTime * colorAnimationSpeed;

        selectedColor = HSVtoRGB(currentHue, currentSaturation, currentBrightness);

        float totalHeight = collapsedHeight + currentPanelExtraHeight;

        ImVec2 minPos = new ImVec2(baseX, baseY);
        ImVec2 maxPos = new ImVec2(baseX + size.x, baseY + totalHeight);

        ImDrawList drawList = ImGui.getWindowDrawList();

        // Label
        float textOffsetX = 0f;
        float textOffsetY = 5f;
        ImVec2 labelPos = new ImVec2(minPos.x + textOffsetX, minPos.y + textOffsetY);
        int textColor = ColorConverter.colorToInt(200, 200, 200, 255);
        drawList.addText(labelPos, textColor, label);

        // Preview (quadrado de cor)
        float previewWidth = 40f;
        float previewHeight = collapsedHeight - (textOffsetY * 2f);
        ImVec2 previewMin = new ImVec2(
                maxPos.x - previewWidth - 5f,
                minPos.y + (collapsedHeight - previewHeight) / 2f
        );
        ImVec2 previewMax = new ImVec2(
                previewMin.x + previewWidth,
                previewMin.y + previewHeight
        );
        int col = ColorConverter.colorToInt(
                (int) selectedColor.x,
                (int) selectedColor.y,
                (int) selectedColor.z,
                (int) selectedColor.w
        );
        drawList.addRectFilled(previewMin, previewMax, col, 4f, 0);

        // Clique no preview -> Expande/contrai
        if (ImGui.isMouseHoveringRect(previewMin.x, previewMin.y, previewMax.x, previewMax.y)
                && ImGui.isMouseClicked(0)) {
            expanded = !expanded;
        }

        // Painel expandido
        if(currentPanelAlpha > 0.01f) {
            float panelX = baseX;
            float panelY = baseY + collapsedHeight;
            drawColorPanel(drawList, panelX, panelY, size.x, currentPanelExtraHeight, currentPanelAlpha);
        }

        // Ajusta cursor para continuar layout
        ImGui.setCursorScreenPos(new ImVec2(baseX, baseY + totalHeight));
    }

    private void drawColorPanel(ImDrawList drawList, float x, float y, float totalWidth, float animHeight, float panelAlpha) {
        float usedWidth = 250f;
        float hueBarHeight = 20f;

        // Originalmente: spacing = 0f
        // Vamos definir DOIS espaçamentos:
        // - hueToSquareSpacing = espaço entre a Hue bar e o quadrado
        // - squareToSwatchesSpacing = espaço entre o quadrado e os presets
        float hueToSquareSpacing = 2f;           // Ajuste a gosto
        float colorAreaVerticalOffset = 5f;      // Ajuste a gosto
        float squareToSwatchesSpacing = -20f;      // Deixe 0f ou 1f p/ ficar bem perto

        // Mantemos o offset para o canto direito
        float offsetX = totalWidth - usedWidth - 5f;
        if(offsetX < 0) offsetX = 0;
        float baseX = x + offsetX;

        // Hue bar
        ImVec2 hueBarMin = new ImVec2(baseX, y);
        ImVec2 hueBarMax = new ImVec2(baseX + usedWidth, y + hueBarHeight);

        // Quadrado de cor (sat/bright)
        float colorAreaHeight = 100f;
        ImVec2 colorAreaMin = new ImVec2(baseX, hueBarMax.y + hueToSquareSpacing + colorAreaVerticalOffset);
        ImVec2 colorAreaMax = new ImVec2(baseX + usedWidth, colorAreaMin.y + colorAreaHeight);

        // Swatches (bem perto do quadrado, dependendo do squareToSwatchesSpacing)
        ImVec2 swatchAreaMin = new ImVec2(baseX, colorAreaMax.y + squareToSwatchesSpacing);
        ImVec2 swatchAreaMax = new ImVec2(baseX + usedWidth, y + animHeight);

        // Desenhos
        drawHueBar(drawList, hueBarMin, hueBarMax, panelAlpha);
        drawSatBrightArea(drawList, colorAreaMin, colorAreaMax, panelAlpha);
        drawIndicators(drawList, hueBarMin, hueBarMax, colorAreaMin, colorAreaMax, panelAlpha);
        handleInput(hueBarMin, hueBarMax, colorAreaMin, colorAreaMax);

        // Atualiza cor
        selectedColor = HSVtoRGB(currentHue, currentSaturation, currentBrightness);

        // Desenha swatches
        drawSwatches(drawList, swatchAreaMin, swatchAreaMax, panelAlpha);
    }

    // Demais métodos (igual ao seu código atual):

    private void drawHueBar(ImDrawList drawList, ImVec2 hueBarMin, ImVec2 hueBarMax, float alpha) {
        int numSegments = 6;
        float barWidth = hueBarMax.x - hueBarMin.x;
        float barHeight = hueBarMax.y - hueBarMin.y;
        float segmentWidth = barWidth / numSegments;
        for (int i = 0; i < numSegments; i++) {
            float h1 = i * (360f / numSegments);
            float h2 = (i + 1) * (360f / numSegments);
            ImVec4 col1 = HSVtoRGB(h1, 1f, 1f);
            ImVec4 col2 = HSVtoRGB(h2, 1f, 1f);
            int c1 = ColorConverter.colorToInt((int) col1.x, (int) col1.y, (int) col1.z, (int)(255 * alpha));
            int c2 = ColorConverter.colorToInt((int) col2.x, (int) col2.y, (int) col2.z, (int)(255 * alpha));
            float xMin = hueBarMin.x + i * segmentWidth;
            float xMax = xMin + segmentWidth;
            drawList.addRectFilledMultiColor(
                    new ImVec2(xMin, hueBarMin.y),
                    new ImVec2(xMax, hueBarMin.y + barHeight),
                    c1, c2, c2, c1
            );
        }
    }

    private void drawSatBrightArea(ImDrawList drawList, ImVec2 areaMin, ImVec2 areaMax, float alpha) {
        ImVec4 pureHue = HSVtoRGB(currentHue, 1f, 1f);
        int colWhite = ColorConverter.colorToInt(255, 255, 255, (int)(255 * alpha));
        int colPureHue = ColorConverter.colorToInt((int) pureHue.x, (int) pureHue.y, (int) pureHue.z, (int)(255 * alpha));
        drawList.addRectFilledMultiColor(
                areaMin, areaMax,
                colWhite,    // top-left
                colPureHue,  // top-right
                colPureHue,  // bottom-right
                colWhite     // bottom-left
        );
        int transparent = ColorConverter.colorToInt(0, 0, 0, 0);
        int black = ColorConverter.colorToInt(0, 0, 0, (int)(255 * alpha));
        drawList.addRectFilledMultiColor(
                areaMin, areaMax,
                transparent,
                transparent,
                black,
                black
        );
    }

    private void drawIndicators(ImDrawList drawList, ImVec2 hueBarMin, ImVec2 hueBarMax, ImVec2 areaMin, ImVec2 areaMax, float alpha) {
        float hueRatio = currentHue / 360f;
        float hueX = hueBarMin.x + hueRatio * (hueBarMax.x - hueBarMin.x);
        int markerColor = ColorConverter.colorToInt(255, 255, 255, (int)(255 * alpha));
        drawList.addLine(hueX, hueBarMin.y, hueX, hueBarMax.y, markerColor, 2f);

        float satX = areaMin.x + currentSaturation * (areaMax.x - areaMin.x);
        float brightY = areaMin.y + (1f - currentBrightness) * (areaMax.y - areaMin.y);
        drawList.addCircle(satX, brightY, 5f, markerColor, 12, 2f);
    }

    private void handleInput(ImVec2 hueBarMin, ImVec2 hueBarMax, ImVec2 areaMin, ImVec2 areaMax) {
        float mouseX = ImGui.getMousePosX();
        float mouseY = ImGui.getMousePosY();
        // Hue
        if (ImGui.isMouseHoveringRect(hueBarMin.x, hueBarMin.y, hueBarMax.x, hueBarMax.y)
                && ImGui.isMouseDown(0)) {
            float ratio = (mouseX - hueBarMin.x) / (hueBarMax.x - hueBarMin.x);
            hue = clamp(ratio, 0f, 1f) * 360f;
        }
        // Saturation / Brightness
        if (ImGui.isMouseHoveringRect(areaMin.x, areaMin.y, areaMax.x, areaMax.y)
                && ImGui.isMouseDown(0)) {
            float satRatio = (mouseX - areaMin.x) / (areaMax.x - areaMin.x);
            saturation = clamp(satRatio, 0f, 1f);
            float brightRatio = (mouseY - areaMin.y) / (areaMax.y - areaMin.y);
            brightness = 1f - clamp(brightRatio, 0f, 1f);
        }
    }

    private void drawSwatches(ImDrawList drawList, ImVec2 swatchAreaMin, ImVec2 swatchAreaMax, float alpha) {
        int transparentColor = ColorConverter.colorToInt(25, 25, 25, 0);
        drawList.addRectFilled(swatchAreaMin, swatchAreaMax, transparentColor, 0, 0);

        int count = SWATCH_COLORS.length;
        float spacing = 4f;
        float availableWidth = swatchAreaMax.x - swatchAreaMin.x;
        float availableHeight = swatchAreaMax.y - swatchAreaMin.y;
        float totalSpacing = spacing * (count - 1);
        float swatchWidth = (availableWidth - totalSpacing) / count;
        float swatchHeight = 20f;
        float yMin = swatchAreaMin.y + (availableHeight - swatchHeight) / 2f;
        float yMax = yMin + swatchHeight;
        for (int i = 0; i < count; i++) {
            float xMinF = swatchAreaMin.x + i * (swatchWidth + spacing);
            float xMaxF = xMinF + swatchWidth;
            float xMin = (float)Math.floor(xMinF);
            float xMax = (float)Math.floor(xMaxF);
            float yMinAligned = (float)Math.floor(yMin);
            float yMaxAligned = (float)Math.floor(yMax);
            ImVec4 c = SWATCH_COLORS[i];
            int col = ColorConverter.colorToInt((int)c.x, (int)c.y, (int)c.z, (int)(c.w * alpha));
            drawList.addRectFilled(new ImVec2(xMin, yMinAligned), new ImVec2(xMax, yMaxAligned), col, 6f, 0);
            int borderCol = ColorConverter.colorToInt(40, 40, 40, (int)(0 * alpha));
            drawList.addRect(new ImVec2(xMin, yMinAligned), new ImVec2(xMax, yMaxAligned), borderCol, 6f, 0, 1f);
            if (ImGui.isMouseHoveringRect(xMin, yMinAligned, xMax, yMaxAligned) && ImGui.isMouseDown(0)) {
                setColorFromRGB(c);
            }
        }
    }

    private float clamp(float val, float min, float max) {
        return (val < min) ? min : (val > max ? max : val);
    }

    private void setColorFromRGB(ImVec4 col) {
        float r = col.x / 255f;
        float g = col.y / 255f;
        float b = col.z / 255f;
        float[] hsv = rgbToHSV(r, g, b);
        hue = hsv[0];
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
            h = 0;
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
        if (h < 60) { r1 = c; g1 = x; b1 = 0; }
        else if (h < 120) { r1 = x; g1 = c; b1 = 0; }
        else if (h < 180) { r1 = 0; g1 = c; b1 = x; }
        else if (h < 240) { r1 = 0; g1 = x; b1 = c; }
        else if (h < 300) { r1 = x; g1 = 0; b1 = c; }
        else { r1 = c; g1 = 0; b1 = x; }
        int rr = (int)((r1 + m) * 255);
        int gg = (int)((g1 + m) * 255);
        int bb = (int)((b1 + m) * 255);
        return new ImVec4(rr, gg, bb, 255);
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
