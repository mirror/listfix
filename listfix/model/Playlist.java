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

import listfix.model.enums.PlaylistType;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import listfix.controller.GUIDriver;
import listfix.io.Constants;
import listfix.io.FileCopier;
import listfix.io.FileUtils;
import listfix.io.FileLauncher;
import listfix.io.IPlaylistReader;
import listfix.io.PlaylistReaderFactory;
import listfix.io.UNCFile;
import listfix.io.UnicodeInputStream;

import listfix.util.ExStack;
import listfix.util.FileNameTokenizer;
import listfix.util.OperatingSystem;
import listfix.util.UnicodeUtils;

import listfix.view.support.IPlaylistModifiedListener;
import listfix.view.support.IProgressObserver;
import listfix.view.support.ProgressAdapter;
import listfix.view.support.ProgressWorker;

import org.apache.log4j.Logger;

public class Playlist
{
	private final static String FS = System.getProperty("file.separator");
	private final static String BR = System.getProperty("line.separator");
	private final static String HOME_DIR = System.getProperty("user.home");
	private static int _newListCount = -1;
	private File _file;
	private List<PlaylistEntry> _entries = new ArrayList<PlaylistEntry>();
	private List<PlaylistEntry> _originalEntries = new ArrayList<PlaylistEntry>();
	private boolean _utfFormat = false;
	private PlaylistType _type = PlaylistType.UNKNOWN;
	private int _fixedCount;
	private int _urlCount;
	private int _missingCount;
	private boolean _isModified;
	private boolean _isNew;
	private static final Logger _logger = Logger.getLogger(Playlist.class);

	public List<PlaylistEntry> getEntries()
	{
		return _entries;
	}

	public void copySelectedEntries(List<Integer> entryIndexList, File y, IProgressObserver observer)
	{
		ProgressAdapter progress = ProgressAdapter.wrap(observer);
		progress.setTotal(entryIndexList.size());
		PlaylistEntry tempEntry;
		File fileToCopy;
		File dest;
		for (int i = 0; i < entryIndexList.size(); i++)
		{
			if (!observer.getCancelled())
			{
				tempEntry = this.get(entryIndexList.get(i));
				if (!tempEntry.isURL())
				{
					fileToCopy = tempEntry.getAbsoluteFile();
					if (tempEntry.isFound()) // && fileToCopy.exists())
					{
						dest = new File(y.getPath() + Constants.FS + tempEntry.getFileName());
						try
						{
							FileCopier.copy(fileToCopy, dest);
						}
						catch (IOException e)
						{
							// eat the error and continue
							_logger.error(ExStack.toString(e));
						}
					}
				}
				progress.stepCompleted();
			}
			else
			{
				return;
			}
		}
	}

	public enum SortIx
	{
		None,
		Filename,
		Path,
		Status,
		Random,
		Reverse
	}

	// This constructor creates a temp-file backed playlist from a list of entries, currently used for playback.
	public Playlist(List<PlaylistEntry> sublist) throws IOException
	{
		_utfFormat = true;
		setEntries(sublist);
		_file = File.createTempFile("yay", ".m3u8");
		_file.deleteOnExit();

		_type = PlaylistType.M3U;
		_isModified = false;
		refreshStatus();
		quickSave();
	}

	public Playlist(File playlist, IProgressObserver observer) throws IOException
	{
		init(playlist, observer);
	}

	public Playlist() throws IOException
	{
		_newListCount++;
		_file = new File(HOME_DIR + FS + "Untitled-" + _newListCount + ".m3u8");
		_file.deleteOnExit();
		_utfFormat = true;
		_type = PlaylistType.M3U;
		_isModified = false;
		_isNew = true;
		refreshStatus();
	}

	private void init(File playlist, IProgressObserver observer)
	{
		try
		{
			IPlaylistReader playlistProcessor = PlaylistReaderFactory.getPlaylistReader(playlist);
			if (observer != null)
			{
				List<PlaylistEntry> tempEntries = playlistProcessor.readPlaylist(observer);
				if (tempEntries != null)
				{
					this.setEntries(tempEntries);
				}
				else
				{
					return;
				}
			}
			else
			{
				this.setEntries(playlistProcessor.readPlaylist());
			}
			_utfFormat = playlistProcessor.getEncoding().equals("UTF-8");
			_file = playlist;
			_type = playlistProcessor.getPlaylistType();
			if (_type == PlaylistType.PLS)
			{
				// let's override our previous determination in the PLS case so we don't end up saving it out incorrectly
				_utfFormat = false;
			}
			_isModified = false;
			refreshStatus();
		}
		catch (IOException e)
		{
			_logger.error(ExStack.toString(e));
		}
	}
	
