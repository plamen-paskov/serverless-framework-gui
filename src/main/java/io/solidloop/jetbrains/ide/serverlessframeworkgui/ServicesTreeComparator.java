package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Comparator;

public class ServicesTreeComparator implements Comparator<DefaultMutableTreeNode> {
    @Override
    public int compare(final DefaultMutableTreeNode defaultMutableTreeNode, final DefaultMutableTreeNode t1) {
        if (defaultMutableTreeNode.getUserObject() instanceof Service) {
            return ((Service) defaultMutableTreeNode.getUserObject()).compareTo((Service) t1.getUserObject());
        }

        return defaultMutableTreeNode.getUserObject().toString().compareTo(t1.getUserObject().toString());
    }
}