/*
 * QuickQuiz
 * Copyright (C) 2005 David C. Briccetti
 * www.davebsoft.com
 *
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation; either version 2 of the License, or 
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software Foundation, 
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package com.dbschools.quickquiz.client.giver;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import com.dbschools.gui.JwsClipboardUtil;

/**
 * Provides a dialog for collecting a set of questions.
 * 
 * @author David C. Briccetti
 */
public class QuestionsDialog extends javax.swing.JDialog {
    private static final long serialVersionUID = 1123834263972529116L;
    private String[] names;

    /** Creates new form NamesDialog */
    QuestionsDialog(final java.awt.Frame parent, final boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private final void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaNames = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jButtonOK = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuEdit = new javax.swing.JMenu();
        menuEditPaste = new javax.swing.JMenuItem();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setTitle(java.util.ResourceBundle.getBundle("quickquiz").getString("questionsDialogTitle"));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public final void windowClosing(final java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel2.setText(java.util.ResourceBundle.getBundle("quickquiz").getString("questionsInstructions"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 0);
        getContentPane().add(jLabel2, gridBagConstraints);

        jTextAreaNames.setColumns(35);
        jTextAreaNames.setRows(15);
        jScrollPane1.setViewportView(jTextAreaNames);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 4, 4);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        jButtonOK.setText(java.util.ResourceBundle.getBundle("quickquiz").getString("ok"));
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public final void actionPerformed(final java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });

        jPanel2.add(jButtonOK);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        getContentPane().add(jPanel2, gridBagConstraints);

        menuEdit.setText(java.util.ResourceBundle.getBundle("quickquiz").getString("edit"));
        menuEditPaste.setText(java.util.ResourceBundle.getBundle("quickquiz").getString("paste"));
        menuEditPaste.addActionListener(new java.awt.event.ActionListener() {
            public final void actionPerformed(final java.awt.event.ActionEvent evt) {
                menuEditPasteActionPerformed(evt);
            }
        });

        menuEdit.add(menuEditPaste);

        jMenuBar1.add(menuEdit);

        setJMenuBar(jMenuBar1);

        pack();
    }//GEN-END:initComponents

    private final void menuEditPasteActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditPasteActionPerformed
        try {
            jTextAreaNames.setText(JwsClipboardUtil.getStringFromJwsOrSystemClipboard());
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_menuEditPasteActionPerformed

    private final void jButtonOKActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
        // Add your handling code here:
        final String text = jTextAreaNames.getText();
        if (text.trim().length() > 0) {
            names = text.split("\n");
        } else {
			names = new String[0];
        }
        
        setVisible(false);
    }//GEN-LAST:event_jButtonOKActionPerformed
    
    /** Closes the dialog */
    private final void closeDialog(final java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
    }//GEN-LAST:event_closeDialog
    
    final String[] getQuestions() {
        return this.names;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonOK;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaNames;
    private javax.swing.JMenu menuEdit;
    private javax.swing.JMenuItem menuEditPaste;
    // End of variables declaration//GEN-END:variables

}