	public Playlist getSublist(int[] rows) throws IOException
	{
		List<PlaylistEntry> tempList = new ArrayList<PlaylistEntry>();
		for (int i : rows)
		{
			tempList.add(_entries.get(i));
		}
		return new Playlist(tempList);
	}

	public List<PlaylistEntry> getSelectedEntries(int[] rows) throws IOException
	{
		List<PlaylistEntry> tempList = new ArrayList<PlaylistEntry>();
		for (int i : rows)
		{
			tempList.add(_entries.get(i));
		}
		return tempList;
	}

	/**
	 * @return the _type
	 */
	public PlaylistType getType()
	{
		return _type;
	}

	/**
	 * @param _type the _type to set
	 */
	public void setType(PlaylistType type)
	{
		_type = type;
	}

	public File getFile()
	{
		return _file;
	}

	public void setFile(File file)
	{
		_file = file;
	}

	public void addModifiedListener(IPlaylistModifiedListener listener)
	{
		if (listener != null)
		{
			if (!_listeners.contains(listener))
			{
				_listeners.add(listener);
			}
		}
	}

	public void removeModifiedListener(IPlaylistModifiedListener listener)
	{
		_listeners.remove(listener);
	}

	public List<IPlaylistModifiedListener> getModifiedListeners()
	{
		return _listeners;
	}

	private void firePlaylistModified()
	{
		for (IPlaylistModifiedListener listener : _listeners)
		{
			if (listener != null)
			{
				listener.playlistModified(this);
			}
		}
	}
	
	private void refreshStatusAndFirePlaylistModified()
	{
		refreshStatus();
		firePlaylistModified();
	}
	List<IPlaylistModifiedListener> _listeners = new ArrayList<IPlaylistModifiedListener>();

	private void setEntries(List<PlaylistEntry> aEntries)
	{
		replaceEntryListContents(aEntries, _originalEntries);
		replaceEntryListContents(aEntries, _entries);
	}
	
	private void replaceEntryListContents(List<PlaylistEntry> src, List<PlaylistEntry> dest)
	{
		dest.clear();
		for (int i = 0; i < src.size(); i++)
		{
			dest.add((PlaylistEntry) src.get(i).clone());
		}
	}

	public int size()
	{
		return _entries.size();
	}

	public void updateModifiedStatus()
	{
		boolean result = false;
		
		// Run a full comparison against the original entry list we created when we were constructed
		if (_originalEntries.size() != _entries.size())
		{
			result = true;
		}
		else
		{
			for (int i = 0; i < _entries.size(); i++)
			{
				PlaylistEntry entryA = _entries.get(i);
				PlaylistEntry entryB = _originalEntries.get(i);
				if ((entryA.isURL() && entryB.isURL()) || (!entryA.isURL() && !entryB.isURL()))
				{
					if (!entryA.isURL())
					{
						if (!entryA.getFile().getPath().equals(entryB.getFile().getPath()))
						{
							result = true;
							break;
						}
					}
					else
					{
						if (!entryA.getURI().toString().equals(entryB.getURI().toString()))
						{
							result = true;
							break;
						}
					}
				}
				else
				{
					result = true;
					break;
				}
			}
		}
		
		// if we refer to a file on disk, and aren't a new file, make sure that file still exists...
		if (_file != null && !isNew())
		{
			_isModified = !_file.exists();			
		}
		else
		{
			_isModified = result;
		}
		
		// notify the listeners if we have changed...
		if (_isModified)
		{
			firePlaylistModified();
		}
	}

	private void refreshStatus()
	{
		_urlCount = 0;
		_missingCount = 0;
		_fixedCount = 0;

		for (int ix = 0; ix < _entries.size(); ix++)
		{
			PlaylistEntry entry = _entries.get(ix);
			boolean entryIsUrl = entry.isURL();
			if (entryIsUrl)
			{
				_urlCount++;
			}
			else if (!entry.isFound())
			{
				_missingCount++;
			}
			if (entry.isFixed())
			{
				_fixedCount++;
			}
		}
		
		updateModifiedStatus();
	}

	public int getFixedCount()
	{
		return _fixedCount;
	}

	public int getUrlCount()
	{
		return _urlCount;
	}

