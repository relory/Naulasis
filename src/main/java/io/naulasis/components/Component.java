package io.naulasis.components;

import lombok.Getter;
import lombok.Setter;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@Getter @Setter
public abstract class Component {
    public void draw(){}
    public  void destroy(){}

    public void onKeyboardChar(char key) {}
    public void onKeyboardInt(int key) {}

    private KeyListener keyListener;

    public void setKeyListener(KeyListener listener) {
        this.keyListener = listener;
    }

    public void keyPressed(KeyEvent e) {
        if (keyListener != null) {
            keyListener.keyPressed(e);
        }
    }

    public void keyReleased(KeyEvent e) {
        if (keyListener != null) {
            keyListener.keyReleased(e);
        }
    }

    public void keyTyped(KeyEvent e) {
        if (keyListener != null) {
            keyListener.keyTyped(e);
        }
    }
}