/*
 *  listFix() - Fix Broken Playlists!
 *  Copyright (C) 2001-2010 Jeremy Caron
 * 
 *  This file is part of listFix().
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, please see http://www.gnu.org/licenses/
 */

/*
 * MultiListBatchClosestMatchResultsDialog.java
 *
 * Created on Mar 28, 2011, 6:26:47 PM
 */

package listfix.view.dialogs;

import java.util.List;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import listfix.model.BatchRepair;
import listfix.model.BatchRepairItem;
import listfix.model.Playlist;
import listfix.view.controls.ClosestMatchesSearchScrollableResultsPanel;
import listfix.view.controls.PlaylistsList;
import listfix.view.support.DualProgressWorker;
import listfix.view.support.IPlaylistModifiedListener;

/**
 *
 * @author jcaron
 */
public class MultiListBatchClosestMatchResultsDialog extends javax.swing.JDialog
{
	private BatchRepair _batch;
	private boolean _userCancelled = false;

    /** Creates new form MultiListBatchClosestMatchResultsDialog */
    public MultiListBatchClosestMatchResultsDialog(java.awt.Frame parent, boolean modal)
	{
        super(parent, modal);
        initComponents();
    }

	public MultiListBatchClosestMatchResultsDialog(java.awt.Frame parent, boolean modal, BatchRepair br)
	{
		super(parent, br.getDescription(), modal);
		_batch = br;
		initComponents();

//		getRootPane().setDefaultButton(_btnSave);
//		_txtBackup.setText(_batch.getDefaultBackupName());

		// load and repair lists
		final DualProgressDialog pd = new DualProgressDialog(parent, "Finding Closest Matches...", "Please wait...", "Overall Progress:");
		DualProgressWorker dpw = new DualProgressWorker<Void, String>()
		{
			@Override
			protected void process(List<ProgressItem<String>> chunks)
			{
				ProgressItem<String> titem = new ProgressItem<String>(true, -1, null);
				ProgressItem<String> oitem = new ProgressItem<String>(false, -1, null);
				getEffectiveItems(chunks, titem, oitem);

				if (titem.percentComplete >= 0)
				{
					pd.getTaskProgressBar().setValue(titem.percentComplete);
				}
				if (titem.state != null)
				{
					pd.getTaskLabel().setText(titem.state);
				}
				if (oitem.percentComplete >= 0)
				{
					pd.getOverallProgressBar().setValue(oitem.percentComplete);
				}
				if (oitem.state != null)
				{
					pd.getOverallLabel().setText(oitem.state);
				}
			}

			@Override
			protected Void doInBackground() throws Exception
			{
				_batch.performClosestMatchRepair(this);
				return null;
			}
		};
		pd.show(dpw);

		if (!dpw.getCancelled())
		{
			ListSelectionModel lsm = _pnlList.getSelectionModel();
			lsm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			lsm.addListSelectionListener(new ListSelectionListener()
			{
				@Override
				public void valueChanged(ListSelectionEvent e)
				{
					if (e.getValueIsAdjusting())
					{
						return;
					}
					updateSelectedPlaylist();
				}
			});
			_pnlList.initPlaylistsList();

			for (BatchRepairItem item : _batch.getItems())
			{
				item.getPlaylist().addModifiedListener(listener);
			}

			String listCountTxt;
			if (_batch.getItems().size() == 1)
			{
				listCountTxt = "1 playlist";
			}
			else
			{
				listCountTxt = String.format("%d playlists", _batch.getItems().size());
			}
			_pnlList.setText(listCountTxt);
		}
		else
		{
			_userCancelled = true;
		}
	}

	private final IPlaylistModifiedListener listener = new IPlaylistModifiedListener()
	{
		@Override
		public void playlistModified(Playlist list)
		{
			onPlaylistModified(list);
		}
	};

	private void onPlaylistModified(Playlist list)
	{
		_pnlList.playlistModified(list);
	}

	private void updateSelectedPlaylist()
	{
		// Keep the table anchored left...
		_pnlList.anchorLeft();
		int selIx = _pnlList.getSelectedModelRow();
		if (selIx >= 0)
		{
			BatchRepairItem item = _batch.getItem(selIx);
			_pnlResults.setResults(item.getClosestMatches());
		}
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        _pnlResults = new ClosestMatchesSearchScrollableResultsPanel();
        _pnlList = new PlaylistsList(_batch);
        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jSplitPane1.setDividerLocation(150);
        jSplitPane1.setRightComponent(_pnlResults);
        jSplitPane1.setLeftComponent(_pnlList);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
	{//GEN-HEADEREND:event_formWindowClosing
		_userCancelled = true;
	}//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private listfix.view.controls.PlaylistsList _pnlList;
    private listfix.view.controls.ClosestMatchesSearchScrollableResultsPanel _pnlResults;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSplitPane jSplitPane1;
    // End of variables declaration//GEN-END:variables

	/**
	 * @param userCancelled the _userCancelled to set
	 */
	public void setUserCancelled(boolean userCancelled)
	{
		_userCancelled = userCancelled;
	}

	public boolean getUserCancelled()
	{
		return _userCancelled;
	}

}