	public int getMissingCount()
	{
		return _missingCount;
	}

	public boolean isModified()
	{
		return _isModified;
	}

	public String getFilename()
	{
		if (_file == null)
		{
			return "";
		}
		return _file.getName();
	}

	public boolean isEmpty()
	{
		return _entries.isEmpty();
	}

	public void play()
	{
		try
		{
			FileLauncher.launch(_file);
		}
		catch (Exception e)
		{
			_logger.warn(ExStack.toString(e));
		}
	}

	public boolean isUtfFormat()
	{
		return _utfFormat;
	}

	public void setUtfFormat(boolean utfFormat)
	{
		this._utfFormat = utfFormat;
	}

	public boolean isNew()
	{
		return _isNew;
	}

	public void replace(int index, PlaylistEntry newEntry)
	{

		if (!_entries.get(index).isFound())
		{
			newEntry.markFixedIfFound();
		}
		_entries.set(index, newEntry);
		refreshStatusAndFirePlaylistModified();
	}

	public void moveUp(int[] indexes)
	{
		Arrays.sort(indexes);
		int ceiling = 0;
		for (int ix = 0; ix < indexes.length; ix++)
		{
			int rowIx = indexes[ix];
			if (rowIx != ceiling)
			{
				Collections.swap(_entries, rowIx, rowIx - 1);
				indexes[ix] = rowIx - 1;
			}
			else
			{
				ceiling++;
			}
		}
		refreshStatusAndFirePlaylistModified();
	}

	public void moveTo(int initialPos, int finalPos)
	{
		PlaylistEntry temp = _entries.get(initialPos);
		_entries.remove(initialPos);
		_entries.add(finalPos, temp);
		refreshStatusAndFirePlaylistModified();
	}

	public void moveDown(int[] indexes)
	{
		Arrays.sort(indexes);
		int floor = _entries.size() - 1;
		for (int ix = indexes.length - 1; ix >= 0; ix--)
		{
			int rowIx = indexes[ix];
			if (rowIx != floor)
			{
				Collections.swap(_entries, rowIx, rowIx + 1);
				indexes[ix] = rowIx + 1;
			}
			else
			{
				floor--;
			}
		}
		refreshStatusAndFirePlaylistModified();
	}

	public int addAllAt(int i, List<PlaylistEntry> entries)
	{
		_entries.addAll(i, entries);
		refreshStatusAndFirePlaylistModified();
		return entries.size();
	}

	public int add(File[] files, IProgressObserver<String> observer) throws FileNotFoundException, IOException
	{
		List<PlaylistEntry> newEntries = getEntriesForFiles(files, observer);
		if (newEntries != null)
		{
			_entries.addAll(newEntries);
			refreshStatusAndFirePlaylistModified();
			return newEntries.size();
		}
		else
		{
			return 0;
		}
	}

	public int addAt(int ix, File[] files, IProgressObserver<String> observer) throws FileNotFoundException, IOException
	{
		List<PlaylistEntry> newEntries = getEntriesForFiles(files, observer);
		if (newEntries != null)
		{
			_entries.addAll(ix, newEntries);
			refreshStatusAndFirePlaylistModified();
			return newEntries.size();
		}
		else
		{
			return 0;
		}
	}

	private List<PlaylistEntry> getEntriesForFiles(File[] files, IProgressObserver<String> observer) throws FileNotFoundException, IOException
	{
		ProgressAdapter<String> progress = ProgressAdapter.wrap(observer);
		progress.setTotal(files.length);

		List<PlaylistEntry> ents = new ArrayList<PlaylistEntry>();
		for (File file : files)
		{
			if (observer == null || !observer.getCancelled())
			{
				if (Playlist.isPlaylist(file))
				{
					// playlist file
					IPlaylistReader reader = PlaylistReaderFactory.getPlaylistReader(file);
					ents.addAll(reader.readPlaylist(progress));
				}
				else
				{
					// regular file
					ents.add(new PlaylistEntry(file, null, _file));
				}
			}
			else
			{
				return null;
			}

			if (progress.getCompleted() != progress.getTotal())
				progress.stepCompleted();
		}

		return ents;
	}

	public void changeEntryFileName(int ix, String newName)
	{
		_entries.get(ix).setFileName(newName);
		refreshStatusAndFirePlaylistModified();
	}

