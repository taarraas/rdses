package ua.kiev.univ.master;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ua.kiev.univ.master.tree.CheckBoxTreeCellEditor;
import ua.kiev.univ.master.tree.CheckBoxTreeCellRenderer;
import ua.kiev.univ.master.tree.FileSystemTreeModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author jamanal
 * @version Nov 21, 2010
 */
public class MainForm implements Observer {

    private JList slavesList;
    private JTree filesystemTree;
    private JPanel rootPanel;
    private JButton startButton;
    private JList extensionsList;
    private JLabel statusLabel;
    private JList processedFiles;

    public MainForm(ApplicationContext context) {
        init();
        Observable slaveRegistry = (Observable) context.getBean("slaveRegistry");
        slaveRegistry.addObserver(this);
        OutputQueueListener outputQueueListener = (OutputQueueListener) context.getBean("outputQueueListener");
        outputQueueListener.addObserver(this);
    }

    public void init() {
        startButton.setEnabled(false);
        extensionsList.setEnabled(false);
        filesystemTree.setEnabled(false);

        slavesList.setModel(new DefaultListModel());
        slavesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        slavesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                SlaveConfig slaveConfig = (SlaveConfig) slavesList.getSelectedValue();
                if (slaveConfig == null)
                    return;
                filesystemTree.setModel(new FileSystemTreeModel(slaveConfig.getFileService()));
                filesystemTree.setCellRenderer(new CheckBoxTreeCellRenderer(slaveConfig.getSelectedDirectoriesModel()));
                filesystemTree.setCellEditor(new CheckBoxTreeCellEditor(slaveConfig.getSelectedDirectoriesModel()));
                setPreparingMode();
            }
        });

/*
        SelectionModel selectionModel = new SelectionModel();
        filesystemTree.setCellRenderer(new CheckBoxTreeCellRenderer(selectionModel));
        filesystemTree.setCellEditor(new CheckBoxTreeCellEditor(selectionModel));
*/
        filesystemTree.setEditable(true);
        filesystemTree.setModel(new DefaultTreeModel(null));

        DefaultListModel filesModel = new DefaultListModel();
        processedFiles.setModel(filesModel);
        processedFiles.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    File file = (File) processedFiles.getSelectedValue();
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        DefaultListModel extensionsModel = new DefaultListModel();
        extensionsList.setModel(extensionsModel);
        extensionsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                SlaveConfig slaveConfig = (SlaveConfig) slavesList.getSelectedValue();
                if (slaveConfig == null)
                    return;
                Set<String> extensions = slaveConfig.getSelectedExtension();
                extensions.clear();
                for (Object ext: extensionsList.getSelectedValues()) {
                    String extension = (String) ext;
                    extensions.add(extension);
                }
            }
        });
        extensionsModel.add(0, "doc");
        extensionsModel.add(0, "rtf");
        extensionsModel.add(0, "txt");
        extensionsModel.add(0, "pdf");
        extensionsModel.add(0, "html");

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SlaveConfig slaveConfig = (SlaveConfig) slavesList.getSelectedValue();
                if (slaveConfig == null)
                    return;
                if (startButton.getText().equals("Start")) {
                    slaveConfig.startSearch();
                } else {
                    slaveConfig.stopSearch();
                }
            }
        });
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof OutputQueueListener) {
            DefaultListModel model = (DefaultListModel) processedFiles.getModel();
            model.add(0, (File) o);
        } else
        if (observable instanceof SlaveRegistryImpl) {
            String ip = (String) o;
            DefaultListModel model = (DefaultListModel) slavesList.getModel();
            SlaveConfig slaveConfig = new SlaveConfig(ip);
            if (!model.contains(slaveConfig)) {
                model.add(model.size(), slaveConfig);
                slaveConfig.addObserver(this);
            }
        } else {
            statusLabel.setText("Status: " + o);
        }
    }

    public void show() {
        JFrame frame = new JFrame("RDSES Master");
//        280
        frame.setContentPane(rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setBounds(frame.getX(), frame.getY(), 1000, 500);
        setOnScreenCenter(frame);
        frame.setVisible(true);
    }

    private static void setOnScreenCenter(JFrame frame) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int w = frame.getSize().width;
        int h = frame.getSize().height;
        int x = (dim.width-w)/2;
        int y = (dim.height-h)/2;
        frame.setLocation(x, y);
    }
    
    private void setRunningMode() {
        startButton.setText("Stop");
        extensionsList.setEnabled(false);
        filesystemTree.setEnabled(false);
    }

    private void setPreparingMode() {
        startButton.setEnabled(true);
        extensionsList.setEnabled(true);
        filesystemTree.setEnabled(true);
    }
}
