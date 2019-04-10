package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Set;

public class ServiceNodeFactory {
    public DefaultMutableTreeNode create(Service service) {
        DefaultMutableTreeNode serviceNode = new DefaultMutableTreeNode(service);

        Set<String> functions = service.getFunctions();
        if (functions != null) {
            for (String functionName : service.getFunctions()) {
                serviceNode.add(new DefaultMutableTreeNode(new Function(service, functionName)));
            }
        }

        return serviceNode;
    }
}