	// returns positions of repaired rows
	public List<Integer> repair(String[] librayFiles, IProgressObserver observer)
	{
		ProgressAdapter progress = ProgressAdapter.wrap(observer);
		progress.setTotal(_entries.size());

		List<Integer> fixed = new ArrayList<Integer>();
		for (int ix = 0; ix < _entries.size(); ix++)
		{
			if (!observer.getCancelled())
			{
				progress.stepCompleted();

				PlaylistEntry entry = _entries.get(ix);
				if (!entry.isFound() && !entry.isURL())
				{
					entry.findNewLocationFromFileList(librayFiles);
					if (entry.isFound())
					{
						fixed.add(ix);
						refreshStatusAndFirePlaylistModified();
					}
				}
				else if(entry.isFound() && !entry.isURL())
				{
					if (entry.updatePathToMediaLibraryIfFoundOutside())
					{
						fixed.add(ix);
						refreshStatusAndFirePlaylistModified();
					}
				}
			}
			else
			{
				return null;
			}
		}
		
		return fixed;
	}

	// similar to repair, but doesn't return repaired row information
	public void batchRepair(String[] fileList, IProgressObserver observer)
	{
		ProgressAdapter progress = ProgressAdapter.wrap(observer);
		progress.setTotal(_entries.size());

		boolean isModified = false;
		for (PlaylistEntry entry : _entries)
		{
			progress.stepCompleted();

			if (!entry.isFound() && !entry.isURL())
			{
				entry.findNewLocationFromFileList(fileList);
				if (!isModified && entry.isFound())
				{
					isModified = true;
				}
			}
			else if(entry.isFound() && !entry.isURL())
			{
				if (entry.updatePathToMediaLibraryIfFoundOutside())
				{
					isModified = true;
				}
			}
		}

		if (isModified)
		{
			refreshStatusAndFirePlaylistModified();
		}
	}

	public List<BatchMatchItem> findClosestMatches(String[] libraryFiles, IProgressObserver observer)
	{
		ProgressAdapter progress = ProgressAdapter.wrap(observer);
		progress.setTotal(_entries.size());

		List<BatchMatchItem> fixed = new ArrayList<BatchMatchItem>();
		PlaylistEntry entry = null;
		List<MatchedPlaylistEntry> matches = null;
		for (int ix = 0; ix < _entries.size(); ix++)
		{
			progress.stepCompleted();
			if (!observer.getCancelled())
			{
				entry = _entries.get(ix);				
				if (!entry.isURL() && !entry.isFound())
				{
					matches = entry.findClosestMatches(libraryFiles, null);
					if (!matches.isEmpty())
					{
						fixed.add(new BatchMatchItem(ix, entry, matches));
					}
				}
			}
			else
			{
				return null;
			}
		}

		return fixed;
	}	

	public List<BatchMatchItem> findClosestMatchesForSelectedEntries(List<Integer> rowList, String[] libraryFiles, IProgressObserver observer)
	{
		ProgressAdapter progress = ProgressAdapter.wrap(observer);
		progress.setTotal(_entries.size());

		List<BatchMatchItem> fixed = new ArrayList<BatchMatchItem>();
		PlaylistEntry entry = null;
		List<MatchedPlaylistEntry> matches = null;
		for (Integer ix : rowList)
		{
			progress.stepCompleted();
			if (!observer.getCancelled())
			{
				entry = _entries.get(ix);				
				if (!entry.isURL() && !entry.isFound())
				{
					matches = entry.findClosestMatches(libraryFiles, null);
					if (!matches.isEmpty())
					{
						fixed.add(new BatchMatchItem(ix, entry, matches));
					}
				}
			}
			else
			{
				return null;
			}
		}

		return fixed;
	}

	public List<Integer> applyClosestMatchSelections(List<BatchMatchItem> items)
	{
		List<Integer> fixed = new ArrayList<Integer>();
		for (BatchMatchItem item : items)
		{
			if (item.getSelectedIx() >= 0)
			{
				int ix = item.getEntryIx();
				fixed.add(ix);
				_entries.set(ix, item.getSelectedMatch().getPlaylistFile());
				_entries.get(ix).markFixedIfFound();
			}
		}

		if (!fixed.isEmpty())
		{
			refreshStatusAndFirePlaylistModified();
		}
		return fixed;
	}

	public PlaylistEntry get(int index)
	{
		return _entries.get(index);
	}

	public void remove(int[] indexes)
	{
		Arrays.sort(indexes);
		for (int ix = indexes.length - 1; ix >= 0; ix--)
		{
			int rowIx = indexes[ix];
			_entries.remove(rowIx);
		}
		refreshStatusAndFirePlaylistModified();
	}

