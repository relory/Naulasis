package io.naulasis;

import io.naulasis.components.Checkbox;
import io.naulasis.components.TextInput;
import lombok.Getter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class componentsManager {
    private static componentsManager instance;
    @Getter
    private final List<component> components = new ArrayList<>();

    private componentsManager() {
        if (components.isEmpty()) {
            components.addAll(Arrays.asList(
                    new TextInput(),
                    new Checkbox()
            ));
        }
    }

    public static componentsManager getInstance() {
        if (instance == null) {
            instance = new componentsManager();
        }
        return instance;
    }
}
