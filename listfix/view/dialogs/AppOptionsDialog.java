/*
 * listFix() - Fix Broken Playlists!
 * Copyright (C) 2001-2012 Jeremy Caron
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

package listfix.view.dialogs;

/**
 *
 * @author  jcaron
 */
import com.jidesoft.swing.FolderChooser;

import java.awt.Font;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import listfix.model.AppOptions;
import listfix.util.ExStack;
import listfix.view.controls.JTransparentTextArea;
import listfix.view.support.FontExtensions;

import net.mariottini.swing.JFontChooser;

import org.apache.log4j.Logger;

public class AppOptionsDialog extends javax.swing.JDialog
{
	private static final long serialVersionUID = 3409894354485158935L;
	public static final int OK = 0;
	public static final int CANCEL = 1;
	private static final Logger _logger = Logger.getLogger(AppOptionsDialog.class);
	
	private FolderChooser _jMediaDirChooser = new FolderChooser();

	private static class IntegerRangeComboBoxModel extends AbstractListModel implements ComboBoxModel
	{
		private List<Integer> intList = new ArrayList<Integer>();
		Object _selected;

		public IntegerRangeComboBoxModel(int i, int i0)
		{
			for (int j = i; j <= i0; j++)
			{
				intList.add(j);
			}
		}

		@Override
		public void setSelectedItem(Object anItem)
		{
			_selected = anItem;
		}

		@Override
		public Object getSelectedItem()
		{
			return _selected;
		}

		@Override
		public int getSize()
		{
			return intList.size();
		}

		@Override
		public Object getElementAt(int index)
		{
			return intList.get(index);
		}
	}

	private int _resultCode;
	private String _fileName;
	private AppOptions _options = null;
	private Font _chosenFont = null;

	/** Creates new form EditFilenameDialog
	 * @param parent
	 * @param title
	 * @param modal 
	 * @param opts  
	 */
	public AppOptionsDialog(java.awt.Frame parent, String title, boolean modal, AppOptions opts)
	{
		super(parent, title, modal);
		if (opts == null)
		{
			_options = new AppOptions();
		}
		else
		{
			_options = opts;
		}
		initComponents();
		_chosenFont = opts.getAppFont();
		_fontDisplayLabel.setText(FontExtensions.formatFont(_chosenFont));
		initPlaylistDirectoryFolderChooser();
	}

	private void initPlaylistDirectoryFolderChooser()
	{
		_jMediaDirChooser.setDialogTitle("Specify a playlists directory...");
		_jMediaDirChooser.setAcceptAllFileFilterUsed(false);
		_jMediaDirChooser.setAvailableButtons(FolderChooser.BUTTON_DESKTOP | FolderChooser.BUTTON_MY_DOCUMENTS | FolderChooser.BUTTON_NEW | FolderChooser.BUTTON_REFRESH);
		_jMediaDirChooser.setRecentListVisible(false);
	}

	public AppOptionsDialog()
	{
	}

	public String getFileName()
	{
		return _fileName;
	}

	public void setFileName(String x)
	{
		_fileName = x;
	}

	public void setResultCode(int i)
	{
		_resultCode = i;
	}

	public int getResultCode()
	{
		return _resultCode;
	}	

	private LookAndFeelInfo[] getInstalledLookAndFeels()
	{
		LookAndFeelInfo[] plafs = UIManager.getInstalledLookAndFeels();
		List<LookAndFeelInfo> lafs = new ArrayList<LookAndFeelInfo>();
		for (LookAndFeelInfo laf : plafs)
		{
			if (!laf.getName().toLowerCase().contains("nimbus"))
			{
				lafs.add(laf);
			}
		}
		plafs = lafs.toArray(new LookAndFeelInfo[0]);
		return plafs;
	}

	private DefaultComboBoxModel getLookAndFeelMenuItems()
	{
		LookAndFeelInfo[] plafs = getInstalledLookAndFeels();

		String[] model = new String[plafs.length];
		for (int i = 0; i < plafs.length; i++)
		{
			model[i] = plafs[i].getName();
		}
		return new DefaultComboBoxModel(model);
	}

	private LookAndFeelInfo getInstalledLookAndFeelAtIndex(int index)
	{
		UIManager.LookAndFeelInfo[] plafs = getInstalledLookAndFeels();
		if (index < plafs.length)
		{
			return plafs[index];
		}
		return plafs[0];
	}

	private LookAndFeelInfo getInstalledLookAndFeelByClassName(String name)
	{
		UIManager.LookAndFeelInfo[] plafs = getInstalledLookAndFeels();
		for (int i = 0; i < plafs.length; i++)
		{
			if (name.equals(plafs[i].getClassName()))
			{
				return plafs[i];
			}
		}
		return plafs[0];
	}

