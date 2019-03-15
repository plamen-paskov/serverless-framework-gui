package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import javax.swing.tree.DefaultMutableTreeNode;

public class ServiceNodeFactory {
    public DefaultMutableTreeNode create(Service service) {
        DefaultMutableTreeNode serviceNode = new DefaultMutableTreeNode(service);

        for (String functionName : service.getFunctions()) {
            serviceNode.add(new DefaultMutableTreeNode(new Function(service, functionName)));
        }

        return serviceNode;
    }
}
