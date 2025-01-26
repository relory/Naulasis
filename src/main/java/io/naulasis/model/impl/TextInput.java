package io.naulasis.model.impl;

import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImDrawFlags;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiKey;
import imgui.type.ImString;
import io.naulasis.model.Component;
import io.naulasis.utils.ColorConverter;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class TextInput extends Component {

    private ImString value = new ImString("test");
    private static boolean focused = false;
    private int cursorPosition = 0;

    @Override
    public void draw() {
        ImDrawList drawList = ImGui.getWindowDrawList();

        drawList.addRectFilled(new ImVec2(100, 100), new ImVec2(225, 130), ColorConverter.colorToInt(50, 50, 50, 255), 6f, ImDrawFlags.RoundCornersAll);
        drawList.addText(ImGui.getFont(), 20, new ImVec2(137, 105), ColorConverter.colorToInt(150, 150, 150, 255), value.get());
        drawList.addRect(new ImVec2(100, 100), new ImVec2(225, 130), ColorConverter.colorToInt(75, 75, 75, 255), 6f, ImDrawFlags.RoundCornersAll, 0.5f);

        ImGui.setNextWindowPos(new ImVec2(100, 100), ImGuiCond.Always, new ImVec2(0.5f, 0.5f));
        ImGui.setNextWindowSize(new ImVec2(125, 50), ImGuiCond.Always);
        ImGui.setNextWindowFocus();

        // TODO -> fix this logic
        if (ImGui.isMouseHoveringRect(new ImVec2(100, 100), new ImVec2(225, 130))) {
            if (ImGui.isMouseClicked(0)) {
                focused = true;
            }
        } else {
            focused = false;
        }

        // TODO -> Fix this logic
        if (focused) {
            setKeyListener(new KeyListener() {
                @Override
                public void keyPressed(KeyEvent e) {
                    System.out.println("hello " + e.getKeyCode());
                    if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                        if (cursorPosition > 0) {
                            value.set(value.get().substring(0, value.get().length() - 1));
                            cursorPosition--;
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                        if (cursorPosition < value.get().length()) {
                            value.set(value.get().substring(0, cursorPosition) + value.get().substring(cursorPosition + 1));
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        if (cursorPosition > 0) {
                            cursorPosition--;
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        if (cursorPosition < value.get().length()) {
                            cursorPosition++;
                        }
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }

                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    value.set(value.get().substring(0, cursorPosition) + c + value.get().substring(cursorPosition));
                    cursorPosition++;

                    System.out.println("ni " + c);
                }
            });
        }
    }
}