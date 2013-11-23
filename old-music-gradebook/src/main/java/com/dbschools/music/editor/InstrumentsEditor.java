package com.dbschools.music.editor;

import com.dbschools.gui.ActionUtil;
import com.dbschools.gui.PopupListener;
import com.dbschools.gui.TableUtil;
import com.dbschools.music.dao.RemoteDao;
import com.dbschools.music.events.Event;
import com.dbschools.music.events.EventObserver;
import com.dbschools.music.orm.AbstractPersistentObject;
import com.dbschools.music.orm.Instrument;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.jdesktop.swingx.decorator.HighlighterFactory;

/**
 * Defines instruments.
 * @author  Dave Briccetti
 */
public class InstrumentsEditor extends javax.swing.JPanel {
    private final RemoteDao remoteDao;
    private final InstrumentsModel model;

    private static class InstrumentsModel extends AbstractTableModel {
        private final List<Instrument> instruments = Collections.synchronizedList(new ArrayList<Instrument>());

        public InstrumentsModel(final RemoteDao remoteDao) {
            super(remoteDao, new String[] {"Sequence", "Instrument Name"}, 
                    new Class<?>[] {Integer.class, String.class}, 
                    new boolean[] {true, true});
            super.setEntities(instruments);
            instruments.addAll(remoteDao.getInstruments());
            remoteDao.addEventObserver(new EventObserver() {
                public void notify(Event event) {
                    if (event.getDetails() instanceof Instrument) {
                        instruments.clear();
                        instruments.addAll(remoteDao.getInstruments());
                        fireTableDataChangedFromSwing();
                    }
                }
            });
        }
        
        public Object getValueAt(int rowIndex, int columnIndex) {
            final Instrument instrument = instruments.get(rowIndex);
            switch(columnIndex) {
                case 0:
                    return instrument.getSequence();
                case 1:
                    return instrument.getName();
            }
            return "";
        }
        
        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            final Instrument instrument = instruments.get(rowIndex);
            switch(columnIndex) {
                case 0:
                    instrument.setSequence((Integer) aValue);
                    break;
                case 1:
                    instrument.setName((String) aValue);
                    break;
            }
            saveOrUpdateObject(instrument, instrument.getId());
        }

        @Override
        protected AbstractPersistentObject getNewEntity() {
            return new Instrument("untitled", 0);
        }

    }
    
    public InstrumentsEditor(RemoteDao remoteDao) {
        this.remoteDao = remoteDao;
        model = new InstrumentsModel(remoteDao);
        initComponents();
        table.setHighlighters(HighlighterFactory.createSimpleStriping());
        table.setSortable(false);
        table.getColumn(0).setMaxWidth(60);
        table.getColumn(1).setPreferredWidth(150);
        createActions();
    }

    private void createActions() {
        JPopupMenu popup = new JPopupMenu();
        table.addMouseListener(new PopupListener(table, popup));

        Action deleteAction = new AbstractAction("Delete...") {
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = table.getSelectedRows();
                if (selectedRows.length > 0) {
                    if (TableUtil.isOkToDelete(InstrumentsEditor.this, selectedRows.length)) {
                        model.deleteRows(selectedRows);
                    }
                }
            }
        };
        popup.add(new JMenuItem(deleteAction));
        ActionUtil.attachDeleteAction(table, deleteAction);

        Action insertAction = new AbstractAction("Insert") {
            public void actionPerformed(ActionEvent e) {
                model.insertRow();
            }
        };
        popup.add(new JMenuItem(insertAction));
    }
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        table = new org.jdesktop.swingx.JXTable();
        jLabel1 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        table.setModel(model);
        jScrollPane1.setViewportView(table);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);

        jLabel1.setText("You may only delete instruments to which no students are assigned.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        add(jLabel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTable table;
    // End of variables declaration//GEN-END:variables

}