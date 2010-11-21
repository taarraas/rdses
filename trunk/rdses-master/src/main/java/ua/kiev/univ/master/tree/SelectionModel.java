package ua.kiev.univ.master.tree;

import java.util.HashSet;
import java.util.Set;

/**
 * @author jamanal
 * @version Nov 21, 2010
 */
public class SelectionModel {

    private Set<Directory> selectedDirectories = new HashSet<Directory>();

    public boolean isSelected(Directory directory) {
        return selectedDirectories.contains(directory);
    }

    public void select(Directory directory) {
        selectedDirectories.add(directory);
    }

    public void unselect(Directory directory) {
        selectedDirectories.remove(directory);
    }

    public boolean isEmpty() {
        return selectedDirectories.isEmpty();
    }

    public Set<Directory> getSelectedDirectories() {
        return selectedDirectories;
    }
}
