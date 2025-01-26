package io.naulasis.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class Component {
    public abstract void draw();

    public void onKeyboardChar(char key) {}
    public void onKeyboardInt(int key) {}
}