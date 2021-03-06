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

package listfix.model;

import java.awt.Font;
import listfix.model.enums.AppOptionsEnum;
import java.io.File;
import java.util.HashMap;
import javax.swing.UIManager;

/**
 *
 * @author jcaron
 */
public class AppOptions
{
	private static final String EMPTY_STRING = "None Selected";
	private boolean savePlaylistsWithRelativePaths = false;
	private boolean autoLocateEntriesOnPlaylistLoad = false;
	private boolean autoRefreshMediaLibraryOnStartup = false;
	private boolean alwaysUseUNCPaths = false;
	private int maxPlaylistHistoryEntries = 5;
	private String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
	private String playlistsDirectory = EMPTY_STRING;
	private Font appFont = new Font("SansSerif", 0, 11);
	private int maxClosestResults = 20;
	
	public static final HashMap<String, Integer> optionEnumTable = new HashMap<String, Integer>();

	static
	{
		optionEnumTable.put("SAVE_RELATIVE_REFERENCES", AppOptionsEnum.SAVE_RELATIVE_REFERENCES);
		optionEnumTable.put("AUTO_FIND_ENTRIES_ON_PLAYLIST_LOAD", AppOptionsEnum.AUTO_FIND_ENTRIES_ON_PLAYLIST_LOAD);
		optionEnumTable.put("MAX_PLAYLIST_HISTORY_SIZE", AppOptionsEnum.MAX_PLAYLIST_HISTORY_SIZE);
		optionEnumTable.put("AUTO_REFRESH_MEDIA_LIBRARY_ON_LOAD", AppOptionsEnum.AUTO_REFRESH_MEDIA_LIBRARY_ON_LOAD);
		optionEnumTable.put("LOOK_AND_FEEL", AppOptionsEnum.LOOK_AND_FEEL);
		optionEnumTable.put("ALWAYS_USE_UNC_PATHS", AppOptionsEnum.ALWAYS_USE_UNC_PATHS);
		optionEnumTable.put("PLAYLISTS_DIRECTORY", AppOptionsEnum.PLAYLISTS_DIRECTORY);
		optionEnumTable.put("APP_FONT", AppOptionsEnum.APP_FONT);
		optionEnumTable.put("MAX_CLOSEST_RESULTS", AppOptionsEnum.MAX_CLOSEST_RESULTS);
	}

	public boolean getAutoLocateEntriesOnPlaylistLoad()
	{
		return autoLocateEntriesOnPlaylistLoad;
	}

	public void setAutoLocateEntriesOnPlaylistLoad(boolean autoLocateEntriesOnPlaylistLoad)
	{
		this.autoLocateEntriesOnPlaylistLoad = autoLocateEntriesOnPlaylistLoad;
	}

	public int getMaxPlaylistHistoryEntries()
	{
		return maxPlaylistHistoryEntries;
	}

	public void setLookAndFeel(String lookAndFeel)
	{
		this.lookAndFeel = lookAndFeel;
	}

	public String getLookAndFeel()
	{
		return lookAndFeel;
	}

	public void setMaxPlaylistHistoryEntries(int maxPlaylistHistoryEntries)
	{
		this.maxPlaylistHistoryEntries = maxPlaylistHistoryEntries;
	}

	public void setAutoRefreshMediaLibraryOnStartup(boolean autoRefreshMediaLibraryOnStartup)
	{
		this.autoRefreshMediaLibraryOnStartup = autoRefreshMediaLibraryOnStartup;
	}

	public boolean getAutoRefreshMediaLibraryOnStartup()
	{
		return autoRefreshMediaLibraryOnStartup;
	}

	public boolean getSavePlaylistsWithRelativePaths()
	{
		return savePlaylistsWithRelativePaths;
	}

	public void setSavePlaylistsWithRelativePaths(boolean savePlaylistsWithRelativePaths)
	{
		this.savePlaylistsWithRelativePaths = savePlaylistsWithRelativePaths;
	}

	public boolean getAlwaysUseUNCPaths()
	{
		return alwaysUseUNCPaths;
	}

	public void setAlwaysUseUNCPaths(boolean alwaysUseUNCPaths)
	{
		this.alwaysUseUNCPaths = alwaysUseUNCPaths;
	}

	public String getPlaylistsDirectory()
	{
		return playlistsDirectory;
	}

	public void setPlaylistsDirectory(String playlistsDirectory)
	{
		if (new File(playlistsDirectory).exists())
		{
			this.playlistsDirectory = playlistsDirectory;
		}
	}

	/**
	 * @return the appFont
	 */ public Font getAppFont()
	{
		return appFont;
	}

	/**
	 * @param appFont the appFont to set
	 */ public void setAppFont(Font appFont)
	{
		this.appFont = appFont;
	}

	/**
	 * @return the maxClosestResults
	 */ public int getMaxClosestResults()
	{
		return maxClosestResults;
	}

	/**
	 * @param maxClosestResults the maxClosestResults to set
	 */ public void setMaxClosestResults(int maxClosestResults)
	{
		this.maxClosestResults = maxClosestResults;
	}
}
