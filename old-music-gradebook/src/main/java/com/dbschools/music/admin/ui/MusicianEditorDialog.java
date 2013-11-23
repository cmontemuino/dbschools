package com.dbschools.music.admin.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import com.dbschools.music.admin.ui.MusicianEditor.GroupAndInstrument;
import com.dbschools.music.orm.Musician;
import com.dbschools.music.orm.NamedItem;

import javax.swing.*;

/**
 * Dialog for editing a musician. Contains a {@link MusicianEditor}.
 * 
 * @author David C. Briccetti
 */
public class MusicianEditorDialog extends javax.swing.JDialog {
    private static final long serialVersionUID = 8927533996058041875L;
    private boolean saved;

    /** Creates new form MusicianEditorDialog */
    public MusicianEditorDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public boolean isSaved() {
        return saved;
    }

    public Collection<GroupAndInstrument> getGroupAndInstrumentAssignments() {
        return musicianEditor.getGroupAndInstrumentAssignments();
    }

    public Musician getMusician() {
        return musicianEditor.getMusician();
    }

    public void setGroupAndInstrumentAssignments(
            Collection<GroupAndInstrument> assignments) {
        musicianEditor.setGroupAndInstrumentAssignments(assignments);
    }

    public void setGroups(Collection<? extends NamedItem> namedItems) {
        musicianEditor.setGroups(namedItems);
    }

    public void setInstruments(Collection<? extends NamedItem> namedItems) {
        musicianEditor.setInstruments(namedItems);
    }

    
    public void setSchoolYear(int schoolYear) {
        musicianEditor.setSchoolYear(schoolYear);
    }

    public void setMusician(Musician musician) {
        musicianEditor.setMusician(musician);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cancelButton = new JButton();
        saveButton = new JButton();
        musicianEditor = new MusicianEditor();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add or Change Musician");
        setResizable(false);

        cancelButton.setMnemonic('C');
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        saveButton.setMnemonic('S');
        saveButton.setText("Save");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(444, Short.MAX_VALUE)
                .add(saveButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cancelButton)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(musicianEditor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(musicianEditor, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(saveButton)
                    .add(cancelButton)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        saved = false;
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        try {
            getMusician();
            if (getGroupAndInstrumentAssignments().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Missing group and instrument assignment",
                        "Error", JOptionPane.WARNING_MESSAGE);
            } else {
                saved = true;
                dispose();
            }
        } catch(IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_saveButtonActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton cancelButton;
    private MusicianEditor musicianEditor;
    private JButton saveButton;
    // End of variables declaration//GEN-END:variables
    
}