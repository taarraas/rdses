package ua.kiev.univ.master.tree;

import javax.swing.*;
import javax.swing.tree.TreeCellEditor;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author jamanal
 * @version Nov 21, 2010
 */
public class CheckBoxTreeCellEditor extends AbstractCellEditor implements TreeCellEditor {

    private SelectionModel selectionModel;

    public CheckBoxTreeCellEditor(SelectionModel selectionModel) {
        this.selectionModel = selectionModel;
    }

    @Override
    public Component getTreeCellEditorComponent(final JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
        if (!(value instanceof Directory))
            return tree.getCellRenderer().getTreeCellRendererComponent(tree, value, true, expanded, leaf, row, true);
        
        final Directory directory = (Directory) value;
        CheckBoxTreeCellRenderer renderer = (CheckBoxTreeCellRenderer) tree.getCellRenderer();
        Component editor = renderer.getTreeCellRendererComponent(tree, value, true, expanded, leaf, row, true);
        ItemListener itemListener = new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent) {
                JCheckBox checkbox = (JCheckBox) itemEvent.getItem();
                if (checkbox.isSelected())
                    selectionModel.select(directory);
                else
                    selectionModel.unselect(directory);
                stopCellEditing();
                checkbox.removeItemListener(this);
//                tree.getSelectionModel().clearSelection();
            }
        };
        if (editor instanceof JCheckBox) {
            ((JCheckBox) editor).addItemListener(itemListener);
        }
        return editor;
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }
}
