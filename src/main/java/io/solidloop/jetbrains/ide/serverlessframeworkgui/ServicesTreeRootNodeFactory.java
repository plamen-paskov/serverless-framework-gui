package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.util.ui.tree.TreeUtil;
import lombok.AllArgsConstructor;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Set;

@AllArgsConstructor
public class ServicesTreeRootNodeFactory {
    private ServiceNodeFactory serviceNodeFactory;
    private ServicesTreeComparator servicesTreeComparator;

    public DefaultMutableTreeNode create(Set<Service> services) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
        services.forEach(service -> rootNode.add(serviceNodeFactory.createOrUpdate(service)));
        TreeUtil.sort(rootNode, servicesTreeComparator);

        return rootNode;
    }
}
