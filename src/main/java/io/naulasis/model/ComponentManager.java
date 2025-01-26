package io.naulasis.model;

import io.naulasis.model.impl.CheckBox;
import io.naulasis.model.impl.TextInput;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class ComponentManager {

    private static ComponentManager instance;
    private final List<Component> Components = new ArrayList<>();

    private ComponentManager() {
        Components.addAll(Arrays.asList(
                new TextInput(),
                new CheckBox()
        ));
    }

    public static ComponentManager getInstance() {
        if (instance == null) {
            instance = new ComponentManager();
        }

        return instance;
    }
}