	public int remove(PlaylistEntry entry)
	{
		int result = _entries.indexOf(entry);
		_entries.remove(entry);
		refreshStatusAndFirePlaylistModified();
		return result;
	}

	public void reorder(SortIx sortIx, boolean isDescending)
	{
		switch (sortIx)
		{
			case Filename:
			case Path:
			case Status:
				Collections.sort(_entries, new EntryComparator(sortIx, isDescending));
				break;

			case Random:
				Collections.shuffle(_entries);
				break;

			case Reverse:
				Collections.reverse(_entries);
				break;
		}
		refreshStatusAndFirePlaylistModified();
	}

	private static class EntryComparator implements Comparator<PlaylistEntry>
	{
		public EntryComparator(SortIx sortIx, boolean isDescending)
		{
			_sortIx = sortIx;
			_isDescending = isDescending;
		}
		final SortIx _sortIx;
		final boolean _isDescending;

		public int compare(PlaylistEntry lhs, PlaylistEntry rhs)
		{
			int rc = 0;

			switch (_sortIx)
			{
				case Filename:
					rc = lhs.getFileName().compareToIgnoreCase(rhs.getFileName());
					break;
				case Path:
					rc = lhs.getPath().compareToIgnoreCase(rhs.getPath());
					break;
				case Status:
					// Randomly chosen order... Found > Fixed > Missing > URL (seemed reasonable)
					if (lhs.isURL())
					{
						if (rhs.isURL())
						{
							rc = 0;
							break;
						}
						rc = 1;
						break;
					}
					else if (!lhs.isFound())
					{
						if (rhs.isURL())
						{
							rc = -1;
							break;
						}
						else if (!rhs.isFound())
						{
							rc = 0;
							break;
						}
						rc = 1;
						break;
					}
					else if (lhs.isFixed())
					{
						if (!rhs.isFound() || rhs.isURL())
						{
							rc = -1;
							break;
						}
						else if (rhs.isFixed())
						{
							rc = 0;
							break;
						}
						rc = 1;
						break;
					}
					rc = -1;
					break;
			}

			return _isDescending ? -rc : rc;
		}
	}

	public int removeDuplicates()
	{
		int removed = 0;
		Set<String> found = new HashSet<String>();
		for (int ix = 0; ix < _entries.size();)
		{
			PlaylistEntry entry = _entries.get(ix);
			String name = entry.getFileName();
			if (found.contains(name))
			{
				// duplicate found, remove
				_entries.remove(ix);
				removed++;
			}
			else
			{
				found.add(name);
				ix++;
			}
		}
		if (removed > 0)
		{
			refreshStatusAndFirePlaylistModified();
		}
		return removed;
	}

	public int removeMissing()
	{
		int removed = 0;
		for (int ix = _entries.size() - 1; ix >= 0; ix--)
		{
			PlaylistEntry entry = _entries.get(ix);
			if (!entry.isURL() && !entry.isFound())
			{
				_entries.remove(ix);
				removed++;
			}
		}
		if (removed > 0)
		{
			refreshStatusAndFirePlaylistModified();
		}
		return removed;
	}

	public void saveAs(File destination, boolean saveRelative, IProgressObserver observer) throws IOException
	{
		_file = destination;
		_type = determinePlaylistType(destination);
		if (_type == PlaylistType.PLS)
		{
			// apparently winamp shits itself if PLS files are saved in UTF-8 (who knew...)
			// but, since VLC supports UTF-8 formatted PLS files, and winamp can't read the filenames right
			// when we don't save as UTF-8 anyway, I'm removing this restriction
			// _utfFormat = false;
		}
		save(saveRelative, observer);
	}

	public final void save(boolean saveRelative, IProgressObserver observer) throws IOException
	{
		// avoid resetting total if part of batch operation
		boolean hasTotal = observer instanceof ProgressAdapter;
		ProgressAdapter progress = ProgressAdapter.wrap(observer);
		if (!hasTotal)
		{
			progress.setTotal(_entries.size());
		}

		if (getType() == PlaylistType.M3U)
		{
			saveM3U(saveRelative, progress);
		}
		else if (getType() == PlaylistType.PLS)
		{
			savePLS(saveRelative, progress);
		}
		else if (getType() == PlaylistType.WPL)
		{
			saveWPL(saveRelative, progress);
		}
		
		// change original _entries
		replaceEntryListContents(_entries, _originalEntries);

		// set entries unfixed if we're being watched...
		// (otherwise writing out a temp file for playback, at least right now)
		if (observer != null)
		{
			for (PlaylistEntry entry : _entries)
			{
				entry.setFixed(false);
			}

			_isModified = false;
			_isNew = false;
			refreshStatusAndFirePlaylistModified();
		}
	}

