package ua.kiev.univ.master.tree;

import ua.kiev.univ.slave.FileService;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * @author jamanal
 * @version Nov 21, 2010
 */
public class FileSystemTreeModel implements TreeModel {

    private FileSystem root;

    public FileSystemTreeModel(FileService fileService) {
        this.root = new FileSystem(fileService);
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        return getChildren(parent)[index];
    }

    @Override
    public int getChildCount(Object parent) {
        return getChildren(parent).length;
    }

    @Override
    public boolean isLeaf(Object node) {
        return getChildren(node).length == 0;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        Directory[] children = getChildren(parent);
        for (int i = 0; i < children.length; i++)
            if (children[i].equals(child))
                return i;
        return -1;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
    }

    private Directory[] getChildren(Object parent) {
        if (parent instanceof FileSystem)
            return ((FileSystem) parent).listDrives();
        return ((Directory) parent).getChildren();
    }
}
