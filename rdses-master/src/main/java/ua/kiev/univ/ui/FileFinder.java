/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ProgressDialog.java
 *
 * Created on Mar 11, 2011, 9:30:39 AM
 */

package ua.kiev.univ.ui;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
 *
 * @author taras
 */
public class FileFinder extends javax.swing.JDialog {
    String dir;
    FileFilter filter;
    ArrayList<File> files = new ArrayList<File>();
    int processed;
    class FileFinderThread extends Thread {
        private void recurseSearch(File dir) {
            if (dir.isDirectory()) {
                incrementProcessed(dir);
                for (File file : dir.listFiles()) {
                    recurseSearch(file);
                }
            } else {
                if (filter.accept(dir)) {
                    addFile(dir);
                }
            }
        }

        @Override
        public void run() {
            recurseSearch(new File(dir));
            continueButton.setEnabled(true);
            jLabel1.setText("����� ���������");
            nowProcessing.setText("");
        }        
    }
    FileFinderThread finder = new FileFinderThread();
    private void addFile(File file) {
        files.add(file);
        jTextArea1.append(file.toString()+"\n");
    }
    private void incrementProcessed(File file) {
        processed++;
        if (processed % 100 == 0) {
            nowProcessing.setText(file.toString());
        }
    }
    /** Creates new form ProgressDialog */
    public FileFinder(JFrame owner, String dir, FileFilter filter) {
        super(owner, "����� �����", true);
        this.filter = filter;
        this.dir = dir;
        initComponents();
        nowProcessing.setText(dir);
        finder.start();
    }
    public File[] getFiles() {
        processed = 0;
        files.clear();
        setVisible(true);
        return files.toArray(new File[files.size()]);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        continueButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        nowProcessing = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        continueButton.setText("����������");
        continueButton.setEnabled(false);
        continueButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                continueButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("��������� ����-�����, ��� ����� �����");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        nowProcessing.setText("jLabel2");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(nowProcessing, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(continueButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(continueButton)
                    .addComponent(nowProcessing))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void continueButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_continueButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_continueButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton continueButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel nowProcessing;
    // End of variables declaration//GEN-END:variables

}
