package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Function {
    private Service service;
    private String name;
}