	private void quickSave() throws IOException
	{
		saveM3U(false, null);
	}

	private void saveM3U(boolean saveRelative, ProgressAdapter progress) throws IOException
	{
		boolean track = progress != null;

		StringBuilder buffer = new StringBuilder();
		buffer.append("#EXTM3U").append(BR);
		PlaylistEntry entry = null;
		String relativePath = "";
		for (int i = 0; i < _entries.size(); i++)
		{
			if (!track || !progress.getCancelled())
			{
				if (track)
				{
					progress.stepCompleted();
				}
				entry = _entries.get(i);
				entry.setPlaylist(_file);
				if (!entry.isURL())
				{
					if (!saveRelative && entry.isRelative() && entry.getAbsoluteFile() != null)
					{
						// replace existing relative entry with a new absolute one
						File absolute = entry.getAbsoluteFile().getCanonicalFile();
						
						// Switch to UNC representation if selected in the options
						if (GUIDriver.getInstance().getAppOptions().getAlwaysUseUNCPaths())
						{
							UNCFile temp = new UNCFile(absolute);
							absolute = new File(temp.getUNCPath());
						}
						
						// make the entry and addAt it
						entry = new PlaylistEntry(absolute, entry.getExtInf(), _file);
						_entries.set(i, entry);
					}
					else if (saveRelative && entry.isFound())
					{
						// replace existing entry with a new relative one
						relativePath = FileUtils.getRelativePath(entry.getAbsoluteFile().getCanonicalFile(), _file);
						if (!OperatingSystem.isWindows() && relativePath.indexOf(Constants.FS) < 0)
						{
							relativePath = "." + Constants.FS + relativePath;
						}
						
						// make a new file out of this relative path, and see if it's really relative...
						// if it's absolute, we have to perform the UNC check and convert if necessary.
						File temp = new File(relativePath);
						if (temp.isAbsolute())
						{
							// Switch to UNC representation if selected in the options
							if (GUIDriver.getInstance().getAppOptions().getAlwaysUseUNCPaths())
							{
								UNCFile uncd = new UNCFile(temp);
								temp = new File(uncd.getUNCPath());
							}
						}
						
						// make the entry and addAt it
						entry = new PlaylistEntry(temp, entry.getExtInf(), _file);
						_entries.set(i, entry);
					}
				}

				buffer.append(entry.toM3UString()).append(BR);
			}
			else
			{
				return;
			}
		}

		if (!track || !progress.getCancelled())
		{
			File dirToSaveIn = _file.getParentFile().getAbsoluteFile();
			if (!dirToSaveIn.exists())
			{
				dirToSaveIn.mkdirs();
			}

			FileOutputStream outputStream = new FileOutputStream(_file);
			if (isUtfFormat() || _file.getName().toLowerCase().endsWith("m3u8"))
			{
				Writer osw = new OutputStreamWriter(outputStream, "UTF8");
				BufferedWriter output = new BufferedWriter(osw);
				if (OperatingSystem.isWindows())
				{
					// For some reason, linux players seem to choke on this header when I addAt it... perhaps the stream classes do it automatically.
					output.write(UnicodeUtils.getBOM("UTF-8"));
				}
				output.write(buffer.toString());
				output.close();
				outputStream.close();
				setUtfFormat(true);
			}
			else
			{
				BufferedOutputStream output = new BufferedOutputStream(outputStream);
				output.write(buffer.toString().getBytes());
				output.close();
				outputStream.close();
				setUtfFormat(false);
			}
		}
	}

