package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.vfs.VirtualFile;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
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

    private Map<VirtualFile, Service> cache = new HashMap<>();

    public Service create(VirtualFile file) {
        JsonNode jsonNode = parseServerlessYaml(file);
        Service service = getOrCreateService(file);

        String name = null;
        String stage = null;
        String region = null;
        Set<String> functions = null;

        if (jsonNode != null) {
            if (jsonNode.has(SERVICE)) {
                name = jsonNode.get(SERVICE).textValue();
            }

            if (jsonNode.has(PROVIDER)) {
                JsonNode provider = jsonNode.get(PROVIDER);

                if (provider.has(STAGE)) {
                    stage = provider.get(STAGE).textValue();
                }

                if (provider.has(REGION)) {
                    region = provider.get(REGION).textValue();
                }
            }

            if (jsonNode.has(FUNCTIONS)) {
                functions = getFunctions(jsonNode);
            }
        }

        service.setFile(file);
        service.setName(name);
        service.setStage(stage);
        service.setRegion(region);
        service.setFunctions(functions);

        return service;
    }

    private Service getOrCreateService(VirtualFile file) {
        Service service = cache.get(file);
        if (service == null) {
            service = new Service();
            cache.put(file, service);
        }
        return service;
    }

    private JsonNode parseServerlessYaml(VirtualFile file) {
        try {
            return objectMapper.readTree(file.getInputStream());
        } catch (IOException ignored) {
            return null;
        }
    }

    private Set<String> getFunctions(JsonNode jsonNode) {
        Set<String> functions = new LinkedHashSet<>();
        jsonNode.get(FUNCTIONS).fields().forEachRemaining(function -> functions.add(function.getKey()));
        return functions;
    }
}