	private void center()
	{
		Point parentLocation = this.getParent().getLocationOnScreen();
		double x = parentLocation.getX();
		double y = parentLocation.getY();
		int width = this.getParent().getWidth();
		int height = this.getParent().getHeight();

		this.setLocation((int) x + (width - this.getPreferredSize().width) / 2, (int) y + (height - this.getPreferredSize().height) / 2);
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        topPanel = new javax.swing.JPanel();
        optionsPanel = new javax.swing.JPanel();
        _pnlRecentListLimit = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        recentPlaylistLimitComboBox = new javax.swing.JComboBox();
        _pnlfontChooser = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        _fontDisplayLabel = new javax.swing.JLabel();
        _changeFontButton = new javax.swing.JButton();
        _pnlLookAndFeel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        lookAndFeelComboBox = new javax.swing.JComboBox();
        _pnlAutoLocate = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        autoLocateCheckBox = new javax.swing.JCheckBox();
        _pnlSaveRelative = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        relativePathsCheckBox = new javax.swing.JCheckBox();
        _pnRefreshMediaLibraryOnStart = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        autoRefreshOnStartupCheckBox = new javax.swing.JCheckBox();
        _pnlUseUnc = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        alwaysUseUNCPathsCheckBox = new javax.swing.JCheckBox();
        _pnlListsDir = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        playlistDirectoryTextField = new javax.swing.JTextField();
        playlistDirectoryBrowseButton = new javax.swing.JButton();
        _pnlNumClosestMatches = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        _cbxMaxClosestMatches = new javax.swing.JComboBox();
        _pnlbottomSpacer = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setModal(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        topPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        topPanel.setLayout(new javax.swing.BoxLayout(topPanel, javax.swing.BoxLayout.Y_AXIS));

        optionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("General Config"));
        optionsPanel.setLayout(new java.awt.GridBagLayout());

        _pnlRecentListLimit.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));

        jLabel1.setText("Recent Playlist Limit: ");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel1.setMinimumSize(new java.awt.Dimension(111, 9));
        jLabel1.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        _pnlRecentListLimit.add(jLabel1);

        recentPlaylistLimitComboBox.setModel(new IntegerRangeComboBoxModel(1, 15));
        recentPlaylistLimitComboBox.setSelectedItem(_options.getMaxPlaylistHistoryEntries());
        recentPlaylistLimitComboBox.setMaximumSize(null);
        recentPlaylistLimitComboBox.setMinimumSize(null);
        recentPlaylistLimitComboBox.setPreferredSize(null);
        _pnlRecentListLimit.add(recentPlaylistLimitComboBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        optionsPanel.add(_pnlRecentListLimit, gridBagConstraints);

        _pnlfontChooser.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));

        jLabel8.setText("Font:");
        _pnlfontChooser.add(jLabel8);

        _fontDisplayLabel.setText("SansSerif, Plain, 10");
        _pnlfontChooser.add(_fontDisplayLabel);

        _changeFontButton.setText("...");
        _changeFontButton.setAlignmentY(0.0F);
        _changeFontButton.setMargin(new java.awt.Insets(2, 6, 2, 6));
        _changeFontButton.setMaximumSize(null);
        _changeFontButton.setMinimumSize(null);
        _changeFontButton.setPreferredSize(null);
        _changeFontButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _changeFontButtonActionPerformed(evt);
            }
        });
        _pnlfontChooser.add(_changeFontButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        optionsPanel.add(_pnlfontChooser, gridBagConstraints);

        _pnlLookAndFeel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));

        jLabel5.setText("Look and Feel:");
        jLabel5.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel5.setMinimumSize(new java.awt.Dimension(111, 9));
        jLabel5.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        _pnlLookAndFeel.add(jLabel5);

        lookAndFeelComboBox.setModel(this.getLookAndFeelMenuItems());
        lookAndFeelComboBox.setSelectedItem(this.getInstalledLookAndFeelByClassName(_options.getLookAndFeel()).getName());
        lookAndFeelComboBox.setMaximumSize(null);
        lookAndFeelComboBox.setMinimumSize(null);
        lookAndFeelComboBox.setPreferredSize(null);
        _pnlLookAndFeel.add(lookAndFeelComboBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        optionsPanel.add(_pnlLookAndFeel, gridBagConstraints);

        _pnlAutoLocate.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));

        jLabel2.setText("Auto-locate missing playlist entries on load:");
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel2.setMinimumSize(new java.awt.Dimension(111, 9));
        jLabel2.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        _pnlAutoLocate.add(jLabel2);

        autoLocateCheckBox.setSelected(_options.getAutoLocateEntriesOnPlaylistLoad());
        _pnlAutoLocate.add(autoLocateCheckBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        optionsPanel.add(_pnlAutoLocate, gridBagConstraints);

        _pnlSaveRelative.setMinimumSize(new java.awt.Dimension(165, 20));
        _pnlSaveRelative.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));

        jLabel3.setText("Save playlists with relative file references:");
        jLabel3.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel3.setMinimumSize(new java.awt.Dimension(111, 9));
        jLabel3.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        _pnlSaveRelative.add(jLabel3);

        relativePathsCheckBox.setSelected(_options.getSavePlaylistsWithRelativePaths());
        _pnlSaveRelative.add(relativePathsCheckBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        optionsPanel.add(_pnlSaveRelative, gridBagConstraints);

        _pnRefreshMediaLibraryOnStart.setMinimumSize(new java.awt.Dimension(165, 20));
        _pnRefreshMediaLibraryOnStart.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));

        jLabel4.setText("Auto refresh media library at startup:");
        jLabel4.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel4.setMinimumSize(new java.awt.Dimension(111, 9));
        jLabel4.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        _pnRefreshMediaLibraryOnStart.add(jLabel4);

        autoRefreshOnStartupCheckBox.setSelected(_options.getAutoRefreshMediaLibraryOnStartup());
        _pnRefreshMediaLibraryOnStart.add(autoRefreshOnStartupCheckBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        optionsPanel.add(_pnRefreshMediaLibraryOnStart, gridBagConstraints);

        _pnlUseUnc.setMinimumSize(new java.awt.Dimension(165, 20));
        _pnlUseUnc.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));

        jLabel6.setText("Media library uses UNC paths for directories on mapped drives:");
        jLabel6.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel6.setMinimumSize(new java.awt.Dimension(111, 9));
        jLabel6.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        _pnlUseUnc.add(jLabel6);

        alwaysUseUNCPathsCheckBox.setSelected(_options.getAlwaysUseUNCPaths());
        _pnlUseUnc.add(alwaysUseUNCPathsCheckBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        optionsPanel.add(_pnlUseUnc, gridBagConstraints);

        _pnlListsDir.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING, 8, 0));

        jLabel7.setText("Playlists Directory:");
        jLabel7.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel7.setMaximumSize(null);
        jLabel7.setMinimumSize(null);
        jLabel7.setPreferredSize(null);
        jLabel7.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        _pnlListsDir.add(jLabel7);

        playlistDirectoryTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        playlistDirectoryTextField.setText(_options.getPlaylistsDirectory() + "     ");
        playlistDirectoryTextField.setMaximumSize(null);
        playlistDirectoryTextField.setMinimumSize(null);
        playlistDirectoryTextField.setPreferredSize(null);
        playlistDirectoryTextField.setRequestFocusEnabled(false);
        _pnlListsDir.add(playlistDirectoryTextField);

        playlistDirectoryBrowseButton.setText("...");
        playlistDirectoryBrowseButton.setMargin(new java.awt.Insets(2, 6, 2, 6));
        playlistDirectoryBrowseButton.setMaximumSize(null);
        playlistDirectoryBrowseButton.setMinimumSize(null);
        playlistDirectoryBrowseButton.setPreferredSize(null);
        playlistDirectoryBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playlistDirectoryBrowseButtonActionPerformed(evt);
            }
        });
        _pnlListsDir.add(playlistDirectoryBrowseButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        optionsPanel.add(_pnlListsDir, gridBagConstraints);

        _pnlNumClosestMatches.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));

        jLabel9.setText("Number of closest matches to return when searching:");
        jLabel9.setToolTipText("This value has memory usage implications, if you run out of memory while repairing a list, dial this down to 20 or less.");
        _pnlNumClosestMatches.add(jLabel9);

        _cbxMaxClosestMatches.setModel(new IntegerRangeComboBoxModel(10, 100));
        _cbxMaxClosestMatches.setSelectedItem(_options.getMaxClosestResults());
        _pnlNumClosestMatches.add(_cbxMaxClosestMatches);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        optionsPanel.add(_pnlNumClosestMatches, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        optionsPanel.add(_pnlbottomSpacer, gridBagConstraints);

        topPanel.add(optionsPanel);

        buttonPanel.setMaximumSize(null);
        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jButton1.setText("OK");
        jButton1.setMinimumSize(new java.awt.Dimension(49, 20));
        jButton1.setPreferredSize(null);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        buttonPanel.add(jButton1);

        jButton2.setText("Cancel");
        jButton2.setMinimumSize(new java.awt.Dimension(67, 20));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        buttonPanel.add(jButton2);

        topPanel.add(buttonPanel);

        getContentPane().add(topPanel);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
		setVisible(false);
		dispose();
		setResultCode(CANCEL);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
		setVisible(false);
		dispose();
		setResultCode(OK);
    }//GEN-LAST:event_jButton1ActionPerformed

	/** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
		setVisible(false);
		dispose();
    }//GEN-LAST:event_closeDialog

	private void playlistDirectoryBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playlistDirectoryBrowseButtonActionPerformed
		if (_options.getPlaylistsDirectory() != null && _options.getPlaylistsDirectory().length() > 0)
		{
			_jMediaDirChooser.setCurrentDirectory(new File(_options.getPlaylistsDirectory()));
			_jMediaDirChooser.setSelectedFolder(new File(_options.getPlaylistsDirectory()));
		}
		int response = _jMediaDirChooser.showOpenDialog(this);
		if (response == JFileChooser.APPROVE_OPTION)
		{
			try
			{
				String path = _jMediaDirChooser.getSelectedFile().getPath();
				if (new File(path).exists())
				{
					playlistDirectoryTextField.setText(path + "     ");
				}
				else
				{
					throw new FileNotFoundException();
				}
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(this, new JTransparentTextArea("The directory you selected/entered does not exist."));
				_logger.info(ExStack.toString(e));
			}
		}
	}//GEN-LAST:event_playlistDirectoryBrowseButtonActionPerformed

	private void _changeFontButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event__changeFontButtonActionPerformed
	{//GEN-HEADEREND:event__changeFontButtonActionPerformed
		JFontChooser jfc = new JFontChooser();
		jfc.setSelectedFont(_chosenFont);
		jfc.showDialog(this);
		_chosenFont = jfc.getSelectedFont();
		_fontDisplayLabel.setText(FontExtensions.formatFont(_chosenFont));
	}//GEN-LAST:event__changeFontButtonActionPerformed

	public AppOptions showDialog()
	{
		this.center();
		this.setVisible(true);
		if (this.getResultCode() == OK)
		{
			_options.setAutoLocateEntriesOnPlaylistLoad(autoLocateCheckBox.isSelected());
			_options.setMaxPlaylistHistoryEntries(((Integer) recentPlaylistLimitComboBox.getItemAt(recentPlaylistLimitComboBox.getSelectedIndex())).intValue());
			_options.setSavePlaylistsWithRelativePaths(relativePathsCheckBox.isSelected());
			_options.setAutoRefreshMediaLibraryOnStartup(autoRefreshOnStartupCheckBox.isSelected());
			_options.setLookAndFeel(this.getInstalledLookAndFeelAtIndex(lookAndFeelComboBox.getSelectedIndex()).getClassName());
			_options.setAlwaysUseUNCPaths(this.alwaysUseUNCPathsCheckBox.isSelected());
			_options.setPlaylistsDirectory(playlistDirectoryTextField.getText().trim());
			_options.setAppFont(_chosenFont);
			_options.setMaxClosestResults(((Integer) _cbxMaxClosestMatches.getItemAt(_cbxMaxClosestMatches.getSelectedIndex())).intValue());
		}
		return _options;
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox _cbxMaxClosestMatches;
    private javax.swing.JButton _changeFontButton;
    private javax.swing.JLabel _fontDisplayLabel;
    private javax.swing.JPanel _pnRefreshMediaLibraryOnStart;
    private javax.swing.JPanel _pnlAutoLocate;
    private javax.swing.JPanel _pnlListsDir;
    private javax.swing.JPanel _pnlLookAndFeel;
    private javax.swing.JPanel _pnlNumClosestMatches;
    private javax.swing.JPanel _pnlRecentListLimit;
    private javax.swing.JPanel _pnlSaveRelative;
    private javax.swing.JPanel _pnlUseUnc;
    private javax.swing.JPanel _pnlbottomSpacer;
    private javax.swing.JPanel _pnlfontChooser;
    private javax.swing.JCheckBox alwaysUseUNCPathsCheckBox;
    private javax.swing.JCheckBox autoLocateCheckBox;
    private javax.swing.JCheckBox autoRefreshOnStartupCheckBox;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JComboBox lookAndFeelComboBox;
    private javax.swing.JPanel optionsPanel;
    private javax.swing.JButton playlistDirectoryBrowseButton;
    private javax.swing.JTextField playlistDirectoryTextField;
    private javax.swing.JComboBox recentPlaylistLimitComboBox;
    private javax.swing.JCheckBox relativePathsCheckBox;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}
