package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.vfs.VirtualFile;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class ServiceFactory {
    private static final String SERVICE = "service";
    private static final String FUNCTIONS = "functions";
    private static final String PROVIDER = "provider";
    private static final String STAGE = "stage";
    private static final String REGION = "region";
    @NonNull
    private ObjectMapper objectMapper;

    public Service create(VirtualFile serverlessYaml) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(serverlessYaml.getInputStream());
        Set<String> functions = getFunctions(jsonNode);

        Service service = new Service();
        service.setName(jsonNode.get(SERVICE).textValue());
        service.setFile(serverlessYaml);
        service.setFunctions(functions);

        if (jsonNode.has(PROVIDER)) {
            JsonNode provider = jsonNode.get(PROVIDER);

            if (provider.has(STAGE)) {
                service.setStage(provider.get(STAGE).textValue());
            }

            if (provider.has(REGION)) {
                service.setRegion(provider.get(REGION).textValue());
            }
        }

        return service;
    }

    private Set<String> getFunctions(JsonNode jsonNode) {
        Set<String> functions = new HashSet<>();

        if (jsonNode.has(FUNCTIONS)) {
            jsonNode.get(FUNCTIONS).fields().forEachRemaining(function -> functions.add(function.getKey()));
        }

        return functions;
    }
}
