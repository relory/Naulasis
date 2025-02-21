package io.naulasis.components;

import io.naulasis.components.impl.Checkbox;
import io.naulasis.components.impl.TextInput;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class ComponentManager {

    private static ComponentManager instance;
    private final List<Component> components = new ArrayList<>();

    private ComponentManager() {
        components.addAll(Arrays.asList(
                new TextInput(),
                new Checkbox()
        ));
    }

    /**
     * Allows you to register a new component from an external project
     * @param component to identify the component you want to register!
     */
    public void registerComponent(Component component) {
        if (!components.contains(component)) {
            components.add(component);
        }
    }

    /**
     * Getting the instance of the component manager
     * @return ComponentManager.java
     */
    public static ComponentManager getInstance() {
        if (instance == null) {
            instance = new ComponentManager();
        }

        return instance;
    }
}