	private void savePLS(boolean saveRelative, ProgressAdapter progress) throws IOException
	{
		boolean track = progress != null;

		PlaylistEntry entry = null;
		StringBuilder buffer = new StringBuilder();
		buffer.append("[playlist]").append(BR);
		for (int i = 0; i < _entries.size(); i++)
		{
			if (!track || !progress.getCancelled())
			{
				if (track)
				{
					progress.stepCompleted();
				}
				entry = _entries.get(i);
				if (!entry.isURL())
				{
					if (!saveRelative && entry.getAbsoluteFile() != null)
					{
						// replace existing relative entry with a new absolute one
						File absolute = entry.getAbsoluteFile().getCanonicalFile();

						// Switch to UNC representation if selected in the options
						if (GUIDriver.getInstance().getAppOptions().getAlwaysUseUNCPaths())
						{
							UNCFile temp = new UNCFile(absolute);
							absolute = new File(temp.getUNCPath());
						}
						
						entry = new PlaylistEntry(absolute, entry.getTitle(), entry.getLength(), _file);
						_entries.set(i, entry);
					}
					else if (saveRelative && entry.isFound())
					{
						// replace existing entry with a new relative one
						String relativePath = FileUtils.getRelativePath(entry.getAbsoluteFile().getCanonicalFile(), _file);
						if (!OperatingSystem.isWindows() && relativePath.indexOf(Constants.FS) < 0)
						{
							relativePath = "." + Constants.FS + relativePath;
						}

						// make a new file out of this relative path, and see if it's really relative...
						// if it's absolute, we have to perform the UNC check and convert if necessary.
						File temp = new File(relativePath);
						if (temp.isAbsolute())
						{
							// Switch to UNC representation if selected in the options
							if (GUIDriver.getInstance().getAppOptions().getAlwaysUseUNCPaths())
							{
								UNCFile uncd = new UNCFile(temp);
								temp = new File(uncd.getUNCPath());
							}
						}

						// make the entry and addAt it						
						entry = new PlaylistEntry(temp, entry.getTitle(), entry.getLength(), _file);
						_entries.set(i, entry);
					}
				}
				buffer.append(entry.toPLSString(i + 1));
			}
			else
			{
				return;
			}
		}

		buffer.append("NumberOfEntries=").append(_entries.size()).append(BR);
		buffer.append("Version=2");

		if (!track || !progress.getCancelled())
		{
			File dirToSaveIn = _file.getParentFile().getAbsoluteFile();
			if (!dirToSaveIn.exists())
			{
				dirToSaveIn.mkdirs();
			}

			if (isUtfFormat())
			{
				FileOutputStream outputStream = new FileOutputStream(_file);
				Writer osw = new OutputStreamWriter(outputStream, "UTF8");
				BufferedWriter output = new BufferedWriter(osw);
				output.write(UnicodeUtils.getBOM("UTF-8") + buffer.toString());
				output.close();
				outputStream.close();
				setUtfFormat(true);
			}
			else
			{
				FileOutputStream outputStream = new FileOutputStream(_file);
				BufferedOutputStream output = new BufferedOutputStream(outputStream);
				output.write(buffer.toString().getBytes());
				output.close();
				outputStream.close();
				setUtfFormat(false);
			}
		}
	}

	private void saveWPL(boolean saveRelative, ProgressAdapter progress) throws IOException
	{
		boolean track = progress != null;

		StringBuilder buffer = new StringBuilder();
		buffer.append(getWPLHead());
		PlaylistEntry entry = null;
		String relativePath = "";
		for (int i = 0; i < _entries.size(); i++)
		{
			if (!track || !progress.getCancelled())
			{
				if (track)
				{
					progress.stepCompleted();
				}
				entry = _entries.get(i);
				entry.setPlaylist(_file);
				if (!entry.isURL())
				{
					if (!saveRelative && entry.isRelative() && entry.getAbsoluteFile() != null)
					{
						// replace existing relative entry with a new absolute one
						File absolute = entry.getAbsoluteFile().getCanonicalFile();
						
						// Switch to UNC representation if selected in the options
						if (GUIDriver.getInstance().getAppOptions().getAlwaysUseUNCPaths())
						{
							UNCFile temp = new UNCFile(absolute);
							absolute = new File(temp.getUNCPath());
						}
						entry = new PlaylistEntry(absolute, entry.getExtInf(), _file, entry.getCID(), entry.getTID());
						_entries.set(i, entry);
					}
					else if (saveRelative && entry.isFound())
					{						
						// replace existing entry with a new relative one
						relativePath = FileUtils.getRelativePath(entry.getAbsoluteFile().getCanonicalFile(), _file);
						if (!OperatingSystem.isWindows() && relativePath.indexOf(Constants.FS) < 0)
						{
							relativePath = "." + Constants.FS + relativePath;
						}
						
						// make a new file out of this relative path, and see if it's really relative...
						// if it's absolute, we have to perform the UNC check and convert if necessary.
						File temp = new File(relativePath);
						if (temp.isAbsolute())
						{
							// Switch to UNC representation if selected in the options
							if (GUIDriver.getInstance().getAppOptions().getAlwaysUseUNCPaths())
							{
								UNCFile uncd = new UNCFile(temp);
								temp = new File(uncd.getUNCPath());
							}
						}
						
						// make the entry and addAt it						
						entry = new PlaylistEntry(temp, entry.getExtInf(), _file, entry.getCID(), entry.getTID());
						_entries.set(i, entry);
					}
				}

				String media = "\t\t\t<media src=\"" + XMLEncode(entry.toWPLString()) + "\"";
				if (!entry.getCID().isEmpty()) media += " cid=\"" + entry.getCID() + "\"";
				if (!entry.getTID().isEmpty()) media += " tid=\"" + entry.getTID() + "\"";
				media += "/>" + BR;				
				buffer.append(media);
			}
			else
			{
				return;
			}
		}
		buffer.append(getWPLFoot());
		
		if (!track || !progress.getCancelled())
		{
			File dirToSaveIn = _file.getParentFile().getAbsoluteFile();
			if (!dirToSaveIn.exists())
			{
				dirToSaveIn.mkdirs();
			}

			FileOutputStream outputStream = new FileOutputStream(_file);
			Writer osw = new OutputStreamWriter(outputStream, "UTF8");
			BufferedWriter output = new BufferedWriter(osw);
			output.write(buffer.toString());
			output.close();
			outputStream.close();
			setUtfFormat(true);
		}
	}
	
