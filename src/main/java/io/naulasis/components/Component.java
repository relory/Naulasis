package io.naulasis.components;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class Component {
    public void draw(){}
    public abstract void build();
    public abstract void destroy();
}