/*
 * listFix() - Fix Broken Playlists!
 *
 * This file is part of listFix().
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, please see http://www.gnu.org/licenses/
 */

package listfix.view;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import listfix.model.BatchMatchItem;
import listfix.model.MatchedPlaylistEntry;
import listfix.view.support.ZebraJTable;

public class BatchClosestMatchResultsDialog extends javax.swing.JDialog
{
    public BatchClosestMatchResultsDialog(java.awt.Frame parent, List<BatchMatchItem> items)
    {
        super(parent, true);
        _items = items;
        initComponents();

        int cwidth = 0;
        TableColumnModel cm = _uiTable.getColumnModel();
        cm.getColumn(3).setMinWidth(0);
        _uiTable.setDefaultRenderer(Integer.class, new ZebraJTable.IntRenderer());
        _uiTable.initFillColumnForScrollPane(_uiScrollPane);
        cwidth += _uiTable.autoResizeColumn(1);
        cwidth += _uiTable.autoResizeColumn(2);
        _uiTable.setFillerColumnWidth(_uiScrollPane);

        TableCellRenderer renderer = _uiTable.getDefaultRenderer(Integer.class);
        Component comp = renderer.getTableCellRendererComponent(_uiTable, (items.size() + 1) * 10, false, false, 0, 0);
        int width = comp.getPreferredSize().width + 4;
        TableColumn col = cm.getColumn(0);
        col.setMinWidth(width);
        col.setMaxWidth(width);
        col.setPreferredWidth(width);
        cwidth += width;

        cm.getColumn(2).setCellEditor(new MatchEditor());

        int screenWidth = getGraphicsConfiguration().getDevice().getDisplayMode().getWidth();
        int newWidth = cwidth + getWidth() - _uiScrollPane.getWidth() + 2;
        //int newWidth = cwidth + get() - _uiScrollPane.getVisibleRect().width;
        //int newWidth = cwidth + 10;
        setSize(Math.min(newWidth, screenWidth - 50), getHeight());

        // set sort to #
        ArrayList<RowSorter.SortKey> keys = new ArrayList<RowSorter.SortKey>();
        keys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        _uiTable.getRowSorter().setSortKeys(keys);

        _uiTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public boolean isAccepted()
    {
        return _isAccepted;
    }
    private boolean _isAccepted;

    public List<BatchMatchItem> getMatches()
    {
        return _items;
    }
    private List<BatchMatchItem> _items;

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        _btnOk = new javax.swing.JButton();
        _btnCancel = new javax.swing.JButton();
        _uiScrollPane = new javax.swing.JScrollPane();
        _uiTable = createTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Select Closest Matches");

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        _btnOk.setText("OK");
        _btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onBtnOkActionPerformed(evt);
            }
        });
        jPanel1.add(_btnOk);

        _btnCancel.setText("Cancel");
        _btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onBtnCancelActionPerformed(evt);
            }
        });
        jPanel1.add(_btnCancel);

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        _uiTable.setAutoCreateRowSorter(true);
        _uiTable.setModel(new MatchTableModel());
        _uiTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        _uiTable.setFont(new java.awt.Font("Verdana", 0, 9)); // NOI18N
        _uiScrollPane.setViewportView(_uiTable);

        getContentPane().add(_uiScrollPane, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void onBtnOkActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_onBtnOkActionPerformed
    {//GEN-HEADEREND:event_onBtnOkActionPerformed
        _isAccepted = true;
        setVisible(false);
    }//GEN-LAST:event_onBtnOkActionPerformed

    private void onBtnCancelActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_onBtnCancelActionPerformed
    {//GEN-HEADEREND:event_onBtnCancelActionPerformed
        _isAccepted = false;
        setVisible(false);
    }//GEN-LAST:event_onBtnCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton _btnCancel;
    private javax.swing.JButton _btnOk;
    private javax.swing.JScrollPane _uiScrollPane;
    private listfix.view.support.ZebraJTable _uiTable;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

    private ZebraJTable createTable()
    {
        return new ZebraJTable()
        {
            @Override
            public String getToolTipText(MouseEvent event)
            {
                Point point = event.getPoint();
                int rawRowIx = rowAtPoint(point);
                int rawColIx = columnAtPoint(point);
                if (rawRowIx >= 0 && rawColIx >= 0)
                {
                    int rowIx = convertRowIndexToModel(rawRowIx);
                    int colIx = convertColumnIndexToModel(rawColIx);
                    if (rowIx >= 0 && rowIx < _items.size() && (colIx == 1 || colIx == 2))
                    {
                        BatchMatchItem item = _items.get(rowIx);
                        if (colIx == 1)
                        {
                            return item.getEntry().getPath();
                        }
                        else
                        {
                            MatchedPlaylistEntry match = item.getSelectedMatch();
                            if (match != null)
                                return match.getPlaylistFile().getPath();
                        }
                    }
                }
                return super.getToolTipText(event);
            }
        };
    }

    private class MatchTableModel extends AbstractTableModel
    {
        public int getRowCount()
        {
            return _items.size();
        }

        public int getColumnCount()
        {
            return 4;
        }

        public Object getValueAt(int rowIndex, int columnIndex)
        {
            BatchMatchItem item = _items.get(rowIndex);
            switch (columnIndex)
            {
                case 0: return rowIndex + 1;

                case 1: return item.getEntry().getFileName();

                case 2:
                    MatchedPlaylistEntry match = item.getSelectedMatch();
                    if (match != null)
                        return match.getPlaylistFile().getFileName();
                    else
                        return "< skip >";

                default: return null;
            }
        }

        @Override
        public String getColumnName(int column)
        {
            switch (column)
            {
                case 0: return "#";
                case 1: return "Original Name";
                case 2: return "Matched Name";
                default: return null;
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex)
        {
            return columnIndex == 0 ? Integer.class : Object.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
            //return super.isCellEditable(rowIndex, columnIndex);
            return columnIndex == 2;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex)
        {
            if (columnIndex == 2)
            {
                int ix = (Integer)aValue;
                //Log.write("set %d to %d", rowIndex, ix);
                BatchMatchItem item = _items.get(rowIndex);
                item.setSelectedIx(ix);
            }
        }
    }

    private class MatchEditor extends AbstractCellEditor implements TableCellEditor
    {
        public MatchEditor()
        {
            _model = new MatchComboBoxModel();
            _combo = new JComboBox(_model);
            _combo.setMaximumRowCount(25);
            _combo.setFocusable(false);
        }

        JComboBox _combo;
        MatchComboBoxModel _model;

        public Object getCellEditorValue()
        {
            return _combo.getSelectedIndex() - 1;
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
        {
            BatchMatchItem item = _items.get(row);
            _model.setMatches(item.getMatches());
            _combo.setSelectedIndex(item.getSelectedIx() + 1);
            return _combo;
        }
    }

    private class MatchEditor2 extends DefaultCellEditor
    {
        public MatchEditor2()
        {
            super(new JComboBox());
            _combo = (JComboBox)getComponent();
            _combo.setModel(_model);
        }

        MatchComboBoxModel _model = new MatchComboBoxModel();
        JComboBox _combo; 

        @Override
        public Object getCellEditorValue()
        {
            return _combo.getSelectedIndex();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
        {
            BatchMatchItem item = _items.get(row);
            _model.setMatches(item.getMatches());
            _combo.setSelectedIndex(item.getSelectedIx());
            return _combo;
        }

    }

    private static class MatchComboBoxModel extends AbstractListModel implements ComboBoxModel
    {

        List<MatchedPlaylistEntry> _matches;
        Object _selected;

        public void setMatches(List<MatchedPlaylistEntry> matches)
        {
            _matches = matches;
            _selected = null;
            fireContentsChanged(this, 0, _matches.size());
        }

        public int getSize()
        {
            return _matches != null ? _matches.size() + 1 : 0;
        }

        public Object getElementAt(int index)
        {
            if (_matches != null)
            {
                if (index > 0)
                {
                    MatchedPlaylistEntry match = _matches.get(index - 1);
                    return Integer.toString(match.getScore()) + ": " + match.getPlaylistFile().getFileName();
                }
                else
                    return "< skip >";
            }
            else
                return null;
        }

        public void setSelectedItem(Object anItem)
        {
            _selected = anItem;
        }

        public Object getSelectedItem()
        {
            return _selected;
        }


    }


}