	// WPL Helper Method
	private String XMLEncode(String s)
	{
		s = s.replaceAll("&", "&amp;");
		s = s.replaceAll("'", "&apos;");		
		s = s.replaceAll("<", "&lt;");
		s = s.replaceAll(">", "&gt;");
		return s;
	}
	
	// WPL Helper Method
	private String getWPLHead() throws IOException
	{
		String head = "";
		boolean newHead = false;
		try
		{
			BufferedReader buffer = new BufferedReader(new InputStreamReader(new UnicodeInputStream(new FileInputStream(_file), "UTF-8"), "UTF8"));
			String line = buffer.readLine();
			while (line != null)
			{
				if (line.trim().startsWith("<media"))
				{
					break;
				}
				head += line + BR;
				line = buffer.readLine();
			}
			buffer.close();
			// determine if a head was read
			if (!head.contains("<?wpl"))
			{
				newHead = true;
			}
		}
		catch (Exception ex)
		{
			// Don't bother logging here, it's expected when saving out a new file
			// _logger.error(ExStack.toString(ex));
		}
		finally
		{
			newHead = true;
		}
		if (newHead) head = "<?wpl version=\"1.0\"?>\r\n<smil>\r\n\t<body>\r\n\t\t<sec>\r\n";
		return head;
	}
	
	// WPL Helper Method
	private String getWPLFoot() throws IOException
	{
		return "\t\t</sec>\r\n\t</body>\r\n</smil>";
	}
	
	public void reload(IProgressObserver observer)
	{
		if (_isNew)
		{
			_file = new File(HOME_DIR + FS + "Untitled-" + _newListCount + ".m3u8");
			_file.deleteOnExit();
			_utfFormat = true;
			_type = PlaylistType.M3U;
			_isModified = false;
			_isNew = true;
			_entries.clear();
			_originalEntries.clear();
		}
		else if (_file.exists())
		{
			init(_file, observer);
		}
		else
		{
			replaceEntryListContents(_originalEntries, _entries);
		}
		refreshStatusAndFirePlaylistModified();
	}

	public static boolean isPlaylist(File input)
	{
		return determinePlaylistType(input) != PlaylistType.UNKNOWN;
	}

	private static PlaylistType determinePlaylistType(File input)
	{
		if (input != null)
		{
			String lowerCaseExtension = FileNameTokenizer.getExtensionFromFileName(input.getName()).toLowerCase();
			if (lowerCaseExtension.equals("m3u") || lowerCaseExtension.equals("m3u8"))
			{
				return PlaylistType.M3U;
			}
			else if (lowerCaseExtension.equals("pls"))
			{
				return PlaylistType.PLS;
			}
			else if (lowerCaseExtension.equals("wpl"))
			{
				return PlaylistType.WPL;
			}
		}
		return PlaylistType.UNKNOWN;
	}
}
