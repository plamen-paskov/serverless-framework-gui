package io.solidloop.jetbrains.ide.serverlessframeworkgui.function;

import io.solidloop.jetbrains.ide.serverlessframeworkgui.service.Service;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Function {
    private Service service;
    private String name;

    @Override
    public String toString() {
        return name;
    }
}
