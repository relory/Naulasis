package io.naulasis.components;

import lombok.Getter;
import lombok.Setter;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@Getter @Setter
public abstract class Component {
    public void draw() {
    }

    public  void destroy() {
    }

    public void onKeyboardChar(char key) {
    }

    public void onKeyboardInt(int key) {
    }
}