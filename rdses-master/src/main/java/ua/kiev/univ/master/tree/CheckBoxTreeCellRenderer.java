package ua.kiev.univ.master.tree;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

/**
 * @author jamanal
 * @version Nov 21, 2010
 */
public class CheckBoxTreeCellRenderer implements TreeCellRenderer {

    private SelectionModel selectionModel;
    private DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();
    private JCheckBox renderer = new JCheckBox();
    private Color selectionForeground, selectionBackground, textForeground, textBackground;

    public CheckBoxTreeCellRenderer(SelectionModel selectionModel) {
        this.selectionModel = selectionModel;
        renderer.setFont(UIManager.getFont("Tree.font"));
        renderer.setFocusPainted((Boolean) UIManager.get("Tree.drawsFocusBorderAroundIcon"));
        selectionForeground = UIManager.getColor("Tree.selectionForeground");
        selectionBackground = UIManager.getColor("Tree.selectionBackground");
        textForeground = UIManager.getColor("Tree.textForeground");
        textBackground = UIManager.getColor("Tree.textBackground");
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
                                                  boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (!(value instanceof Directory))
            return defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        String stringValue = tree.convertValueToText(value, selected, expanded, leaf, row, false);
        renderer.setText(stringValue);
        renderer.setSelected(selectionModel.isSelected((Directory) value));
        renderer.setEnabled(tree.isEnabled());
        if (selected) {
            renderer.setForeground(selectionForeground);
            renderer.setBackground(selectionBackground);
        } else {
            renderer.setForeground(textForeground);
            renderer.setBackground(textBackground);
        }
        return renderer;
    }
}
