package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ServiceNodeFactory {
    private Map<Service, DefaultMutableTreeNode> cache = new HashMap<>();

    public DefaultMutableTreeNode createOrUpdate(Service service) {
        DefaultMutableTreeNode serviceNode = cache.get(service);
        if (serviceNode == null) {
            serviceNode = new DefaultMutableTreeNode(service);
            cache.put(service, serviceNode);
        }
        attachFunctionNodes(serviceNode, service);
        return serviceNode;
    }

    private void attachFunctionNodes(DefaultMutableTreeNode serviceNode, Service service) {
        if (serviceNode.getChildCount() > 0) {
            serviceNode.removeAllChildren();
        }

        Set<String> functions = service.getFunctions();
        if (functions != null) {
            for (String functionName : service.getFunctions()) {
                serviceNode.add(new DefaultMutableTreeNode(new Function(service, functionName)));
            }
        }
    }
}
